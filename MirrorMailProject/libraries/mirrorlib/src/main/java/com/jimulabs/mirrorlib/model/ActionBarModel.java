package com.jimulabs.mirrorlib.model;

import java.io.File;

/**
 * Created by lintonye on 2013-06-25.
 */
public class ActionBarModel extends MetaNode {


    public static final String ATTR_CUSTOM_VIEW = "customView";
    public static final String ATTR_ICON = "icon";
    public static final String ATTR_LOGO = "logo";
    public static final String ATTR_TITLE = "title";
    public static final String ATTR_SUBTITLE = "subtitle";
    public static final String ATTR_MENU = "menu";
    public static final String ATTR_HOME = "showHome";
    public static final String ATTR_SHOW_TABS_FOR = "showTabsFor";

    private ResRef mCustomView;
    private ResRef mIcon;
    private ResRef mLogo;
    private String mTitle;
    private String mSubtitle;
    private ResRef mMenu;
    private boolean mShowHome;
    private ResRef mShowTabsFor;

    protected ActionBarModel(File underlyingFile) {
        super(underlyingFile);
    }

    public void setAttribute(String name, String value) throws MetaNodeFactoryException {
        if (name.equals(ATTR_CUSTOM_VIEW)) {
            setCustomView(value);
        } else if (name.equals(ATTR_ICON)) {
            setIcon(value);
        } else if (name.equals(ATTR_LOGO)) {
            setLogo(value);
        } else if (name.equals(ATTR_TITLE)) {
            setTitle(value);
        } else if (name.equals(ATTR_SUBTITLE)) {
            setSubtitle(value);
        } else if (name.equals(ATTR_MENU)) {
            setMenu(value);
        } else if (name.equals(ATTR_HOME)) {
            setShowHome(value);
        } else if (ATTR_SHOW_TABS_FOR.equals(name)) {
            setShowTabsFor(value);
        } else {
            throw new MetaNodeFactoryException("Unknown attribute: " + name);
        }
    }

    public ResRef getCustomView() {
        return mCustomView;
    }

    public void setCustomView(String customView) {
        mCustomView = ResRef.parseResourceRefOrThrow(customView, ResRef.Type.layout);
    }

    public void setCustomView(ResRef customView) {
        mCustomView = customView;
    }

    public ResRef getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = ResRef.parseAnyRefOrThrow(icon, ResRef.Type.drawable);
    }

    public void setIcon(ResRef icon) {
        mIcon = icon;
    }

    public ResRef getLogo() {
        return mLogo;
    }

    public void setLogo(String logo) {
        mLogo = ResRef.parseAnyRefOrThrow(logo, ResRef.Type.drawable);
    }

    public void setLogo(ResRef logo) {
        mLogo = logo;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public ResRef getMenu() {
        return mMenu;
    }

    public void setMenu(String menu) {
        mMenu = ResRef.parseResourceRefOrThrow(menu, ResRef.Type.menu);
    }

    public void setShowTabsFor(String pagerResId) {
        mShowTabsFor = ResRef.parseResourceRefOrThrow(pagerResId, ResRef.Type.id);
    }

    public ResRef getShowTabsFor() {
        return mShowTabsFor;
    }

    public void setMenu(ResRef menu) {
        mMenu = menu;
    }

    public boolean isShowHome() {
        return mShowHome;
    }

    public void setShowHome(boolean showHome) {
        mShowHome = showHome;
    }

    public void setShowHome(String showHome) {
        mShowHome = Boolean.parseBoolean(showHome);
    }

}
