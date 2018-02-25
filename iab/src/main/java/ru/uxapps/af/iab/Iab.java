package ru.uxapps.af.iab;

import android.content.Intent;

import java.util.List;

public interface Iab {

    interface Callback {

        void onConnected(Iab iab, boolean success);

        /**
         * @param complete whether complete or not
         * @param itemId   item id or null, if can't parse (shouldn't happen, but)
         */
        void onPurchase(boolean complete, String itemId);

        void onConsume(boolean complete, String itemId);
    }

    void onActivityResult(int requestCode, int resultCode, Intent intent);

    void onDestroy();

    boolean isConnected();

    boolean isAvailable();

    List<String> getOwnedItems();

    boolean requestPurchase(String itemId);

    boolean requestConsume(String itemId);

    class Stub {

        public static final Iab INSTANCE = new Iab() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            }

            @Override
            public void onDestroy() {
            }

            @Override
            public boolean isConnected() {
                return false;
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public List<String> getOwnedItems() {
                return null;
            }

            @Override
            public boolean requestPurchase(String itemId) {
                return false;
            }

            @Override
            public boolean requestConsume(String itemId) {
                return false;
            }
        };

    }

}
