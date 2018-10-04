package ru.uxapps.af.ad;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ru.uxapps.af_ad.R;

/**
 * Use view visibility to hide/show ad
 *
 * <strong>Attrs:</strong>
 * <pre>
 * app:adSize - one of {@link Size}(similar to {@link AdSize}) (default - {@link Size#BANNER})
 * app:appId - app ad id (REQUIRED)
 * app:unitId - unit ad id (REQUIRED)
 * </pre>
 *
 */
public class AfBannerAdView extends FrameLayout {

    public enum Size {
        BANNER, SMART, FLUID
    }

    private static final int SCHEDULING_MS = 5000;

    private static boolean sInit;

    private final boolean mDebug;
    private final String mUnitId;
    private final String mBannerSize;

    @Nullable
    private AdView mAdView;
    private boolean mLoaded;

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
            mBannerSize = styledAttrs.getString(R.styleable.AfBannerAdView_bannerSize);
        } finally {
            styledAttrs.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAdView = new AdView(getContext());
        mAdView.setAdUnitId(mUnitId);
        //set size
        if (Size.SMART.name().equals(mBannerSize)) mAdView.setAdSize(AdSize.SMART_BANNER);
        else if (Size.FLUID.name().equals(mBannerSize)) mAdView.setAdSize(AdSize.FLUID);
        else if (Size.BANNER.name().equals(mBannerSize) || mBannerSize == null) {
            mAdView.setAdSize(AdSize.BANNER);
        } else throw new IllegalArgumentException("Unknown banner size: " + mBannerSize);

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
        setAdVisible(isAdEnabled());
    }

    private boolean isAdEnabled() {
        return getVisibility() == VISIBLE;
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

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        setAdVisible(isAdEnabled());
    }

    private void setAdVisible(boolean visible) {
        if (mAdView == null) return;//detached, this method will be called again on attach
        //sync enabled state
        if (!visible) mAdView.setVisibility(GONE);
        else load();
    }

    private void load() {
        if (!isAdEnabled()|| mAdView == null) return;//disabled or detached, shouldn't load
        if (mLoaded) {
            mAdView.setVisibility(VISIBLE);
            return;//already loaded
        }

        //actually load, if not loaded
        mAdView.setVisibility(GONE);
        if (AdUtils.hasConnection(getContext())) {
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
}
