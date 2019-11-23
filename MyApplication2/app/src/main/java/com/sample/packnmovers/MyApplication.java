package com.sample.packnmovers;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

       /* Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .schemaVersion(2)
                .migration(new MyRealMigration())
                .build();
        Realm.setDefaultConfiguration(config);*/

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("torontoride.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);

    }
}