package com.jimulabs.mirrorlib.receive;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.jimulabs.mirrorlib.R;
import com.jimulabs.mirrorlib.model.ResourceDirModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by lintonye on 2013-07-02.
 */
public abstract class BaseResourceReceiver implements ResourceReceiver {
    private static final String LOG_TAG = BaseResourceReceiver.class.getSimpleName();
    private final Context mContext;
    private Callback mCallback;

    public BaseResourceReceiver(Context context, Callback callback) {
        mContext = context;
        setReceivingCallback(callback);
    }

    @Override
    public void setReceivingCallback(Callback callback) {
        if (callback == null) {
            throw new IllegalStateException("Callback cannot be null. Programming error?");
        }
        mCallback = callback;
    }

    private HandlerThread mPackProcessingThread;

    protected void asyncProcessReceivedPack(final File zip) {
        if (mPackProcessingThread == null) {
            mPackProcessingThread = new HandlerThread("PackProcessingThread");
        }
        if (!mPackProcessingThread.isAlive()) {
            mPackProcessingThread.start();
        }
        Handler handler = new Handler(mPackProcessingThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                processReceivedPack(zip);
            }
        });
    }

    @Override
    public void start() {
        // empty impl by default
    }

    @Override
    public void stop() {
        if (mPackProcessingThread != null)
            mPackProcessingThread.quit();
    }

    protected void processReceivedPack(File zip) {
        Log.i("BaseResRec", "sending onAllFilesReady");
        mCallback.onAllFilesReady(null);
//        try {
//            Context context = getContext();
//            File tempDir = FileHelper.getCleanTempDir(context);
//            FileHelper.unzip(zip, tempDir);
//            String packageName = ResourceDirModel.packageNameForModelFile(tempDir);
//            File newRootDir = new File(FileHelper.getInternalDataRoot(context), packageName);
//            if (newRootDir.exists()) {
//                FileUtils.deleteDirectory(newRootDir);
//            }
//            FileUtils.moveDirectory(tempDir, newRootDir);
//            ResourceDirModel newRm = new ResourceDirModel(newRootDir);
//            mCallback.onAllFilesReady(newRm);
//        } catch (ResourceDirModel.FailedToLoadResourceModelException e) {
//            Log.e(LOG_TAG, "Error loading resource model", e);
//            mCallback.onError(e.getMessage(), e);
//        } catch (FileNotFoundException e) {
//            Log.e(LOG_TAG, "File not found", e);
//            mCallback.onError(mContext.getString(R.string.cannot_find_resource_bundle), e);
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Failed to open file", e);
//            mCallback.onError(mContext.getString(R.string.cannot_open_resource_bundle), e);
//        }
    }


    protected Callback getCallback() {
        return mCallback;
    }

    public Context getContext() {
        return mContext;
    }
}
