package ru.uxapps.af.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import ru.uxapps.af.R;


public class AfImageView extends AppCompatImageView {

    public AfImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fixTintBug();
        TypedArray styledAttrs = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.AfImageView, 0, 0);
        try {
            int bgRes = styledAttrs.getResourceId(R.styleable.AfImageView_vectorBackground, -1);
            if (bgRes != -1) {
                if (Build.VERSION.SDK_INT < 21) {
                    setBackgroundDrawable(VectorDrawableCompat.create(getResources(), bgRes, getContext().getTheme()));
                } else {
                    setBackgroundResource(bgRes);// TODO: 20.02.2018 need test
                }
            }
        } finally {
            styledAttrs.recycle();
        }
    }

    private void fixTintBug() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList imageTintList = getImageTintList();
            if (imageTintList != null) {
                setColorFilter(imageTintList.getDefaultColor(), PorterDuff.Mode.SRC_IN);
            }
        }
    }

}
