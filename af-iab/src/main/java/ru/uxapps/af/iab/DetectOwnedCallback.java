package ru.uxapps.af.iab;

import java.util.List;

public class DetectOwnedCallback extends Iab.CallbackAdapter {

    public interface OwnedListener {

        void onDetectOwned(boolean owned);

    }

    private final OwnedListener mOwnedListener;
    private final String mItemId;

    public DetectOwnedCallback(String itemId, OwnedListener listener) {
        mItemId = itemId;
        mOwnedListener = listener;
    }

    @Override
    public void onConnected(Iab iab) {
        List<String> ownedItems = iab.getOwnedItems();
        mOwnedListener.onDetectOwned(ownedItems != null && ownedItems.contains(mItemId));
    }

    @Override
    public void onConnectNotAvailable(Iab iab) {
        mOwnedListener.onDetectOwned(false);
    }

}
