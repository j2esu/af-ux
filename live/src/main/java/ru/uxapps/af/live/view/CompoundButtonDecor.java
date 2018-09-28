package ru.uxapps.af.live.view;

import android.widget.CompoundButton;

import ru.uxapps.af.base.AfProvider;
import ru.uxapps.af.live.LiveEvent;
import ru.uxapps.af.live.MutableLiveEvent;

public class CompoundButtonDecor<T extends CompoundButton> implements AfProvider<T> {

    private final T mCb;
    private final MutableLiveEvent<Void> mLiveClicks = new MutableLiveEvent<>();

    public CompoundButtonDecor(T cb) {
        mCb = cb;
        mCb.setOnClickListener(v -> {
            mCb.setChecked(!mCb.isChecked());
            mCb.jumpDrawablesToCurrentState();
            mLiveClicks.send(null);
        });
    }

    public LiveEvent<Void> clicks() {
        return mLiveClicks;
    }

    @Override
    public T get() {
        return mCb;
    }
}
