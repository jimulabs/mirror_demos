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
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.jimulabs.mirrorlib.Refresher;
import com.jimulabs.mirrorlib.receive.ResourceReceiveService;

public class ReadActivity extends Activity {

    private Refresher.Connection mConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Refresher.createActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);

        ActionBar actionBar = getActionBar();
        setTitle(R.string.read_activity_title);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            InboxItem item = getIntent().getParcelableExtra(ReadMailFragment.ARG_ITEM);
            args.putParcelable(ReadMailFragment.ARG_ITEM, item);
            ReadMailFragment fragment = new ReadMailFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .add(R.id.read_mail_fragment, fragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Refresher.startActivity(this);
        mConn = new Refresher.Connection();
        bindService(new Intent(this, ResourceReceiveService.class), mConn, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Refresher.stopActivity(this);
        unbindService(mConn);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_menu, menu);
        Refresher.addRefreshAction(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, ListActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        } else if (id == R.id.mirror_refresh) {
            Refresher.refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
