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

import android.widget.EditText;


import io.reactivex.Observable;

public class RxTextView {

  private final static char KEY_SPACE = ' ';
  private final static int POSITION_SPACE = 5;

  private final static char KEY_DELIMETER = '/';
  private final static int POSITION_DELIMETER = 3;

  public static Observable<CharSequence> textChanges(EditText view) {
    return new TextChangeObservable(view);
  }

  public static Observable<CharSequence> textChangesForCreditCardNumber(EditText view) {
    return textChangesForCreditCard(view, KEY_SPACE, POSITION_SPACE);
  }

  public static Observable<CharSequence> textChangesForCreditCardExpire(EditText view) {
    return textChangesForCreditCard(view, KEY_DELIMETER, POSITION_DELIMETER);
  }

  private static Observable<CharSequence> textChangesForCreditCard(EditText view, char specialChar, int specialCharPosition) {
    return new TextCreditCardChangeObservable(view, specialChar, specialCharPosition);
  }

  private RxTextView() {
    throw new RuntimeException("You can not have instance of this object");
  }
}