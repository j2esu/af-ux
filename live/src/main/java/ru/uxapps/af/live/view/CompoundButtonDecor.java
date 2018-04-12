package ru.uxapps.af.live.view;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

public class CompoundButtonDecor<T extends CompoundButton> {

    private final T mCb;
    @Nullable
    private View.OnClickListener mOnClickListener;

    public CompoundButtonDecor(T cb) {
        mCb = cb;
        mCb.setOnClickListener(v -> {
            mCb.setChecked(!mCb.isChecked());
            mCb.jumpDrawablesToCurrentState();
            if (mOnClickListener != null) mOnClickListener.onClick(mCb);
        });
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public T getView() {
        return mCb;
    }
}
