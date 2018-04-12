package ru.uxapps.af.iab;

import android.app.Activity;
import android.content.Intent;

import java.util.List;

public interface Iab {

    interface Callback {

        void onConnected(Iab iab);

        void onDisconnected(Iab iab);

        void onNotAvailable();

        /**
         * @param success whether complete or not
         * @param itemId   item id or null, if can't parse (shouldn't happen, but)
         */
        void onPurchase(boolean success, String itemId);

        void onConsume(boolean success, String itemId);
    }

    /**
     * Directly call this method in onActivityResult
     */
    void onActivityResult(int requestCode, int resultCode, Intent intent);

    boolean isConnected();

    void connect();

    /**
     * @return list of owned skus, or null if not connected
     */
    List<String> getOwnedItems();

    void requestPurchase(Activity activity, String itemId);

    void requestConsume(String itemId);

    void destroy();

}
