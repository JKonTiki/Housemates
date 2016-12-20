package com.jeremyfryd.housemates.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jeremyfryd.housemates.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewHouseActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.useLocationButton) Button mUseLocationButton;
    @Bind(R.id.createHouseButton) Button mCreateHouseButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_house);
        ButterKnife.bind(this);
        mUseLocationButton.setOnClickListener(this);
        mCreateHouseButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v){
        if (v == mUseLocationButton){



        } else if (v== mCreateHouseButton){

        }
    }
}
