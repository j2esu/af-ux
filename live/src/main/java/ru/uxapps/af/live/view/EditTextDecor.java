package ru.uxapps.af.live.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import ru.uxapps.af.base.AfProvider;
import ru.uxapps.af.live.LiveFlow;
import ru.uxapps.af.live.MutableLiveFlow;

public class EditTextDecor implements AfProvider<EditText> {

    private final EditText mEt;
    private final MutableLiveFlow<String> mLiveText = new MutableLiveFlow<>();

    private boolean mObserve = true;

    public EditTextDecor(EditText et) {
        mEt = et;
        mEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (mObserve) mLiveText.send(s.toString());
            }
        });
    }

    public void setTextSilent(CharSequence text) {
        if (!TextUtils.equals(mEt.getText(), text)) {
            mObserve = false;
            mEt.getText().replace(0, mEt.length(), text);
            mObserve = true;
        }
    }

    public LiveFlow<String> getLiveInput() {
        return mLiveText;
    }

    @Override
    public EditText get() {
        return mEt;
    }
}
