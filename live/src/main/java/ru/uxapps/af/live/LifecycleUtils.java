package ru.uxapps.af.live;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import ru.uxapps.af.base.Registry;
import ru.uxapps.af.base.SetRegistry;

public interface LifecycleUtils {

    static void onDestroy(Lifecycle lifecycle, Runnable r) {
        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                r.run();
            }
        });
    }

    static Registry<Runnable> onDestroyRegistry(Lifecycle lifecycle) {
        SetRegistry<Runnable> observers = new SetRegistry<>();
        onDestroy(lifecycle, () -> observers.forEach(Runnable::run));
        return observers;
    }

    static <T> MutableLiveData<T> liveDataWithDefault(T val) {
        MutableLiveData<T> liveData = new MutableLiveData<>();
        liveData.setValue(val);
        return liveData;
    }

    static MutableLiveData<String> liveInput(TextView textView) {
        MutableLiveData<String> live = new MutableLiveData<>();
        live.setValue(textView.getText().toString());
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                live.setValue(s.toString());
            }
        });
        return live;
    }

}
