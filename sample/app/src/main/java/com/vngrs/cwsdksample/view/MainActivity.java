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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.R;
import com.vngrs.cwsdksample.SelectServerActivity;
import com.vngrs.cwsdksample.base.AbstractActivity;
import com.vngrs.cwsdksample.base.ObservableList;
import com.vngrs.cwsdksample.model.Category;
import com.vngrs.cwsdksample.presenter.MainActivityPresenter;
import com.vngrs.cwsdksample.presenter.MainActivityPresenterImp;
import com.vngrs.cwsdksample.view.adapter.CategoryAdapter;

public class MainActivity extends AbstractActivity<MainActivityPresenter> implements MainActivityView {


  private ProgressBar viewProgress;
  private final ObservableList<Category> dataSet = new ObservableList<>();

  @Override public void setUp() {
    Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_seperator, getTheme());
    DividerItemDecoration div = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
    if (divider != null) {
      div.setDrawable(divider);
    }
    RecyclerView viewRecycler = findViewById(R.id.viewRecycler);
    viewRecycler.setItemViewCacheSize(10);
    viewRecycler.setHasFixedSize(true);
    viewRecycler.addItemDecoration(div);
    viewRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    viewRecycler.setAdapter(new CategoryAdapter(dataSet));

    Toolbar viewToolbar = findViewById(R.id.viewToolbar);
    viewToolbar.setTitle(R.string.app_name);

    viewProgress = findViewById(R.id.viewProgress);

    hideProgress();
  }

  @Override public int layoutRes() {
    return R.layout.view_main_activity;
  }

  @NonNull @Override public MainActivityPresenter presenter() {
    return new MainActivityPresenterImp(this, dataSet);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(this, SelectServerActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void showError(String message) {
    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
    snackbar.setActionTextColor(Color.RED);
    snackbar.setAction(android.R.string.ok, v -> snackbar.dismiss());
    snackbar.show();
  }

  @Override public void showProgress() {
    viewProgress.setVisibility(View.VISIBLE);
    viewProgress.setIndeterminate(true);
  }

  @Override public void hideProgress() {
    viewProgress.setIndeterminate(false);
    viewProgress.setVisibility(View.GONE);
  }

  @Override public Activity getActivity() {
    return this;
  }

  @Override public boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override public String getClassTag() {
    return MainActivity.class.getSimpleName();
  }
}