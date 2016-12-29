package com.project.authenitcation;

import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pro100svitlo.fingerprintAuthHelper.FahErrorType;
import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity implements FahListener {
    private FingerprintAuthHelper mFAH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFAH = new FingerprintAuthHelper
                .Builder(this, this) //(Context inscance of Activity, FahListener)
                .build();

        if (mFAH.isHardwareEnable()){
            //do some stuff here
            Log.e("tag","hardware enabled");
        } else {
            //otherwise do
            Log.e("tag","hardware disabled");

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mFAH.startListening();
        Log.e("tag","fp start listening");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFAH.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFAH.stopListening();
    }

    @Override
    public void onFingerprintStatus(boolean authSuccessful, int i, @NotNull CharSequence charSequence) {
        if (authSuccessful){
            // do some stuff here in case auth was successful
            Log.e("tag","fp success");
        } else if (mFAH != null){
            // do some stuff here in case auth failed
            Log.e("tag","fp failed");

            switch (i){
                case FahErrorType.General.LOCK_SCREEN_DISABLED:
                case FahErrorType.General.NO_FINGERPRINTS:
                    mFAH.showSecuritySettingsDialog();
                    break;
                case FahErrorType.Auth.AUTH_NOT_RECOGNIZED:
                    //do some stuff here
                    break;
                case FahErrorType.Auth.AUTH_TO_MANY_TRIES:
                    //do some stuff here
                    break;
            }
        }
    }

    @Override
    public void onFingerprintListening(boolean b, long l) {

    }


}
