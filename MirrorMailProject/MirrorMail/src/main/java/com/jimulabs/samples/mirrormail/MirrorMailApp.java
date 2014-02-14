package com.jimulabs.samples.mirrormail;

import android.app.Application;

import com.jimulabs.mirrorlib.Refresher;

/**
 * Created by matt on 2014-02-13.
 */
public class MirrorMailApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Refresher.init(this);
    }
}
