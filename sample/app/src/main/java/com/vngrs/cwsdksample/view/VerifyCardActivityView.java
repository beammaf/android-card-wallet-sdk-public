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
package com.vngrs.cwsdksample.view;

import android.view.View;
import com.vngrs.cwsdksample.base.ViewType;
import io.reactivex.Observable;

public interface VerifyCardActivityView extends ViewType {

  void bindToolbarTitle(String title);
  void showInputError(String error);
  void hideInputError();
  Observable<CharSequence> observeAmountChange();
  Observable<View> observeChargeClick();
  Observable<View> observeNavigationClick();
}