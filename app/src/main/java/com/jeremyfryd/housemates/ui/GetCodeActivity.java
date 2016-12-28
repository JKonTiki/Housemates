package com.jeremyfryd.housemates.ui;

import android.content.Intent;
import android.net.Uri;
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

public class GetCodeActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.codeDisplay) TextView mCodeDisplay;
    @Bind(R.id.sendCodeTextMessage) Button mSendCodeTextMessage;
    private House mHouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_code);
        ButterKnife.bind(this);
        mHouse = Parcels.unwrap(getIntent().getParcelableExtra("currentHouse"));
        mCodeDisplay.setVisibility(View.VISIBLE);
        mCodeDisplay.setText(mHouse.getHouseCode());

        mSendCodeTextMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v == mSendCodeTextMessage){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("sms:"));
            intent.putExtra("sms_body", "Here's the code for our house on Housemates: " + mHouse.getHouseCode());
            startActivity(intent);
        }

    }
}
