package com.project.authenitcation;

import android.os.CountDownTimer;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class OTPActivity extends AppCompatActivity {

    TextView tvOtp,sec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        tvOtp = (TextView) findViewById(R.id.otptv);
        sec = (TextView) findViewById(R.id.tvsec);

        String otp = getIntent().getStringExtra("otp");
        if(otp!=null){
            tvOtp.setText(otp);
        }
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                sec.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                sec.setText("OTP expired !");
            }
        }.start();

    }
}
