package ru.uxapps.af.live;

import android.arch.lifecycle.LifecycleOwner;

public interface LiveEvent {

    Runnable observe(LifecycleOwner owner, Runnable observer);

}
