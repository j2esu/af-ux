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

import java.lang.ref.WeakReference;
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

    private final Context mContext;
    private final Callback mCallback;
    private final String mPublicKey;
    private final int mRequestCode;

    //null when disconnected
    private IInAppBillingService mService;

    public IabImp(Context context, String publicKey, int requestCode, Callback callback) {
        mRequestCode = requestCode;
        mPublicKey = publicKey;
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void connect() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND")
                .setPackage("com.android.vending");
        if (!mContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)) {
            mCallback.onNotAvailable();
        }
    }

    @Override
    public boolean isConnected() {
        return mService != null;
    }

    @Override
    public void destroy() {
        mContext.unbindService(this);
        mService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = IInAppBillingService.Stub.asInterface(service);
        mCallback.onConnected(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
        mCallback.onDisconnected(this);
    }

    @Override
    public void requestPurchase(Activity activity, String itemId) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(IAB_VERSION, mContext.getPackageName(),
                    itemId, VALUE_INAPP, null);
            if (buyIntentBundle != null && buyIntentBundle.getInt(KEY_RESPONSE_CODE) == RESPONSE_CODE_OK) {
                PendingIntent pendingIntent = buyIntentBundle.getParcelable(KEY_BUY_INTENT);
                if (pendingIntent != null) {
                    activity.startIntentSenderForResult(
                            pendingIntent.getIntentSender(), mRequestCode, new Intent(), 0, 0, 0);
                    return;//waiting result from activity
                }
            }
        } catch (RemoteException | IntentSender.SendIntentException e) {
            Log.e(TAG, "request purchase fail: ", e);
        }
        //if not return in success way - fail immediately
        mCallback.onPurchase(false, itemId);
    }

    @Override
    public void requestConsume(String itemId) {
        //start async operation
        new ConsumePurchaseAsync(this).execute(itemId);
    }

    @Override
    public List<String> getOwnedItems() {
        if (!isConnected()) return null;

        List<String> ownedItems = new ArrayList<>();
        try {
            Bundle ownedItemsBundle = mService.getPurchases(IAB_VERSION, mContext.getPackageName(),
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
            Log.e(TAG, "can't get owned items: ", e);
        }
        return ownedItems;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != mRequestCode) return;//not interested in other codes

        boolean success = false;
        String itemId = null;
        if (resultCode == Activity.RESULT_OK && intent != null) {
            String data = intent.getStringExtra(KEY_PURCHASE_DATA);
            if (getResponseCode(intent) == RESPONSE_CODE_OK && data != null) {
                success = true;
                try {
                    JSONObject jo = new JSONObject(data);
                    itemId = jo.getString(KEY_SKU_ID);
                } catch (JSONException e) {
                    Log.e(TAG, "can't parse sku id: ", e);
                }
            }
        }
        mCallback.onPurchase(success, itemId);
    }

    private int getResponseCode(Intent intent) {
        Object code = intent.getExtras() != null ? intent.getExtras().get(KEY_RESPONSE_CODE) : null;
        return code instanceof Number ? ((Number) code).intValue() : RESPONSE_CODE_FAIL;
    }

    private static class ConsumePurchaseAsync extends AsyncTask<String, Void, Object[]> {

        private final WeakReference<IabImp> mOuter;
        private final String mPkg;

        ConsumePurchaseAsync(IabImp outer) {
            mOuter = new WeakReference<>(outer);
            mPkg = outer.mContext.getPackageName();
        }

        @Override
        protected Object[] doInBackground(String... params) {
            if (params.length != 1) throw new RuntimeException("Can only consume 1 item");

            String itemId = params[0];
            int response = RESPONSE_CODE_FAIL;
            IInAppBillingService service = mOuter.get() != null ? mOuter.get().mService : null;
            if (service != null) {
                try {
                    Bundle ownedItemsBundle = service.getPurchases(IAB_VERSION, mPkg,
                            VALUE_INAPP, null);
                    if (ownedItemsBundle != null && ownedItemsBundle.getInt(KEY_RESPONSE_CODE) == RESPONSE_CODE_OK) {
                        List<String> itemsData = ownedItemsBundle.getStringArrayList(KEY_PURCHASE_DATA_LIST);
                        if (itemsData != null) {
                            for (String data : itemsData) {
                                JSONObject jo = new JSONObject(data);
                                //if found appropriate purchase data
                                if (itemId.equals(jo.getString(KEY_SKU_ID))) {
                                    String token = jo.getString(KEY_PURCHASE_TOKEN);
                                    response = service.consumePurchase(IAB_VERSION, mPkg, token);
                                }
                            }
                        }
                    }
                } catch (RemoteException | JSONException e) {
                    Log.e(TAG, "error while consuming item: ", e);
                }
            }
            return new Object[]{response, itemId};
        }

        @Override
        protected void onPostExecute(Object[] data) {
            if (mOuter.get() != null) {
                mOuter.get().mCallback.onConsume((int) data[0] == RESPONSE_CODE_OK, (String) data[1]);
            }
        }

    }

}
