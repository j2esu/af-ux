package ru.uxapps.af.live;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.uxapps.af.base.AfAction;

public class MutableLiveEvent<T> implements LiveEvent<T> {

    @Nullable
    private AfAction<T> mObserver;
    @Nullable
    private LifecycleOwner mLifecycleOwner;

    private List<T> mPending = new ArrayList<>();

    private final LifecycleObserver mLifecycleObserver = new DefaultLifecycleObserver() {
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            for (T data : mPending) sendInternal(data);
            mPending.clear();
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            mLifecycleOwner = null;
            mObserver = null;
        }
    };

    public void send(T data) {
        //if can start immediately
        if (mLifecycleOwner != null
                && mLifecycleOwner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            sendInternal(data);
        } else {
            mPending.add(data);
        }
    }

    private void sendInternal(T data) {
        if (mObserver != null) mObserver.perform(data);
    }

    @Override
    public void setObserver(LifecycleOwner owner, AfAction<T> observer) {
        if (mLifecycleOwner != null) {
            //remove prev helper observer
            mLifecycleOwner.getLifecycle().removeObserver(mLifecycleObserver);
        }
        mLifecycleOwner = owner;
        if (mLifecycleOwner != null) {
            //add helper observer
            mLifecycleOwner.getLifecycle().addObserver(mLifecycleObserver);
        }
        mObserver = observer;
    }
}
