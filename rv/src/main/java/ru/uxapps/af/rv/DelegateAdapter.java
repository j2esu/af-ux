package ru.uxapps.af.rv;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class DelegateAdapter<T> extends RecyclerView.Adapter {

    public static class Builder<T> {

        private final SparseArray<Delegate<? extends T>> mDelegates = new SparseArray<>();

        public Builder<T> add(int type, Delegate<? extends T> delegate) {
            mDelegates.put(type, delegate);
            return this;
        }

        public DelegateAdapter<T> build() {
            return new DelegateAdapter<>(mDelegates);
        }
    }

    public interface Delegate<T> {

        RecyclerView.ViewHolder createViewHolder(ViewGroup parent);

        void bindViewHolder(RecyclerView.ViewHolder vh, T data);

        long getId(T data);

    }

    public static class Ids {

        private long mId;
        private final Map<String, Long> mKeyToId = new HashMap<>();

        public long get(String key) {
            Long id = mKeyToId.get(key);
            if (id == null) {
                id = mId++;
                mKeyToId.put(key, id);
            }
            return id;
        }
    }

    private final Ids mIds = new Ids();
    private final SparseArray<Delegate<? extends T>> mDelegates;
    private TypedListData<T> mData = ListDataUtils.empty();

    private DelegateAdapter(SparseArray<Delegate<? extends T>> delegates) {
        mDelegates = delegates;
    }

    public void setData(TypedListData<T> newData) {
        mData = newData;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mDelegates.get(viewType).createViewHolder(parent);
    }

    @Override
    public int getItemViewType(int pos) {
        return mData.getType(pos);
    }

    @Override
    public long getItemId(int pos) {
        if (!hasStableIds()) return RecyclerView.NO_ID;
        int type = mData.getType(pos);
        Delegate delegate = mDelegates.get(type);
        //noinspection unchecked
        return mIds.get(type + "_" + delegate.getId(mData.get(pos)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        Delegate delegate = mDelegates.get(getItemViewType(pos));
        //noinspection unchecked
        delegate.bindViewHolder(holder, mData.get(pos));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
