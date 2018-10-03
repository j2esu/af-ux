package ru.uxapps.af.rv;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.uxapps.af.base.AfConverter;

public class ListDataUtils {

    private ListDataUtils() {}

    private static final TypedListData EMPTY = new TypedListData() {
        @Override
        public int getType(int pos) {
            return TypedListData.NO_TYPE;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Object get(int pos) {
            return null;
        }
    };

    public static <T> TypedListData<T> empty() {
        //noinspection unchecked
        return (TypedListData<T>) EMPTY;
    }

    public static <T> ListData<T> from(final T item) {
        return new ListData<T>() {
            @Override
            public int size() {
                return 1;
            }

            @Override
            public T get(int pos) {
                return item;
            }
        };
    }

    public static <T> ListData<T> from(final List<? extends T> items) {
        return new ListData<T>() {
            @Override
            public int size() {
                return items.size();
            }

            @Override
            public T get(int pos) {
                return items.get(pos);
            }
        };
    }

    public static <T> ListData<T> from(final Cursor cursor, final AfConverter<Cursor, T> converter) {
        return new ListData<T>() {
            @Override
            public int size() {
                return cursor.getCount();
            }

            @Override
            public T get(int pos) {
                cursor.moveToPosition(pos);
                return converter.convert(cursor);
            }
        };
    }

    public static <T, R> ListData<R> map(final ListData<T> src, final AfConverter<T, R> mapper) {
        return new ListData<R>() {
            @Override
            public int size() {
                return src.size();
            }

            @Override
            public R get(int pos) {
                return mapper.convert(src.get(pos));
            }
        };
    }

    public static <T> Iterable<T> toIterable(final ListData<T> listData) {
        return new Iterable<T>() {
            @NonNull
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    private int currentPos = 0;

                    @Override
                    public boolean hasNext() {
                        return currentPos < listData.size();
                    }

                    @Override
                    public T next() {
                        return listData.get(currentPos++);
                    }
                };
            }
        };
    }

    public static <T> ListData<T> cache(final ListData<T> data) {
        return new ListData<T>() {

            private final SparseArray<T> mCache = new SparseArray<>();

            @Override
            public int size() {
                return data.size();
            }

            @Override
            public T get(int pos) {
                T item = mCache.get(pos);
                if (item == null) {
                    item = data.get(pos);
                    mCache.put(pos, item);
                }
                return item;
            }
        };
    }

    public static <T> List<T> toList(ListData<T> data) {
        ArrayList<T> list = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) list.add(data.get(i));
        return list;
    }

    public static <T> TypedListData<T> from(final ListData<? extends T> data, final int type) {
        return new TypedListData<T>() {
            @Override
            public int getType(int pos) {
                return type;
            }

            @Override
            public int size() {
                return data.size();
            }

            @Override
            public T get(int pos) {
                return data.get(pos);
            }
        };
    }

}
