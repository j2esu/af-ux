package ru.uxapps.af.iab;

import android.arch.lifecycle.LiveData;
import android.content.Intent;

import ru.uxapps.af.annotations.SysBind;

public interface SinglePurchaseHelper {

    LiveData<Boolean> isPurchased();

    @SysBind
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void requestPurchase();

    void consumePurchase();

}
