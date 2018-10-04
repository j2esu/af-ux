package ru.uxapps.af.iab;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

/**
 * Class for working with in-app billing service. For integration you should forward
 * onActivityResult calls into {@link Iab#onActivityResult(int, int, Intent)}.
 * To start work with iab call {@link Iab#bind()}. Then, after receiving {@link Callback#onConnected(Iab)}
 * you can use iab methods.
 * Iab has 2 main sates - available and connected. Available iab can be connected, and connected
 * iab is ready to perform work
 */
public interface Iab {

    enum Result {
        OK,
        ERROR,
        USER_CANCEL,
        NO_NETWORK,
        NOT_AVAILABLE,
        NOT_CONNECTED
    }

    /**
     * Receives all iab events. You can also use {@link Adapter}
     */
    interface Callback {

        /**
         * Called when iab becomes connected
         * @param iab event source
         *
         * @see android.content.ServiceConnection#onServiceConnected(ComponentName, IBinder)
         */
        void onConnected(Iab iab);

        /**
         * Called when iab becomes disconnected
         * @param iab event source
         *
         * @see android.content.ServiceConnection#onServiceDisconnected(ComponentName)
         */
        void onDisconnected(Iab iab);

        /**
         * Called in response to {@link Iab#bind()} to indicate that connection not
         * available on device and other callbacks will never be called
         * @param iab event source
         */
        void onBindNotAvailable(Iab iab);

        /**
         * Called in response to {@link Iab#requestPurchase(Activity, String)}
         * @param result purchase result
         * @param itemId requested item
         */
        void onPurchase(Result result, String itemId);

        /**
         * Called in response to {@link Iab#requestConsume(String)}
         * @param success whether consumed successfully
         * @param itemId requested item
         */
        void onConsume(boolean success, String itemId);

        /**
         * No-op adapter for {@link Callback}. Extend it and override only methods you need
         */
        abstract class Adapter implements Callback {
            @Override
            public void onConnected(Iab iab) {}

            @Override
            public void onDisconnected(Iab iab) {}

            @Override
            public void onBindNotAvailable(Iab iab) {}

            @Override
            public void onPurchase(Result result, String itemId) {}

            @Override
            public void onConsume(boolean success, String itemId) {}
        }

    }

    /**
     * <strong>INTEGRATION METHOD</strong>
     * <br/>
     * You should directly call this method in onActivityResult
     */
    void onActivityResult(int requestCode, int resultCode, Intent intent);

    /**
     * Bind service. All work with iab should be started by calling this method
     * @return true if iab available and should expect {@link Callback#onConnected(Iab)} call,
     * otherwise {@link Callback#onBindNotAvailable(Iab)} will be called
     */
    boolean bind();

    /**
     * Unbind service. Should be used in {@link Activity#onDestroy()} for release resources
     */
    void unbind();

    /**
     * @return bind status
     */
    boolean isBind();

    /**
     * @return list of owned skus, or null if cant get (not available, disconnected or internal error)
     * @throws IllegalStateException if not bind
     */
    List<String> getOwnedItems();

    /**
     * Start purchase ui flow. Result will be received via {@link Callback#onPurchase(Result, String)}
     * @param activity activity to host billing interaction & ui
     * @param itemId item to purchase
     * @throws IllegalStateException if not bind
     */
    void requestPurchase(Activity activity, String itemId);

    /**
     * Request consuming item. Works in background thread, status will be received via
     * {@link Callback#onConsume(boolean, String)}
     * @param itemId item to consume
     * @throws IllegalStateException if not bind
     */
    void requestConsume(String itemId);

    /**
     * @return whether iab is available and connected. Can use iab methods
     * @throws IllegalStateException if not bind
     */
    boolean isConnected();

    /**
     * @return whether iab service available on device
     * @throws IllegalStateException if not bind
     */
    boolean isAvailable();

}
