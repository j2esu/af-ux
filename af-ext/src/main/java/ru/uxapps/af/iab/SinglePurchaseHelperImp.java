package ru.uxapps.af.iab;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class SinglePurchaseHelperImp implements SinglePurchaseHelper {

    public SinglePurchaseHelperImp(FragmentActivity activity, String publicKey, String item) {

        // init iab

        // bind iab

        activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                // unbind iab
            }
        });
    }

    // UXAPPS: congrats
    protected DialogFragment getPurchaseSuccessDialog() {
        return null;
    }

    // UXAPPS: error
    protected DialogFragment getPurchaseFailDialog() {
        return null;
    }

    // UXAPPS: japan & co
    protected DialogFragment getIabNotSupported() {
        return null;
    }

    @Override
    public LiveData<Boolean> isPurchased() {
        return null;// UXAPPS: emit true by default
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // UXAPPS: forward to iab
    }

    @Override
    public void requestPurchase() {

    }
}
