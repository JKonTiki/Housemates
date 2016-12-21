package com.jeremyfryd.housemates.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfryd.housemates.R;

import butterknife.Bind;

public class UseCodeActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.submitCode) Button mSubmitCodeButton;
    @Bind(R.id.codeInput) TextView mCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_code);
        mSubmitCodeButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v){
        if (v == mSubmitCodeButton){
            String inputtedCode = mCodeInput.getText().toString();
            if (inputtedCode.length()>0){





            } else{
                Toast.makeText(UseCodeActivity.this, "Please enter a code!", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
