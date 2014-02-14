package com.jimulabs.mirrorlib.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lintonye on 2013-07-04.
 */
public class Populator extends MetaNode {

    public enum Type {
        Simple, Populated
    }

    public static class CustomAttribute {
        public final String name;
        public final String value;
        public final ResRef valueRef;
        public final boolean valueIsRef;

        public CustomAttribute(String name, String value) {
            this.name = StringUtils.toUpperCamel(name);
            this.value = value;
            if (value.startsWith("@")) {
                // Can't use short reference form for custom attributes
                this.valueRef = ResRef.parseFullRef(value);
                this.valueIsRef = true;
            } else {
                this.valueRef = ResRef.invalidRef("");
                this.valueIsRef = false;
            }
        }
    }

    private Type mType;
    private ViewRef mViewRef;

    // supported attributes
    private String mText;
    private String mTextColor;
    private ResRef mSrc;
    private String mVisibility;
    private List<PopulatorContainer> mChildren;
    private ResRef mUrl;
    private List<CustomAttribute> mCustomAttrs;

    public Populator(File underlyingFile, String viewId) {
        super(underlyingFile);
        mViewRef = new ViewRef(viewId);
        mChildren = new LinkedList<PopulatorContainer>();
        mCustomAttrs = new ArrayList<CustomAttribute>();
    }

    public static class ViewRef {
        private boolean mContainAndroidPrefix;
        private String mViewId;
        static final Pattern SHORT_REF_REGEX = Pattern.compile("(android-)?(\\w+)");
        private List<String> mParentIds = new LinkedList<String>();

        public ViewRef(String viewId) {
            String[] idParts = viewId.split("\\.");
            for (int i = 0; i < idParts.length; i++) {
                String part = idParts[i];
                Matcher m = SHORT_REF_REGEX.matcher(part);
                if (!m.matches())
                    throw new IllegalArgumentException(String.format("Invalid viewRef:%s, at:%s", viewId, part));

                if (i == idParts.length - 1) {
                    mContainAndroidPrefix = m.group(1) != null;
                    mViewId = m.group(2);
                } else {
                    mParentIds.add(m.group(2));
                }
            }
        }

        public String getViewId() {
            return mViewId;
        }

        public boolean containAndroidPrefix() {
            return mContainAndroidPrefix;
        }


        public List<String> getParentIds() {
            return mParentIds;
        }

        public ResRef toResRef(boolean inAndroidInternalLayout) {
            boolean isAndroidInternal = inAndroidInternalLayout ? true : containAndroidPrefix();
            ResRef idRef = new ResRef(mViewId, ResRef.Type.id, isAndroidInternal);
            return idRef;
        }
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    List<PopulatorContainer> getChildren() {
        return mChildren;
    }

    public static class InvalidPopulatorChildException extends Exception {
        public InvalidPopulatorChildException(String s) {
            super(s);
        }
    }

    public void addChild(PopulatorContainer populatorContainer) {
        mChildren.add(populatorContainer);
    }

    public boolean hasEmbeddedContent() {
        return getEmbeddedContent() != null;
    }

    public boolean hasEmbeddedPages() {
        return !getEmbeddedPages().isEmpty();
    }

    public PopulatorHost getEmbeddedContent() {
        List<PopulatorHost> contentNodes = selectChildren(PopulatorContainer.Type.Content);
        return contentNodes.size() > 0 ? contentNodes.get(0) : null;
    }

    public List<PopulatorHost> getEmbeddedPages() {
        return selectChildren(PopulatorContainer.Type.Page);
    }

    private <E extends PopulatorContainer> List<E> selectChildren(PopulatorContainer.Type type) {
        List<E> results = new ArrayList<E>();
        for (PopulatorContainer pc : mChildren) {
            if (pc.getType() == type) {
                results.add((E) pc);
            }
        }
        return results;
    }

    public List<PopulatorHost> getEmbeddedPopulatorHosts() {
        List<PopulatorHost> phs = new LinkedList<PopulatorHost>();
        for (MetaNode node : mChildren) {
            if (node instanceof PopulatorHost) {
                phs.add((PopulatorHost) node);
            } else if (node instanceof Items) {
                Items items = (Items) node;
                phs.addAll(items.getPopulatorHosts());
            }
        }
        return phs;
    }

    public void setAttribute(String name, String value) {
        if (name.equals("src")) {
            setSrc(value);
        } else if (name.equals("visibility")) {
            setVisibility(value);
        } else if (name.equals("textColor")) {
            setTextColor(value);
        } else if (name.equals("url")) {
            setUrl(value);
        } else {
            addCustomAttribute(name, value);
        }
    }

    private void addCustomAttribute(String name, String value) {
        CustomAttribute attr = new CustomAttribute(name, value);
        mCustomAttrs.add(attr);
    }

    public List<CustomAttribute> getCustomAttributes() {
        return mCustomAttrs;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTextColor() {
        return mTextColor;
    }

    /**
     * Can be a string accepted by Color.parseColor, like "#FF00FF", "blue", or
     * can be a color resource reference, like "@android:color/white".
     *
     * @param color
     */
    public void setTextColor(String color) {
        // Whether this is a resource or a direct colour string will be sorted
        // out when we actually set the colour of the TextView
        mTextColor = color;
    }

    public ResRef getSrc() {
        return mSrc;
    }

    void setSrc(String src) {
        mSrc = ResRef.parseAnyRefOrThrow(src, ResRef.Type.drawable);
    }

    public String getVisibility() {
        return mVisibility;
    }

    public void setVisibility(String visibility) {
        mVisibility = visibility;
    }

    public ResRef getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = ResRef.parseUrlRef(mUrl);
    }

    public ViewRef getViewRef() {
        return mViewRef;
    }

    public void setViewRef(ViewRef viewRef) {
        mViewRef = viewRef;
    }
}
