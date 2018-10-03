package ru.uxapps.af.rv;

import java.util.List;

public class CompositeListData<T> implements ListData<T> {

    protected final List<? extends ListData<? extends T>> mDataList;

    private final int[] mBorders;//exclusive border of position that data can handle
    private final int mSize;

    public CompositeListData(List<? extends ListData<? extends T>> dataList) {
        mDataList = dataList;
        mBorders = new int[mDataList.size()];
        //calc borders
        int offset = 0;
        for (int i = 0; i < mDataList.size(); i++) mBorders[i] = offset += mDataList.get(i).size();
        mSize = offset;
    }

    @Override
    public int size() {
        return mSize;
    }

    @Override
    public T get(int pos) {
        int dataInd = getDataIndex(pos);
        return mDataList.get(dataInd).get(getRealPos(dataInd, pos));
    }

    protected int getRealPos(int dataInd, int joinedPos) {
        return joinedPos - mBorders[dataInd] + mDataList.get(dataInd).size();
    }

    protected int getDataIndex(int pos) {
        for (int i = 0; i < mBorders.length; i++) {
            if (pos < mBorders[i]) return i;
        }
        throw new RuntimeException("No data at position " + pos);
    }
}
