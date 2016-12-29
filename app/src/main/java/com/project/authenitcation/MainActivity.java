package com.project.authenitcation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mattprecious.swirl.SwirlView;
import com.pro100svitlo.fingerprintAuthHelper.FahErrorType;
import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;
import com.project.authenitcation.Model.CheckEmi;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity implements FahListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1 ;
    private FingerprintAuthHelper mFAH;
    Gson gson = new Gson();
    SwirlView swirlView;
    TextView tvInfo;
    CheckEmi model = new CheckEmi();
    String emiString;
    TelephonyManager telephonyManager;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swirlView = (SwirlView) findViewById(R.id.imgF);
        tvInfo = (TextView) findViewById(R.id.txtInfo);
        c =getApplicationContext();
        mFAH = new FingerprintAuthHelper
                .Builder(this, this) //(Context inscance of Activity, FahListener)
                .build();
         telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        tvInfo.setTypeface(face);
        if (mFAH.isHardwareEnable()){
            //do some stuff here
            Log.e("tag","hardware enabled");
        } else {
            //otherwise do
            Log.e("tag","hardware disabled");

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("tag","permision granded for req "+requestCode);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    emiString = telephonyManager.getDeviceId();
                    Log.e("tag","permision granded for emi ="+emiString);

                    Log.e("tag","permision granded for 1");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        ){
            mFAH.startListening();
            swirlView.setState(SwirlView.State.ON);
            emiString = telephonyManager.getDeviceId();

            Log.e("tag","fp start listening");
        }else{
            Log.e("tag","on resume no permision. ");

        }



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
           // Intent reg = new Intent(MainActivity.this,RegisterActivity.class);
           // startActivity(reg);
            if(emiString!=null&&!emiString.equals("")){
                sendRequest(Config.CheckEmiUrl+"?emi="+emiString);
            }
        } else if (mFAH != null){
            // do some stuff here in case auth failed
            Log.e("tag","fp failed");
            swirlView.setState(SwirlView.State.ERROR);
            //swirlView.setState(SwirlView.State.ON);

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

        if (b){
          //  swirlView.setState(SwirlView.State.ON);
            Log.e("TAG","onFingerprintListening   b is true");

        }else{
            Log.e("TAG","onFingerprintListening   b is false");

        }
    }

    public void sendRequest(String url){

        Log.e("TAG","sendRequest url = "+url);

        RequestQueue queue = Volley.newRequestQueue(this);
       // String url ="http://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG","resposne = "+response);
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    model = gson.fromJson(response.toString(),CheckEmi.class);
                        Log.e("Tag","succes response status="+model.getStatus());
                        if(model!=null){
                            if(model.getStatus()==1){
                                String Otp = model.getOtp().toString();
                                Log.e("Tag","status = 1  Otp ="+Otp);
                                Intent otpAct = new Intent(MainActivity.this,OTPActivity.class);
                                //otpAct.addFlags()
                                startActivity(otpAct);
                            }else if(model.getStatus()==2){
                                Intent registerUser = new Intent(MainActivity.this,RegisterActivity.class);
                                startActivity(registerUser);
                            }else if(model.getStatus()==0){
                                Log.e("Tag","status = 0");

                            }
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.e("Tag","Failure response msg="+error.getLocalizedMessage());
                Toast.makeText(c,"Network issue",Toast.LENGTH_LONG).show();

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}
