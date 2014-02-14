package com.jimulabs.samples.mirrormail;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jimulabs.mirrorlib.Refresher;

public class ReadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Refresher.startActivity(this);
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
            return true;
        } else if (id == R.id.mirror_refresh) {
            Refresher.refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
