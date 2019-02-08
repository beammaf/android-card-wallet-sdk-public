package com.vngrs.cwsdksample;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.vngrs.cwsdksample.base.RxTextView;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

  // private View startButton;

  private final CompositeDisposable disposeBag = new CompositeDisposable();

  private final static String EMPTY = "";

  private EditText viewEditTextCardNumber;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.test_layout);

    viewEditTextCardNumber = findViewById(R.id.viewEditTextCardNumber);
  }

  @Override protected void onStart() {
    super.onStart();
    Disposable observeCardNumberText = RxTextView.textChangesForCreditCardNumber(viewEditTextCardNumber)
      .concatMap(text -> {
        if (text.length() == 19) {
          return Observable.just(text.toString().replace(" ", ""));
        } else {
          return Observable.just("card number should be at least 16 char and only numbers")
            .doOnNext(error -> showInputError(R.id.viewTextCardNumberInputLayout, error))
            .map(error -> EMPTY);
        }
      }).filter(text -> text.length() >= 16)
        .subscribe(cardNumber -> {
          Log.println(Log.ERROR, MainActivity.class.getSimpleName(), cardNumber);
          hideInputError(R.id.viewTextCardNumberInputLayout);
      });

    disposeBag.add(observeCardNumberText);
  }

  @Override protected void onStop() {
    disposeBag.clear();
    super.onStop();
  }

  private void showInputError(@IdRes int layoutRes, String errorText) {
    bindErrorText(layoutRes, errorText);
  }

  private void hideInputError(@IdRes int layoutRes) {
    bindErrorText(layoutRes, null);
  }

  private void bindErrorText(@IdRes int layoutRes, String errorText) {
    TextInputLayout textInputLayout = findViewById(layoutRes);
    textInputLayout.setError(errorText);
    textInputLayout.setErrorEnabled(errorText != null);
  }
  /*
        startButton = findViewById(R.id.btnStart);
        startButton.setOnClickListener(view -> startParkingExperience());


    }

    private void startParkingExperience() {
        FSSdk.getInstance().addCreditCard(this,100);
    }

     */
}
