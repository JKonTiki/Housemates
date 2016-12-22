package com.jeremyfryd.housemates.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UseCodeActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.submitCode) Button mSubmitCodeButton;
    @Bind(R.id.codeInput) TextView mCodeInput;
    private Roommate mRoommate;
    private String mRoommateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_code);
        ButterKnife.bind(this);
        mRoommate = Parcels.unwrap(getIntent().getParcelableExtra("currentRoommate"));
        mRoommateId = mRoommate.getRoommateId();

        mSubmitCodeButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v){
        if (v == mSubmitCodeButton){
            final String inputtedCode = mCodeInput.getText().toString().toUpperCase();
            if (inputtedCode.length()>0){


                DatabaseReference houseRef = FirebaseDatabase
                        .getInstance()
                        .getReference(Constants.FIREBASE_CHILD_HOUSES);
                houseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(inputtedCode)) {
                            DatabaseReference houseRef = FirebaseDatabase
                                    .getInstance()
                                    .getReference(Constants.FIREBASE_CHILD_HOUSES)
                                    .child(inputtedCode);

                            House house = snapshot.child(inputtedCode).getValue(House.class);
                            List<String> houseInhabitants = house.getRoommates();
                            boolean alreadyLivesHere = false;
                            for (String inhabitant: houseInhabitants){
                                if (inhabitant.equals(mRoommateId)){
                                    alreadyLivesHere = true;
                                }
                            }
                            if (alreadyLivesHere){
                                Toast.makeText(UseCodeActivity.this, "You are already listed as a housemate here!", Toast.LENGTH_LONG).show();
                            } else{
                                DatabaseReference roommateRef = FirebaseDatabase
                                        .getInstance()
                                        .getReference(Constants.FIREBASE_CHILD_ROOMMATES);
                                DatabaseReference roommatePushRef = roommateRef.child(mRoommateId);
                                mRoommate.addHouseId(house.getHouseCode());
                                house.addRoommateId(mRoommateId);
                                roommatePushRef.setValue(mRoommate);
                                houseRef.setValue(house);
                                Intent intent = new Intent(UseCodeActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } else{
                            Toast.makeText(UseCodeActivity.this, "This code does not exist, please ensure you've entered it correctly", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            } else{
                Toast.makeText(UseCodeActivity.this, "Please enter a code!", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
