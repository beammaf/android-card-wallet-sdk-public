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

public class TextChangeObservable extends Observable<CharSequence> {

  private final EditText view;

  TextChangeObservable(EditText view) {
    this.view = view;
  }

  @Override protected void subscribeActual(Observer<? super CharSequence> observer) {
    if (CheckUiThread.checkIfNotUIThread(observer)) {
      Listener listener = new Listener(view, observer);
      observer.onSubscribe(listener);
      view.addTextChangedListener(listener);
    }
  }

  static class Listener extends MainThreadDisposable implements TextWatcher {

    private final EditText view;
    private final Observer<? super CharSequence> observer;

    Listener(EditText view, Observer<? super CharSequence> observer) {
      this.view = view;
      this.observer = observer;
    }

    @Override public void afterTextChanged(Editable s) { }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (!isDisposed()) {
        observer.onNext(s);
      }
    }

    @Override protected void onDispose() {
      view.removeTextChangedListener(this);
    }
  }
}