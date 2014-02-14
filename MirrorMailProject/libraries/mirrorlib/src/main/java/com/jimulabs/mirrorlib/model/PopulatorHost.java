package com.jimulabs.mirrorlib.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class PopulatorHost extends PopulatorContainer {
    public static final String ATTR_TITLE = "title";
    public static final String ATTR_ICON = "icon";

    private String mTitle; // for pages only
    private ResRef mIcon; // for pages only
    private List<Populator> mPopulators = new LinkedList<Populator>();
    private ResRef mDefaultLayout;

    protected PopulatorHost(File underlyingFile) {
        super(underlyingFile);
    }

    public static PopulatorHost withType(File underlyingFile, Type type) {
        PopulatorHost populatorHost = new PopulatorHost(underlyingFile);
        populatorHost.mType = type;
        return populatorHost;
    }

    @Override
    public void setAttribute(String name, String value) throws MetaNodeFactoryException {
        if (name.equals(ATTR_TITLE)) {
            setTitle(value);
        } else if (name.equals(ATTR_ICON)) {
            setIcon(value);
        } else {
            super.setAttribute(name, value);
        }
    }

    public void setDefaultLayout(ResRef resRef) {
        mDefaultLayout = resRef;
    }

    @Override
    public void setLayout(String layout) {
        if (layout != null) {
            layout = layout.replaceAll("\\.xml$", "");
            mLayout = parseResRef(layout, ResRef.Type.layout);
        } else
            mLayout = null;
    }

    public void setIcon(String icon) {
        mIcon = parseResRef(icon, ResRef.Type.drawable);
    }

    private ResRef parseResRef(String name, ResRef.Type resType) {
        if (name != null) {
            return ResRef.parseResourceRefOrThrow(name, resType);
        } else {
            return null;
        }
    }


    public ResRef getIcon() {
        return mIcon;
    }

    @Override
    public ResRef getLayout() {
        if (mLayout != null)
            return mLayout;
        else
            return mDefaultLayout;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<Populator> getPopulators() {
        return mPopulators;
    }

    @Override
    protected boolean isCompatibleType(Type external) {
        return super.isCompatibleType(external) || external.equals(Type.Wildcard);
    }

    @Override
    public void includeExternal(PopulatorContainer container) throws InvalidIncludeException {
        if (container instanceof PopulatorHost && isCompatibleType(container.getType())) {
            PopulatorHost external = (PopulatorHost) container;
            MetaNode parent = getParent();
            if (mType.equals(Type.Content) && parent instanceof Screen) {
                Screen screen = (Screen) parent;
                screen.setContent(external);
            } else {
                // two cases to handle, one were the populator host is a child of an items tag
                // and when it is a direct child of a populator
                List<PopulatorContainer> children;
                if (parent instanceof Items)
                    children = ((Items) parent).getItems();
                else
                    children = ((Populator) parent).getChildren();
                int indexOf = children.indexOf(this);
                children.set(indexOf, external);
                copyNameAndAttrs(this, external);
            }
        } else
            throw new InvalidIncludeException("Tried to include incompatible file" + container.getUnderlyingFile());
    }

    public void copyNameAndAttrs(PopulatorHost from, PopulatorHost to) {
        to.setType(from.getType());
        if (from.hasLayout()) to.mLayout = from.getLayout();
        if (from.mTitle != null) to.mTitle = from.getTitle();
        if (from.mIcon != null) to.mIcon = from.getIcon();
    }

    /**
     * TODO: rather than create extra copies of pointers, we can push the logic into the
     * populate commands for a more memory efficient implementation
     */
    @Override
    public void resolveCount() {
        List<PopulatorContainer> children;
        if (getParent() == null) {
            throw new RuntimeException("Parent of count attribute should never be null");
        }
        if (getParent() instanceof Items)
            children = ((Items) getParent()).getItems();
        else
            children = ((Populator) getParent()).getChildren();

        int indexOf = children.indexOf(this);
        if (getCount() == 0) {
            children.remove(indexOf);
        } else {
            for (int i = 1; i < getCount(); i++) {
                children.add(indexOf, this);
            }
        }
    }
}
