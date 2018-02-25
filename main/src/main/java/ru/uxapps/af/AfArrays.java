package ru.uxapps.af;

import android.support.annotation.NonNull;

import java.util.Arrays;

public class AfArrays {

    @SafeVarargs
    public static <T> T[] concat(T[] first, T... second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static int indexOf(@NonNull Object value, Object... arr) {
        for (int i = 0; i < arr.length; i++) {
            if (value.equals(arr[i])) return i;
        }
        return -1;
    }
}
