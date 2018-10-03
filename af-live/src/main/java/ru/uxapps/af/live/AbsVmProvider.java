package ru.uxapps.af.live;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.annotation.NonNull;

public abstract class AbsVmProvider<Vm extends AbsBaseVm, Arg> extends AndroidViewModel implements LifecycleOwner {

    private final LifecycleRegistry mLifecycleRegistry;

    protected Vm mVm;

    public AbsVmProvider(@NonNull Application app) {
        super(app);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    public Vm get(Arg arg) {
        if (mVm == null) {
            mVm = createVm(arg);
            if (mVm != null) mVm.onCreated();
        }
        return mVm;
    }

    public boolean isVmCreated() {
        return mVm != null;
    }

    public Vm getCreatedVm() {
        return mVm;
    }

    protected abstract Vm createVm(Arg arg);

    @Override
    protected void onCleared() {
        if (mVm != null) mVm.onCleared();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
