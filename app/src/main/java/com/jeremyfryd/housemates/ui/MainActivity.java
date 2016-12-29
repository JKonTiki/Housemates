package com.jeremyfryd.housemates.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeremyfryd.housemates.Constants;
import com.jeremyfryd.housemates.Manifest;
import com.jeremyfryd.housemates.R;
import com.jeremyfryd.housemates.adapters.InhabitantListAdapter;
import com.jeremyfryd.housemates.models.House;
import com.jeremyfryd.housemates.models.Roommate;
import com.jeremyfryd.housemates.services.GeofenceTransitionService;


import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener,
        ResultCallback<Status> {
    @Bind(R.id.userLogo) ImageView mUserLogo;
    @Bind(R.id.addNewPlus) ImageView mAddNewPlusIcon;
    @Bind(R.id.addNewHouse) ImageView mAddNewHouseIcon;
    @Bind(R.id.joinArrow) ImageView mJoinArrowIcon;
    @Bind(R.id.joinHouse) ImageView mJoinHouseIcon;
    @Bind(R.id.noHousesMessage) TextView mNoHousesTextView;
    @Bind(R.id.houseName) TextView mHouseName;
    @Bind(R.id.roommatesList) ListView mActiveRoommatesListView;
    @Bind(R.id.toGetCode) Button mToGetCodeButton;
    private SharedPreferences mSharedPreferences;
    private Roommate mRoommate;
    private FirebaseUser mUser;
    private String mUsername;
    private String mUserId;
    private House mHouse;
    private String mActiveHouseId;
    private InhabitantListAdapter mAdapter;
    private List<String> mActiveHouseInhabitantIds;
    private List<Roommate> mActiveHouseInhabitants = new ArrayList<Roommate>();
    private List<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private MapFragment mapFragment;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private final int REQ_PERMISSION = 999;
    private Marker locationMarker;
    private Marker geoFenceMarker;
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private Circle geoFenceLimits;
    private boolean initMapsReadyForHouse;
    private boolean initMapsReadyForApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLogo.setOnClickListener(this);
        mAddNewPlusIcon.setOnClickListener(this);
        mAddNewHouseIcon.setOnClickListener(this);
        mJoinArrowIcon.setOnClickListener(this);
        mJoinHouseIcon.setOnClickListener(this);
        mToGetCodeButton.setOnClickListener(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mUser.getUid();
        mUsername = mSharedPreferences.getString(Constants.PREFERENCES_USERNAME_KEY, null);
        createGoogleApi();


        final DatabaseReference roommateRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_ROOMMATES);

        DatabaseReference roommateChildRef = roommateRef.child(mUserId);


        roommateChildRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot roommateSnapshot) {
                if (roommateSnapshot.exists()) {
                    mRoommate = roommateSnapshot.getValue(Roommate.class);
                    if (mRoommate.getHouseIds().size()>0){
                        mActiveHouseId = mRoommate.getHouseIds().get(0);
                        Log.d("active user profile", mRoommate.getName());
                        DatabaseReference houseRef = FirebaseDatabase
                                .getInstance()
                                .getReference(Constants.FIREBASE_CHILD_HOUSES)
                                .child(mActiveHouseId);

                        houseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot houseSnapshot) {
                                if (houseSnapshot.exists()){
                                    mHouse = houseSnapshot.getValue(House.class);

                                    Log.d("house", mHouse.getName());
                                    initMapsReadyForHouse = true;
                                    initGMaps();
                                    initMapsReadyForHouse = false;



                                    mHouseName.setText(mHouse.getName()+ ":");
                                    mActiveHouseInhabitantIds = mHouse.getRoommates();
                                    ViewGroup.LayoutParams params = mActiveRoommatesListView.getLayoutParams();
                                    params.height = 225 * mActiveHouseInhabitantIds.size();
                                    mActiveRoommatesListView.setLayoutParams(params);
                                    Log.d(TAG, "set listview height");
                                    for (int i=0; i< mActiveHouseInhabitantIds.size(); i++){
                                        roommateRef.child(mActiveHouseInhabitantIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot inhabitantSnapshot) {
                                                if (inhabitantSnapshot.exists()){
                                                    Roommate activeHouseInhabitant = inhabitantSnapshot.getValue(Roommate.class);
                                                    mActiveHouseInhabitants.add(activeHouseInhabitant);


//                                                    TODO set each roommates geofence status here
                                                    activeHouseInhabitant.isHome(true);


                                                    if (mAdapter == null){
                                                        mAdapter = new InhabitantListAdapter(MainActivity.this, mActiveHouseInhabitants);
                                                        mActiveRoommatesListView.setAdapter(mAdapter);
                                                    } else{
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }

                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });

                                    }





                                } else{
                                    mNoHousesTextView.setText("YOU DO NOT YET BELONG TO ANY HOUSES");
                                    Log.d("house retrieval: ", "fail, no houses from listener");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    } else{
                        Log.d("house retrieval: ", "roommate exists, no houses");
                        mNoHousesTextView.setText("YOU DO NOT YET BELONG TO ANY HOUSES");
                    }
                } else{
                    if (mUsername == null){
                        mUsername = "dude";
                    }
                    mRoommate = new Roommate(mUsername, mUser.getUid());
                    mNoHousesTextView.setText("YOU DO NOT YET BELONG TO ANY HOUSES");
                    DatabaseReference roommatePushRef = roommateRef.child(mRoommate.getRoommateId());
                    roommatePushRef.setValue(mRoommate);
                    Log.d("house retrieval: ", "profile just generated");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onClick(View v){
        if (v == mUserLogo){
            logout();
        } else if(v == mAddNewHouseIcon || v == mAddNewPlusIcon){
            Intent intent = new Intent(MainActivity.this, NewHouseActivity.class);
            intent.putExtra("currentRoommate", Parcels.wrap(mRoommate));
            startActivity(intent);
        } else if(v == mJoinArrowIcon || v == mJoinHouseIcon){
            Intent intent = new Intent(MainActivity.this, UseCodeActivity.class);
            intent.putExtra("currentRoommate", Parcels.wrap(mRoommate));
            startActivity(intent);
        } else if (v == mToGetCodeButton){
            Intent intent = new Intent(MainActivity.this, GetCodeActivity.class);
            intent.putExtra("currentHouse", Parcels.wrap(mHouse));
            startActivity(intent);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void initGMaps() {
        Log.d(TAG, "initGMaps()");
        if (initMapsReadyForApiClient && initMapsReadyForHouse){
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            getLastKnownLocation();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
        markerForGeofence(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        initMapsReadyForApiClient = true;
        initGMaps();
        initMapsReadyForApiClient = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private void writeActualLocation(Location location) {
//        TESTING
//        mHouse = new House("testHouse","37.0","-125.0","TESTY");
        markerLocation(new LatLng(Double.parseDouble(mHouse.getLatitude()), Double.parseDouble(mHouse.getLongitude())));
    }

    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }


    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(Constants.LOCATION_FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }

    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if ( map!=null ) {
            // Remove the anterior marker
            if ( locationMarker != null ){
                locationMarker.remove();
            }
            locationMarker = map.addMarker(markerOptions);
            float zoom = 22;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if ( map!=null ) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(markerOptions);
        }
    }

    private Geofence createGeofence( LatLng latLng, float radius ) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(Constants.GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( Constants.GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }


    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
//                add mGeofences list
                .build();
    }

    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTransitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }


    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            drawGeofence();
        } else {
            // inform about fail
        }
    }

    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( geoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( Constants.GEOFENCE_RADIUS );
        geoFenceLimits = map.addCircle( circleOptions );
    }

    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if( geoFenceMarker != null ) {
            Geofence geofence = createGeofence( geoFenceMarker.getPosition(), Constants.GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

}