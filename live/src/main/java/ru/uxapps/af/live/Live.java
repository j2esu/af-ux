package ru.uxapps.af.live;

import ru.uxapps.af.base.AfAction;

public interface Live<T> {

    void onEachActivation(AfAction<T> action);

    void onActivated(AfAction<T> action);

    void ifActive(AfAction<T> action);

    boolean isActive();

}
