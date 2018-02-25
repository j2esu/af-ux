package ru.uxapps.af.view;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import ru.uxapps.af.R;

class TintHelper implements TintableBackgroundView {

    private final View mView;
    private ColorStateList mTint;

    public TintHelper(View view) {
        mView = view;
    }

    public void applyTintFromAttrs(AttributeSet attrs) {
        TypedArray styledAttrs = mView.getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.ViewGroup, 0, 0);
        try {
            ColorStateList bgTintColor = styledAttrs.getColorStateList(
                    R.styleable.ViewGroup_viewGroupTint);
            if (bgTintColor != null) {
                ViewCompat.setBackgroundTintList(mView, bgTintColor);
            }
        } finally {
            styledAttrs.recycle();
        }
    }

    @Override
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        mTint = tint;
        applyTint(mTint, mView.getBackground());
    }

    /**
     * Applies tint to drawable
     */
    public static void applyTint(@Nullable ColorStateList tint, @Nullable Drawable drawable) {
        if (drawable == null) return;
        //if drawable not null - continue
        if (tint != null) drawable.setColorFilter(tint.getDefaultColor(), PorterDuff.Mode.SRC_IN);
        else drawable.clearColorFilter();
        //find in source code
        if (Build.VERSION.SDK_INT <= 23) {
            // Pre-v23 there is no guarantee that a state change will invoke an invalidation,
            // so we force it ourselves
            drawable.invalidateSelf();
        }
    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return mTint;
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        throw new UnsupportedOperationException("Not implemented");
    }

}
