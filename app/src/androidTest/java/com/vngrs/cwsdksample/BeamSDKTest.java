package com.vngrs.cwsdksample;

import android.app.Application;
import android.content.Context;


import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import android.os.Handler;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.beamuae.cwsdk.CWSdk;
import com.beamuae.cwsdk.CreditCardDTO;
import com.beamuae.cwsdk.shared.CWCallback;
import com.beamuae.cwsdk.shared.CWError;
import com.beamuae.cwsdk.shared.CWInitializationListener;
import com.beamuae.cwsdk.shared.CWServer;
import com.beamuae.cwsdk.shared.ResultWithMessage;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

@RunWith(AndroidJUnit4.class)
public class BeamSDKTest {


    private static String DEFINED_EMAIL = "kgnkgn@mangal.com";
    private static String DEFINED_PIN = "2659";
    private static String TEST_CARD_NUMBER = "5506900140100305";
    private static String TEST_NEW_MODEL = "Opel";
    private static TestActions testActions;

    private static CreditCardDTO createdFundingSource;
    private BMVehicleModel lastRegisterendCar;

    @BeforeClass
    public static void initializeTest() {
        testActions = new TestActions();

    }

    @Test
    public void AAACreateADefaultUser() throws Exception {
        testActions.startExperience();
        DEFINED_EMAIL = generateRandomEmail();
        testActions.checkUser(DEFINED_EMAIL, null);
        testActions.signUp(DEFINED_PIN, null);

    }

