package com.jeremyfryd.housemates.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeremyfryd.housemates.Constants;
import com.jeremyfryd.housemates.R;
import com.jeremyfryd.housemates.adapters.InhabitantListAdapter;
import com.jeremyfryd.housemates.models.House;
import com.jeremyfryd.housemates.models.Roommate;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status>,
        NavigationView.OnNavigationItemSelectedListener{
    @Bind(R.id.noHousesMessage) TextView mNoHousesTextView;
    @Bind(R.id.houseName) TextView mHouseNameTextView;
    @Bind(R.id.roommatesList) ListView mActiveRoommatesListView;
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
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private LocationRequest locationRequest;
    private final int REQ_PERMISSION = 999;
    private boolean atHouseCheckReadyForHouse;
    private boolean atHouseCheckReadyForApiClient;
    private LatLng mHouseLatLng;
    private DatabaseReference mCurrentRoommatePushRef;
    private DatabaseReference roommateRef;
    private boolean mAdapterTriggered;
    private DatabaseReference houseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        createNavDrawer();
        ButterKnife.bind(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mUser.getUid();
        mUsername = mSharedPreferences.getString(Constants.PREFERENCES_USERNAME_KEY, null);
        createGoogleApi();
        roommateRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_ROOMMATES);
        DatabaseReference roommateChildRef = roommateRef.child(mUserId);

//      our first call to the database: get roommate object based on authenticated user's ID
        roommateChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot roommateSnapshot) {
                if (roommateSnapshot.exists()) {
                    mRoommate = roommateSnapshot.getValue(Roommate.class);
                    if (mRoommate.getHouseIds().size()>0){
                        mActiveHouseId = mRoommate.getHouseIds().get(0);
//                        at this point we are only calling the first house associated with a roommate
                        Log.d("active user profile", mRoommate.getName());
                        houseRef = FirebaseDatabase
                                .getInstance()
                                .getReference(Constants.FIREBASE_CHILD_HOUSES)
                                .child(mActiveHouseId);
                        // TODO also add a child event listener for the house that would add a corresponding new individual roommate event listener
//                        our second call through the database, we call the house object (currently just the first one chosen) associated with the roommate
                        houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot houseSnapshot) {
                                if (houseSnapshot.exists()){
                                    mHouse = houseSnapshot.getValue(House.class);
                                    atHouseCheckReadyForHouse = true;
                                    Log.d(TAG, "house location: Lat: " + mHouse.getLatitude() + ", Long: " + mHouse.getLongitude());
                                    mHouseLatLng = new LatLng(Double.parseDouble(mHouse.getLatitude()), Double.parseDouble(mHouse.getLongitude()));
                                    if (atHouseCheckReadyForHouse && atHouseCheckReadyForApiClient){
//                                  we don't want to launch this function until we have both the user's location AND the house location
                                        checkIfCurrentUserAtHouse();
                                    }
                                    mHouseNameTextView.setText(mHouse.getName()+ ":");
                                    mActiveHouseInhabitantIds = mHouse.getRoommates();
                                    ViewGroup.LayoutParams params = mActiveRoommatesListView.getLayoutParams();
                                    params.height = 250 * mActiveHouseInhabitantIds.size();
                                    mActiveRoommatesListView.setLayoutParams(params);
                                    Log.d(TAG, "set listview height");
                                    mActiveHouseInhabitants.clear();
                                    for (int i=0; i< mActiveHouseInhabitantIds.size(); i++){
//                                        our third database call, this one attaches persisting listeners to all members of the house being displayed
                                        roommateRef.child(mActiveHouseInhabitantIds.get(i)).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot inhabitantSnapshot) {
                                                if (inhabitantSnapshot.exists()){
                                                    Roommate activeHouseInhabitant = inhabitantSnapshot.getValue(Roommate.class);
                                                    int activeInhabitantListPosition = updateRoommateToHouse(activeHouseInhabitant);
                                                    if (mAdapter == null){
                                                        mAdapter = new InhabitantListAdapter(MainActivity.this, mActiveHouseInhabitants);
                                                        mActiveRoommatesListView.setAdapter(mAdapter);
                                                    } else{
//                                                        notify add child if position is -1, notify itemchanged at i otherwise
                                                        mAdapter.notifyDataSetChanged();
                                                        Log.d(TAG, "notifyDataSetChanged()");
                                                    }
                                                    mAdapterTriggered = true;
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
                    mCurrentRoommatePushRef = roommateRef.child(mRoommate.getRoommateId());
                    mCurrentRoommatePushRef.setValue(mRoommate);
                    Log.d("house retrieval: ", "profile just generated");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }

    @Override
    public void onClick(View v){
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
        atHouseCheckReadyForApiClient = true;
        startSendingLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }



//TODO refactor this as a service running on all activities
    private void startSendingLocation() {
        Log.d(TAG, "startSendingLocation()");
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( mLastLocation != null ) {
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }


    private void writeLastLocation() {
        Log.d(TAG, "LastKnown location. " +
                "Lat: " + mLastLocation.getLatitude() +
                " | Long: " + mLastLocation.getLongitude());
        if (atHouseCheckReadyForHouse && atHouseCheckReadyForApiClient){
            checkIfCurrentUserAtHouse();
        }
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
        mLastLocation = location;
        writeLastLocation();
    }


    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
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


    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
        } else {
            // inform about fail
        }
    }

    
    public void checkIfCurrentUserAtHouse(){
        Log.d(TAG, "checkIfCurrentUserAtHouse()");
        if ((!Double.isNaN(mLastLocation.getLatitude()) && !Double.isNaN(mLastLocation.getLongitude())) && (mHouse.getLatitude().length()>0 && mHouse.getLatitude().length()>0)){
            Double latDifference =  mHouseLatLng.latitude - mLastLocation.getLatitude();
            Double longDifference =  mHouseLatLng.longitude - mLastLocation.getLongitude();
            Log.d("latDifference", latDifference.toString());
            Log.d("longDifference", longDifference.toString());
            if ((latDifference <= Constants.HOUSE_FENCE_DISTANCE && latDifference >= -Constants.HOUSE_FENCE_DISTANCE) && (longDifference <= Constants.HOUSE_FENCE_DISTANCE && longDifference >= -Constants.HOUSE_FENCE_DISTANCE)){
                mRoommate.isHome(true);
                Log.d("changeHomeStatus", "true");
            } else{
                mRoommate.isHome(false);
                Log.d("changeHomeStatus", "false");
            }
            mCurrentRoommatePushRef = roommateRef.child(mRoommate.getRoommateId());
            mCurrentRoommatePushRef.child("atHome").setValue(mRoommate.getAtHome());
            if (mAdapterTriggered){
                mAdapter.notifyDataSetChanged();
                Log.d("updateDataSet", "true");
            }
        }else{
            Log.d(TAG, "some location data is incomplete for checking if home");
        }
    }

    public int updateRoommateToHouse(Roommate activeInhabitant){
        int position = -1;
        for (int i=0; i < mActiveHouseInhabitants.size(); i++){
            if (mActiveHouseInhabitants.get(i).getRoommateId().equals(activeInhabitant.getRoommateId())){
                position = i;
            }
        }
        if (position == -1){
            mActiveHouseInhabitants.add(activeInhabitant);
        } else{
            mActiveHouseInhabitants.set(position, activeInhabitant);
        }
        return position;
    }




//   the following bloc regards the Navigation drawer, which should also be refactored as a fragment for better readibility/resuability
    protected void createNavDrawer() {
        Log.d(TAG, "createNavDrawer()");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.homeIcon) {
            // Handle the camera action
        } else if (id == R.id.addHouseIcon) {
            Intent intent = new Intent(MainActivity.this, NewHouseActivity.class);
            intent.putExtra("currentRoommate", Parcels.wrap(mRoommate));
            startActivity(intent);
        } else if (id == R.id.joinHouseIcon) {
            Intent intent = new Intent(MainActivity.this, UseCodeActivity.class);
            intent.putExtra("currentRoommate", Parcels.wrap(mRoommate));
            startActivity(intent);
        } else if (id == R.id.inviteIcon) {
            Intent intent = new Intent(MainActivity.this, GetCodeActivity.class);
            intent.putExtra("currentHouse", Parcels.wrap(mHouse));
            startActivity(intent);
        } else if (id == R.id.settingsIcon) {
            Toast.makeText(MainActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.logoutIcon) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}