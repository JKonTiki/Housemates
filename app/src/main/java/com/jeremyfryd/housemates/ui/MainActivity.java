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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.jeremyfryd.housemates.R;
import com.jeremyfryd.housemates.models.House;
import com.jeremyfryd.housemates.models.Roommate;

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
//        mChildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                otherCodes.add(dataSnapshot.getValue(House.class).getCode());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
    }

    @Override
    public void onClick(View v){
        if (v == mUserLogo){
            logout();
        } else if(v == mAddNewHouseIcon || v == mAddNewPlusIcon){
            Intent intent = new Intent(MainActivity.this, NewHouseActivity.class);
            startActivity(intent);
        } else if(v == mJoinArrowIcon || v == mJoinHouseIcon){
            Intent intent = new Intent(MainActivity.this, UseCodeActivity.class);
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
