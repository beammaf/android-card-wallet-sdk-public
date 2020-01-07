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
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.beamuae.cwsdk.CreditCard;
import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.R;
import com.vngrs.cwsdksample.SelectServerActivity;
import com.vngrs.cwsdksample.base.AbstractActivity;
import com.vngrs.cwsdksample.base.ObservableList;
import com.vngrs.cwsdksample.base.RxToolbar;
import com.vngrs.cwsdksample.base.RxView;
import com.vngrs.cwsdksample.base.SimpleItemTouchCallback;
import com.vngrs.cwsdksample.presenter.CardListActivityPresenter;
import com.vngrs.cwsdksample.presenter.CardListActivityPresenterImp;
import com.vngrs.cwsdksample.view.adapter.CardListAdapter;

import io.reactivex.Observable;

public class CardListActivity extends AbstractActivity<CardListActivityPresenter> implements CardListActivityView {

  private final ObservableList<CreditCard> dataSet = new ObservableList<>();

  private Toolbar viewToolbar;
  private ProgressBar viewProgress;
  private FloatingActionButton viewButtonAddCard;

  @Override public int layoutRes() {
    return R.layout.view_card_list_activity;
  }

  @Override public void setUp() {
    Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_seperator, getTheme());
    DividerItemDecoration div = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
    if (divider != null) {
      div.setDrawable(divider);
    }
    // load views
    viewProgress = findViewById(R.id.viewProgress);
    viewToolbar = findViewById(R.id.viewToolbar);
    viewButtonAddCard = findViewById(R.id.viewButtonAddCard);
    RecyclerView viewRecycler = findViewById(R.id.viewRecycler);
    viewRecycler.setHasFixedSize(true);
    viewRecycler.setItemViewCacheSize(10);
    viewRecycler.setAdapter(new CardListAdapter(dataSet));
    viewRecycler.addItemDecoration(div);
    viewRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    ItemTouchHelper.SimpleCallback callback = new SimpleItemTouchCallback(presenter().swipeListener());
    ItemTouchHelper helper = new ItemTouchHelper(callback);
    helper.attachToRecyclerView(viewRecycler);
    // initial progress state
    hideProgress();
  }

  @Override public void showProgress() {
    viewProgress.setVisibility(View.VISIBLE);
    viewProgress.setIndeterminate(true);
  }

  @Override public void hideProgress() {
    viewProgress.setIndeterminate(false);
    viewProgress.setVisibility(View.GONE);
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

  @NonNull @Override public CardListActivityPresenter presenter() {
    return new CardListActivityPresenterImp(this, dataSet);
  }

  @Override public boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override public String getClassTag() {
    return CardListActivity.class.getSimpleName();
  }

  @Override public Activity getActivity() {
    return this;
  }

  @Override public Observable<View> observeNavigationClick() {
    return RxToolbar.clicks(viewToolbar);
  }

  @Override public Observable<View> observeAddCardClick() {
    return RxView.clicks(viewButtonAddCard);
  }
}