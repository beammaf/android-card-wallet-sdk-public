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
* To your root build.gradle
  ```groovy
       maven { url "https://repo.beamuae.app" }
  ```

* Gradle compile dependency
  ```groovy
    implementation ('app.beamuae:cwsdk:1.0.34:release@aar'){
        transitive = true
    }
  ```

* SDK also requires extra library of Android Support Design. Minimum requirement of Support version is **27.1.0**
    ```groovy
       implementation 'com.android.support:design:YOUR_SUPPORT_VERSION'
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

    abstract public void verifyCard(CreditCard creditCard, String verifiedAmount, Context context, CWCallback<CreditCard> callback);

    abstract public void addCreditCard(Activity activity, int requestCode, CwErrorListener addCardErrorListener);

    abstract public void addCreditCard(Activity activity, int requestCode, CwErrorListener addCardErrorListener, ActivityOptions options);

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

**status, requiresVerification, verificationAttemptsLeft, canSendNewVerification** fields returns from backend when getting cards. That fields should be **null** when adding card.


```java
public class CreditCard implements Parcelable {

    private String cardToken = "";
    private String cardNumber = "";
    private CardStatus status;
    private boolean requiresVerification = true;
    private int verificationAttemptsLeft = 3;
    private boolean canSendNewVerification = true;

}


```

#### Card status

|  Enum   | Description              |
| ------------ | ------------------------|
|  PENDING | Card is added, but waiting for verification.|
|  AVAILABLE | Card is added and verified.|
|  ERROR | There was an error on either adding or verifying the card.|
|  DISABLED | Card is deleted and won't be used.|



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
CWSdk.getInstance().deleteCard(getCreditCard(), new CWCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                
            }

            @Override
            public void onError(CWError error) {
                
            }
    });
```
* This function takes to Credit Card and returns boolean response.

#### Example usage of get and delete

```java
List<CreditCard> cardList = new ArrayList<>;

private void getCards() {
        CWSdk.getInstance().getCreditCards(new CWCallback<List<CreditCard>>() {
                    @Override
                    public void onSuccess(List<CreditCard> result) {
                            cardList = result;                
                    }
        
                    @Override
                    public void onError(CWError error) {
        
                    }
                });
}

private void removeCard(int position) {

        CWSdk.getInstance().deleteCard(cardList.get(position), new CWCallback<Boolean>() {
            @Override public void onSuccess(Boolean result) {
                
            }

            @Override public void onError(CWError error) {
                
            }
        });
    }
```




#### Add Credit Card
In order to add credit card, there is a function called addCreditCard(). This function is going to launch. addCreditCardActivity which is inside the Card Wallet SDK. **addCreditCard** takes **CwErrorListener** as a parameter. It returns current context and backend error if there is any. This UI is fully customizable

Firstly, `AddCardActivity` should define in xml. Suggested theme is `Theme.AppCompat.Light.NoActionBar` but it is customizable.  

In order to saving data when screen rotation, `android:configChanges="orientation|screenSize"` should be added to manifest.
```xml
<activity
          android:theme="@style/Theme.AppCompat.Light.NoActionBar"
          android:configChanges="orientation|screenSize"
          android:name="com.beamuae.cwsdk.AddCardActivity"/>
``` 

Then addCreditCard can use like;

```java
  CWSdk.getInstance().addCreditCard(getActivity(), ADD_CARD_REQUEST_CODE, (context, cwError) -> { } )
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

`addCreditCard` calls `startActivityForResult` and it starts an activity. Added card from add card screen and result of operation comes back to `activityResult`. Addedd card can get with `data.getParcelableExtra(CWSdk.BUNDLE_CARD_OPERATION_RESULT)` and result can check as `Activity.RESULT_OK` or `Activity.RESULT_CANCELED`      

### Verify Credit Card
```java
CWSdk.getInstance().verifyCard(creditCard, amount, view.getContext(), new CWCallback<CreditCard>() {
            @Override public void onSuccess(CreditCard result) {

            }

            @Override public void onError(CWError error) {

            }
        });
```


* This function takes to Credit Card, verification amount and returns CWCallback.

### clear

Cw Sdk uses observable pattern on card operations. It observes status of operations as Async. **clear()** should use in **onStop** or **onDestroy** (according to integrator's usage) on screens that getting, deleting and verifying cards. For **add** operation, Cw Sdk automatically handles them.



## UI Customization.
Card Wallet SDK supports custom UI for AddCreditCard and VerifyCreditCard functionalities. 


### Add Credit Card Page
In order to customize Add Credit card ui, **cw_add_card_layout** should be added to project. Card Wallet SDK automatically detects if cw_add_card_layout.xml exists and render it to screen. This xml should contain this fields with specified ids.

Scan card feature is optional. In order to not use, btnScanCw should not add to custom **cw_add_card_layout**.


|  View Type   | Description              |id              |
| ------------ | ------------------------ |--------------- |
|  TextInputLayout | Credit Card Number Field |edtPanCw       |
|  TextInputLayout | Name Surname             |edtFullNameCw |
|  TextInputLayout | CVC Number               |edtCvcCw       |
|  TextInputLayout | Expiry Date Field        |edtExpiryCw    |
|  View   | Submit Button            |btnSubmitCw    |
|  View (Optional)  | Scan Card Button         |btnScanCw    |

In order to customize **Theme** of Add Card screen, `AddCardActivity`'s theme should change in Manifest.

```xml
<activity
          android:theme="@style/CustomTheme"
          android:name="com.beamuae.cwsdk.AddCardActivity"/>
