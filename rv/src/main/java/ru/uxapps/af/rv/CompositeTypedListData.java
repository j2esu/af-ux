package ru.uxapps.af.rv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeTypedListData<T> extends CompositeListData<T> implements TypedListData<T> {

    public static class Builder<T> {

        public final List<TypedListData<? extends T>> mDataList = new ArrayList<>();

        public Builder<T> addListData(ListData<? extends T> data, int type) {
            mDataList.add(ListDataUtils.from(data, type));
            return this;
        }

        public Builder<T> addItem(T item, int type) {
            addList(Collections.singletonList(item), type);
            return this;
        }

        public Builder<T> addList(List<? extends T> items, int type) {
            addListData(ListDataUtils.from(items), type);
            return this;
        }

        public CompositeTypedListData<T> build() {
            return new CompositeTypedListData<>(mDataList);
        }
    }

    private CompositeTypedListData(List<TypedListData<? extends T>> dataList) {
        super(dataList);
    }

    @Override
    public int getType(int pos) {
        return ((TypedListData) mDataList.get(getDataIndex(pos))).getType(pos);
    }
}
