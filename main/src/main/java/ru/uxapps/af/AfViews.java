package ru.uxapps.af;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.uxapps.af.base.AfAction;

public class AfViews {

    public static View inflate(@LayoutRes int res, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
    }

    public static View inflate(@LayoutRes int res, Context context) {
        return LayoutInflater.from(context).inflate(res, null, false);
    }

    public static void afterTextChanged(TextView textView, final AfAction<String> action) {
        action.perform(textView.getText().toString());
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                action.perform(s.toString());
            }
        });
    }

    public static <T extends View> T withOnClick(T view, final Runnable onClick) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.run();
            }
        });
        return view;
    }

    public static void setVisible(@Nullable View v, boolean visible) {
        if (v != null) v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void show(View v) {
        setVisible(v, true);
    }

    public static void hide(View v) {
        setVisible(v, false);
    }

    public static boolean isVisible(View v) {
        return v.getVisibility() == View.VISIBLE;
    }

    public static void toggleVisible(View v) {
        boolean visible = v.getVisibility() == View.VISIBLE;
        setVisible(v, !visible);
    }

}
