package ru.uxapps.af.iab;

import java.util.List;

public class PurchasedWatcher extends Iab.Callback.Adapter {

    public interface Listener {

        void onPurchaseStatusReceived(boolean purchased);

        void onPurchaseSuccess();

        void onPurchaseFail();

    }

    private final Listener mListener;
    private final String mItemId;

    public PurchasedWatcher(String itemId, Listener listener) {
        mItemId = itemId;
        mListener = listener;
    }

    @Override
    public void onConnected(Iab iab) {
        List<String> ownedItems = iab.getOwnedItems();
        mListener.onPurchaseStatusReceived(ownedItems != null && ownedItems.contains(mItemId));
    }

    @Override
    public void onBindNotAvailable(Iab iab) {
        mListener.onPurchaseStatusReceived(false);
    }

}
