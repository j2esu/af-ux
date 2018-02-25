package ru.uxapps.af.rv;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.uxapps.af_rv.R;

public abstract class DataVh<T> extends RecyclerView.ViewHolder {

    public DataVh(ViewGroup root, @LayoutRes int layoutRes) {
        super(LayoutInflater.from(root.getContext()).inflate(layoutRes, root, false));
    }

    public final void bind(T data) {
        itemView.setTag(R.id.data_vh_tag, data);
        onBind(data);
    }

    protected String str(@StringRes int res) {
        return itemView.getContext().getString(res);
    }

    protected <V extends View> V find(@IdRes int id) {
        return itemView.findViewById(id);
    }

    protected T data() {
        //noinspection unchecked
        return (T) itemView.getTag(R.id.data_vh_tag);
    }

    protected abstract void onBind(T data);

}
