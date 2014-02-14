package com.jimulabs.mirrorlib.model;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lintonye on 2013-06-26.
 */


public abstract class MetaNode {

    private final File mUnderlyingFile;
    private List<Exception> mParseExceptions;
    private List<PopulatorContainer> mHaveIncludes;
    private List<PopulatorContainer> mHaveCounts;

    protected MetaNode(File underlyingFile) {
        mParseExceptions = new LinkedList<Exception>();
        mUnderlyingFile = underlyingFile;
        mHaveIncludes = new LinkedList<PopulatorContainer>();
        mHaveCounts = new LinkedList<PopulatorContainer>();
    }

    public File getUnderlyingFile() {
        return mUnderlyingFile;
    }

    public List<Exception> getParseExceptions() {
        return Collections.unmodifiableList(mParseExceptions);
    }

    public void addException(Exception e) {
        mParseExceptions.add(e);
    }

    public String getErrorMessage() {
        if (hasError()) {
            StringBuilder message = new StringBuilder();
            for (Exception e : getParseExceptions()) {
                if (message.length() > 0) message.append("; ");
                message.append(e.getMessage());
            }
            return message.toString();
        } else
            return null;
    }

    public File resolveRelativePath(String relativePath) {
        File parent = mUnderlyingFile.getParentFile();
        return new File(parent, relativePath);
    }

    public boolean hasError() {
        return mParseExceptions.size() > 0;
    }

    public void addInclude(PopulatorContainer populatorContainer) {
        mHaveIncludes.add(0, populatorContainer);
    }

    public List<PopulatorContainer> getIncludes() {
        return mHaveIncludes;
    }

    public void addCount(PopulatorContainer populatorContainer) {
        mHaveCounts.add(0, populatorContainer);
    }

    public List<PopulatorContainer> getCounts() {
        return mHaveCounts;
    }

    public static abstract class MetaNodeVisitor {

        public void visit(PopulatorHost ph) {
        }

        public void visit(Items items) {
        }

        public void visit(PopulatorContainer populatorContainer) {
        }

        public void visit(Populator populator) {
        }

        public void visit(Screen screen) {
        }

        public void visit(ActionBarModel actionBarModel) {
        }
    }

    public void traverse(MetaNodeVisitor v) {
        if (this instanceof Screen) {
            visitScreen((Screen) this, v);
        } else if (this instanceof Populator) {
            visitPopulator((Populator) this, v);
        } else if (this instanceof Items) {
            visitItems((Items) this, v);
        } else if (this instanceof PopulatorHost) {
            visitPopulatorHost((PopulatorHost) this, v);
        } else
            throwUnknownMetaNodeClass(this);
    }

    private void visitScreen(Screen screen, MetaNodeVisitor v) {
        v.visit(screen);
        v.visit(screen.getActionBarModel());
        visitPopulatorHost(screen.getContent(), v);
    }

    private void visitPopulatorHost(PopulatorHost populatorHost, MetaNodeVisitor v) {
        v.visit(populatorHost);
        PopulatorContainer pc = populatorHost;
        v.visit(pc);
        List<Populator> ps = populatorHost.getPopulators();
        for (Populator p : ps) {
            visitPopulator(p, v);
        }
    }

    private void visitPopulator(Populator p, MetaNodeVisitor v) {
        v.visit(p);
        for (PopulatorContainer pc : p.getChildren()) {
            if (pc instanceof PopulatorHost)
                visitPopulatorHost((PopulatorHost) pc, v);
            else if (pc instanceof Items)
                visitItems((Items) pc, v);
            else
                throwUnknownMetaNodeClass(pc);
        }
    }

    private void throwUnknownMetaNodeClass(MetaNode node) {
        throw new IllegalStateException("Unknown MetaNode subclass:" + node.getClass().getName());
    }

    private void visitItems(Items items, MetaNodeVisitor v) {
        v.visit(items);
        PopulatorContainer pc = items;
        v.visit(pc);
        for (PopulatorHost ph : items.getPopulatorHosts()) {
            visitPopulatorHost(ph, v);
        }
    }
}
