package ru.uxapps.af;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.provider.MediaStore;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class AfContext {

    public static void requestPermsIfDenied(Activity activity, int requestCode, String... perms) {
        if (!hasPerms(activity, perms)) {
            ActivityCompat.requestPermissions(activity, perms, requestCode);
        }
    }

    public static boolean hasPerms(Context context, String... perms) {
        if (perms == null || Build.VERSION.SDK_INT < 23) return true;
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllPermsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    public static boolean isBillingAllowed(Context context) {
        final String japanCode = "jp";
        if (Build.VERSION.SDK_INT <= 24) {
            return !japanCode.equalsIgnoreCase(context.getResources().getConfiguration().locale.getCountry());
        } else {
            LocaleList locales = context.getResources().getConfiguration().getLocales();
            for (int i = 0; i < locales.size(); i++) {
                if (japanCode.equalsIgnoreCase(locales.get(i).getCountry())) return false;
            }
            return true;
        }
    }

    public static int getColorFromAttr(Context context, int attr) {
        return getDataFromAttr(context, attr);
    }

    public static String getStringFromAttr(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.string.toString();
    }

    public static int getIntFromAttr(Context context, @AttrRes int attr) {
        return getDataFromAttr(context, attr);
    }

    public static int getStyleFromAttr(Context context, @AttrRes int attr) {
        return getDataFromAttr(context, attr);
    }

    private static int getDataFromAttr(Context context, @AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static Bitmap getCircleBitmap(Context context, Bitmap b, int sizeDp) {
        if (b == null) return null;
        RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(context.getResources(), b);
        d.setCircular(true);//very important line!
        return getBitmap(context, d, sizeDp, sizeDp);
    }

    public static Bitmap getCircleBitmap(Context context, @DrawableRes int drawableRes, int sizeDp) {
        return getCircleBitmap(context, getBitmap(context, drawableRes, sizeDp, sizeDp), sizeDp);
    }

    public static Bitmap getBitmap(Context context, Drawable d, int wDp, int hDp) {
        if (d == null) return null;
        Bitmap out = Bitmap.createBitmap(AfContext.dpToPx(context, wDp), AfContext.dpToPx(context, hDp),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return out;
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int drawableRes, int wDp, int hDp) {
        return getBitmap(context, ContextCompat.getDrawable(context, drawableRes), wDp, hDp);
    }

    public static Bitmap getBitmap(Context context, @Nullable String uri, int wDp, int hDp) {
        if (TextUtils.isEmpty(uri)) return null;
        try {
            Bitmap b = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
            return Bitmap.createScaledBitmap(b, wDp, hDp, false);
        } catch (IOException e) {
            return null;
        }
    }

    public static long getAppInstallTime(Context context, long def) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            return def;
        }
    }

    public static boolean isAppInstalled(Context context, String pkg) {
        try {
            context.getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void copyToClipboard(Context context, CharSequence text, @Nullable String toast) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(text, text));
        if (toast != null) Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public static boolean isRtl(Context context) {
        return Build.VERSION.SDK_INT >= 17
                && context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

}
