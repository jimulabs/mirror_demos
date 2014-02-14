package com.jimulabs.samples.mirrormail;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

import com.jimulabs.mirrorlib.Refresher;
import com.jimulabs.mirrorlib.receive.ResourceReceiveService;

public class ListActivity extends Activity
        implements MailListFragment.Callbacks {
    private ActionBar mActionBar;
    private ServiceConnection mConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Refresher.createActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        setupActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Refresher.startActivity(this);
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        bindService(new Intent(this, ResourceReceiveService.class), mConn, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Refresher.stopActivity(this);
        unbindService(mConn);
    }

    private void setupActionBar() {
        // set styled titled for action bar
//        final SpannableString drawerClose = new SpannableString(getResources().getString(R.string.list_activity_title));
//        drawerClose.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.highlight_color)), 6, 7,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String title = getString(R.string.list_activity_title);
        mActionBar = getActionBar();
        mActionBar.setTitle(title);

        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public Resources getResources() {
        return Refresher.getResources(super.getResources());
    }

    @Override
    public AssetManager getAssets() {
        return Refresher.getAssets(super.getAssets());
    }

    @Override
    public Resources.Theme getTheme() {
        return Refresher.getTheme(super.getTheme());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mirror_refresh) {
            Refresher.refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        Refresher.addRefreshAction(menu);
        return true;
    }

    @Override
    public void onItemSelected(InboxItem item) {
        Intent readIntent = new Intent(this, ReadActivity.class);
        readIntent.putExtra(ReadMailFragment.ARG_ITEM, item);
        startActivity(readIntent);
    }
}
