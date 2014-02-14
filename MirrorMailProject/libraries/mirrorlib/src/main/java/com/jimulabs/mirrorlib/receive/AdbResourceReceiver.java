package com.jimulabs.mirrorlib.receive;

import android.content.Context;
import android.os.FileObserver;
import android.os.StatFs;
import android.util.Log;

import com.jimulabs.mirrorlib.FileConsts;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by lintonye on 2013-06-29.
 */
public class AdbResourceReceiver extends BaseResourceReceiver {
    private static final String LOG_TAG = "AdbResourceReceiver";

    // Minimum amount of free space to not show the "SD card almost full" warning
    private static final float MIN_MB_AVAILABLE = 20.f;

    private boolean mManualRefreshOnly = false;
    private boolean mRunning = false;
    private final FileObserver mObserver;
    private File mAdbPushDir;

    public AdbResourceReceiver(Context context, Callback callback, File adbPushDir) {
        super(context, callback);
        mAdbPushDir = adbPushDir;
        ensurePushDir();
        mObserver = createFileObserver();
        Log.d(LOG_TAG, "AdbPushDir=" + adbPushDir);
    }

    private void ensurePushDir() {
        // Make sure push dir exists
        if (!mAdbPushDir.exists()) {
            try {
                FileUtils.forceMkdir(mAdbPushDir);
            } catch (IOException e) {
                String msg = "Can't mkdir " + mAdbPushDir;
                Log.e(LOG_TAG, msg, e);
                getCallback().onError(msg, e);

                // M TODO notify error can't find sd card
            }
        }

        // Push dir exists, make sure filesystem has enough space
        if (!enoughSpaceOnSdCard()) {
            String msg = "Not enough space on sdcard to receive resources";
            Log.e(LOG_TAG, msg);

            // M TODO notify error sd card almost full
        }
    }

    private boolean enoughSpaceOnSdCard() {
        // See https://trello.com/c/9RARYq14/1047-check-enough-diskspace-crashes-on-webtech-device
        try {
            StatFs stat = new StatFs(mAdbPushDir.getAbsolutePath());
            long availableBlocks = stat.getAvailableBlocks();
            long blockSize = stat.getBlockSize();
            long availableBytes = availableBlocks * blockSize;
            float availableMb = availableBytes / (1024.f * 1024.f);
            return availableMb >= MIN_MB_AVAILABLE;
        } catch (Exception e) {
            Log.e(LOG_TAG, "failed to determine free space.", e);
            return false;
        }
    }

    private FileObserver createFileObserver() {
       /*
          listening on the file does not work for some reason.
         */
        FileObserver observer = new FileObserver(mAdbPushDir.getAbsolutePath(),
                FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String path) {
                /*
                  Weird, sometimes we'll get this one:
                  pack changed: 32768 path=null  (32768 = IGNORED)
                 */
                Log.d(LOG_TAG, "pack changed: " + event + " path=" + path);
                if (path != null) {
                    File zip = new File(mAdbPushDir, path);
                    asyncProcessReceivedPack(zip);
                }
            }
        };
        return observer;
    }

    @Override
    public void setManualRefreshOnly(boolean manualOnly) {
        mManualRefreshOnly = manualOnly;
        if (manualOnly) {
            mObserver.stopWatching();
        } else if (mRunning) {
            mObserver.startWatching();
        }
    }

    @Override
    public void forceRefresh() throws MissingResourcesException {
        File resourcePackage = new File(mAdbPushDir, FileConsts.JIMU_ZIP_FILE_NAME);
        processReceivedPack(resourcePackage);
    }

    @Override
    public void start() {
        super.start();
        mRunning = true;
        if (!mManualRefreshOnly) {
            mObserver.startWatching();
        }
    }

    @Override
    public void stop() {
        super.stop();
        mRunning = false;
        mObserver.stopWatching();
    }

}
