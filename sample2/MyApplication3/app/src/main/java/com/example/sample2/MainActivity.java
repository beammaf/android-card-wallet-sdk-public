package com.example.sample2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.beamuae.cwsdk.CWSdk;
import com.beamuae.cwsdk.CreditCard;
import com.beamuae.cwsdk.shared.CWCallback;
import com.beamuae.cwsdk.shared.CWError;
import com.beamuae.cwsdk.shared.CWInitializationListener;
import com.beamuae.cwsdk.shared.CWServer;
import com.beamuae.cwsdk.shared.CardWalletCredentials;
import com.beamuae.cwsdk.shared.CwErrorListener;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.fabric.sdk.android.Fabric;


/**
 * This sample class to describe how to use the Card wallet SDK in easy way.
 * This class have 2 step to init SDK :
 * 1- CWSdk.getInstance().start(....) using : - Token  - Partner Id.
 * 2- Active provide authentication token to feed sdk with token &
 * receive all events u needs using cardWalletCredentialsListener.onCredentialReceived(...)
 *
 * All sdk functions is:
 * - getCreditCards(...).
 * - addCreditCard (..).
 * - deleteCard(....).
 * - verifyCard(....).
 *
 * If you need to get more information, please visit the full document https://github.com/beammaf/android-card-wallet-sdk-public
 *
 * Note that if the sample app not working please check the valid token ID  & valid partner ID.
 *
 * Created by AhmadSamhan
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_status)
    TextView mTvStatus;
    private String mToken;
    private String mPartnerID;
    private int ADD_CARD_REQUEST_CODE = 1;
    private boolean isClickInstallSdk = false;
    private CreditCard mCard;
    private List<CreditCard> cardList = new ArrayList<>();
    private int VERIFY_CARD_REQUEST_CODE = 55;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }


    @OnClick(R.id.btn_install_sdk)
    void generateToken() {
        //Show alert dialog to add your token and partner id
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.alert_label_editor, null);
        TextInputEditText editTextPartnerId = dialogView.findViewById(R.id.edTextPartnerId);
        TextInputEditText editTextTokenId = dialogView.findViewById(R.id.edTextTokenId);
        dialogBuilder.setTitle(getString(R.string.dialog_title));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle(getString(R.string.dialog_title));
        dialogBuilder.setNeutralButton(getString(R.string.txt_submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mToken = Objects.requireNonNull(editTextTokenId.getText()).toString();
                mPartnerID = Objects.requireNonNull(editTextPartnerId.getText()).toString();
                if (TextUtils.isEmpty(mToken) || TextUtils.isEmpty(mPartnerID)) {
                    showToastMessage(getString(R.string.txt_add_token));
                    return;
                }
                onClickInstallSdk();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.txtCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToastMessage(getString(R.string.txtCancelintSDk));

            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    void onClickInstallSdk() {
        CWSdk.getInstance().start(CWServer.STAGING, cardWalletCredentialsListener -> {
                    CardWalletCredentials cardWalletCredentials = new CardWalletCredentials()
                            .setToken(mToken)
                            .setPartnerId(mPartnerID);
                    cardWalletCredentialsListener.onCredentialReceived(cardWalletCredentials);
                },
                new CWInitializationListener() {
                    @Override
                    public void onSdkInitialized() {
                        //Sdk initialized successfully
                        String txt = "Initialized SDk successfully,\n Now you can add your card";
                        updateTitle(txt, R.color.green_900);
                        isClickInstallSdk = true;
                    }

                    @Override
                    public void onError(CWError error) {
                        //An error occurred
                        String txt = "Initialized SDk ? " + error.getErrorDescription();
                        updateTitle(txt, R.color.red);
                    }
                });
    }

    @OnClick(R.id.btn_manage_card)
    void onClickManageCard() {
        if (!isClickInstallSdk) {
            showToastMessage("Please first step install our SDk !");
            return;
        }
        CWSdk.getInstance().addCreditCard(this, ADD_CARD_REQUEST_CODE, new CwErrorListener() {
            @Override
            public void onError(Context context, CWError cwError) {
                showError(cwError.getErrorDescription(), Color.RED);
            }
        });
    }

    @OnClick(R.id.btn_get_card)
    void onClickGetCard() {
        if (!isClickInstallSdk) {
            showToastMessage("Please first step install our SDk !");
            return;
        }
        CWSdk.getInstance().getCreditCards(new CWCallback<List<CreditCard>>() {
            @Override
            public void onSuccess(List<CreditCard> creditCards) {
                cardList = creditCards;
                mCard = creditCards.get(0);
                for (CreditCard s : cardList) {
                    showToastMessage(s.getCardNumber());
                }
            }

            @Override
            public void onError(CWError cwError) {
                showToastMessage("No card added until now !, Add one");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResult(requestCode, resultCode, data);
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CARD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mCard = data.getParcelableExtra(CWSdk.BUNDLE_CARD_OPERATION_RESULT);
                    if (mCard != null) {
                        showToastMessage(mCard.getCardNumber() + " " + mCard.getStatus());
                    }
                }
            }
        }
    }

    @OnLongClick(R.id.btn_manage_card)
    void deleteCard() {
        if (!isClickInstallSdk) {
            showToastMessage("Please first step install our SDk !");
            return;
        }
        if (mCard == null) {
            return;
        }
        CWSdk.getInstance().deleteCard(mCard, new CWCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                showToastMessage("Delete successfully !");
            }

            @Override
            public void onError(CWError cwError) {
                showToastMessage("Not deleted !");
            }
        });
    }

    @OnClick(R.id.btn_verify)
    void verifyCard() {
        if (!isClickInstallSdk) {
            showToastMessage("Please first step install our SDk !");
            return;
        }
        if (mCard == null) {
            return;
        }
        CWSdk.getInstance().verifyCard(this, mCard, (context, cwError) -> showToastMessage(cwError.getErrorDescription()), VERIFY_CARD_REQUEST_CODE, null, new CWCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard creditCard) {
                showToastMessage("Success verify !");
            }

            @Override
            public void onError(CWError cwError) {
                showToastMessage("Error verify !");
            }
        });

    }

    private void updateTitle(String txt, int color) {
        mTvStatus.setText(txt);
        mTvStatus.setTextColor(ContextCompat.getColor(this, color));
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_insert_emoticon_green_800_24dp);
        mTvStatus.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        showToastMessage(txt);
    }

    private void initViews() {
        mToken = getString(R.string.ny_token);
        mPartnerID = getString(R.string.txt_loyalty);
    }

    public void showError(String message, int color) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(color);
        snackbar.setAction(android.R.string.ok, v -> snackbar.dismiss());
        snackbar.show();
    }

    public void showToastMessage(String txt) {
        Toast toast = Toast.makeText(this, txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CWSdk.getInstance().clear();
    }
}
