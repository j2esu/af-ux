package ru.uxapps.demo;

import android.view.View;

import ru.uxapps.af.live.LiveEvent;
import ru.uxapps.af.live.MutableLiveEvent;

public class AskPerms {

    private final MutableLiveEvent<Void> mAskEvent = new MutableLiveEvent<>();

    public AskPerms(View root) {
        mAskEvent.send(null);
        root.findViewById(R.id.ask_btn).setOnClickListener(v -> mAskEvent.send(null));
    }

    public LiveEvent<Void> askEvent() {
        return mAskEvent;
    }

}
