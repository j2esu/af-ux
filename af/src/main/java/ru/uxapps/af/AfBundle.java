package ru.uxapps.af;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;

public class AfBundle {

    public static Bundle from(String key, String value) {
        Bundle b = new Bundle();
        b.putString(key, value);
        return b;
    }

    public static Bundle from(String key, long value) {
        Bundle b = new Bundle();
        b.putLong(key, value);
        return b;
    }

    public static Bundle from(String key, boolean value) {
        Bundle b = new Bundle();
        b.putBoolean(key, value);
        return b;
    }

    public static Bundle from(String key, int value) {
        Bundle b = new Bundle();
        b.putInt(key, value);
        return b;
    }

    public static Bundle from(String key, int[] value) {
        Bundle b = new Bundle();
        b.putIntArray(key, value);
        return b;
    }

    public static Bundle from(String key, String[] value) {
        Bundle b = new Bundle();
        b.putStringArray(key, value);
        return b;
    }

    public static Bundle from(String key, ArrayList<String> value) {
        Bundle b = new Bundle();
        b.putStringArrayList(key, value);
        return b;
    }

    public static Bundle from(String key, Serializable value) {
        Bundle b = new Bundle();
        b.putSerializable(key, value);
        return b;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Bundle bundle) {
        return new Builder(bundle);
    }

    public static class Builder {

        private final Bundle mBundle;

        Builder() {
            this(new Bundle());
        }

        Builder(Bundle bundle) {
            mBundle = bundle;
        }

        public Bundle build() {
            return mBundle;
        }

        public Builder put(String key, String value) {
            mBundle.putString(key, value);
            return this;
        }

        public Builder put(String key, boolean value) {
            mBundle.putBoolean(key, value);
            return this;
        }

        public Builder put(String key, int value) {
            mBundle.putInt(key, value);
            return this;
        }

        public Builder put(String key, long value) {
            mBundle.putLong(key, value);
            return this;
        }

        public Builder put(String key, Serializable object) {
            mBundle.putSerializable(key, object);
            return this;
        }

        public Builder put(String key, String[] stringArray) {
            mBundle.putStringArray(key, stringArray);
            return this;
        }

        public Builder put(String key, ArrayList<String> stringArrayList) {
            mBundle.putStringArrayList(key, stringArrayList);
            return this;
        }

        public Builder put(String key, int[] intArray) {
            mBundle.putIntArray(key, intArray);
            return this;
        }

    }

}
