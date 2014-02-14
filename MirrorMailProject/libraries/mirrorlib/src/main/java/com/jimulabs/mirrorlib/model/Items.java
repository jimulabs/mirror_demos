package com.jimulabs.mirrorlib.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Items extends PopulatorContainer {

    private List<PopulatorContainer> mItems;

    public Items(File underlyingFile) {
        super(underlyingFile);
        mItems = new LinkedList<PopulatorContainer>();
        mType = Type.Items;
    }

    public List<PopulatorContainer> getItems() {
        return mItems;
    }

    public void setItems(List<PopulatorContainer> items) {
        mItems = items;
    }

    public List<PopulatorHost> getPopulatorHosts() {
        return (List<PopulatorHost>)(List<?>) mItems;
    }

    @Override
    public void includeExternal(PopulatorContainer container) throws InvalidIncludeException {
        if (container instanceof Items) {
            Items external = (Items) container;
            mItems.addAll(0, external.getItems());
            if (!hasLayout())
                setLayout(external.getLayout());
            for (PopulatorContainer populatorContainer : mItems) {
                PopulatorHost item = (PopulatorHost) populatorContainer;
                if (!item.hasLayout()) {
                    item.setLayout(getLayout());
                }
            }
        } else
            throw new InvalidIncludeException("Tried to include incompatible file" + container.getUnderlyingFile());
    }

    /**
     *     TODO: rather than create extra copies of pointers, we can push the logic into the
     *     populate commands for a more memory efficient implementation
     */
    @Override
    public void resolveCount() {
        if (getCount() == 0)
            mItems.clear();
        else {
            List<PopulatorContainer> itemsCopy = new LinkedList<PopulatorContainer>(mItems);
            for (int i = 1; i < getCount(); i++)
                mItems.addAll(itemsCopy);
        }
    }
}
