package ru.uxapps.af;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import java.util.Arrays;

import ru.uxapps.af.base.AfConverter;

public class AfStrings {

    public static <T> CharSequence join(Iterable<T> iterable, CharSequence separator,
            AfConverter<T, CharSequence> converter) {
        if (iterable == null) return "";
        if (separator == null) separator = "";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        boolean firstNotNull = true;
        for (T item : iterable) {
            if (item != null) {
                if (!firstNotNull) builder.append(separator);
                builder.append(converter != null ? converter.convert(item) : (
                        item instanceof CharSequence ? (CharSequence) item : item.toString()
                ));
                firstNotNull = false;
            }
        }
        return builder;
    }

    public static <T> CharSequence join(T[] items, CharSequence separator,
            AfConverter<T, CharSequence> converter) {
        return join(Arrays.asList(items), separator, converter);
    }

    public static CharSequence applySpan(CharSequence text, Object what) {
        SpannableString s = new SpannableString(text);
        s.setSpan(what, 0, text.length(), 0);
        return s;
    }

    public static String toLowerCase(String s) {
        if (TextUtils.isEmpty(s)) return s;
        StringBuilder result = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            result.append(Character.isLetter(c) ? Character.toLowerCase(c) : c);
        }
        return result.toString();
    }

    public static boolean isEmptyTrimmed(String s) {
        return s == null || TextUtils.getTrimmedLength(s) == 0;
    }

}
