package com.vngrs.cwsdksample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.beamuae.cwsdk.shared.CWServer;
import com.vngrs.cwsdksample.view.MainActivity;

import static com.vngrs.cwsdksample.SelectServerActivity.SERVER_NAME;

@SuppressLint("ApplySharedPref")
public class TokenActivity extends AppCompatActivity {


    private TextInputLayout lytTextInput;
    private TextInputLayout lytPartnerId;
    private Button btnGo;
    private TextInputEditText etPartner;
    private TextInputEditText etToken;
    private CWServer mCwServer;


    private void findViews() {
        lytTextInput = findViewById(R.id.lytTextInput);
        lytPartnerId = findViewById(R.id.lytPartnerId);
        etPartner = findViewById(R.id.etPartnerId);
        etToken = findViewById(R.id.etToken);
        btnGo = findViewById(R.id.btnGo);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        getExtraData();
        findViews();
        initViews();
        btnGo.setOnClickListener(v -> {
            putStringToPref("TOKEN", etToken.getText().toString());
            putStringToPref("PARTNER", etPartner.getText().toString());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(SERVER_NAME, mCwServer);
            startActivity(intent);
        });
    }

    private void getExtraData() {
        Intent intent = getIntent();
        if (intent != null) {
            mCwServer = (CWServer) intent.getExtras().getSerializable("SERVER_NAME");
            if (BuildConfig.DEBUG) {
                assert mCwServer != null;
                Log.d("ServerName:", mCwServer.toString());
            }
        }
    }

    private void initViews() {
        etPartner.setText(getStringFromPref("PARTNER"));
        etToken.setText(getStringFromPref("TOKEN"));
    }


    void putStringToPref(String key, String val) {
        SharedPreferences preferences = getSharedPreferences("CWTEST", Context.MODE_PRIVATE);
        preferences.edit().putString(key, val).commit();
    }

    String getStringFromPref(String key) {
        return getSharedPreferences("CWTEST", Context.MODE_PRIVATE).getString(key, "");
    }
}
