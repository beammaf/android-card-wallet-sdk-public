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

import android.support.annotation.Nullable;

import io.reactivex.CompletableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtility {

  public static <T> SingleTransformer<T, T> toAsync() {
    return source -> source.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  public static <T> SingleTransformer<T, T> toAsyncUI(@Nullable ViewType view) {
    return source -> source.compose(toAsync())
      .doOnSubscribe(d -> {
        if (view != null && view.isAvailable()) {
          view.showProgress();
        }
      }).doOnSuccess(data -> {
        if (view != null && view.isAvailable()) {
          view.hideProgress();
        }
      });
  }

  private static CompletableTransformer toAsyncCompletable() {
    return source -> source.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  public static CompletableTransformer toAsyncComplerableUI(@Nullable ViewType view) {
    return source -> source.compose(toAsyncCompletable())
      .doOnSubscribe(d -> {
        if (view != null && view.isAvailable()) {
          view.showProgress();
        }
      }).doOnComplete(() -> {
        if (view != null && view.isAvailable()) {
          view.hideProgress();
        }
      });
  }
}