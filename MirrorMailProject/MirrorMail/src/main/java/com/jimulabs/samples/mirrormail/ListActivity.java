package com.jimulabs.samples.mirrormail;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jimulabs.mirrorlib.Refresher;

public class ListActivity extends Activity
        implements MailListFragment.Callbacks {
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Refresher.startActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        setupActionBar();
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
