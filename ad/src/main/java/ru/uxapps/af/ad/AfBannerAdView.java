package ru.uxapps.af.ad;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ru.uxapps.af_ad.R;

public class AfBannerAdView extends FrameLayout {

    private static final int SCHEDULING_MS = 5000;

    private static boolean sInit;

    private final boolean mDebug;
    private final String mUnitId;

    @Nullable
    private AdView mAdView;
    private boolean mLoaded;
    private boolean mEnabled;

    @Nullable
    private Runnable mScheduled;

    public AfBannerAdView(@NonNull Context context) {
        this(context, null);
    }

    public AfBannerAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDebug  = ((getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        //parse attrs
        TypedArray styledAttrs = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.AfBannerAdView, 0, 0);
        try {
            //init once
            if (!sInit) {
                String appId = styledAttrs.getString(R.styleable.AfBannerAdView_appId);
                MobileAds.initialize(context.getApplicationContext(), appId);
                sInit = true;
            }
            mUnitId = styledAttrs.getString(R.styleable.AfBannerAdView_unitId);
            mEnabled = styledAttrs.getBoolean(R.styleable.AfBannerAdView_adEnabled, false);
        } finally {
            styledAttrs.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAdView = new AdView(getContext());
        mAdView.setAdUnitId(mUnitId);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mLoaded = true;
            }

            @Override
            public void onAdFailedToLoad(int code) {
                scheduleLoading();
            }
        });
        addView(mAdView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setAdEnabled(mEnabled);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
        if (mScheduled != null) removeCallbacks(mScheduled);
        removeAllViews();
        mLoaded = false;
    }

    public void setAdEnabled(boolean enabled) {
        mEnabled = enabled;
        if (mAdView == null) return;//detached, this method will be called again on attach
        //sync enabled state
        if (!mEnabled) mAdView.setVisibility(GONE);
        else load();
    }

    public void load() {
        if (!mEnabled || mAdView == null) return;//disabled or detached, shouldn't load
        if (mLoaded) {
            mAdView.setVisibility(VISIBLE);
            return;//already loaded
        }

        //actually load, if not loaded
        mAdView.setVisibility(GONE);
        if (hasConnection()) {
            mAdView.setVisibility(VISIBLE);
            mAdView.loadAd(AdUtils.buildRequest(getContext(), mDebug));
        } else {
            scheduleLoading();
        }
    }

    private void scheduleLoading() {
        if (mScheduled != null) return;//wait for previous to complete
        mScheduled = new Runnable() {
            @Override
            public void run() {
                mScheduled = null;
                load();
            }
        };
        postDelayed(mScheduled, SCHEDULING_MS);
    }

    private boolean hasConnection() {
        ConnectivityManager connect = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connect != null ? connect.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
