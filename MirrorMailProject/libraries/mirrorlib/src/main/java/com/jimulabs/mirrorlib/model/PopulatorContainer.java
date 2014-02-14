package com.jimulabs.mirrorlib.model;

import java.io.File;

public abstract class PopulatorContainer extends MetaNode {

    private static final String ATTR_LAYOUT = "layout";
    private static final String ATTR_COUNT = "count";
    private static final String ATTR_INCLUDE = "include";

    protected MetaNode mParent;
    protected ResRef mLayout;
    protected String mInclude;
    protected Integer mCount;
    protected Type mType;

    protected PopulatorContainer(File underlyingFile) {
        super(underlyingFile);
    }

    public void setAttribute(String name, String value) throws MetaNodeFactoryException {
        if (name.equals(ATTR_LAYOUT)) {
            setLayout(value);
        } else if (name.equals(ATTR_COUNT)) {
            if (mType.equals(Type.Content)) {
                throw new MetaNodeFactoryException("count is not a valid attribute " +
                        "for content tag");
            } else {
                setCount(value);
            }
        } else if (name.equals(ATTR_INCLUDE)) {
            setInclude(value);
        } else {
            throw new MetaNodeFactoryException("Unknown attribute: " + name);
        }
    }

    public boolean hasCount() {
        return mCount != null;
    }

    public Integer getCount() {
        return mCount;
    }

    public void setCount(String count) {
        mCount = Integer.parseInt(count);
    }

    public void setCount(int count) {
        mCount = count;
    }

    public boolean hasInclude() {
        return mInclude != null;
    }

    public String getInclude() {
        return mInclude;
    }

    public void setInclude(String include) {
        mInclude = include;
    }

    public ResRef getLayout() {
        return mLayout;
    }

    public void setLayout(ResRef layout) {
        mLayout = layout;
    }

    public void setLayout(String layout) {
        mLayout = ResRef.parseResourceRefOrThrow(layout, ResRef.Type.layout);
    }

    public boolean hasLayout() {
        return (mLayout != null);
    }

    public MetaNode getParent() {
        return mParent;
    }

    public void setParent(MetaNode parent) {
        mParent = parent;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    protected boolean isCompatibleType(Type external) {
        return mType.equals(external);
    }

    public abstract void includeExternal(PopulatorContainer external) throws InvalidIncludeException;

    public abstract void resolveCount();

    public enum Type {
        Content, Item, Page, Wildcard, Items;

        public boolean canBeSiblingOf(Type type) {
            switch (type) {
                case Content:
                    return false;
                case Page:
                    return this == Page;
                case Item:
                case Items:
                    return this == Item || this == Items;
                default:
                    return true;
            }
        }

        //TODO should use this method instead of constants in MetaNodeFactory
        public String getTagName() {
            String prefix = "_";
            switch (this) {
                case Wildcard:
                    return prefix;
                case Items:
                    return name().toLowerCase();
                default:
                    return prefix + name().toLowerCase();
            }
        }

        public boolean canBeAncestorOf(Type type) {
            switch (this) {
                case Content:
                case Page:
                case Wildcard:
                    return type == Items || type == Item;
                case Items:
                    return type == Item;
                case Item:
                    return false;
                default:
                    return true;
            }
        }
    }
}
