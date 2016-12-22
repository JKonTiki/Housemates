package com.jeremyfryd.housemates.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.jeremyfryd.housemates.models.House;
import com.jeremyfryd.housemates.models.Roommate;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.userLogo) ImageView mUserLogo;
    @Bind(R.id.addNewPlus) ImageView mAddNewPlusIcon;
    @Bind(R.id.addNewHouse) ImageView mAddNewHouseIcon;
    @Bind(R.id.joinArrow) ImageView mJoinArrowIcon;
    @Bind(R.id.joinHouse) ImageView mJoinHouseIcon;
    private ChildEventListener mChildEventListener;
    private ArrayList<House> mHouses= new ArrayList<House>();
    private ArrayList<Roommate> mRoommates= new ArrayList<Roommate>();
    private DatabaseReference databaseRef;
    private ChildEventListener roommatesListener;
    private ChildEventListener housesListener;
    private Roommate mRoommate;
    private FirebaseUser mUser;
    private String mUserId;
    private House mHouse;
    private String mSingleHouseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mUserLogo.setOnClickListener(this);
        mAddNewPlusIcon.setOnClickListener(this);
        mAddNewHouseIcon.setOnClickListener(this);
        mJoinArrowIcon.setOnClickListener(this);
        mJoinHouseIcon.setOnClickListener(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mUser.getUid();

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
                        mSingleHouseId = mRoommate.getHouseIds().get(0);

                        DatabaseReference houseRef = FirebaseDatabase
                                .getInstance()
                                .getReference(Constants.FIREBASE_CHILD_HOUSES)
                                .child(mSingleHouseId);

                        houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot houseSnapshot) {
                                if (houseSnapshot.exists()){
                                    mHouse = houseSnapshot.getValue(House.class);
//                                TODO frontend - populate views with house Info
                                    Log.d("sucess house retrieve: ", mHouse.getName());
                                } else{
//                    TODO frontend - set text 'no houses yet'
                                    Log.d("test house retrieval: ", "fail, no houses from listener");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    } else{
                        Log.d("test house retrieval: ", "fail, no houses for roommate");
                    }
                } else{
                    mRoommate = new Roommate(mUser.getDisplayName(), mUser.getUid());
//                    TODO frontend - set text 'no houses yet'
                    DatabaseReference roommatePushRef = roommateRef.child(mRoommate.getRoommateId());
                    roommatePushRef.setValue(mRoommate);
                    Log.d("test house retrieval: ", "profile just generated");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });






        databaseRef = FirebaseDatabase
                .getInstance()
                .getReference();
        housesListener = databaseRef.child(Constants.FIREBASE_CHILD_HOUSES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mHouses.add(dataSnapshot.getValue(House.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        roommatesListener = databaseRef.child(Constants.FIREBASE_CHILD_ROOMMATES).child(mUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {





            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRef.removeEventListener(housesListener);
        databaseRef.removeEventListener(roommatesListener);
    }
}
