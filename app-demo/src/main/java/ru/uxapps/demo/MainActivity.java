package ru.uxapps.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.uxapps.af.base.AfAction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        AskPerms askPerms = new AskPerms(findViewById(android.R.id.content));
        askPerms.askEvent().setObserver(this, new AfAction<Void>() {
            @Override
            public void perform(Void item) {
                System.out.println("Ask!");
            }
        });
    }
}
