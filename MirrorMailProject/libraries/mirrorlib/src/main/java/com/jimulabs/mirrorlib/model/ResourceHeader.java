package com.jimulabs.mirrorlib.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by matt on 2013-12-17.
 */
public class ResourceHeader {

    public static final String PROP_PACKAGE_NAME = "package";
    public static final String PROP_APP_THEME = "appTheme";
    public static final String PROP_DEX_STATE = "dexState";
    public static final String PROP_VERSION = "version";
    private static final int DEFAULT_VERSION = 0;

    private final String mPackageName;
    private final int mVersion;
    private final ResRef mAppTheme;
    private final DexState mDexState;

    private ResourceHeader(File headerFile) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(headerFile));

        mPackageName = props.getProperty(PROP_PACKAGE_NAME);
        mVersion = readVersion(props);
        mAppTheme = readAppTheme(props);
        mDexState = readDexState(props);
    }

    public static ResourceHeader fromHeaderFile(File header) throws IOException {
        return new ResourceHeader(header);
    }

    public static ResourceHeader fromModelRoot(File root) throws IOException {
        File header = new File(root, ResourceDirModel.HEADER_FILE_NAME);
        return new ResourceHeader(header);
    }

    public String getPackageName() {
        return mPackageName;
    }

    public int getVersion() {
        return mVersion;
    }

    public ResRef getAppTheme() {
        return mAppTheme;
    }

    public DexState getDexState() {
        return mDexState;
    }

    private static int readVersion(Properties props) {
        if (props.containsKey(PROP_VERSION)) {
            try {
                return Integer.parseInt(props.getProperty(PROP_VERSION));
            } catch (NumberFormatException e) {
                return DEFAULT_VERSION;
            }
        } else {
            return DEFAULT_VERSION;
        }
    }

    private static ResRef readAppTheme(Properties props) {
        if (props.containsKey(PROP_APP_THEME)) {
            String appTheme = props.getProperty(PROP_APP_THEME);
            return ResRef.parseResourceRef(appTheme, ResRef.Type.style);
        } else {
            return ResRef.invalidRef("");
        }
    }

    private static DexState readDexState(Properties props) {
        if (props.containsKey(PROP_DEX_STATE)) {
            try {
                return DexState.valueOf(props.getProperty(PROP_DEX_STATE));
            } catch (IllegalArgumentException e) {
                return DexState.NONE;
            }
        } else {
            return DexState.NONE;
        }
    }
}