``` 

**Note that;** If Theme used with a toolbar for example `Theme.AppCompat.Light.DarkActionBar` and a toolbar component added in custom xml, there will be two toolbar in app. Toolbar should delete from xml or theme should be use with `NoActionBar` theme.

#### Error Messages
In order to customize validation error messages and location of error fields, firstly, text fields should create with specified ids.

|  View Type   | Description              |id              |
| ------------ | ------------------------ |--------------- |
|  TextView | Card number error field|txtPanError|
|  TextView | CVC error field|txtCVCError|
|  TextView | Expire date error field |txtExprError|
|  TextView | Card holder full name error field |txtNameError|

Secondly, a string array should define in **strings.xml** named **cwsdk_error_map** with specified keys. Keys and values are separated by '|' sign.
Items should have same pattern as:  `<item>key|value</item>`

```xml
<string-array name="cwsdk_error_map">
        <item>CARD_EXPIRED_ERROR|YOUR CUSTOM ERROR</item>
        <item>CARD_EXPIRY_ERROR|YOUR CUSTOM ERROR</item>
        <item>CARD_ERROR|YOUR CUSTOM ERROR</item>
        <item>CVV_ERROR|YOUR CUSTOM ERROR</item>
        <item>NAME_ERROR|YOUR CUSTOM ERROR</item>
        <item>SCHEME_ERROR|YOUR CUSTOM ERROR</item>
    </string-array>
```

The description of error messages are as follows:

|  Key   | Description              |
| ------------ | ------------------------ |
|  CARD_EXPIRED_ERROR | Cards expiry date is not in future. |
|  CARD_EXPIRY_ERROR  | Cards expiry date is not in valid format |
|  CARD_ERROR | Card does not pass the Lohan validation |
|  CVV_ERROR  | CVV length is less than 3 digits             |
|  NAME_ERROR | Holder name contains only one word             |
|  SCHEME_ERROR | Card Scheme is not Master Card or VISA             |


#### Example Layout
 
```xml
<?xml version="1.0" encoding="utf-8"?>

