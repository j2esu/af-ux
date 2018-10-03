package ru.uxapps.af.live;

import android.arch.lifecycle.LifecycleOwner;

import ru.uxapps.af.base.AfAction;

public interface LiveEvent<T> {

    void setObserver(LifecycleOwner owner, AfAction<T> observer);

}
