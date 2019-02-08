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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface ViewType {

  void setUp();

  void hideProgress();
  void showProgress();

  void showError(String error);
  void showInfo(String message);

  void startActivityForResult(int requestCode, Intent intent, @Nullable Bundle options);
  void startActivity(Intent intent, @Nullable Bundle options);

  void finish();
  void finishAndSetResult(int resultCode, @Nullable Intent data);

  void requestPermissions(String[] permissions, int requestCode);

  Context getContext();

  String getStringRes(@StringRes int stringRes, Object... args);

  boolean isAvailable();
}