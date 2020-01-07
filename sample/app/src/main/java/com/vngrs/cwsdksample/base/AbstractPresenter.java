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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractPresenter<V extends ViewType> {

  protected final V view;

  protected AbstractPresenter(@NonNull V view) {
    this.view = view;
  }

  public void onResume() {}
  public void onPause() {}
  public void onStart() {}
  public void onStop() {}
  public void onCreate() {}
  public void onDestroy() {}
  public void onBackPressed() {}
  public void restoreState(Bundle restoreState) {}
  public void storeState(Bundle storeState) {}
  public void activityResult(int requestCode, int resultCode, Intent data) {}
  public void requestPermissionResult(int requestCode, String[] permissions, int[] grants) {}
  public boolean onOptionsItemSelected(MenuItem item) { return false; }

  protected abstract boolean isLogEnabled();
  protected abstract String getClassTag();

  protected void log(String message) {
    log(Log.DEBUG, message);
  }

  private void log(int level, String message) {
    if (isLogEnabled()) {
      Log.println(level, getClassTag(), message);
    }
  }

  protected void log(Throwable error) {
    StringWriter sw = new StringWriter(128);
    PrintWriter pw = new PrintWriter(sw);
    error.printStackTrace(pw);
    log(Log.ERROR, sw.toString());
  }
}