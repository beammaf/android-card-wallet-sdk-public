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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractDialogFragment<P extends PresenterType> extends DialogFragment {

  private P presenter;

  @Nullable @Override
  public View onCreateView(LayoutInflater factory, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return factory.inflate(layoutRes(), container, false);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    presenter = presenter();
    presenter.restoreState(savedInstanceState != null ? savedInstanceState : getArguments());
    presenter.onCreate();
  }

  @Override public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    presenter.storeState(outState);
  }

  @Override public void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override public void onPause() {
    presenter.onPause();
    super.onPause();
  }

  @Override public void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override public void onDestroy() {
    presenter.onDestroy();
    super.onDestroy();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    presenter.activityResult(requestCode, resultCode, data);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    presenter.requestPermissionResult(requestCode, permissions, grantResults);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return presenter.onOptionsItemSelected(item);
  }

  public void startActivityForResult(int requestCode, Intent intent, Bundle options) {
    startActivityForResult(intent, requestCode, options);
  }

  public void showProgress() {
    throw new RuntimeException("You should implement this method without calling super");
  }

  public void hideProgress() {
    throw new RuntimeException("You should implement this method without calling super");
  }

  public void showError(String errorString) {
    final View view = getView();
    if (view != null) {
      Snackbar.make(view, errorString, Snackbar.LENGTH_LONG).show();
    }
  }

  public void showError(String errorString, String actionTextString, final View.OnClickListener callback) {
    final View view = getView();
    if (view != null) {
      final Snackbar snackbar = Snackbar.make(view, errorString, Snackbar.LENGTH_LONG);
      snackbar.setAction(actionTextString, v -> {
        if (callback != null) {
          callback.onClick(v);
        }
        snackbar.dismiss();
      });
      snackbar.show();
    }
  }

  public void setUp() {
    setUp(getView());
  }

  protected abstract void setUp(@Nullable View view);

  public String getStringRes(@StringRes int stringId, Object...args) {
    return getString(stringId, args);
  }

  public Context getContext() {
    return getActivity();
  }

  public boolean isAvailable() {
    return getActivity() != null && isAdded();
  }

  public void onBackPressed() {
    throw new IllegalArgumentException("you can not try to implement it in here since activity will grab action for this.");
  }

  public void finish() {
    throw new IllegalArgumentException("fragment instances does not support finish options");
  }

  @Override public final void dismiss() {
    super.dismiss();//change of state loss
  }

  @Override public final int show(FragmentTransaction transaction, String tag) {
    return transaction.add(this, tag).commit();
  }

  @Override public final void show(FragmentManager manager, String tag) {
    FragmentTransaction trans = manager.beginTransaction();
    show(trans, tag);
  }

  public void showInfo(String info) {
    Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
  }

  /* no-opt */
  public void finishAndSetResult(int resultCode, Intent data) {}

  protected abstract String getClassTag();
  protected abstract boolean isLogEnabled();

  @LayoutRes protected abstract int layoutRes();
  @NonNull protected abstract P presenter();

  protected void log(final String str) {
    log(Log.DEBUG, str);
  }

  protected void log(Throwable error) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    error.printStackTrace(printWriter);
    log(Log.ERROR, stringWriter.toString());
  }

  private void log(final int lv, final String str) {
    if (isLogEnabled()) {
      Log.println(lv, getClassTag(), str);
    }
  }
}