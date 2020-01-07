/*
 * TPS SDK Android Copyright (C) 2018 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vngrs.cwsdksample.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractActivity<P extends PresenterType> extends AppCompatActivity {

  private P presenter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layoutRes());

    presenter = presenter();
    presenter.restoreState(savedInstanceState != null ? savedInstanceState : getIntent().getExtras());
    presenter.onCreate();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override public void onPause() {
    presenter.onPause();
    super.onPause();
  }

  @Override public void onStop() {
    presenter.onStop();
    super.onStop();
  }

  public void showProgress() { }
  public void hideProgress() { }
  public void setUp() {}

  public void finishAndSetResult(int resultCode, Intent data) {
    if (data != null) {
      setResult(resultCode, data);
    } else {
      setResult(resultCode);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      finishAfterTransition();
    } else {
      finish();
    }
  }

  public Context getContext() {
    return this;
  }

  public String getStringRes(@StringRes int stringRes, Object... args) {
    if (args != null) {
      return getString(stringRes, args);
    } else {
      return getString(stringRes);
    }
  }

  public boolean isAvailable() { return !isFinishing(); }

  public void showError(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG)
      .show();
  }

  public void showInfo(String message) {
    showError(message);
  }

  @Override public void onBackPressed() {
    presenter.onBackPressed();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    presenter.activityResult(requestCode, resultCode, data);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    presenter.requestPermissionResult(requestCode, permissions, grantResults);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return presenter.onOptionsItemSelected(item);
  }

  public void startActivityForResult(int requestCode, Intent intent, @Nullable Bundle options) {
    ActivityCompat.startActivityForResult(this, intent, requestCode, options);
  }

  @LayoutRes public abstract int layoutRes();
  @NonNull public abstract P presenter();

  public abstract boolean isLogEnabled();
  public abstract String getClassTag();

  public void log(String message) {
    log(Log.DEBUG, message);
  }

  private void log(int level, String message) {
    if (isLogEnabled()) {
      Log.println(level, getClassTag(), message);
    }
  }

  public void log(Throwable error) {
    StringWriter sw = new StringWriter(128);
    PrintWriter pw = new PrintWriter(sw);
    error.printStackTrace(pw);
    log(Log.ERROR, sw.toString());
  }
}