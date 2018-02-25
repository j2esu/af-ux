package ru.uxapps.af.live;

import android.arch.lifecycle.LifecycleOwner;

import ru.uxapps.af.base.AfAction;

public interface LiveFlow<T> {

    Runnable observe(LifecycleOwner owner, AfAction<T> observer);

}
