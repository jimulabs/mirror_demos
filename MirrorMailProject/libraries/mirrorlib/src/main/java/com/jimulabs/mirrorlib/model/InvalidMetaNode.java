package com.jimulabs.mirrorlib.model;

import java.io.File;

// used when type is indeterminable, i.e. in the case of an empty file or invalid root tag
public class InvalidMetaNode extends MetaNode {
    int mErrorLineNumber;

    public InvalidMetaNode(File underlyingFile, int errorLineNumber) {
        super(underlyingFile);
        mErrorLineNumber = errorLineNumber;
    }

    public int getErrorLineNumber() {
        return mErrorLineNumber;
    }

    void setLineNumber(int errorLineNumber) {
        mErrorLineNumber = errorLineNumber;
    }
}
