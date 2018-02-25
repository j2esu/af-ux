package ru.uxapps.af.rv;

public interface TypedListData<T> extends ListData<T> {

    int getType(int pos);

    int NO_TYPE = -1;

}
