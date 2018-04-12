package ru.uxapps.af.iab;

public class DetectOwnedCallback implements Iab.Callback {

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
        mOwnedListener.onDetectOwned(iab.getOwnedItems().contains(mItemId));
    }

    @Override
    public void onNotAvailable() {
        mOwnedListener.onDetectOwned(false);
    }

    @Override
    public void onDisconnected(Iab iab) {
    }

    @Override
    public void onPurchase(boolean success, String itemId) {
    }

    @Override
    public void onConsume(boolean success, String itemId) {
    }

}
