package ru.uxapps.af.base;

public interface AfConverter<T, R> {

    R convert(T item);

}
