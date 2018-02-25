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
    public void onConnected(Iab iab, boolean success) {
        mOwnedListener.onDetectOwned(success && iab.getOwnedItems().contains(mItemId));
    }

    @Override
    public void onPurchase(boolean complete, String itemId) {
    }

    @Override
    public void onConsume(boolean complete, String itemId) {
    }

}
