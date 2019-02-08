package com.vngrs.cwsdksample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vngrs.cwsdksample.view.MainActivity;

@SuppressLint("ApplySharedPref")
public class TokenActivity extends AppCompatActivity {


    private TextInputLayout lytTextInput;
    private TextInputLayout lytPartnerId;
    private Button btnGo;
    private TextInputEditText etPartner;
    private TextInputEditText etToken;


    private void findViews() {
        lytTextInput = (TextInputLayout)findViewById( R.id.lytTextInput );
        lytPartnerId = (TextInputLayout)findViewById( R.id.lytPartnerId );
        etPartner = findViewById(R.id.etPartnerId);
        etToken = findViewById(R.id.etToken);
        btnGo = (Button)findViewById( R.id.btnGo );
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        findViews();
        initViews();
        btnGo.setOnClickListener(v -> {
            putStringToPref("TOKEN",etToken.getText().toString());
            putStringToPref("PARTNER",etPartner.getText().toString());
            startActivity(new Intent(this,MainActivity.class));
        });
    }

    private void initViews() {
        etPartner.setText(getStringFromPref("PARTNER"));
        etToken.setText(getStringFromPref("TOKEN"));
    }


    void putStringToPref(String key, String val){
        SharedPreferences preferences = getSharedPreferences("CWTEST",Context.MODE_PRIVATE);
        preferences.edit().putString(key,val).commit();
    }

    String getStringFromPref(String key){
        return getSharedPreferences("CWTEST",Context.MODE_PRIVATE).getString(key,"");
    }
}
