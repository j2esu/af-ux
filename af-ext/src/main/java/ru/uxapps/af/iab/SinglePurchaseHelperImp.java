package ru.uxapps.af.iab;

import android.app.Activity;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class SinglePurchaseHelperImp implements SinglePurchaseHelper {

    private final MutableLiveData<Boolean> mIsPurchasedLive = new MutableLiveData<>();
    {
        mIsPurchasedLive.setValue(true);
    }

    private final Iab mIab;
    private final Activity mActivity;
    private final String mItemId;

    public SinglePurchaseHelperImp(FragmentActivity activity, String publicKey, int requestCode, String itemId) {
        mActivity = activity;
        mItemId = itemId;
        // init iab
        mIab = new IabImp(activity, publicKey, requestCode, new Iab.Callback.Adapter() {
            @Override
            public void onConnected(Iab iab) {
                mIsPurchasedLive.setValue(iab.getOwnedItems().contains(itemId));

                /*
                uncomment to consume purchase
                */
//                iab.requestConsume(itemId);
            }

            @Override
            public void onPurchase(Iab.Result result, String itemId) {
                switch (result) {
                    case OK:
                        getPurchaseSuccessDialog().show(activity.getSupportFragmentManager(), null);
                        break;
                    case ERROR:
                    case NO_NETWORK:
                    case USER_CANCEL:
                    case NOT_CONNECTED:
                        getPurchaseFailDialog().show(activity.getSupportFragmentManager(), null);
                        break;
                    case NOT_AVAILABLE:
                        getIabNotSupported().show(activity.getSupportFragmentManager(), null);
                }
            }
        });
        // bind iab
        mIab.bind();
        activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                mIab.unbind();
            }
        });
    }

    protected DialogFragment getPurchaseSuccessDialog() {
        return new PurchaseSuccessDialog();
    }

    protected DialogFragment getPurchaseFailDialog() {
        return new PurchaseFailDialog();
    }

    protected DialogFragment getIabNotSupported() {
        return new BillingNotSupportedDialog();
    }

    @Override
    public LiveData<Boolean> isPurchased() {
        return mIsPurchasedLive;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mIab.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void requestPurchase() {
        mIab.requestPurchase(mActivity, mItemId);
    }

}