<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:support="http://schemas.android.com/apk/res-auto"
  xmlns:tools="http://schemas.android.com/tools"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  >

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
      support:titleTextColor="@color/colorToolbarTextAndIconTint"
      />


    <android.support.design.widget.TextInputLayout
      android:id="@+id/number_layout"
      android:layout_width="0dip"
      android:layout_height="wrap_content"
      android:layout_marginBottom="@dimen/spacing_2x"
      android:layout_marginEnd="@dimen/spacing_2x"
      android:layout_marginStart="@dimen/spacing_2x"
      android:layout_marginTop="@dimen/spacing_2x"
      android:theme="@style/Beam.TextInputLayout"
      support:layout_constraintEnd_toEndOf="parent"
      support:layout_constraintStart_toStartOf="parent"
      support:layout_constraintTop_toBottomOf="@+id/viewToolbar"
      style="@style/Beam.TextInputLayout"
      >

        <android.support.design.widget.TextInputEditText
          android:id="@+id/edtPanCw"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:digits="0,1,2,3,4,5,6,7,8,9, "
          android:hint="@string/label_card_number"
          android:inputType="number"
          android:longClickable="false"
          android:maxLength="19"
          />
    </android.support.design.widget.TextInputLayout>

    <TextView
      android:id="@+id/txtPanError"
      android:layout_width="0dp"
      android:layout_height="wrap_content"
      android:text="PAN ERROR"
      android:textColor="@color/beam_error"
      android:textSize="@dimen/font_xss"
      android:visibility="invisible"
      support:layout_constraintLeft_toLeftOf="@id/number_layout"
      support:layout_constraintRight_toRightOf="@id/number_layout"
      support:layout_constraintTop_toBottomOf="@id/number_layout"
      />


    <android.support.design.widget.TextInputLayout
      android:id="@+id/expiry_layout"
      android:layout_width="0dip"
      android:layout_height="wrap_content"
      android:layout_marginBottom="@dimen/spacing_2x"
      android:layout_marginEnd="@dimen/spacing_2x"
      android:layout_marginTop="@dimen/spacing_2x"
      android:theme="@style/Beam.TextInputLayout"
      support:layout_constraintEnd_toStartOf="@id/cvc_layout"
      support:layout_constraintStart_toStartOf="@id/number_layout"
      support:layout_constraintTop_toBottomOf="@+id/txtPanError"
      style="@style/Beam.TextInputLayout"
      >

        <android.support.design.widget.TextInputEditText
          android:id="@+id/edtExpiryCw"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:digits="0,1,2,3,4,5,6,7,8,9,/"
          android:hint="@string/label_mm_yy"
          android:inputType="number"
          android:longClickable="false"
          android:maxLength="5"
          />
    </android.support.design.widget.TextInputLayout>

    <TextView
      android:id="@+id/txtExprError"
      android:layout_width="0dp"
      android:layout_height="wrap_content"
      android:layout_marginStart="@dimen/spacing_half"
      android:text="EXP ERROR"
      android:textColor="@color/beam_error"
      android:textSize="@dimen/font_xss"
      android:visibility="invisible"
      support:layout_constraintEnd_toEndOf="@id/expiry_layout"
      support:layout_constraintStart_toStartOf="@id/expiry_layout"
      support:layout_constraintTop_toBottomOf="@+id/expiry_layout"
      />


    <android.support.design.widget.TextInputLayout
      android:id="@+id/cvc_layout"
      android:layout_width="0dip"
      android:layout_height="wrap_content"
      android:layout_marginBottom="@dimen/spacing_2x"
      android:layout_marginEnd="@dimen/spacing_2x"
      android:layout_marginStart="@dimen/spacing_2x"
      android:layout_marginTop="@dimen/spacing_2x"
      android:theme="@style/Beam.TextInputLayout"
      support:layout_constraintEnd_toEndOf="parent"
      support:layout_constraintStart_toEndOf="@+id/expiry_layout"
      support:layout_constraintTop_toBottomOf="@+id/txtPanError"
      style="@style/Beam.TextInputLayout"
      >

        <android.support.design.widget.TextInputEditText
          android:id="@+id/edtCvcCw"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:drawableEnd="@drawable/ic_payment_black_24_px"
          android:hint="@string/label_cvc"
          android:inputType="number"
          android:longClickable="false"
          android:maxLength="3"
          />
    </android.support.design.widget.TextInputLayout>


    <TextView
      android:id="@+id/txtCVCError"
      android:layout_width="0dp"
      android:layout_height="wrap_content"
      android:layout_marginStart="@dimen/spacing_half"
      android:text="CVC ERROR"
      android:textColor="@color/beam_error"
      android:textSize="@dimen/font_xss"
      android:visibility="invisible"
      support:layout_constraintEnd_toEndOf="@id/cvc_layout"
      support:layout_constraintStart_toStartOf="@id/cvc_layout"
      support:layout_constraintTop_toBottomOf="@+id/cvc_layout"
      />


    <android.support.design.widget.TextInputLayout
      android:id="@+id/full_name_layout"
      android:layout_width="0dip"
      android:layout_height="wrap_content"
      android:layout_marginBottom="@dimen/spacing_2x"
      android:layout_marginEnd="@dimen/spacing_2x"
      android:layout_marginTop="@dimen/spacing_2x"
      android:theme="@style/Beam.TextInputLayout"
      support:layout_constraintEnd_toEndOf="parent"
      support:layout_constraintStart_toStartOf="@id/number_layout"
      support:layout_constraintTop_toBottomOf="@+id/cvc_layout"
      style="@style/Beam.TextInputLayout"
      >

        <android.support.design.widget.TextInputEditText
          android:id="@+id/edtFullNameCw"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:hint="@string/label_name_on_card"
          android:inputType="textCapWords"
          android:longClickable="false"
          android:maxLines="1"
          />
    </android.support.design.widget.TextInputLayout>


    <TextView
      android:id="@+id/txtNameError"
      android:layout_width="0dp"
      android:layout_height="wrap_content"
      android:layout_marginStart="@dimen/spacing_half"
      android:text="NAME ERROR"
      android:textColor="@color/beam_error"
      android:textSize="@dimen/font_xss"
      android:visibility="invisible"
      support:layout_constraintEnd_toEndOf="@id/full_name_layout"
      support:layout_constraintStart_toStartOf="@id/full_name_layout"
      support:layout_constraintTop_toBottomOf="@+id/full_name_layout"
      />

    <ProgressBar
      android:id="@+id/viewProgress"
      android:layout_width="wrap_content"
      android:layout_height="0dip"
      android:theme="@style/Widget.AppCompat.ProgressBar"
      support:layout_constraintBottom_toBottomOf="@+id/btnSubmitCw"
      support:layout_constraintEnd_toEndOf="@+id/btnSubmitCw"
      support:layout_constraintTop_toTopOf="@+id/btnSubmitCw"
      />


    <Button
      android:id="@+id/btnScanCw"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_gravity="center"
      android:layout_marginBottom="@dimen/spacing_2x"
      android:layout_marginEnd="@dimen/spacing_2x"
      android:layout_marginStart="@dimen/spacing_2x"
      android:layout_marginTop="@dimen/spacing_2x"
      android:text="@string/label_scan"
      support:layout_constraintTop_toBottomOf="@id/full_name_layout"
      support:layout_constraintStart_toStartOf="parent"
      />


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
      support:layout_constraintTop_toBottomOf="@id/full_name_layout"
      />


</android.support.constraint.ConstraintLayout>
```





## Version
* 1.0.343280
