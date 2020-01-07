package com.vngrs.cwsdksample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.beamuae.cwsdk.shared.CWServer;

public class SelectServerActivity extends AppCompatActivity implements View.OnClickListener {

    private CWServer mCwServer;
    public static final String SERVER_NAME = "SERVER_NAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_branch);
        findViewById(R.id.btn_stg).setOnClickListener(this);
        findViewById(R.id.btn_uat).setOnClickListener(this);
        findViewById(R.id.btn_sandbox).setOnClickListener(this);
        findViewById(R.id.btn_pro).setOnClickListener(this);
        findViewById(R.id.btn_lt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_uat:
                mCwServer = CWServer.UAT;
                startMainActivity();
                break;
            case R.id.btn_stg:
                mCwServer = CWServer.STAGING;
                startMainActivity();
                break;
            case R.id.btn_sandbox:
                mCwServer = CWServer.SANDBOX;
                startMainActivity();
                break;
            case R.id.btn_pro:
                mCwServer = CWServer.PRODUCTION;
                startMainActivity();
                break;
            case R.id.btn_lt:
                mCwServer = CWServer.LT;
                startMainActivity();
                break;
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, TokenActivity.class);
        intent.putExtra(SERVER_NAME, mCwServer);
        startActivity(intent);
    }
}
