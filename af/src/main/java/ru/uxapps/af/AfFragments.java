package ru.uxapps.af;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class AfFragments {

    public static <T extends Fragment> T addArgs(@NonNull T fragment, @Nullable Bundle args) {
        if (fragment.getArguments() == null) fragment.setArguments(args);
        else if (args != null) fragment.getArguments().putAll(args);
        return fragment;
    }

}
