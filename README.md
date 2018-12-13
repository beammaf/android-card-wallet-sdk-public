### Card Wallet SDK - Android Guide

The Card Wallet SDK is for interacting with the Beam Wallet platform to add, manage or remove cards. 

In order to use SDK you must be a registered developer with a provisioned API key.

## Requirements
* SDK Supports min api level 18.
* SDK Requires Java 8.
* BAPFES Authentication token. 

## Integration
* Java 8+ support should be added to project. To do that below code should be added to project level gradle file.
  ```groovy
  android {
      compileOptions{
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
  }
  ```
* Gradle compile dependency
  ```groovy
    implementation 'com.vngrs.beam:cwsdk:1.0:release@aar'
  ```

* To your root build.gradle
    ```groovy
         maven { url "https://repo.beamuae.app" }
    ```

* SDK also requires extra library of Android Support Design.
    ```groovy
       implementation 'com.android.support:design:27.1.0'
     ```

# SDK Overview

## Card Wallet TokenProvider

 
 CWCredentialProvider is an interface in order to provide authentication token, partner id and user id. This interface should be implemented to feed sdk with token, partner id and user id.
 
 * Make sure to provide token, partner id and user id before calling card actions.
 ```java
new CWCredentialProvider() {
            @Override
            public void getCredentialListener(CardWalletCredentialsListener tokenListener) {

            }
        };
 ```
#### CardWalletCredentialsListener
  
  ```java
  public interface CardWalletCredentialsListener {
      void onCredentialReceived(CardWalletCredentials cardWalletCredentials);
  }
  ```

#### CardWalletCredentials

```java
    public class CardWalletCredentials implements Parcelable {
         private String partnerId;
         private String token;
      }
```



## Initialize and Start Sdk
* In order to in initialize sdk, CWSDK start() function should called from Instance

```java
      void start(@NonNull CWServer server, @NonNull CWCredentialProvider cwCredentialProvider, CWInitializationListener listener);
```
* CWServer: This is an enum in order to specify environment of funding service.



Example Usage: 
```java

CWSdk.getInstance()
        .start(CWServer.STAGING,
             cardWalletCredentialsListener -> {
                        CardWalletCredentials cardWalletCredentials =
                            new CardWalletCredentials()
                            .setToken("TOKEN")
                            .setPartnerId("PARTNER ID")

                        cardWalletCredentialsListener.onCredentialReceived(cardWalletCredentials);
                    },
            new CWInitializationListener() {
                @Override public void onSdkInitialized() {
                    //Sdk initialized successfully
                    }

                @Override public void onError(CWError error) {
                    //An error occurred
                    }
                });

```

## CWSdk 
* All sdk functions can be accessed from CWSdk object. It's a simple abstract class to access functionalities of Card Wallet SDK

```java
public abstract class CWSdk {
  public static final String BUNDLE_CARD_OPERATION_RESULT = "bundle_card_operation_result";
  public static String LOG_TAG = "FUNDING-SDK";

  public static CWSdk getInstance() {
    return CWSdkImpl.getInstance();
  }


  abstract public void start(@NonNull CWServer server, @NonNull CWCredentialProvider cwCredentialProvider, CWInitializationListener listener);

  abstract public void getCreditCards(CWCallback<List<CreditCard>> callback);

  abstract public void addCreditCard(Activity activity, int requestCode);

  abstract public void addCreditCard(Activity activity, int requestCode, ActivityOptions options);

  abstract public void verifyCard(CreditCard creditCard, Activity activity, int requestCode);

  abstract public void verifyCard(CreditCard creditCard, Activity activity, int requestCode, ActivityOptions options);

  abstract public void deleteCard(CreditCard creditCard, CWCallback<Boolean> listener);


  abstract public void clear();
}

```

#### CWCallback\<T>
* An interface in order to get results for async operations.

#### CWServer
* An enum object to specify target environment of sdk
```java
public enum CWServer {
    DEV,
    PRODUCTION,
    STAGING
}

```


###  Credit Card Operations
#### CreditCard
This class is a model used as a reference of CreditCard. isValid() returns status of Credit card.


```java
public class CreditCard implements Parcelable {

    private String cardToken;
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private String cardholderName;
    private CardStatus status;
    private boolean requiresVerification = false;
    private int verificationAttemptsLeft = 3;
    private boolean canSendNewVerification = true;

    public boolean isValid() {
        return status == CardStatus.SUCCESS
            || status == CardStatus.PENDING
            || status == CardStatus.AVAILABLE;
    }
}

```

