package com.jimulabs.mirrorlib.model;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lintonye on 2013-07-03.
 */
public class ResourceDirModel {
    private static final String RESOURCE_MAP_NAME = "R.txt";
    private static final String ANDROID_DEX_FILE_NAME = "classes.dex";

    public static final String ANDROID_RESOURCE_FILE_NAME = "resources.ap_";
    public static final String HEADER_FILE_NAME = "header";

    private File mRootDir;
    private DexState mDexState;
    private List<Screen> mScreens;
    private List<InvalidMetaNode> mInvalidNodes;

    private ResourceHeader mHeader;

    /**
     * Matches lines like "int[] styleable CircularImageView { 0x7f010000, 0x7f010001 }"
     * group(1) gives array name (ex. "CircularImageView")
     * group(2) gives array contents (ex. "0x7f010000, 0x7f010001 ")
     */
    private static final Pattern sStyleableRegex =
            Pattern.compile("int\\[\\] styleable (\\S+) \\{ ((0x[0-9a-f]{8}(,)? )+)\\}");
    private Map<String, int[]> mAttrMap;

    public ResourceDirModel(File dir) throws IOException, InvalidIncludeException,
            XmlPullParserException, CircularDependencyException, MetaNodeFactoryException {
        mRootDir = dir;
        verify();

        mHeader = parseHeader();
        mDexState = mHeader.getDexState();
        mAttrMap = parseResourceMap();

        MetaNodeFactory.ParsedNodes nodes = parseMetaNodes();
        mScreens = nodes.getScreenNodes();
        mInvalidNodes = nodes.getInvalidNodes();
    }

    private void verify() throws FileNotFoundException {
        assertFileExist(getHeaderFile());
        assertFileExist(getAndroidResourceFile());
    }

    private static void assertFileExist(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    private HashMap<String, int[]> parseResourceMap() throws IOException {
        // For now only the styleable arrays are parsed and stored, can grab more
        // of the file as needed

        HashMap attrMap = new HashMap<String, int[]>();

        File resourceMapFile = new File(getRootDir(), RESOURCE_MAP_NAME);
        for (String line : FileUtils.readLines(resourceMapFile)) {
            // Format: int[] styleable CustomViewName { 0x7fxxxxxx, ..., 0x7fxxxxxx }
            Matcher matcher = sStyleableRegex.matcher(line);
            if (matcher.matches()) {
                String arrayName = matcher.group(1);
                String[] idStrings = matcher.group(2).split(", ");
                int[] ids = hexStringstoInts(idStrings);
                attrMap.put(arrayName, ids);
            }
        }

        return attrMap;
    }

    // Converts strings like 0x7f010000 to corresponding integers
    private static int[] hexStringstoInts(String[] hexStrings) {
        int[] ints = new int[hexStrings.length];
        for (int i=0; i<hexStrings.length; i++) {
            String justNumber = hexStrings[i].substring(2).trim(); // Remove leading 0x
            ints[i] = Integer.parseInt(justNumber, 16);
        }

        return ints;
    }

    private MetaNodeFactory.ParsedNodes parseMetaNodes() throws XmlPullParserException, IOException,
            MetaNodeFactoryException, InvalidIncludeException, CircularDependencyException {
        MetaNodeFactory factory = createFactory();
        MetaNodeFactory.ParsedNodes parsedNodes = factory.createFromMetaDir(getRootDir());
        return parsedNodes;
    }

    private ResourceHeader parseHeader() throws IOException {
        return ResourceHeader.fromHeaderFile(getHeaderFile());
    }

    public File getRootDir() {
        return mRootDir;
    }

    private File getHeaderFile() {
        return new File(getRootDir(), HEADER_FILE_NAME);
    }

    public String getPackageName() {
        return mHeader.getPackageName();
    }

    public int getVersion() {
        return mHeader.getVersion();
    }

    public ResRef getAppTheme() {
        return mHeader.getAppTheme();
    }

    public DexState getDexState() {
        return mDexState;
    }

    public void setDexState(DexState dexState) {
        mDexState = dexState;
    }

    public File getAndroidResourceFile() {
        return new File(getRootDir(), ANDROID_RESOURCE_FILE_NAME);
    }

    public File getAndroidDexFile() {
        return new File(getRootDir(), ANDROID_DEX_FILE_NAME);
    }

    public List<Screen> getScreens() {
        return mScreens;
    }

    public List<InvalidMetaNode> getInvalidNodes() {
        return mInvalidNodes;
    }

    public Map<String, int[]> getAttrMap() {
        return mAttrMap;
    }

    private MetaNodeFactory createFactory() {
        return new MetaNodeFactory(new XmlImporter());
    }

    public Screen findScreenByUnderlyingFilePath(String path) {
        if (new File(path).exists()) {
            List<Screen> screens = getScreens();
            for (Screen s : screens) {
                if (s.getUnderlyingFile().getAbsolutePath().equals(path)) {
                    return s;
                }
            }
        }
        return null;
    }

    public static ResourceDirModel detectAndCreateFromPath(File pathInsideModelDir) throws IOException, XmlPullParserException, MetaNodeFactoryException, InvalidIncludeException, CircularDependencyException {
        File modelRoot = findModelRoot(pathInsideModelDir);
        return new ResourceDirModel(modelRoot);
    }

    public static File findModelRoot(File pathInsideModelDir) {
        File dir = pathInsideModelDir;
        while (dir != null) {
            if (dir.isDirectory() && new File(dir, HEADER_FILE_NAME).exists()) {
                return dir;
            }
            dir = dir.getParentFile();
        }

        throw new IllegalArgumentException("Path not inside model dir: " +
                pathInsideModelDir.getPath());
    }
}
