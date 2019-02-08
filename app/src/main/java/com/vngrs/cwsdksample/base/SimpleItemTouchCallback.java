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

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleItemTouchCallback extends ItemTouchHelper.SimpleCallback {

  private final Callback callback;

  public SimpleItemTouchCallback(Callback callback) {
    super(0, ItemTouchHelper.END);
    this.callback = callback;
  }

  @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    return false;
  }

  @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    callback.whenPositionSwiped(viewHolder.getAdapterPosition());
  }


  public interface Callback {
    void whenPositionSwiped(int position);
  }
}