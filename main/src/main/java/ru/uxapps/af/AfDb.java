package ru.uxapps.af;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class AfDb {

    public static String contains(String textOrCol, String filter) {
        return like(textOrCol, filter, '%', '%');
    }

    public static String startsWith(String textOrCol, String filter) {
        return like(textOrCol, filter, null, '%');
    }

    public static String endsWith(String textOrCol, String filter) {
        return like(textOrCol, filter, '%', null);
    }

    private static String like(String textOrCol, String filter, Character prefix, Character postfix) {
        StringBuilder builder = new StringBuilder(filter.length() + 16);
        if (prefix != null) builder.append(prefix);
        for (int i = 0; i < filter.length(); i++) {
            char c = filter.charAt(i);
            if (c == '\\' || c == '_' || c == '%') builder.append('\\');
            builder.append(c);
        }
        if (postfix != null) builder.append(postfix);
        return textOrCol + " like " + DatabaseUtils.sqlEscapeString(builder.toString()) + " escape '\\'";
    }

    //NOTE: can't do it common, because Cursor implements closable since 16
    public static void tryWith(Cursor cursor, Runnable action) {
        if (cursor == null) return;
        try {
            action.run();
        } finally {
            cursor.close();
        }
    }

    public static void inTransaction(SQLiteDatabase db, Runnable action) {
        db.beginTransaction();
        try {
            action.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
