package ru.uxapps.af;


import android.support.v4.util.LongSparseArray;

import java.util.Collection;

public class AfCollections {

    public static <T> void toggle(Collection<T> collection, T item) {
        if (!collection.remove(item)) collection.add(item);
    }

    public static <T> void toggle(LongSparseArray<T> longSparseArray, long key, T val) {
        if (longSparseArray.indexOfKey(key) >= 0) longSparseArray.remove(key);
        else longSparseArray.put(key, val);
    }

}
