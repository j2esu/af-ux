package ru.uxapps.af;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AfIntents {

    private AfIntents() {
    }

    public static class Recognizer {

        public interface SupportedLangCallback {

            void onLangReceived(List<String> lang, List<String> langNames);

        }

        public static Intent start(@Nullable String prompt, @Nullable String lang) {
            return new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    .putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        }

        public static Intent installVoiceRecognition() {
            return openUrl("https://play.google.com/store/apps/details?id=com.google.android.googlequicksearchbox");
        }

        public static ArrayList<String> getRecognitionResults(Intent intent) {
            if (intent.getExtras() != null) {
                //noinspection unchecked
                ArrayList<String> recognizingResults = (ArrayList<String>) intent.getExtras()
                        .get(RecognizerIntent.EXTRA_RESULTS);
                if (recognizingResults != null) return recognizingResults;
            }
            return new ArrayList<>(0);
        }

        public static void getSupportedLang(Context context, final SupportedLangCallback callback) {
            Intent detailsIntent = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
            context.sendOrderedBroadcast(detailsIntent, null, new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle results = getResultExtras(true);
                    List<String> lang = results.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
                    List<String> langNames = results.getStringArrayList("android.speech.extra.SUPPORTED_LANGUAGE_NAMES");
                    if (lang != null && langNames == null) {
                        langNames = lang;
                    }
                    callback.onLangReceived(lang, langNames);
                }
            }, null, Activity.RESULT_OK, null, null);
        }

    }

    public static boolean safeStart(Activity activity, Intent intent, int requestCode) {
        return safeStartInner(activity, intent, true, requestCode);
    }

    private static boolean safeStartInner(Context context, Intent intent, boolean forResult, int requestCode) {
        //check can start
        if (intent == null || intent.resolveActivity(context.getPackageManager()) == null) {
            return false;
        }
        //try start
        try {
            if (forResult) ((Activity) context).startActivityForResult(intent, requestCode);
            else context.startActivity(intent);
            return true;
        } catch (SecurityException ex) {//happens sometimes on fantastic devices
            return false;
        }
    }

    public static boolean safeStart(Context context, Intent intent) {
        return safeStartInner(context, intent, false, -1);
    }

    public static void startOrChooser(Context context, Intent intent) {
        if (!AfIntents.safeStart(context, intent)) {
            context.startActivity(Intent.createChooser(intent, null));
        }
    }

    public static void startOrToast(Context context, Intent intent) {
        if (!AfIntents.safeStart(context, intent)) {
            Toast.makeText(context, R.string.af_no_apps_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    public static Intent openContact(Context context, String lookupKey) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
        Cursor c = context.getContentResolver().query(lookupUri,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (c == null || !c.moveToNext()) return null;
        try {
            return new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_URI, String.valueOf(c.getLong(0))));
        } finally {
            c.close();
        }
    }

    public static Intent addOrInsertContact(String number) {
        return new Intent(Intent.ACTION_INSERT_OR_EDIT)
                .setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE)
                .putExtra(ContactsContract.Intents.Insert.PHONE, number);
    }

    public static Intent openAppPrefs(String packageName) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null));
    }

    public static Intent runApp(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    public static Intent openUrl(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    public static Intent openOnGooglePlay(String packageName) {
        return openUrl("https://play.google.com/store/apps/details?id=" + packageName);
    }

    public static Intent shareApp(String appName, String appPackage) {
        String text = appName + "\n" + "https://play.google.com/store/apps/details?id=" + appPackage;
        return shareText(text, null);
    }

    public static Intent shareText(CharSequence text, @Nullable String chooserTitle) {
        Intent shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text)
                .setType("text/plain");
        return Intent.createChooser(shareIntent, chooserTitle);
    }

    public static Intent pickContact() {
        return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    }

    public static Intent emailTo(String address, String subject, String text) {
        return new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null))
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, text);
    }

    public static Intent dial(String number) {
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    public static Intent call(String number) {
        return new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", number, null));
    }

}
