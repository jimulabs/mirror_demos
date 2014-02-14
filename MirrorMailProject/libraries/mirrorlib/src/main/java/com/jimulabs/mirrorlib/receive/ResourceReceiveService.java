package com.jimulabs.mirrorlib.receive;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Binder;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.jimulabs.mirrorlib.FileConsts;
import com.jimulabs.mirrorlib.R;
import com.jimulabs.mirrorlib.Refresher;
import com.jimulabs.mirrorlib.model.ResourceDirModel;


public class ResourceReceiveService extends Service implements ResourceReceiver.Callback {

    public static final int NOTIFICATION_RESOURCE_RECEIVED = 1234;
    private ResourceReceiver mResourceReceiver;

    @Override
    public void onAllFilesReady(ResourceDirModel rm) {
        Log.i("ResRecService", "onAllFilesReady");
//        ResourceModelCache.getInstance().setModel(rm);
        // M TODO set model
        sendNotification();
        Log.i("ResRecService", "Sent notification");
        Refresher.refresh();
    }

    private void sendNotification() {
        Log.i("ResRecService", "Sending notification");
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_RESOURCE_RECEIVED);
        Builder builder = new Builder(this);
        int version = 0; // TODO
        int iconResId = getNotificationSmallIconResId(version);
        Notification notification = builder.setSmallIcon(iconResId)
                .setTicker(getString(R.string.version_received, version, "com.jimulabs.samples.MirrorMail")) // TODO
                .setContentTitle(getString(R.string.version, version))
                .setContentText("com.jimulabs.samples.MirrorMail") // TODO
                .build();
        nm.notify(NOTIFICATION_RESOURCE_RECEIVED, notification);
    }

    private int getNotificationSmallIconResId(int version) {
        String name = String.format("ic_stat_%d", version % 10);
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }

    @Override
    public void onError(String message, Throwable e) {
        //TODO properly display error message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSdCardAlmostFull() {
        Toast.makeText(this, getString(R.string.sdcard_almost_full), Toast.LENGTH_LONG).show();
    }

    public class LocalBinder extends Binder {
        public void setManualMode(boolean manualMode) {
            mResourceReceiver.setManualRefreshOnly(manualMode);
        }

        public void forceRefresh() throws ResourceReceiver.MissingResourcesException {
            mResourceReceiver.forceRefresh();
        }
    }

    private FileObserver mObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runForeground();
        mResourceReceiver = createResourceReceiver();
        mResourceReceiver.start();
    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//       // return super.onStartCommand(intent, flags, startId);
//        return Service.START_NOT_STICKY;
//    }

    @Override
    public void onDestroy() {
        mResourceReceiver.stop();
        super.onDestroy();
    }

    private ResourceReceiver createResourceReceiver() {
        //TODO create different ResourceReceiver according to params
        ResourceReceiver receiver = new AdbResourceReceiver(this, this, FileConsts.DEFAULT_ADB_PUSH_DIR);
        return receiver;
    }

    private void runForeground() {
        Builder builder = new Builder(this);
//        Intent notificationIntent = new Intent(this, HookupActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
        Notification notification = builder
//                .setLargeIcon(createLargeIcon())
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
//                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_RESOURCE_RECEIVED, notification);
    }

    private Bitmap createLargeIcon() {
        Bitmap bm = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawARGB(210, 220, 30, 40);
        return bm;
    }

}
