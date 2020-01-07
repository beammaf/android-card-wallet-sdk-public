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
package com.vngrs.cwsdksample.view.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.R;
import com.vngrs.cwsdksample.base.AbstractRecyclerViewHolder;
import com.vngrs.cwsdksample.base.BusManager;
import com.vngrs.cwsdksample.base.RxView;
import com.vngrs.cwsdksample.model.Category;
import com.vngrs.cwsdksample.model.event.SelectedCategoryEvent;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CategoryViewHolder extends AbstractRecyclerViewHolder<Category> {

  private final CompositeDisposable disposeBag = new CompositeDisposable();

  private TextView viewTextCategoryName;

  private CategoryViewHolder(View view) {
    super(view);
    viewTextCategoryName = view.findViewById(R.id.viewTextCategoryName);
  }

  public CategoryViewHolder(ViewGroup parent) {
    this(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_category_item, parent, false));
  }

  @Override public void bind(Category category) {
    viewTextCategoryName.setText(category.getCategoryName());
    // observe selection of category
    Disposable selectedCategoryEventDisposable = bindSelectedCategoryEvent(category).subscribe(BusManager::send);
    // add bag
    disposeBag.add(selectedCategoryEventDisposable);
  }

  @Override public void unbind() {
    disposeBag.clear();
  }

  @Override protected String getClassTag() {
    return CategoryViewHolder.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  private Observable<SelectedCategoryEvent> bindSelectedCategoryEvent(Category category) {
    return RxView.clicks(itemView).map(view -> new SelectedCategoryEvent(category));
  }
}