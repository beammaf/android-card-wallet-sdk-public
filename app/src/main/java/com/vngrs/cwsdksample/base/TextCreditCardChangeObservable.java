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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;



import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

class TextCreditCardChangeObservable extends Observable<CharSequence> {

  private final EditText view;
  private final char specialChar;
  private final int specialCharPosition;

  public TextCreditCardChangeObservable(EditText view, char specialChar, int specialCharPosition) {
    this.view = view;
    this.specialChar = specialChar;
    this.specialCharPosition = specialCharPosition;
  }

  @Override protected void subscribeActual(Observer<? super CharSequence> observer) {
    if (CheckUiThread.checkIfNotUIThread(observer)) {
      Listener listener = new Listener(view, observer, specialChar, specialCharPosition);
      observer.onSubscribe(listener);
      view.addTextChangedListener(listener);
    }
  }

  static class Listener extends MainThreadDisposable implements TextWatcher {

    private final EditText view;
    private final Observer<? super CharSequence> observer;

    private final EditableMask pasteEditableMask;
    private final EditableMask seamlessEditableMask;

    Listener(EditText view, Observer<? super CharSequence> observer, char specialChar, int specialCharPosition) {
      this.view = view;
      this.observer = observer;
      this.pasteEditableMask = new PasteEditableMask(view, specialChar, specialCharPosition);
      this.seamlessEditableMask = new SeamlessEditableMask(specialChar, specialCharPosition);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (!isDisposed()) {
        pasteEditableMask.checkIfMaskingAvailable(count, s);
        seamlessEditableMask.checkIfMaskingAvailable(count, s);
        observer.onNext(s);
      }
    }

    @Override public void afterTextChanged(Editable s) {
      if (!isDisposed()) {
        if (pasteEditableMask.isAvailable()) {
          pasteEditableMask.mask(s);
        } else if (seamlessEditableMask.isAvailable()) {
          seamlessEditableMask.mask(s);
        }
      }
    }

    @Override protected void onDispose() {
      view.removeTextChangedListener(this);
    }
  }
}