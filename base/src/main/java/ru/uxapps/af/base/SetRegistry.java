package ru.uxapps.af.base;

import java.util.HashSet;
import java.util.Set;

public class SetRegistry<T> implements Registry<T> {

    private final Set<T> mItems = new HashSet<>();

    @Override
    public void add(T item) {
        mItems.add(item);
    }

    @Override
    public void remove(T item) {
        mItems.remove(item);
    }

    public void forEach(AfAction<T> consumer) {
        for (T obs : mItems) consumer.perform(obs);
    }
}
