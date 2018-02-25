package ru.uxapps.af.live;

import android.arch.lifecycle.LifecycleOwner;

import ru.uxapps.af.base.Signal;

public class MutableLiveEvent implements LiveEvent {

    private final MutableLiveFlow<Signal> mLiveFlow = new MutableLiveFlow<>();

    @Override
    public Runnable observe(LifecycleOwner owner, Runnable observer) {
        return mLiveFlow.observe(owner, data -> observer.run());
    }

    public void send() {
        mLiveFlow.send(Signal.get());
    }

}
