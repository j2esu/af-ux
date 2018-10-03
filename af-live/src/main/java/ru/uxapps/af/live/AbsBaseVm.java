package ru.uxapps.af.live;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.annotation.NonNull;

public class AbsBaseVm implements LifecycleOwner {

    private final LifecycleRegistry mLifecycleRegistry;

    protected AbsBaseVm() {
        mLifecycleRegistry = new LifecycleRegistry(this);
    }

    protected void onCreated() {
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    protected void onCleared() {
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
