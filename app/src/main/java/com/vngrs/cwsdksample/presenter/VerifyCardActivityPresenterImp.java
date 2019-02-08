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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Toast;
import com.beamuae.cwsdk.CWSdk;
import com.beamuae.cwsdk.CreditCard;
import com.beamuae.cwsdk.shared.CWCallback;
import com.beamuae.cwsdk.shared.CWError;
import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.base.AbstractPresenter;
import com.vngrs.cwsdksample.view.VerifyCardActivityView;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class VerifyCardActivityPresenterImp extends AbstractPresenter<VerifyCardActivityView>
    implements VerifyCardActivityPresenter {

    final static String BUNDLE_ARGS_FUNDING_SOURCE = "bundle.args.funding.source";

    private final static String BUNDLE_ARGS_AMOUNT = "bundle.args.amount";

    private final CompositeDisposable disposeBag = new CompositeDisposable();
    private final CWSdk sdk;

    private CreditCard creditCard;
    private double amount;

    public VerifyCardActivityPresenterImp(@NonNull VerifyCardActivityView view) {
        super(view);
        this.sdk = CWSdk.getInstance();

    }

    @Override
    public void restoreState(Bundle restoreState) {
        if (restoreState != null) {
            if (restoreState.containsKey(BUNDLE_ARGS_AMOUNT)) {
                amount = restoreState.getDouble(BUNDLE_ARGS_AMOUNT, 0.0);
            }
            if (restoreState.containsKey(BUNDLE_ARGS_FUNDING_SOURCE)) {
                creditCard =  restoreState.getParcelable(BUNDLE_ARGS_FUNDING_SOURCE);
            }
        }
    }

    @Override
    public void storeState(Bundle storeState) {
        storeState.putDouble(BUNDLE_ARGS_AMOUNT, amount);
        if (creditCard != null) {
            storeState.putParcelable(BUNDLE_ARGS_FUNDING_SOURCE, creditCard);
        }
    }

    @Override public void activityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void requestPermissionResult(int requestCode, String[] permissions, int[] grants) {

    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onCreate() {
        if (view.isAvailable()) {
            view.setUp();
            view.bindToolbarTitle(view.getStringRes(
                com.beamuae.cwsdk.R.string.label_verify_x, (creditCard != null ? creditCard.getCardNumber() : "")));
        }
    }

    @Override public void onDestroy() {

    }

    @Override public void onResume() {

    }

    @Override public void onPause() {

    }

    @Override
    public void onStart() {
        if (view.isAvailable()) {
            Disposable observeAmountChangeDisposable = view.observeAmountChange()
                    .map(CharSequence::toString)
                    .concatMap(text -> {
                        try {
                            return Observable.just(Double.parseDouble(text));
                        } catch (NumberFormatException error) {
                            return Observable.just(view.getStringRes(com.beamuae.cwsdk.R.string.str_enter_valida_amount))
                                    .doOnNext(view::showInputError)
                                    .map(amount -> 0.0);
                        }
                    }).filter(amount -> amount != 0.0)
                    .subscribe(amount -> {
                        this.amount = amount;
                        view.hideInputError();
                    });
            // add bag
            disposeBag.add(observeAmountChangeDisposable);

            Disposable observeNavigationClick = view.observeNavigationClick()
                    .subscribe(v -> onBackPressed());

            // add bag
            disposeBag.add(observeNavigationClick);

            Disposable observeChargeClick = view.observeChargeClick()
                    .map(view -> amount)
                    .filter(amount -> amount != 0.0)
                    .subscribe(aDouble -> verifyCard(aDouble.toString()));

            // add bag
            disposeBag.add(observeChargeClick);
        }
    }

    @Override
    public void onStop() {
        disposeBag.clear();
    }

    @Override
    public void onBackPressed() {
        if (view.isAvailable()) {
            view.finishAndSetResult(Activity.RESULT_CANCELED, null);
        }
    }

    @Override
    protected boolean isLogEnabled() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected String getClassTag() {
        return VerifyCardActivityPresenterImp.class.getSimpleName();
    }

    private void verifyCard(String amount) {
        view.showProgress();
        sdk.verifyCard(creditCard, amount, view.getContext(), new CWCallback<CreditCard>() {
            @Override public void onSuccess(CreditCard result) {
                showToast("Done");
                view.finishAndSetResult(Activity.RESULT_OK,null);
            }

            @Override public void onError(CWError error) {
                view.hideProgress();
                showToast(error.getErrorDescription());
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(view.getContext(),message, Toast.LENGTH_LONG).show();
    }
}
