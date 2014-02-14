package com.jimulabs.mirrorlib.model;

import java.io.File;

public class Screen extends MetaNode {
    private static final String ATTR_THEME = "theme";
    private static final String ATTR_OVERLAY = "overlay";

    private PopulatorHost mContent;
    private ActionBarModel mActionBarModel;
    private ResRef mTheme;
    private ResRef mOverlay;

    protected Screen(File underlyingFile) {
        super(underlyingFile);
    }

    public void setAttribute(String name, String value) throws MetaNodeFactoryException {
        if (name.equals(ATTR_THEME)) {
            setTheme(value);
        } else if (name.equals(ATTR_OVERLAY)) {
            setOverlay(value);
        } else {
            throw new MetaNodeFactoryException("Unknown attribute: " + name);
        }
    }

    public PopulatorHost getContent() {
        return mContent;
    }

    void setContent(PopulatorHost content) {
        mContent = content;
        if (mContent != null) {
            mContent.setDefaultLayout(new ResRef(getName(), ResRef.Type.layout, false));
        }
    }

    public ActionBarModel getActionBarModel() {
        return mActionBarModel;
    }

    public void setActionBarModel(ActionBarModel actionBarModel) {
        mActionBarModel = actionBarModel;
    }

    // convenience methods for action bar
    public boolean hasActionBarModel() {
        return (mActionBarModel != null);
    }

    public boolean hasActionBarMenu() {
        return (mActionBarModel != null && mActionBarModel.getMenu() != null);
    }

    public ResRef getActionBarMenu() {
        return mActionBarModel.getMenu();
    }

    public ResRef getContentLayout() {
        return getContent().getLayout();
    }

    public ResRef getTheme() {
        return mTheme;
    }

    void setTheme(String theme) {
        mTheme = ResRef.parseResourceRefOrThrow(theme, ResRef.Type.style);
    }

    public ResRef getOverlay() {
        return mOverlay;
    }

    private void setOverlay(String overlay) {
        mOverlay = ResRef.parseResourceRefOrThrow(overlay, ResRef.Type.drawable);
    }

    public boolean hasOverlay() {
        return mOverlay != null;
    }

    public String getName() {
        return getUnderlyingFile().getName().replaceAll("\\.xml$", "");
    }

    @Override
    public String toString() {
        return String.format("%s(layout=%s)", getName(), getContentLayout().name);
    }
}
