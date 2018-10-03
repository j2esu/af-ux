package ru.uxapps.af.iab;

import android.app.Activity;
import android.content.Intent;

import java.util.List;

public interface Iab {

    int RESULT_OK = 0;
    int RESULT_ERROR = 1;
    int RESULT_USER_CANCEL = 2;
    int RESULT_NO_NETWORK = 3;

    interface Callback {

        void onConnected(Iab iab);

        void onDisconnected(Iab iab);

        void onConnectNotAvailable(Iab iab);

        void onPurchase(int result, String itemId);

        void onConsume(boolean success, String itemId);
    }

    abstract class CallbackAdapter implements Callback {
        @Override
        public void onConnected(Iab iab) {}

        @Override
        public void onDisconnected(Iab iab) {}

        @Override
        public void onConnectNotAvailable(Iab iab) {}

        @Override
        public void onPurchase(int result, String itemId) {}

        @Override
        public void onConsume(boolean success, String itemId) {}
    }

    /**
     * Directly call this method in onActivityResult
     */
    void onActivityResult(int requestCode, int resultCode, Intent intent);

    boolean isConnected();

    boolean isAvailable();

    boolean connect();

    /**
     * @return list of owned skus, or null if cant get
     */
    List<String> getOwnedItems();

    boolean requestPurchase(Activity activity, String itemId);

    boolean requestConsume(String itemId);

    void disconnect();

}
