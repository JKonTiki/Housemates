package com.jeremyfryd.housemates.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jeremyfryd.housemates.Constants;
import com.jeremyfryd.housemates.R;
import com.jeremyfryd.housemates.models.House;
import com.jeremyfryd.housemates.models.Roommate;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewHouseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, com.google.android.gms.location.LocationListener{
    @Bind(R.id.useLocationButton) Button mUseLocationButton;
    @Bind(R.id.createHouseButton) Button mCreateHouseButton;
    @Bind(R.id.latitudeTextView) TextView mLatitudeTextView;
    @Bind(R.id.longitudeTextView) TextView mLongitudeTextView;
    @Bind(R.id.nameEditText) EditText mHouseName;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private PendingResult<LocationSettingsResult> result;
    static final Integer LOCATION = 1;
    private FusedLocationProviderApi mFusedLocationProviderApi;
    private String mLatitude;
    private String mLongitude;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean locationUpdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_house);
        ButterKnife.bind(this);
        mUseLocationButton.setOnClickListener(this);
        mCreateHouseButton.setOnClickListener(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        locationUpdated = false;
    }

    @Override
    public void onClick(View v) {
        if (v == mUseLocationButton) {
            findLocation();
        } else if (v == mCreateHouseButton) {
            String houseName = mHouseName.getText().toString();
            if ((mLatitude != null && mLongitude != null) && houseName.length() > 0){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                DatabaseReference houseRef = FirebaseDatabase
                        .getInstance()
                        .getReference(Constants.FIREBASE_CHILD_HOUSES);
                House house = new House(houseName, mLatitude, mLongitude);
                DatabaseReference housePushRef = houseRef.push();
                String housePushId = housePushRef.getKey();
                house.setHouseId(housePushId);

                DatabaseReference roommateRef = FirebaseDatabase
                        .getInstance()
                        .getReference(Constants.FIREBASE_CHILD_ROOMMATES);
                Roommate roommate = new Roommate(user.getDisplayName(), housePushId, user.getUid());
                DatabaseReference roommatePushRef = roommateRef.push();
                String roommatePushId = roommatePushRef.getKey();
                roommate.setRoommateId(roommatePushId);

                house.addRoommateId(roommate.getRoommateId());

                roommatePushRef.setValue(roommate);
                housePushRef.setValue(house);

                Intent intent = new Intent(NewHouseActivity.this, MainActivity.class);
                startActivity(intent);

            } else{
               Toast.makeText(NewHouseActivity.this, "Please enter name and location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public void findLocation(){
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
        } else {
            mLocationRequest = mLocationRequest.create();
            mLocationRequest.setPriority(mLocationRequest.PRIORITY_HIGH_ACCURACY);
            mFusedLocationProviderApi = LocationServices.FusedLocationApi;
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }
        if (locationUpdated == false){
            if (mSharedPreferences.getString(Constants.PREFERENCES_LATITUDE_KEY, null) !=null && mSharedPreferences.getString(Constants.PREFERENCES_LONGITUDE_KEY, null)!= null){
//                TODO this oughtta be a callback!
                mLatitude = mSharedPreferences.getString(Constants.PREFERENCES_LATITUDE_KEY, null);
                mLongitude = mSharedPreferences.getString(Constants.PREFERENCES_LONGITUDE_KEY, null);
                mLatitudeTextView.setText("Latitude: " + mLatitude);
                mLongitudeTextView.setText("Longitude: " + mLongitude);
            }
        }
    }

    public void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(NewHouseActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewHouseActivity.this, permission)) {
                ActivityCompat.requestPermissions(NewHouseActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(NewHouseActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(NewHouseActivity.this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
        findLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = String.valueOf(location.getLatitude());
        mLongitude = String.valueOf(location.getLongitude());
        mEditor.putString(Constants.PREFERENCES_LATITUDE_KEY, mLatitude).apply();
        mEditor.putString(Constants.PREFERENCES_LONGITUDE_KEY, mLongitude).apply();
        mLatitudeTextView.setText("Latitude: " + mLatitude);
        mLongitudeTextView.setText("Longitude: " + mLongitude);
        locationUpdated = true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i){

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }
}
