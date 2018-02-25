package ru.uxapps.af.iab;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IabImp implements Iab, ServiceConnection {

    private static final String TAG = IabImp.class.getName();

    private static final int IAB_VERSION = 3;

    private static final String KEY_RESPONSE_CODE = "RESPONSE_CODE";
    private static final String KEY_BUY_INTENT = "BUY_INTENT";
    private static final String KEY_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    private static final String VALUE_INAPP = "inapp";
    private static final String KEY_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    private static final String KEY_PURCHASE_SIGN_LIST = "INAPP_DATA_SIGNATURE_LIST";
    private static final String KEY_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    private static final String KEY_SKU_ID = "productId";
    private static final String KEY_PURCHASE_TOKEN = "purchaseToken";

    private static final int RESPONSE_CODE_OK = 0;
    private static final int RESPONSE_CODE_FAIL = -1;//my code not specified by api

    private final String mPublicKey;
    private final Activity mActivity;
    private final String mPackage;
    private final Callback mCallback;

    private IInAppBillingService mService;
    private boolean mAvailable = true;

    private final int mRequestCode;

    public IabImp(Activity act, String publicKey, Callback callback, int requestCode) {
        mRequestCode = requestCode;
        mPublicKey = publicKey;
        mActivity = act;
        mPackage = mActivity.getPackageName();
        mCallback = callback;
        startService();
    }

    private void startService() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        if (!mActivity.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)) {
            mAvailable = false;
            mCallback.onConnected(this, false);
        }
    }

    @Override
    public boolean isAvailable() {
        return mAvailable;
    }

    @Override
    public boolean isConnected() {
        return mService != null;
    }

    @Override
    public void onDestroy() {
        mActivity.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = IInAppBillingService.Stub.asInterface(service);
        mCallback.onConnected(this, true);
    }

    @Override
    public boolean requestPurchase(String itemId) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(IAB_VERSION, mPackage, itemId,
                    VALUE_INAPP, null);
            if (buyIntentBundle != null && buyIntentBundle.getInt(KEY_RESPONSE_CODE) == RESPONSE_CODE_OK) {
                PendingIntent pendingIntent = buyIntentBundle.getParcelable(KEY_BUY_INTENT);
                if (pendingIntent != null) {
                    mActivity.startIntentSenderForResult(
                            pendingIntent.getIntentSender(), mRequestCode, new Intent(), 0, 0, 0);
                    return true;
                }
            }
        } catch (RemoteException | IntentSender.SendIntentException e) {
            Log.e(TAG, "requestPurchase: ", e);
        }
        mCallback.onPurchase(false, itemId);
        return false;
    }

    @Override
    public boolean requestConsume(String itemId) {
        new ConsumePurchaseAsync().execute(itemId);
        return true;
    }

    @Override
    public List<String> getOwnedItems() {
        if (!isConnected()) return null;
        List<String> ownedItems = new ArrayList<>();
        try {
            Bundle ownedItemsBundle = mService.getPurchases(IAB_VERSION, mPackage,
                    VALUE_INAPP, null);
            if (ownedItemsBundle.getInt(KEY_RESPONSE_CODE, RESPONSE_CODE_FAIL) == RESPONSE_CODE_OK) {
                List<String> data = ownedItemsBundle.getStringArrayList(KEY_PURCHASE_DATA_LIST);
                List<String> sign = ownedItemsBundle.getStringArrayList(KEY_PURCHASE_SIGN_LIST);
                List<String> items = ownedItemsBundle.getStringArrayList(KEY_PURCHASE_ITEM_LIST);
                if (data != null && sign != null && items != null && items.size() == data.size() &&
                        items.size() == sign.size()) {
                    for (int i = 0; i < items.size(); i++) {
                        if (Security.verifyPurchase(mPublicKey, data.get(i), sign.get(i))) {
                            ownedItems.add(items.get(i));
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "onServiceConnected: ", e);
        }
        return ownedItems;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == mRequestCode) {
            boolean purchaseComplete = false;
            String itemId = null;
            if (resultCode == Activity.RESULT_OK && intent != null) {
                int responseCode = getResponseCode(intent);
                String purchaseData = intent.getStringExtra(KEY_PURCHASE_DATA);
                if (responseCode == RESPONSE_CODE_OK && purchaseData != null) {
                    purchaseComplete = true;
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        itemId = jo.getString(KEY_SKU_ID);
                    } catch (JSONException e) {
                        Log.e(TAG, "onIntentResult: ", e);
                    }
                }
            }
            mCallback.onPurchase(purchaseComplete, itemId);
        }
    }

    private int getResponseCode(Intent i) {
        Object o = i.getExtras().get(KEY_RESPONSE_CODE);
        if (o instanceof Integer) return ((Integer) o);
        else if (o instanceof Long) return (int) ((Long) o).longValue();
        else return RESPONSE_CODE_FAIL;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    private class ConsumePurchaseAsync extends AsyncTask<String, Void, Object[]> {

        @Override
        protected Object[] doInBackground(String... params) {
            if (params.length != 1) throw new RuntimeException("Support only 1 param");
            String itemId = params[0];
            int response = RESPONSE_CODE_FAIL;
            if (mService != null) {
                try {
                    Bundle ownedItemsBundle = mService.getPurchases(IAB_VERSION, mPackage,
                            VALUE_INAPP, null);
                    if (ownedItemsBundle != null && ownedItemsBundle.getInt(KEY_RESPONSE_CODE) == RESPONSE_CODE_OK) {
                        List<String> itemsData = ownedItemsBundle.getStringArrayList(KEY_PURCHASE_DATA_LIST);
                        if (itemsData != null) {
                            for (String data : itemsData) {
                                JSONObject jo = new JSONObject(data);
                                //if found appropriate purchase data
                                if (itemId.equals(jo.getString(KEY_SKU_ID))) {
                                    String token = jo.getString(KEY_PURCHASE_TOKEN);
                                    response = mService.consumePurchase(IAB_VERSION, mPackage, token);
                                }
                            }
                        }
                    }
                } catch (RemoteException | JSONException e) {
                    Log.e(TAG, "doInBackground: ", e);
                }
            }
            return new Object[]{response, itemId};
        }

        @Override
        protected void onPostExecute(Object[] data) {
            mCallback.onConsume((int) data[0] == RESPONSE_CODE_OK, (String) data[1]);
        }

    }

}