#### Get Credit Cards
```java
 CWSdk.getInstance().getCreditCards(new CWCallback<List<CreditCard>>() {
            @Override
            public void onSuccess(List<CreditCard> result) {
                
            }

            @Override
            public void onError(CWError error) {

            }
        });
```
* This function returns registered credit cards which associated to logged in user.

#### Delete Credit Card
```java
CWSdk.deleteCard(getCreditCard(), new CWCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                
            }

            @Override
            public void onError(CWError error) {
                
            }
    });
```
* This function takes to Credit Card and returns boolean response.

#### Add Credit Card
In order to add credit card, there is a function called addCreditCard(). This function is going to launch.  addCreditCardActivity which is inside the Card Wallet  SDK.
This UI is fully customizable

For more information about customizing UI, Please check "Customizing UI" section.

```java
  CWSdk.getInstance().addCreditCard(view.getActivity(), ADD_CARD_REQUEST_CODE)
```

This function launch an activity for result. 

```java
@Override
public void activityResult(int requestCode, int resultCode, Intent data) {
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
}
```

### Verify Credit Card
In order to verify credit card, there is a function called verifyCreditCard(). This function is going to launch verifyCreditCardActivity which is inside the Card Wallet SDK.

This UI is fully customizable

For more information about customizing UI, Please check "Customizing UI" section.

```java
 CWSdk.getInstance().verifyCard(getCreditCard(), getActivity(), VERIFY_CARD_REQUEST_CODE);

```

This function launch an activity for result. 

```java
public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VERIFY_CARD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    CreditCard card = data.getParcelableExtra(CWSdk.BUNDLE_CARD_OPERATION_RESULT);
                }
            }
        }
}
```



## UI Customization.
Card Wallet SDK supports custom UI for AddCreditCard and VerifyCreditCard functionalities. 


### Add Credit Card Page
In order to customize Add Credit card ui, **cw_add_card_layout** should be added to project. Card Wallet SDK automatically detects if cw_add_card_layout.xml exists and render it to screen. This xml should contain this fields with specified ids.

|  View Type   | Description              |id              |
| ------------ | ------------------------ |--------------- |
|  EditText | Credit Card Number Field |edtPanCw       |
|  EditText | Name Surname             |edtFullNameCw |
|  EditText | CVC Number               |edtCvcCw       |
|  EditText | Expiry Date Field        |edtExpiryCw    |
|  Button   | Submit Button            |btnSubmitCw    |



#### Example Layout
 
