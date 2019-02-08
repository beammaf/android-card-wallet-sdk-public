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
package com.vngrs.cwsdksample;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


public class SampleApplication extends Application {

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  /*
  @Override public void onCreate() {
    super.onCreate();
    initBeamSDK();
  }

  private void initBeamSDK() {
    FSSdk.initialize(
        new BMSdkBuilder(this).setApiKey("VFJBUEkxY2I2ZGZhMmI3YWY0ODQ6UGFzc3dvckQhISExMjM=")
            .setServer(FSServer.STAGING));
    FSSdk.getInstance().start(new FSInitializationListener() {
      @Override public void onSdkInitialized() {

      }

      @Override public void onError(BeamError error) {
      }
    });
  }*/
}
