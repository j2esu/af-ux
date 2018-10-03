package ru.uxapps.af.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class TintFrameLayout extends FrameLayout implements TintableBackgroundView {

    private TintHelper mTintHelper;

    public TintFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTintHelper = new TintHelper(this);
        mTintHelper.applyTintFromAttrs(attrs);
    }

    @Override
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        mTintHelper.setSupportBackgroundTintList(tint);
    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return mTintHelper.getSupportBackgroundTintList();
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        mTintHelper.setSupportBackgroundTintMode(tintMode);
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return mTintHelper.getSupportBackgroundTintMode();
    }

}
