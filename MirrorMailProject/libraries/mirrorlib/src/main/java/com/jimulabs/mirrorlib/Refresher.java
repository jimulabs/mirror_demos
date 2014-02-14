package com.jimulabs.mirrorlib;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.jimulabs.mirrorlib.model.ResourceDirModel;
import com.jimulabs.mirrorlib.receive.ResourceReceiveService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matt on 2014-01-28.
 */
public class Refresher {

    private static Refresher sInstance;
    private static Context sAppContext;
    private static boolean sIniting;

    private Resources mRes;
    private AssetManager mAssets;
    private Resources.Theme mTheme;

    private Resources mOrigRes;
    private Resources.Theme mOrigTheme;
    private AssetManager mOrigAssets;

    private Activity mActivity;
    private Map<Activity, ServiceConnection> mConnectionMap;

    private ResourceDirModel mCurrentModel;

    private Refresher(Resources r, Resources.Theme t, AssetManager a) {
        mRes = r;
        mTheme = t;
        mAssets = a;

        mOrigRes = r;
        mOrigTheme = t;
        mOrigAssets = a;

        mConnectionMap = new HashMap<Activity, ServiceConnection>();
    }

    public static void init(Context c) {
        log("Initing with " + c.getClass().getSimpleName());
        sAppContext = c;
        sIniting = true;
        sInstance = new Refresher(c.getResources(), c.getTheme(), c.getAssets());
        sInstance.refreshResources();
        sIniting = false;
    }

    public static void createActivity(Activity a) {
        init(a);
        sInstance.mActivity = a;
    }

    static int count = 0;

    public static void startActivity(Activity a) {
//        sInstance.bindService(a);
        Log.i("Refresher", "Starting activity " + a.getLocalClassName());
        count++;
        Log.i("Refresher", "count = " + sInstance.count);
    }

    private void bindService(Activity a) {
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        a.bindService(new Intent(a, ResourceReceiveService.class), conn, Service.BIND_AUTO_CREATE);
        mConnectionMap.put(a, conn);
    }

    public static void stopActivity(Activity a) {
//        sInstance.unbindService(a);
    }

    private void unbindService(Activity a) {
        if (mConnectionMap.containsKey(a)) {
            ServiceConnection conn = mConnectionMap.remove(a);
            a.unbindService(conn);
        }
    }

    public static void addRefreshAction(Menu m) {
//        MenuInflater inflater = sInstance.mActivity.getMenuInflater();
//        inflater.inflate(R.menu.mirrorlib_menu, m);
    }

    public static boolean isReady() {
        return !sIniting && sInstance != null;
    }

    public static void refresh() {
        Log.i("Refresher", "#refresh: activity null? " + (sInstance.mActivity == null) + " count: " +
                sInstance.count);
        if (sInstance.mActivity != null && sInstance.count > 1) {
            Log.i("Refresher", "Posting restart to UI thread");
            sInstance.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("Refresher", "Restarting activity");
                    sInstance.restartActivity();
                }
            });
        }
    }

    public static Resources getResources() {
        return sInstance.mRes;
    }

    public static Resources getResources(Resources defaultRes) {
        if (isReady()) {
            return sInstance.mRes;
        } else {
            return defaultRes;
        }
    }

    public static AssetManager getAssets(AssetManager defaultAssets) {
        if (isReady()) {
            return sInstance.mAssets;
        } else {
            return defaultAssets;
        }
    }

    public static Resources.Theme getTheme(Resources.Theme defaultTheme) {
        if (isReady()) {
            return sInstance.mTheme;
        } else {
            return defaultTheme;
        }
    }

    private void restartActivity() {
        Intent i = mActivity.getIntent();
        mActivity.startActivity(i);
        mActivity.finish();
        mActivity.overridePendingTransition(0, 0);
    }


    private void refreshResources() {
        try {
            unzipResources();

            File resDir = resDir();
            mCurrentModel = new ResourceDirModel(resDir);
            File resources = mCurrentModel.getAndroidResourceFile();

            mAssets = AssetManager.class.newInstance();
            int cookie = (Integer) AssetManager.class.getMethod("addAssetPath", String.class)
                    .invoke(mAssets, resources.getAbsolutePath());
            log("Cookie for external resources: " + cookie);
            Method getCookieName = AssetManager.class.getMethod("getCookieName", int.class);
            log("Cookie name: " + getCookieName.invoke(mAssets, cookie));

            Resources orig = mOrigRes;
            mRes = new Resources(mAssets, orig.getDisplayMetrics(), orig.getConfiguration());


            mTheme = mRes.newTheme();
            mTheme.setTo(mOrigTheme);

//            dumpThemes();
        } catch (IOException e) {
            // zip not there, ignore
            Log.e("Refresher", "Couldn't find zip file");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Refresher", "Couldn't build ResourceDirModel", e);
            throw new RuntimeException(e);
        }
    }

    private void dumpThemes() {
        log("Original theme: " + id(mOrigTheme));
        mOrigTheme.dump(Log.INFO, "Refresher", ">> ");

        log("New theme: " + id(mTheme));
        mTheme.dump(Log.INFO, "Refresher", ">> ");
    }

    private int id(Resources.Theme t) {
        try {
            Field id = Resources.Theme.class.getDeclaredField("mTheme");
            id.setAccessible(true);
            return (Integer) id.get(t);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void log(String s) {
        Log.i("Refresher", s);
    }

    private File resDir() {
        return sAppContext.getExternalFilesDir("res");
    }

    private File pushDir() {
        //return new File("/sdcard/Android/data/" + packageName() + "/files");
        return new File("/sdcard/");
    }

    private File zipFile() {
        return new File(pushDir(), "jimu.zip");
    }

    private void unzipResources() throws IOException {
        File zip = zipFile();
        File out = resDir();
        if (out.exists()) {
            File newOut = new File(out.getAbsolutePath() + System.currentTimeMillis());
            out.renameTo(newOut);
            FileUtils.deleteDirectory(newOut);
        }

        out.mkdirs();
        FileHelper.unzip(zip, out);
    }

    static ResourceDirModel currentModel() {
        return sInstance.mCurrentModel;
    }

    static String packageName() {
        return sAppContext.getPackageName();
    }
}
