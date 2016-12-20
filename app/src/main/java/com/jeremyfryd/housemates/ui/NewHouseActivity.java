package com.jeremyfryd.housemates.ui;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.jeremyfryd.housemates.Manifest;
import com.jeremyfryd.housemates.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewHouseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, com.google.android.gms.location.LocationListener{
    @Bind(R.id.useLocationButton) Button mUseLocationButton;
    @Bind(R.id.createHouseButton) Button mCreateHouseButton;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private PendingResult<LocationSettingsResult> result;
    static final Integer LOCATION = 1;
    private FusedLocationProviderApi mFusedLocationProviderApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_house);
        ButterKnife.bind(this);
        mUseLocationButton.setOnClickListener(this);
        mCreateHouseButton.setOnClickListener(this);

//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
    }

    @Override
    public void onClick(View v) {
        if (v == mUseLocationButton) {
            findLocation();
        } else if (v == mCreateHouseButton) {
            Log.d("useLocationButton", String.valueOf(mUseLocationButton.getId()));
            Log.d("view number", String.valueOf(v.getId()));

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
    }

    public void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(NewHouseActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewHouseActivity.this, permission)) {
                ActivityCompat.requestPermissions(NewHouseActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(NewHouseActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
        findLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "location :"+location.getLatitude()+" , "+location.getLongitude(), Toast.LENGTH_SHORT).show();
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
