package ru.uxapps.af.live;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.uxapps.af.base.AfAction;


public class BufferedLive<T> implements Live<T> {

    private final List<AfAction<T>> mPendingActions = new ArrayList<>();
    private ItemWrapper<T> mWrapper;

    private final Set<AfAction<T>> mEachActivationObservers = new HashSet<>();

    public void set(LifecycleOwner owner, T item) {
        pullOut();
        mWrapper = new ItemWrapper<>(owner, item,
                wrapper -> {
                    //handle pending actions
                    for (AfAction<T> act : mPendingActions) act.perform(wrapper.item);
                    mPendingActions.clear();
                    //handle each activation observers
                    for (AfAction<T> obs : mEachActivationObservers) obs.perform(wrapper.item);
                },
                wrapper -> pullOut());
    }

    public void pullOut() {
        if (mWrapper != null) {
            mWrapper.cleanup();
            mWrapper = null;
        }
    }

    @Override
    public void onEachActivation(AfAction<T> action) {
        mEachActivationObservers.add(action);
        ifActive(action);//immediately if active
    }

    @Override
    public void onActivated(AfAction<T> action) {
        if (isActive()) action.perform(mWrapper.item);
        else mPendingActions.add(action);
    }

    @Override
    public void ifActive(AfAction<T> action) {
        if (isActive()) action.perform(mWrapper.item);
    }

    @Override
    public boolean isActive() {
        return mWrapper != null && mWrapper.active;
    }

    private static class ItemWrapper<S> implements DefaultLifecycleObserver {

        final S item;
        final LifecycleOwner owner;
        boolean active;

        private final AfAction<ItemWrapper<S>> mOnActive;
        private final AfAction<ItemWrapper<S>> mOnDestroy;

        ItemWrapper(LifecycleOwner owner, S item, AfAction<ItemWrapper<S>> onActive,
                AfAction<ItemWrapper<S>> onDestroy) {
            mOnActive = onActive;
            mOnDestroy = onDestroy;
            this.item = item;
            this.owner = owner;
            this.owner.getLifecycle().addObserver(this);
        }

        void cleanup() {
            owner.getLifecycle().removeObserver(this);
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            active = true;
            mOnActive.perform(this);
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            active = false;
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            mOnDestroy.perform(this);
        }
    }

}
