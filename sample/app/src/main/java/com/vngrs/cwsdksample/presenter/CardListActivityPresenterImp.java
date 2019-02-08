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
package com.vngrs.cwsdksample.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.beamuae.cwsdk.CWSdk;
import com.beamuae.cwsdk.shared.CWCallback;
import com.beamuae.cwsdk.shared.CWError;
import com.beamuae.cwsdk.CreditCard;
import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.base.AbstractPresenter;
import com.vngrs.cwsdksample.base.BusManager;
import com.vngrs.cwsdksample.base.ObservableList;
import com.vngrs.cwsdksample.base.SimpleItemTouchCallback;
import com.vngrs.cwsdksample.model.event.SelectedCardEvent;
import com.vngrs.cwsdksample.view.CardListActivityView;
import com.vngrs.cwsdksample.view.VerifyCardActivity;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.List;

public class CardListActivityPresenterImp extends AbstractPresenter<CardListActivityView>
    implements CardListActivityPresenter {

    private static final int VERIFY_CARD_REQUEST_CODE = 55;
    private static final int ADD_CARD_REQUEST_CODE = 56;
    private final CompositeDisposable disposeBag = new CompositeDisposable();
    private final ObservableList<CreditCard> dataSet;
    private final CWSdk sdk;

    public CardListActivityPresenterImp(@NonNull CardListActivityView view,
        ObservableList<CreditCard> dataSet) {
        super(view);
        this.dataSet = dataSet;
        this.sdk = CWSdk.getInstance();
    }

    @Override public void onCreate() {
        if (view.isAvailable()) {
            view.setUp();
            loadCards();
        }
    }

    @Override public void onStart() {
        if (view.isAvailable()) {
            Disposable observeBusManager = BusManager.add(evt -> {
                if (evt instanceof SelectedCardEvent) {
                    SelectedCardEvent event = (SelectedCardEvent) evt;
                    Intent intent = new Intent(view.getContext(), VerifyCardActivity.class);
                    intent.putExtra(VerifyCardActivityPresenterImp.BUNDLE_ARGS_FUNDING_SOURCE, event.getCreditCard());
                    view.getActivity().startActivityForResult(intent, VERIFY_CARD_REQUEST_CODE);

                }
            });

            disposeBag.add(observeBusManager);

            Disposable observeNavigationClick =
                view.observeNavigationClick().subscribe(v -> onBackPressed());
            disposeBag.add(observeNavigationClick);

            Disposable observeAddCard = view.observeAddCardClick()
                .subscribe(v -> CWSdk.getInstance()
                    .addCreditCard(
                        view.getActivity(),
                        ADD_CARD_REQUEST_CODE,
                        (context, cwError) -> view.showError("error message = " + cwError.getErrorDescription() + "\n error code = " + cwError.getErrorCode())));
            disposeBag.add(observeAddCard);

            //checkIfDataSetNeedsLoad();
        }
    }

    @Override public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CARD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    CreditCard card = data.getParcelableExtra(CWSdk.BUNDLE_CARD_OPERATION_RESULT);
                    if (card != null) {
                        dataSet.add(card);
                    }
                }
            }
        }
        if (requestCode == VERIFY_CARD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                dataSet.clear();
                loadCards();
            }
        }
    }

    @Override public void onStop() {
        disposeBag.clear();
    }

    @Override public void onBackPressed() {
        if (view.isAvailable()) {
            view.finish();
        }
    }

    @Override protected boolean isLogEnabled() {
        return BuildConfig.DEBUG;
    }

    @Override protected String getClassTag() {
        return CardListActivityPresenterImp.class.getSimpleName();
    }

    @Override public SimpleItemTouchCallback.Callback swipeListener() {
        return position -> {
            if (view.isAvailable()) {
                CreditCard  removed = dataSet.remove(position);
                removeCard(removed, position);
            }
        };
    }

    private void checkIfDataSetNeedsLoad() {
        if (dataSet.isEmpty()) {
            loadCards();
          }
    }

    private void removeCard(CreditCard removed, int position) {
        view.showProgress();

        sdk.deleteCard(removed, new CWCallback<Boolean>() {
            @Override public void onSuccess(Boolean result) {
                if (view.isAvailable()) {
                    view.hideProgress();
                    view.showInfo("removed " + removed.getCardNumber());
                }
            }

            @Override public void onError(CWError error) {
                if (view.isAvailable()) {
                    dataSet.add(position, removed);
                    view.hideProgress();
                    view.showError(error.getErrorDescription());
                }
            }
        });
    }

    private void loadCards() {
        view.showProgress();
        sdk.getCreditCards(new CWCallback<List<CreditCard>>() {
            @Override public void onSuccess(List<CreditCard> result) {
                if (result != null && !result.isEmpty()) dataSet.addAll(result);
                CreditCard creditCard = new CreditCard();

                view.hideProgress();
            }

            @Override public void onError(CWError error) {

                view.hideProgress();
            }
        });
    }
}