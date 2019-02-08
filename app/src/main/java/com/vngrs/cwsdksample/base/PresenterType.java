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
import android.view.MenuItem;

public interface PresenterType {

  void onResume();

  void onPause();

  void onStart();

  void onStop();

  void onCreate();

  void onDestroy();

  void onBackPressed();

  void restoreState(Bundle restoreState);

  void storeState(Bundle storeState);

  void activityResult(int requestCode, int resultCode, Intent data);

  void requestPermissionResult(int requestCode, String[] permissions, int[] grants);

  boolean onOptionsItemSelected(MenuItem item);
}