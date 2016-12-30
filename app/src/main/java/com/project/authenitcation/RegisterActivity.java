package com.project.authenitcation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mattprecious.swirl.SwirlView;
import com.project.authenitcation.Model.CheckEmi;
import com.project.authenitcation.Model.RegModel;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEdt,passEdt;
    Button registerBtn;
    Context c;
    String username="";
    String password ="";
    String emi="";
    RegModel model;
    Gson gson = new Gson();
    MaterialDialog dialogue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        c =getApplicationContext();
        usernameEdt = (EditText) findViewById(R.id.input_username);
        passEdt = (EditText) findViewById(R.id.input_password);
        registerBtn = (Button) findViewById(R.id.btn_register);
         dialogue = new MaterialDialog.Builder(this)
                .title("Please Wait")
                .content("Checking the Fingerprint")
                .theme(Theme.LIGHT)
                .progress(true, 0).build();

        if( ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                ){
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            emi = telephonyManager.getDeviceId();

            Log.e("tag","fp start listening");
        }else{
            Log.e("tag","on resume no permision. ");

        }
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEdt.getText()!=null&!usernameEdt.equals("")&&passEdt.getText()!=null&&!passEdt.equals("")&&!emi.equals("")){
                    username = usernameEdt.getText().toString();
                    password = passEdt.getText().toString();
                    startRequest(Config.RegisterEmi);
                }
            }
        });
    }


    public void startRequest(String url){
        startDialogue(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        // String url ="http://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        startDialogue(false);

                        Log.e("TAG","resposne = "+response);
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                       model = gson.fromJson(response.toString(),RegModel.class);
                       // Log.e("Tag","succes response status="+model.getStatus());
                        if(model!=null){
                            if (model.getStatus()==1){
                                Intent otpAct = new Intent(RegisterActivity.this,OTPActivity.class);
                                otpAct.putExtra("otp", model.getOtp());
                                startActivity(otpAct);
                                finish();
                                Toast.makeText(c,"Fingerprint successfully registered",Toast.LENGTH_LONG).show();
                            }else if(model.getStatus() ==2 ){
                                Toast.makeText(c,"Authentication Failed !",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startDialogue(false);
                //mTextView.setText("That didn't work!");
                Log.e("Tag","Failure response msg="+error.getLocalizedMessage());
                Toast.makeText(c,"Network issue",Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",username);
                params.put("pass",password);
                params.put("emi",emi);
                Log.e("Tag","getParams username , pass , emi "+username+" "+password+"  "+emi);

                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void startDialogue(boolean k){
        if(k){
            dialogue.show();
        }else{
            if(dialogue.isShowing()){
                dialogue.dismiss();
            }
        }
    }



}


