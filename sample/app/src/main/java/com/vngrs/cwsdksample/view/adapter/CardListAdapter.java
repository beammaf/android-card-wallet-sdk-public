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
package com.vngrs.cwsdksample.view.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.beamuae.cwsdk.CreditCard;
import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.base.AbstractRecyclerAdapter;
import com.vngrs.cwsdksample.base.ObservableList;
import com.vngrs.cwsdksample.view.holder.CardListViewHolder;

public class CardListAdapter extends AbstractRecyclerAdapter<CreditCard, CardListViewHolder> {

  public CardListAdapter(@NonNull ObservableList<CreditCard> dataSet) {
    super(dataSet);
  }

  @Override public CardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new CardListViewHolder(parent);
  }

  @Override protected String getClassTag() {
    return CardListAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }
}