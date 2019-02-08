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

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.vngrs.cwsdksample.BuildConfig;
import com.vngrs.cwsdksample.R;
import com.vngrs.cwsdksample.base.AbstractActivity;
import com.vngrs.cwsdksample.base.RxTextView;
import com.vngrs.cwsdksample.base.RxView;
import com.vngrs.cwsdksample.presenter.VerifyCardActivityPresenter;
import com.vngrs.cwsdksample.presenter.VerifyCardActivityPresenterImp;
import io.reactivex.Observable;


public class VerifyCardActivity extends AbstractActivity<VerifyCardActivityPresenter> implements VerifyCardActivityView {

    private EditText viewEditText;
    private Button viewButtonCharge;
    private Toolbar viewToolbar;
    private TextInputLayout viewTextInputLayout;
    private ProgressBar viewProgress;

    @Override
    public int layoutRes() {
        return R.layout.cw_verify_card_layout;
    }

    @Override
    public void setUp() {
        viewToolbar = findViewById(R.id.viewToolbar);
        viewEditText = findViewById(R.id.edtVerifyAmountCw);
        viewButtonCharge = findViewById(R.id.edtSubmitVerifyCw);
        viewTextInputLayout = findViewById(R.id.viewTextInputLayout);
        viewProgress = findViewById(R.id.viewProgress);
        // disable it
        viewButtonCharge.setEnabled(false);
        // make progress invisible
        viewProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void bindToolbarTitle(String title) {
        viewToolbar.setTitle(title);
    }

    @NonNull
    @Override
    public VerifyCardActivityPresenter presenter() {
        return new VerifyCardActivityPresenterImp(this);
    }

    @Override
    public boolean isLogEnabled() {
        return BuildConfig.DEBUG;
    }

    @Override
    public String getClassTag() {
        return VerifyCardActivity.class.getSimpleName();
    }

    @Override
    public Observable<CharSequence> observeAmountChange() {
        return RxTextView.textChanges(viewEditText);
    }

    @Override
    public Observable<View> observeChargeClick() {
        return RxView.clicks(viewButtonCharge);
    }

    @Override
    public Observable<View> observeNavigationClick() {
        return RxView.clicks(viewToolbar);
    }

    @Override
    public void showProgress() {
        viewButtonCharge.setVisibility(View.INVISIBLE);
        viewProgress.setVisibility(View.VISIBLE);
        viewProgress.setIndeterminate(true);
    }

    @Override
    public void hideProgress() {
        viewProgress.setIndeterminate(false);
        viewProgress.setVisibility(View.INVISIBLE);
        viewButtonCharge.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInputError(String error) {
        viewButtonCharge.setEnabled(false);
        viewTextInputLayout.setErrorEnabled(true);
        viewTextInputLayout.setError(error);
    }

    @Override
    public void hideInputError() {
        viewButtonCharge.setEnabled(true);
        viewTextInputLayout.setError(null);
        viewTextInputLayout.setErrorEnabled(false);
    }
}