```xml
<?xml version="1.0" encoding="utf-8"?>

<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:support="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.v7.widget.Toolbar
        android:id="@+id/viewToolbar"
        android:layout_width="0dip"
        android:layout_height="wrap_content"
        android:background="@color/colorPrimary"
        android:minHeight="?android:attr/actionBarSize"
        android:theme="@style/ThemeOverlay.AppCompat.Light"
        support:layout_constraintEnd_toEndOf="parent"
        support:layout_constraintStart_toStartOf="parent"
        support:layout_constraintTop_toTopOf="parent"
        support:navigationIcon="@drawable/ic_arrow_back"
        support:title="@string/str_add_new_card"
        support:titleTextColor="@color/colorToolbarTextAndIconTint" />

    <android.support.v7.widget.AppCompatImageView
        android:id="@+id/imgCard"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="@dimen/spacing_2x"
        android:alpha="0.6"
        android:src="@drawable/ic_credit_card_black_24dp"
        support:layout_constraintBottom_toBottomOf="@id/number_layout"
        support:layout_constraintStart_toStartOf="parent"
        support:layout_constraintTop_toTopOf="@id/number_layout"

        tools:layout_editor_absoluteX="0dp"
        tools:layout_editor_absoluteY="72dp" />

    <android.support.design.widget.TextInputLayout
        android:id="@+id/number_layout"
        style="@style/Beam.TextInputLayout"
        android:layout_width="0dip"
        android:layout_height="wrap_content"
        android:layout_marginBottom="@dimen/spacing_2x"
        android:layout_marginEnd="@dimen/spacing_2x"
        android:layout_marginStart="@dimen/spacing_2x"
        android:layout_marginTop="@dimen/spacing_2x"
        android:theme="@style/Beam.TextInputLayout"
        support:layout_constraintEnd_toEndOf="parent"
        support:layout_constraintStart_toEndOf="@id/imgCard"
        support:layout_constraintTop_toBottomOf="@+id/viewToolbar">

        <android.support.design.widget.TextInputEditText
            android:id="@+id/edtPanCw"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:digits="0,1,2,3,4,5,6,7,8,9, "
            android:drawableEnd="@drawable/icon_card_mastercard"
            android:hint="@string/label_card_number"
            android:inputType="number"
            android:maxLength="19" />
    </android.support.design.widget.TextInputLayout>


    <android.support.design.widget.TextInputLayout
        android:id="@+id/expiry_layout"
        style="@style/Beam.TextInputLayout"
        android:layout_width="0dip"
        android:layout_height="wrap_content"
        android:layout_marginBottom="@dimen/spacing_2x"
        android:layout_marginEnd="@dimen/spacing_2x"
        android:layout_marginTop="@dimen/spacing_2x"
        android:theme="@style/Beam.TextInputLayout"
        support:layout_constraintEnd_toStartOf="@id/cvc_layout"
        support:layout_constraintStart_toStartOf="@id/number_layout"
        support:layout_constraintTop_toBottomOf="@+id/number_layout">

        <android.support.design.widget.TextInputEditText
            android:id="@+id/edtExpiryCw"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:digits="0,1,2,3,4,5,6,7,8,9,/"
            android:hint="@string/label_mm_yy"
            android:inputType="number"
            android:maxLength="5" />
    </android.support.design.widget.TextInputLayout>


    <android.support.design.widget.TextInputLayout
        android:id="@+id/cvc_layout"
        style="@style/Beam.TextInputLayout"
        android:layout_width="0dip"
        android:layout_height="wrap_content"
        android:layout_marginBottom="@dimen/spacing_2x"
        android:layout_marginEnd="@dimen/spacing_2x"
        android:layout_marginStart="@dimen/spacing_2x"
        android:layout_marginTop="@dimen/spacing_2x"
        android:theme="@style/Beam.TextInputLayout"
        support:layout_constraintEnd_toEndOf="parent"
        support:layout_constraintStart_toEndOf="@+id/expiry_layout"
        support:layout_constraintTop_toBottomOf="@+id/number_layout">

        <android.support.design.widget.TextInputEditText
            android:id="@+id/edtCvcCw"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/label_cvc"
            android:drawableEnd="@drawable/ic_payment_black_24_px"
            android:inputType="number"
            android:maxLength="3" />
    </android.support.design.widget.TextInputLayout>


    <android.support.design.widget.TextInputLayout
        android:id="@+id/full_name_layout"
        style="@style/Beam.TextInputLayout"
        android:layout_width="0dip"
        android:layout_height="wrap_content"
        android:layout_marginBottom="@dimen/spacing_2x"
        android:layout_marginEnd="@dimen/spacing_2x"
        android:layout_marginTop="@dimen/spacing_2x"
        android:theme="@style/Beam.TextInputLayout"
        support:layout_constraintEnd_toEndOf="parent"
        support:layout_constraintStart_toStartOf="@id/number_layout"
        support:layout_constraintTop_toBottomOf="@+id/cvc_layout">

        <android.support.design.widget.TextInputEditText
            android:id="@+id/edtFulltNameCw"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/label_name_on_card"
            android:inputType="textCapWords"
            android:maxLines="1" />
    </android.support.design.widget.TextInputLayout>

    <ProgressBar
        android:id="@+id/viewProgress"
        android:layout_width="wrap_content"
        android:layout_height="0dip"
        android:theme="@style/Widget.AppCompat.ProgressBar"
        support:layout_constraintBottom_toBottomOf="@+id/btnSubmitCw"
        support:layout_constraintEnd_toEndOf="@+id/btnSubmitCw"
        support:layout_constraintTop_toTopOf="@+id/btnSubmitCw" />


    <Button
        android:id="@+id/btnSubmitCw"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginBottom="@dimen/spacing_2x"
        android:layout_marginEnd="@dimen/spacing_2x"
        android:layout_marginStart="@dimen/spacing_2x"
        android:layout_marginTop="@dimen/spacing_2x"
        android:enabled="false"
        android:text="@string/label_done"
        support:layout_constraintEnd_toEndOf="parent"
        support:layout_constraintTop_toBottomOf="@id/full_name_layout" />


</android.support.constraint.ConstraintLayout>
```


### Verify Credit Card Page
In order to customize Verify Credit card ui, **cw_verify_card_layout.xml** should be added to project. Card Wallet SDK automatically detects if cw_verify_card_layout.xml exists and render it to screen. This xml should contain this fields with specified ids.

|  View Type   | Description       |id                |
| ------------ | ----------------- |------------------ |
|  EditText | Verify Ammount    |edtVerifyAmountCw |
|  Button   | Submit Button     |edtSubmitVerifyCw |

