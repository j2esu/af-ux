package ru.uxapps.af.live;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import ru.uxapps.af.base.AfAction;

public class MutableLiveFlow<T> implements LiveFlow<T> {

    private final Set<AfAction<T>> mObservers = new HashSet<>();

    @Override
    public Runnable observe(LifecycleOwner lifecycleOwner, AfAction<T> observer) {
        DefaultLifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                mObservers.add(observer);
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                mObservers.remove(observer);
            }
        };
        lifecycleOwner.getLifecycle().addObserver(lifecycleObserver);
        return () -> lifecycleOwner.getLifecycle().removeObserver(lifecycleObserver);
    }

    public void send(T data) {
        for (AfAction<T> obs : mObservers) obs.perform(data);
    }

}
