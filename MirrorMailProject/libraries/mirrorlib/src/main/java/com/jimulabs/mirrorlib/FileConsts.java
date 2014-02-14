package com.jimulabs.mirrorlib;

import java.io.File;

/**
 * Created by lintonye on 2013-07-07.
 */
public class FileConsts {
    public static final String JIMU_ZIP_FILE_NAME = "jimu.zip";
    public static final String DEFAULT_ADB_BASE_PUSH_DIR_PATH = "/sdcard";
    public static final String DEFAULT_ADB_RELATIVE_PUSH_DIR_PATH = "Android/data/com.google.android.apps.iosched/files";
    public static final String DEFAULT_ADB_PUSH_DIR_PATH = "/sdcard/Android/data/com.google.android.apps.iosched/files";
    public static final File DEFAULT_ADB_PUSH_DIR = new File("/sdcard/");
    public static final File DEFAULT_ADB_PUSH_ZIP_FILE = new File(DEFAULT_ADB_PUSH_DIR, JIMU_ZIP_FILE_NAME);
    public static final String METADATA_DIR_NAME = "mirror";
    public static final String ANDROID_MANIFEST = "AndroidManifest.xml";
}
