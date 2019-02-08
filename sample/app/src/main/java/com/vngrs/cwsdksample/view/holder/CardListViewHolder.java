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

import com.beamuae.cwsdk.CreditCard;
import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.R;
import com.vngrs.cwsdksample.base.AbstractRecyclerViewHolder;
import com.vngrs.cwsdksample.base.BusManager;
import com.vngrs.cwsdksample.base.RxView;
import com.vngrs.cwsdksample.model.event.SelectedCardEvent;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CardListViewHolder extends AbstractRecyclerViewHolder<CreditCard> {

    private TextView txtCardNumber;
    private TextView txtVerify;
    private final CompositeDisposable disposeBag = new CompositeDisposable();


    private CardListViewHolder(View view) {
        super(view);
        txtCardNumber = view.findViewById(R.id.txtCardNumber);
        txtVerify = view.findViewById(R.id.txtVerify);
    }

    public CardListViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_card_list_item, parent, false));
    }

    @Override
    public void bind(CreditCard entity) {
        txtCardNumber.setText(entity.getCardNumber());

        if (entity.isRequiresVerification()) {
            txtVerify.setVisibility(View.VISIBLE);
            Disposable cardSelectEventObserve = RxView.clicks(itemView)
                    .map(view -> new SelectedCardEvent(entity))
                    .subscribe(BusManager::send);

            disposeBag.add(cardSelectEventObserve);
        } else {
            txtVerify.setVisibility(View.GONE);
        }
    }

    @Override
    public void unbind() {
        disposeBag.clear();
    }

    @Override
    protected String getClassTag() {
        return CardListViewHolder.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return BuildConfig.DEBUG;
    }
}