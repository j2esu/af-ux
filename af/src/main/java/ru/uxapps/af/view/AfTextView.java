package ru.uxapps.af.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import ru.uxapps.af.R;


public class AfTextView extends AppCompatTextView {

    public AfTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray styledAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AfTextView, 0, 0);
        try {
            //vector drawables
            int vdStartId = styledAttrs.getResourceId(R.styleable.AfTextView_vectorDrawableStart, -1);
            VectorDrawableCompat vdStart = vdStartId == -1 ? null :
                    VectorDrawableCompat.create(getResources(), vdStartId, getContext().getTheme());
            int vdEndId = styledAttrs.getResourceId(R.styleable.AfTextView_vectorDrawableEnd, -1);
            VectorDrawableCompat vdEnd = vdEndId == -1 ? null :
                    VectorDrawableCompat.create(getResources(), vdEndId, getContext().getTheme());
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(this, vdStart, null, vdEnd, null);
        } finally {
            styledAttrs.recycle();
        }
    }

}
