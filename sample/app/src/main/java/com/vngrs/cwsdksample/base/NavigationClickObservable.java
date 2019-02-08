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

import android.support.v7.widget.Toolbar;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

final class NavigationClickObservable extends Observable<View> {

  private final Toolbar view;

  NavigationClickObservable(Toolbar view) {
    this.view = view;
  }

  @Override protected void subscribeActual(Observer<? super View> observer) {
    if (CheckUiThread.checkIfNotUIThread(observer)) {
      Listener listener = new Listener(view, observer);
      observer.onSubscribe(listener);
      view.setNavigationOnClickListener(listener);
    }
  }

  static class Listener extends MainThreadDisposable implements Toolbar.OnClickListener {

    private final Toolbar view;
    private final Observer<? super View> observer;

    Listener(Toolbar view, Observer<? super View> observer) {
      this.view = view;
      this.observer = observer;
    }

    @Override public void onClick(View view) {
      if (!isDisposed()) {
        observer.onNext(view);
      }
    }

    @Override protected void onDispose() {
      view.setNavigationOnClickListener(null);
    }
  }
}