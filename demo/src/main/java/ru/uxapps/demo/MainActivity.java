package ru.uxapps.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.uxapps.af.iab.SinglePurchaseHelper;
import ru.uxapps.af.iab.SinglePurchaseHelperImp;


public class MainActivity extends AppCompatActivity {

    private SinglePurchaseHelper mPurchaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        //SinglePurchaseHelper test
        mPurchaseHelper = new SinglePurchaseHelperImp(this,"fakeKey", 1, "fakeId");
        findViewById(R.id.btn).setOnClickListener(v -> mPurchaseHelper.requestPurchase());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPurchaseHelper.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}