package com.jimulabs.mirrorlib.model;

public class MetaNodeFactoryException extends Exception {

    private String mErrorSourceFileName;
    private int mErrorSourceLineNumber;

    public MetaNodeFactoryException(String message) {
        super(message);
    }

    public MetaNodeFactoryException(String message, String errorSourceFileName, int errorSourceLineNumber) {
        super(message);
        this.mErrorSourceFileName = errorSourceFileName;
        this.mErrorSourceLineNumber = errorSourceLineNumber;
    }

    public String getErrorSourceFileName() {
        return mErrorSourceFileName;
    }

    public void setErrorSourceFileName(String fileName) {
        mErrorSourceFileName = fileName;
    }

    public int getErrorSourceLineNumber() {
        return mErrorSourceLineNumber;
    }

    public void setErrorSourceLineNumber(int lineNumber) {
        mErrorSourceLineNumber = lineNumber;
    }
}