```xml
<android.support.constraint.ConstraintLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:support="http://schemas.android.com/apk/res-auto"
  android:layout_width="match_parent"
  android:layout_height="match_parent">

  <android.support.v7.widget.Toolbar
    android:id="@+id/viewToolbar"
    support:navigationIcon="@drawable/ic_arrow_back"
    support:layout_constraintStart_toStartOf="parent"
    support:layout_constraintEnd_toEndOf="parent"
    support:layout_constraintTop_toTopOf="parent"
    android:theme="@style/ThemeOverlay.AppCompat.Light"
    android:minHeight="?android:attr/actionBarSize"
    android:background="@color/colorPrimary"
    support:titleTextColor="@color/colorToolbarTextAndIconTint"
    android:layout_width="0dip"
    android:layout_height="wrap_content" />

  <android.support.design.widget.TextInputLayout
    android:id="@+id/viewTextInputLayout"
    style="@style/Beam.TextInputLayout"
    android:layout_marginBottom="@dimen/spacing_2x"
    android:layout_marginStart="@dimen/spacing_9x"
    android:layout_marginEnd="@dimen/spacing_2x"
    android:layout_marginTop="@dimen/spacing_2x"
    support:layout_constraintStart_toStartOf="parent"
    support:layout_constraintEnd_toEndOf="parent"
    support:layout_constraintTop_toBottomOf="@+id/viewToolbar"
    android:layout_width="0dip"
    android:layout_height="wrap_content">

    <android.support.design.widget.TextInputEditText
      android:id="@+id/edtVerifyAmountCw"
      android:inputType="numberDecimal"
      android:layout_width="match_parent"
      android:layout_height="wrap_content" />

  </android.support.design.widget.TextInputLayout>

  <Button
    android:id="@+id/edtSubmitVerifyCw"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginEnd="@dimen/spacing_2x"
    support:layout_constraintTop_toBottomOf="@+id/viewTextInputLayout"
    support:layout_constraintEnd_toEndOf="parent"
    style="@style/Beam.Button"
    android:textAllCaps="true"
    android:visibility="visible"
    android:text="@string/label_done"/>

  <ProgressBar
    android:id="@+id/viewProgress"
    android:layout_width="wrap_content"
    android:layout_height="0dip"
    android:theme="@style/Widget.AppCompat.ProgressBar"
    support:layout_constraintTop_toTopOf="@+id/edtSubmitVerifyCw"
    support:layout_constraintEnd_toEndOf="@+id/edtSubmitVerifyCw"
    support:layout_constraintBottom_toBottomOf="@+id/edtSubmitVerifyCw" />

  <TextView
    android:id="@+id/viewTextTitle"
    style="@style/Beam.Text.H4"
    android:layout_marginStart="@dimen/spacing_9x"
    android:layout_marginEnd="@dimen/spacing_2x"
    support:layout_constraintStart_toStartOf="parent"
    support:layout_constraintEnd_toEndOf="parent"
    support:layout_constraintTop_toBottomOf="@+id/viewButtonCharge"
    android:layout_width="0dip"
    android:layout_height="wrap_content"
    android:layout_marginTop="@dimen/spacing_4x"
    android:textColor="@color/beam_grey"
    android:typeface="monospace"/>

  <TextView
    style="@style/Beam.Text.S"
    android:id="@+id/viewTextMessage"
    android:layout_marginTop="@dimen/spacing_4x"
    support:layout_constraintTop_toBottomOf="@+id/viewTextTitle"
    android:layout_width="0dip"
    android:layout_height="wrap_content"
    android:layout_marginStart="@dimen/spacing_9x"
    android:layout_marginEnd="@dimen/spacing_2x"
    support:layout_constraintStart_toStartOf="parent"
    support:layout_constraintEnd_toEndOf="parent"
    android:text="@string/label_limits_apply_until_verified"
    android:textColor="@color/beam_grey_light"
    android:typeface="monospace"/>

  <TextView
    android:id="@+id/viewTextLink"
    style="@style/Beam.Text.S"
    support:layout_constraintTop_toBottomOf="@+id/viewTextMessage"
    android:layout_width="0dip"
    android:layout_height="wrap_content"
    android:layout_marginStart="@dimen/spacing_9x"
    android:layout_marginEnd="@dimen/spacing_2x"
    support:layout_constraintStart_toStartOf="parent"
    support:layout_constraintEnd_toEndOf="parent"
    android:layout_marginTop="@dimen/spacing_2x"
    android:textColor="@color/beam_grey_light"
    android:textColorHighlight="@color/beam_cta_20"
    android:textColorLink="@color/beam_cta"
    android:typeface="monospace" />

</android.support.constraint.ConstraintLayout>
```




## Version
* 1.0