    @Test
    public void useAppContext() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();
        Application application = getApplication();
        assertEquals("com.vngrs.beamtpssdksample", appContext.getPackageName());
    }

    @Test
    public void checkUserExistsForExistingUser() throws Exception {
        testActions.startExperience();
        testActions.checkUser(DEFINED_EMAIL, result -> assertTrue(result.isSuccess()));
    }

    @Test
    public void checkUserExistsForNotExistingUser() throws Exception {
        String generatedEmail = generateRandomEmail();
        testActions.startExperience();
        testActions.checkUser(generatedEmail, result -> assertTrue(!result.isSuccess()));
    }


    @Test
    public void signUpNonExistingUser() throws Exception {
        testActions.startExperience();
        testActions.checkUser(generateRandomEmail(), null);
        testActions.signUp(DEFINED_PIN, result -> assertTrue(result.isSuccess()));

    }

    @Test
    public void signUpExistingUser() throws Exception {
        testActions.startExperience();
        testActions.checkUser(DEFINED_EMAIL, null);
        testActions.signUp(DEFINED_PIN, result -> assertEquals(result.getCwError(), CWError.USER_ALREADY_EXISTS));
    }


    @Test
    public void LoginWithNotSecurePin() throws Exception {
        testActions.startExperience();
        testActions.checkUser(DEFINED_EMAIL, null);
        testActions.loginWithPin("1234", result -> assertEquals(result.getCwError(), CWError.UNSECURE_PIN));
    }

    @Test
    public void LoginWithWrongPin() throws Exception {
        testActions.startExperience();
        testActions.checkUser(DEFINED_EMAIL, null);
        testActions.loginWithPin("2435", result -> assertEquals(result.getCwError(), CWError.WRONG_PIN_CODE));

    }

    @Test
    public void LoginWithCorrectPinNewDevice() throws Exception {

    }


    @Test
    public void LoginWithCorrectPin() throws Exception {
        String email = generateRandomEmail();
        testActions.checkUser(email, null);
        testActions.signUp(DEFINED_PIN, null);
        testActions.loginWithPin(DEFINED_PIN, result -> assertTrue(result.isSuccess()));

    }

    @Test
    public void getFundingSources() throws Exception {
        testActions.startExperience();
        testActions.checkUser(generateRandomEmail(), null);
        testActions.signUp(DEFINED_PIN, null);
        testActions.getFundingSources(result -> assertTrue(result.isSuccess()));
    }

    @Test
    public void addFundingSource() throws Exception {
        testActions.startExperience();
        testActions.checkUser(generateRandomEmail(), null);
        testActions.signUp(DEFINED_PIN, null);
        testActions.addCardData(createTestCardValid(), (fundingSource, beamError) -> {
            assertNotNull(fundingSource);

        });
    }

    @Test
    public void addVehicle() throws Exception {
        testActions.startExperience();
        testActions.checkUser(generateRandomEmail(), null);
        testActions.signUp(DEFINED_PIN, null);
        testActions.addCardData(createTestCardValid(), (fundingSource, beamError) -> createdFundingSource = fundingSource);
        testActions.addVehicle(createVehicle(createdFundingSource), (result, beamError) -> assertNotNull(result));
    }


    @Test
    public void getRegisteredVehicles() throws Exception {
        testActions.startExperience();
        testActions.checkUser(generateRandomEmail(), null);
        testActions.signUp(DEFINED_PIN, null);
        testActions.addCardData(createTestCardValid(), (fundingSource, beamError) -> createdFundingSource = fundingSource);
        testActions.addVehicle(createVehicle(createdFundingSource), (result, beamError) -> assertNotNull(result));
        testActions.getRegistesteredVehicles((result, beamError) -> {
            assertTrue(result.size() > 0);
            assertNull(beamError);
        });

    }

    @Test
    public void updateVehicle() throws Exception {
        testActions.startExperience();
        testActions.checkUser(generateRandomEmail(), null);
        testActions.signUp(DEFINED_PIN, null);
        testActions.addCardData(createTestCardValid(), (fundingSource, beamError) -> createdFundingSource = fundingSource);
        testActions.addVehicle(createVehicle(createdFundingSource), (result, beamError) -> assertNotNull(result));
        testActions.getRegistesteredVehicles((result, beamError) -> {
            assertTrue(result.size() > 0);
            assertNull(beamError);
            lastRegisterendCar = result.get(0);
        });

        testActions.updateVehicle(lastRegisterendCar.setLicencePlate("34 "+generateRandomString(3)+" 121"), (result, beamError) -> {
           // assertEquals(result.getModel(), TEST_NEW_MODEL);
            assertNull(beamError);
        });
    }

    @Test
    public void removeVehicle() throws Exception {
        testActions.startExperience();
        testActions.checkUser(generateRandomEmail(), null);
        testActions.signUp(DEFINED_PIN, null);
        testActions.addCardData(createTestCardValid(), (fundingSource, beamError) -> createdFundingSource = fundingSource);
        testActions.addVehicle(createVehicle(createdFundingSource), (result, beamError) -> assertNotNull(result));
        testActions.getRegistesteredVehicles((result, beamError) -> {
            assertTrue(result.size() > 0);
            assertNull(beamError);
            lastRegisterendCar = result.get(0);
        });

        testActions.removeVehicle(lastRegisterendCar, null);
        testActions.getRegistesteredVehicles((result, beamError) -> {
            assertNull(beamError);
            assertTrue(result.size() == 0);
        });
    }


    private BMVehicleModel createVehicle(CreditCardDTO fundingSourceModel) {
        return new BMVehicleModel()
                .setFundingSource(fundingSourceModel)
                .setLicencePlate("59 "+generateRandomString(3)+" 1905")
                .setMake("Wesley")
                .setModel("Sneijder")
                .setYear("1905");
    }


    private BMCardData createTestCardValid() {

        return new BMCardData()
                .setCardholderFirstName("KAGAN")
                .setCardholderLastName("OZUPEK")
                .setCvc("123")
                .setExpiryMonth(12)
                .setExpiryYear(2020)
                .setPan(TEST_CARD_NUMBER);
    }

    private static void initializeSdk() {
        CWSdk.initialize(new
                BMSdkBuilder(getApplication())
                .setApiKey("VFJBUEkxY2I2ZGZhMmI3YWY0ODQ6UGFzc3dvckQhISExMjM=")
                .setServer(CWServer.STAGING)
        );


    }

    private static void runTestOnUIThread(Runnable testRunnable, CountDownLatch latch) throws Exception {
        new Handler(getApplication().getApplicationContext().getMainLooper())
                .post(testRunnable);
        latch.await();
    }

    private static Application getApplication() {
        return (Application) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }


    private String generateRandomEmail() {
        return generateRandomString(10)+ "@kaganetmangal.com";
    }

    private String generateRandomString(int lenght){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < lenght) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr.toLowerCase();
    }


    private static class TestActions {

        void startExperience() throws Exception {
            initializeSdk();
            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> CWSdk.getInstance().start(new CWInitializationListener() {
                @Override
                public void onSdkInitialized() {
                    assertTrue(true);
                    latch.countDown();
                }

                @Override
                public void onError(CWError error) {
                    assertTrue(false);
                    latch.countDown();
                }
            }), latch);
        }

        void checkUser(String generatedEmail, AcceptenceCriteria criteria) throws Exception {

            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> CWSdk.getInstance().checkEmail(generatedEmail, new CWCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (criteria != null)
                        criteria.onReceived(new ResultWithMessage().setResult(result));
                    latch.countDown();
                }

                @Override
                public void onError(CWError error) {
                    if (criteria != null)
                        criteria.onReceived(new ResultWithMessage().setResult(false).setCwError(error));
                    latch.countDown();
                }
            }), latch);

        }


        void signUp(String pin, AcceptenceCriteria criteria) throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> CWSdk.getInstance().signUpWithPin(pin, new CWCallback<ResultWithMessage>() {
                @Override
                public void onSuccess(ResultWithMessage result) {
                    if (criteria != null) criteria.onReceived(result);
                    latch.countDown();
                }

                @Override
                public void onError(CWError error) {
                    if (criteria != null)
                        criteria.onReceived(new ResultWithMessage().setCwError(error));
                    latch.countDown();
                }
            }), latch);
        }

        void loginWithPin(String pin, AcceptenceCriteria criteria) throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> CWSdk.getInstance().loginWithPin(pin, new CWCallback<ResultWithMessage>() {

                @Override
                public void onSuccess(ResultWithMessage result) {
                    if (criteria != null) criteria.onReceived(result);
                    latch.countDown();
                }

                @Override
                public void onError(CWError error) {
                    if (criteria != null)
                        criteria.onReceived(new ResultWithMessage().setCwError(error));
                    latch.countDown();
                }
            }), latch);
        }




        void addCardData(BMCardData cardData, AcceptenceCriteriaForFundingSource criteria) throws Exception {
            /* CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> {
                FSSdk.getInstance().addCreditCard(cardData, new FSCallback<CreditCard>() {
                    @Override
                    public void onSuccess(CreditCard result) {
                        if (criteria != null) criteria.onReceived(result, null);

                        latch.countDown();
                    }

                    @Override
                    public void onError(FSError error) {
                        if (criteria != null) criteria.onReceived(null, error);

                        latch.countDown();
                    }
                });
            }, latch);
            */
        }


        void getRegistesteredVehicles(AcceptenceCriteriaGeneric<List<BMVehicleModel>> criteria) throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> CWSdk.getInstance().getRegisteredVehicles(new CWCallback<List<BMVehicleModel>>() {
                @Override
                public void onSuccess(List<BMVehicleModel> result) {
                    if (criteria != null) criteria.onReceived(result, null);
                    latch.countDown();
                }

                @Override
                public void onError(CWError error) {
                    if (criteria != null) criteria.onReceived(null, error);
                    latch.countDown();
                }
            }), latch);
        }


        void addVehicle(BMVehicleModel vehicleModel, AcceptenceCriteriaGeneric<BMVehicleModel> criteria) throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> {
                CWSdk.getInstance().addVehicle(vehicleModel, new CWCallback<BMVehicleModel>() {
                    @Override
                    public void onSuccess(BMVehicleModel result) {
                        if (criteria != null) criteria.onReceived(result, null);
                        latch.countDown();
                    }

                    @Override
                    public void onError(CWError error) {
                        if (criteria != null) criteria.onReceived(null, error);
                        latch.countDown();

                    }
                });

            }, latch);
        }

        void updateVehicle(BMVehicleModel vehicleModel, AcceptenceCriteriaGeneric<BMVehicleModel> criteria) throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> CWSdk.getInstance().updateVehicle(vehicleModel, new CWCallback<BMVehicleModel>() {
                @Override
                public void onSuccess(BMVehicleModel result) {
                    if (criteria != null) criteria.onReceived(result, null);
                    latch.countDown();
                }

                @Override
                public void onError(CWError error) {
                    if (criteria != null) criteria.onReceived(null, error);
                    latch.countDown();

                }
            }), latch);
        }

        void removeVehicle(BMVehicleModel vehicleModel, AcceptenceCriteria criteria) throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            runTestOnUIThread(() -> CWSdk.getInstance().removeVehicle(vehicleModel, new CWCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (criteria != null)
                        criteria.onReceived(new ResultWithMessage().setResult(true));
                    latch.countDown();
                }

                @Override
                public void onError(CWError error) {
                    if (criteria != null)
                        criteria.onReceived(new ResultWithMessage().setCwError(error));
                    latch.countDown();

                }
            }), latch);
        }


    }


    public interface AcceptenceCriteria {
        void onReceived(ResultWithMessage result);
    }

    public interface AcceptenceCriteriaForFundingSource {
        void onReceived(CreditCardDTO fundingSource, CWError FSError);
    }

    public interface AcceptenceCriteriaGeneric<T> {
        void onReceived(T result, CWError FSError);
    }

}
