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
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.beamuae.cwsdk.CreditCard;
import com.beamuae.cwsdk.shared.CardWalletCredentials;
import com.beamuae.cwsdk.CWSdk;
import com.beamuae.cwsdk.shared.CWCredentialProvider;
import com.beamuae.cwsdk.shared.CWError;
import com.beamuae.cwsdk.shared.CWInitializationListener;
import com.beamuae.cwsdk.shared.CWServer;
import com.beamuae.cwsdk.shared.CardWalletCredentialsListener;
import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.base.AbstractPresenter;
import com.vngrs.cwsdksample.base.BusManager;
import com.vngrs.cwsdksample.base.ObservableList;
import com.vngrs.cwsdksample.model.Category;
import com.vngrs.cwsdksample.model.event.SelectedCategoryEvent;
import com.vngrs.cwsdksample.view.CardListActivity;
import com.vngrs.cwsdksample.view.MainActivityView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class MainActivityPresenterImp extends AbstractPresenter<MainActivityView> implements MainActivityPresenter,
    CWCredentialProvider {

    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final Random RANDOM = new Random();
    private static final String EMAIL_FORMAT = "%s@gmail.com";
    private static final String STORED_PIN = "2659";

    private final CompositeDisposable disposeBag = new CompositeDisposable();
    private final ObservableList<Category> dataSet;
    private final String email;

    private final static int REQUEST_CODE_ADD_CARD = 0x33;

    private final Category[] categories = new Category[]
            {Category.INITIALIZE_CW_SDK,
                    Category.MANAGE_CREDIT_CARDS,
            };

    private boolean sdkInitialized = false;

    public MainActivityPresenterImp(@NonNull MainActivityView view, ObservableList<Category> dataSet) {
        super(view);
        this.dataSet = dataSet;
        this.email = String.format(Locale.US, EMAIL_FORMAT, toSaltString());
    }

    @Override
    public void onCreate() {
        if (view.isAvailable()) {
            view.setUp();
        }
    }

    @Override
    public void onStart() {
        if (view.isAvailable()) {
            Disposable observeBusManager = BusManager.add(evt -> {
                if (evt instanceof SelectedCategoryEvent) {
                    SelectedCategoryEvent event = (SelectedCategoryEvent) evt;
                    onCategorySelected(event.getCategory());
                }
            });
            // add bag
            disposeBag.add(observeBusManager);
            // initial load if needed
            checkIfDataSetNeedsLoad();
        }
    }

    @Override
    public void onStop() {
        disposeBag.clear();
    }

    @Override
    public void onBackPressed() {
        if (view.isAvailable()) {
            view.finish();
        }
    }

    @Override
    public void onDestroy() {
        CWSdk.getInstance().clear();
        super.onDestroy();
    }

    @Override
    protected boolean isLogEnabled() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected String getClassTag() {
        return MainActivityPresenterImp.class.getSimpleName();
    }

    @Override
    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                CreditCard model = data.getParcelableExtra(CWSdk.BUNDLE_CARD_OPERATION_RESULT);
                view.showError("User added card successfully " + model.getCardNumber());
            } else {
                view.showError("User cancelled adding card");
            }
        }
    }

    private void checkIfDataSetNeedsLoad() {
        if (dataSet.isEmpty()) {
            dataSet.addAll(Arrays.asList(categories));
        }
    }

    private void onCategorySelected(Category category) {
        if (Category.INITIALIZE_CW_SDK.equals(category)) {
            initializeBMSdk();
        } else {
            if (sdkInitialized) {
                if (Category.MANAGE_CREDIT_CARDS.equals(category)) {
                    Intent intent = new Intent(view.getContext(), CardListActivity.class);
                    view.startActivity(intent, null);
                }
            } else {
                view.showError("You should initialize sdk before calling other operations!");
            }
        }
    }

    private void initializeBMSdk() {
        view.showProgress();
        CWSdk.getInstance().start(CWServer.STAGING, this, new CWInitializationListener() {
            @Override
            public void onSdkInitialized() {
                sdkInitialized = true;
                if (view.isAvailable()) {
                    view.showInfo("Sdk initialized successfully");
                    view.hideProgress();
                }

            }

            @Override
            public void onError(CWError error) {
                if (view.isAvailable()) {
                    view.hideProgress();
                    view.showError(error.getErrorDescription());
                }
            }
        });
    }

    private String toSaltString() {
        StringBuilder str = new StringBuilder();
        int index;
        while (str.length() < 10) {
            index = RANDOM.nextInt(SALT_CHARS.length());
            str.append(SALT_CHARS.charAt(index));
        }
        return str.toString();
    }

    @Override
    public void getCredentialListener(CardWalletCredentialsListener cardWalletCredentialsListener) {
        new android.os.Handler().postDelayed(() -> {

            CardWalletCredentials cardWalletCredentials =
                new CardWalletCredentials()
                    .setPartnerId(getStringFromPreferences("PARTNER"))
                    .setToken(getStringFromPreferences("TOKEN"));

            cardWalletCredentialsListener.onCredentialReceived(cardWalletCredentials);
        }, 100);
    }

    private String getStringFromPreferences(String key){
       return view.getActivity().getSharedPreferences("CWTEST",Context.MODE_PRIVATE).getString(key,"");
    }
}