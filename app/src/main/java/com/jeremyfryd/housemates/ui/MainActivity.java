package com.jeremyfryd.housemates.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.userLogo) ImageView mUserLogo;
    @Bind(R.id.addNewPlus) ImageView mAddNewPlusIcon;
    @Bind(R.id.addNewHouse) ImageView mAddNewHouseIcon;
    @Bind(R.id.joinArrow) ImageView mJoinArrowIcon;
    @Bind(R.id.joinHouse) ImageView mJoinHouseIcon;
    @Bind(R.id.noHousesMessage) TextView mNoHousesTextView;
    @Bind(R.id.houseName) TextView mHouseName;
    private SharedPreferences mSharedPreferences;
    private Roommate mRoommate;
    private FirebaseUser mUser;
    private String mUsername;
    private String mUserId;
    private House mHouse;
    private String mActiveHouseId;
    private List<String> mActiveHouseInhabitantIds;
    private List<Roommate> mActiveHouseInhabitants = new ArrayList<Roommate>();

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
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mUser.getUid();
        mUsername = mSharedPreferences.getString(Constants.PREFERENCES_USERNAME_KEY, null);
        Log.d("username", mUsername);

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
                        Log.d("this roommate", mRoommate.getAtHome());
                        DatabaseReference houseRef = FirebaseDatabase
                                .getInstance()
                                .getReference(Constants.FIREBASE_CHILD_HOUSES)
                                .child(mActiveHouseId);

                        houseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot houseSnapshot) {
                                if (houseSnapshot.exists()){
                                    mHouse = houseSnapshot.getValue(House.class);
                                    mHouseName.setText(mHouse.getName()+ ":");
                                    mActiveHouseInhabitantIds = mHouse.getRoommates();
                                    for (int i=0; i< mActiveHouseInhabitantIds.size(); i++){
                                        final int currentIteration = i;
                                        roommateRef.child(mActiveHouseInhabitantIds.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot inhabitantSnapshot) {
                                                if (inhabitantSnapshot.exists()){
                                                    Roommate activeHouseInhabitant = inhabitantSnapshot.getValue(Roommate.class);
                                                    Log.d("activeinhabitant", activeHouseInhabitant.getAtHome());
                                                    mActiveHouseInhabitants.add(activeHouseInhabitant);
                                                }
                                                if (currentIteration == mActiveHouseInhabitantIds.size()-1){



//                                                    TODO geofencing listener here



                                                    InhabitantListAdapter adapter = new InhabitantListAdapter(MainActivity.this, mActiveHouseInhabitants);
                                                    Log.d("mainactivity", String.valueOf(currentIteration));
                                                    ListView listView = (ListView) findViewById(R.id.roommatesList);
                                                    listView.setAdapter(adapter);
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
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}