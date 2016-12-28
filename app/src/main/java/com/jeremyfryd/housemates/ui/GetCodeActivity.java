package com.jeremyfryd.housemates.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeremyfryd.housemates.R;
import com.jeremyfryd.housemates.models.House;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GetCodeActivity extends AppCompatActivity {
    @Bind(R.id.codeDisplay) TextView mCodeDisplay;
    private House mHouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_code);
        ButterKnife.bind(this);
        mHouse = Parcels.unwrap(getIntent().getParcelableExtra("currentHouse"));
        mCodeDisplay.setVisibility(View.VISIBLE);
        mCodeDisplay.setText(mHouse.getHouseCode());
    }
}
