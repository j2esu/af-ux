package ru.uxapps.af.live.view;

import android.widget.CompoundButton;

import ru.uxapps.af.base.AfProvider;
import ru.uxapps.af.live.LiveEvent;
import ru.uxapps.af.live.MutableLiveEvent;

public class CompoundButtonDecor<T extends CompoundButton> implements AfProvider<T> {

    private final T mCb;
    private final MutableLiveEvent mLiveClicks = new MutableLiveEvent();

    public CompoundButtonDecor(T cb) {
        mCb = cb;
        mCb.setOnClickListener(v -> {
            mCb.setChecked(!mCb.isChecked());
            mLiveClicks.send();
        });
    }

    public LiveEvent getLiveClicks() {
        return mLiveClicks;
    }

    @Override
    public T get() {
        return mCb;
    }
}
