package com.jimulabs.mirrorlib.model;

import com.jimulabs.mirrorlib.model.PopulatorContainer.Type;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MetaNodeFactory {

    // Error messages
    public static final String ROOT_TAG_ERROR = "Unsupported root tag";
    // XML tags
    private static final String SCREEN_TAG = "screen";
    private static final String ITEMS_TAG = "items";
    public static final String ACTIONBAR_TAG = "actionbar";
    // PopulatorHost XML tags
    private static final String CONTENT_TAG = "_content";
    private static final String ITEM_TAG = "_item";
    private static final String PAGE_TAG = "_page";
    private static final String WILDCARD_TAG = "_";
    private static final Map<String, Type> POPULATOR_HOST_TYPE_MAP = createMap();
    private final Importer mImporter;
    private boolean mHasCount;
    private boolean mHasInclude;
    private LinkedList<MetaNode> mHaveCounts;
    private LinkedList<MetaNode> mHaveIncludes;
    // used to match include references to correct external file
    private Map<String, PopulatorContainer> mIncludeMap;
    private Stack<ResRef> mLayoutStack;
    private MetaNode mRootNode;

    public MetaNodeFactory(Importer importer) {
        mImporter = importer;
        mHaveCounts = new LinkedList<MetaNode>();
        mHaveIncludes = new LinkedList<MetaNode>();
        mIncludeMap = new HashMap<String, PopulatorContainer>();
        mLayoutStack = new Stack<ResRef>();
    }

    private static Map<String, Type> createMap() {
        Map<String, Type> result = new HashMap<String, Type>();
        result.put(CONTENT_TAG, Type.Content);
        result.put(ITEM_TAG, Type.Item);
        result.put(PAGE_TAG, Type.Page);
        result.put(WILDCARD_TAG, Type.Wildcard);
        return Collections.unmodifiableMap(result);
    }

    public MetaNode createFrom(File file) {
        /*
            The XmlPullParser on Android doesn't throw any error for empty files.
            Make sure not to return a null from this method.
         */

        // root node has list of references to all nodes parsed from a file with attributes
        // that need be processed after all files are parsed, e.g. count and include
        mRootNode = null;
        // set to true if any element in the file has a count attribute set
        mHasCount = false;
        // set to true if any element in the file has an include attribute set
        mHasInclude = false;

        MetaNode node = new InvalidMetaNode(file, 0);
        XmlPullParser xpp;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            if (br.readLine() == null) {
                throw new MetaNodeFactoryException("File is empty", file.getName(), 1);
            }
            InputStream inputStream = new FileInputStream(file);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            node.addException(e);
            return node;
        }

        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // root tag is screen
                if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(SCREEN_TAG)) {
                    node = acceptScreen(file, xpp);
                }
                // root tag is a populator container i.e. items, data collection file
                else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(ITEMS_TAG)) {
                    node = acceptItems(file, xpp);
                }
                // root node is a single populator tag, data file
                else if (eventType == XmlPullParser.START_TAG && POPULATOR_HOST_TYPE_MAP.containsKey(xpp.getName())) {
                    String tag = xpp.getName();
                    Type type = POPULATOR_HOST_TYPE_MAP.get(tag);
                    node = acceptPopulatorHost(xpp, file, PopulatorHost.withType(file, type), tag);
                }
                // root tag is something else (invalid)
                else if (eventType == XmlPullParser.START_TAG) {
                    throw new MetaNodeFactoryException("Error parsing " + file.getName() + ": " + ROOT_TAG_ERROR,
                            file.getName(), xpp.getLineNumber());
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            node = new InvalidMetaNode(file, xpp.getLineNumber());
            node.addException(e);
        }
        return node;
    }

    private Screen acceptScreen(File file, XmlPullParser xpp) throws IOException, MetaNodeFactoryException {
        Screen screen = new Screen(file);
        if (mRootNode == null) {
            mRootNode = screen;
        }

        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            try {
                screen.setAttribute(xpp.getAttributeName(i), xpp.getAttributeValue(i));
            } catch (IllegalArgumentException e) {
                mRootNode.addException(e);
            } catch (MetaNodeFactoryException e) {
                e.setErrorSourceFileName(file.getName());
                e.setErrorSourceLineNumber(xpp.getLineNumber());
                mRootNode.addException(e);
            }
        }

        try {
            int eventType = xpp.getEventType();
            while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(SCREEN_TAG))) {
                // TODO: what are valid tags following screen? a single content tag, single tabs, single action bar,
                // TODO: handle other types
                if (eventType == XmlPullParser.END_TAG) {
                    // malformed xml, wrong closing tag
                    throw new MetaNodeFactoryException("Wrong closing tag, expected " + SCREEN_TAG + ", found " + xpp.getName(),
                            file.getName(), xpp.getLineNumber());
                } else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(CONTENT_TAG)) {
                    Type type = POPULATOR_HOST_TYPE_MAP.get(CONTENT_TAG);
                    PopulatorHost pop = acceptPopulatorHost(xpp, file, PopulatorHost.withType(file, type), CONTENT_TAG);
                    pop.setParent(screen);
                    screen.setContent(pop);
                } else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(ACTIONBAR_TAG)) {
                    ActionBarModel actionBarModel = acceptActionBar(file, xpp);
                    screen.setActionBarModel(actionBarModel);
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException exception) {
            MetaNodeFactoryException mnfe = new MetaNodeFactoryException(exception.getMessage(),
                    file.getName(), xpp.getLineNumber());
            screen.addException(mnfe);
        } catch (Exception exception) {
            screen.addException(exception);
        }
        return screen;
    }

    private ActionBarModel acceptActionBar(File file, XmlPullParser xpp) throws IOException, XmlPullParserException, MetaNodeFactoryException {
        ActionBarModel actionBarModel = new ActionBarModel(file);
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            try {
                actionBarModel.setAttribute(xpp.getAttributeName(i), xpp.getAttributeValue(i));
            } catch (MetaNodeFactoryException e) {
                e.setErrorSourceFileName(file.getName());
                e.setErrorSourceLineNumber(xpp.getLineNumber());
                mRootNode.addException(e);
            }
        }
        int eventType = xpp.getEventType();
        // need to eat the closing tag
        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(ACTIONBAR_TAG))) {
            if (eventType == XmlPullParser.END_TAG) {
                // malformed xml, wrong closing tag
                throw new MetaNodeFactoryException("Wrong closing tag, expected " + ACTIONBAR_TAG + ", found " + xpp.getName(),
                        file.getName(), xpp.getLineNumber());
            }
            eventType = xpp.next();
        }
        return actionBarModel;
    }

    // an items tag should contain only _item children
    private Items acceptItems(File file, XmlPullParser xpp) throws IOException, XmlPullParserException, MetaNodeFactoryException {
        Items items = new Items(file);
        if (mRootNode == null) {
            mRootNode = items;
        }

        List<PopulatorContainer> itemList = items.getItems();

        // read in attributes
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            try {
                items.setAttribute(xpp.getAttributeName(i), xpp.getAttributeValue(i));
            } catch (MetaNodeFactoryException e) {
                e.setErrorSourceFileName(file.getName());
                e.setErrorSourceLineNumber(xpp.getLineNumber());
                mRootNode.addException(e);
            }
        }

        // Handle include attribute
        if (items.hasInclude()) {
            mRootNode.addInclude(items);
            mHasInclude = true;
        }

        // Handle count attribute
        if (items.hasCount()) {
            mRootNode.addCount(items);
            mHasCount = true;
        }

        // Push items layout onto the stack
        // if an _item child is parsed and no layout is specified
        // it will inherit the items layout from the stack
        if (items.hasLayout()) {
            mLayoutStack.push(items.getLayout());
        }

        PopulatorHost pop;
        int eventType = xpp.getEventType();
        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(ITEMS_TAG))) {
            if (eventType == XmlPullParser.END_TAG) {
                // malformed xml, wrong closing tag
                throw new MetaNodeFactoryException("Wrong closing tag, expected " + ITEMS_TAG + ", found " + xpp.getName(),
                        file.getName(), xpp.getLineNumber());
            }
            if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(ITEM_TAG)) {
                Type type = POPULATOR_HOST_TYPE_MAP.get(ITEM_TAG);
                pop = acceptPopulatorHost(xpp, file, PopulatorHost.withType(file, type), ITEM_TAG);
                pop.setParent(items);
                itemList.add(pop);
            }
            eventType = xpp.next();
        }
        // done with the items layout so pop it off the stack
        if (items.hasLayout())
            mLayoutStack.pop();
        return items;
    }

    private PopulatorHost acceptPopulatorHost(XmlPullParser xpp, File file, PopulatorHost pop, String tag) throws XmlPullParserException, IOException, MetaNodeFactoryException {
        if (mRootNode == null) {
            mRootNode = pop;
        }

        // read in attributes
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            try {
                pop.setAttribute(xpp.getAttributeName(i), xpp.getAttributeValue(i));
            } catch (MetaNodeFactoryException e) {
                e.setErrorSourceFileName(file.getName());
                e.setErrorSourceLineNumber(xpp.getLineNumber());
                mRootNode.addException(e);
            }
        }

        // Handle layout attribute
        if (!pop.hasLayout() && !mLayoutStack.isEmpty()) {
            pop.setLayout(mLayoutStack.peek());
        }

        // Handle include attribute
        // currently all PopulatorHost types support include
        if (pop.hasInclude()) {
            mRootNode.addInclude(pop);
            mHasInclude = true;
        }

        // Handle count attribute
        if (pop.hasCount()) {
            mRootNode.addCount(pop);
            mHasCount = true;
        }

        // Parse Populators
        List<Populator> populators = pop.getPopulators();
        int eventType = xpp.getEventType();
        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(tag))) {
            if (eventType == XmlPullParser.END_TAG) {
                // malformed xml, wrong closing tag
                throw new MetaNodeFactoryException("Wrong closing tag, expected " + tag + ", found " + xpp.getName(),
                        xpp.getNamespace(), xpp.getLineNumber());
            }
            /* todo: how to best distinguish viewRef from populate or screen tags */
            else if (eventType == XmlPullParser.START_TAG && !POPULATOR_HOST_TYPE_MAP.containsKey(xpp.getName())) {
                Populator populator = acceptPopulator(file, xpp);
                populators.add(populator);
            }
            eventType = xpp.next();
        }
        return pop;
    }

    // Populator could be a listview, gridview, spinner, and have nested populator types
    // or could be a simple view locator. might be useful to have seperate types for each
    private Populator acceptPopulator(File file, XmlPullParser xpp) throws XmlPullParserException, IOException, MetaNodeFactoryException {
        String viewId = xpp.getName();

        Populator populator = new Populator(file, viewId);
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            populator.setAttribute(xpp.getAttributeName(i), xpp.getAttributeValue(i));
        }

        int eventType = xpp.getEventType();
        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().equals(viewId))) {
            /* todo: how to best distinguish viewRef from populate or screen tags */
            if (eventType == XmlPullParser.END_TAG) {
                // malformed xml, wrong closing tag
                throw new MetaNodeFactoryException("Wrong closing tag, expected " + viewId + ", found " + xpp.getName(),
                        file.getName(), xpp.getLineNumber());
            } else if (eventType == XmlPullParser.START_TAG && POPULATOR_HOST_TYPE_MAP.containsKey(xpp.getName())) {
                String tag = xpp.getName();
                Type type = POPULATOR_HOST_TYPE_MAP.get(tag);
                PopulatorHost populatorHost = PopulatorHost.withType(file, type);
                populatorHost.setParent(populator);
                populator.addChild(acceptPopulatorHost(xpp, file, populatorHost, tag));
            } else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(ITEMS_TAG)) {
                Items items = acceptItems(file, xpp);
                items.setParent(populator);
                populator.addChild(items);
                /*
                    on Android, xpp.getText() outside of this loop wouldn't return the text of the current element
                 */
            } else if (eventType == XmlPullParser.TEXT && populator.getText() == null) {
                String text = xpp.getText();
                populator.setText(text);
            }

            //TODO: add cases for remaining types, tabs, spinner, etc
            eventType = xpp.next();
        }
        populator.setType(populator.getChildren().size() > 0 ? Populator.Type.Populated : Populator.Type.Simple);

        return populator;
    }

    public void resolveDependencies(MetaNode rootNode) throws InvalidIncludeException {
        List<PopulatorContainer> haveIncludes = rootNode.getIncludes();
        for (PopulatorContainer existing : haveIncludes) {
            PopulatorContainer external = mIncludeMap.get(existing.getInclude());
            existing.includeExternal(external);
            // inherit count attribute from external node if none is specified for existing node
            if (!existing.hasCount() && external.hasCount()) {
                existing.setCount(external.getCount());
                rootNode.getCounts().add(existing);
                if (!mHaveCounts.contains(rootNode)) {
                    mHaveCounts.add(rootNode);
                }
            }
        }
    }

    /* Inner nested elements with counts are resolved before any outer parent elements with
     * count attributes. This is enforced by the order nodes are added to the haveCounts list during
     * recursive descent parsing
     */
    public void resolveCounts(List<PopulatorContainer> haveCounts) {
        for (PopulatorContainer node : haveCounts)
            node.resolveCount();
    }

    private DependencyGraph buildDependencyGraph() throws MetaNodeFactoryException {
        DependencyGraph dependencyGraph = new DependencyGraph();
        for (MetaNode rootNode : mHaveIncludes) {
            for (PopulatorContainer node : rootNode.getIncludes()) {
                String includeRef = node.getInclude();
                if (mIncludeMap.containsKey(includeRef)) {
                    dependencyGraph.addEdge(rootNode, mIncludeMap.get(includeRef));
                } else {
                    throw new MetaNodeFactoryException("Error parsing + " + node.getUnderlyingFile() +
                            "\nCould not find file to include: " + node.getInclude());
                }
            }
        }
        return dependencyGraph;
    }

    public ParsedNodes createFromMetaDir(File metaDir) throws MetaNodeFactoryException, IOException, XmlPullParserException, CircularDependencyException {
        ParsedNodes parsedNodes = new ParsedNodes();
        URI base = metaDir.toURI();
        // parse each xml file in metaDir and all subdirectories
        Iterator<File> it = FileUtils.iterateFiles(metaDir, new String[]{"xml"}, true);
        while (it.hasNext()) {
            File file = it.next();
            MetaNode node = createFrom(file);
            String relativePath = base.relativize(file.toURI()).getPath();

            if (node instanceof InvalidMetaNode) {
                parsedNodes.addInvalidNode((InvalidMetaNode) node);
            } else {
                if (node instanceof Screen) {
                    parsedNodes.addScreenNode((Screen) node);
                    // we only care about resolving the count attribute for screen files,
                    // if a screen did not originally have a count attribute but includes a data
                    // file that does it will be be added to the haveCounts list in resolveDependencies
                    if (mHasCount) {
                        mHaveCounts.add(node);
                    }
                } else if (node instanceof PopulatorContainer) {
                    mIncludeMap.put(relativePath, (PopulatorContainer) node);
                }

                if (mHasInclude) {
                    mHaveIncludes.add(node);
                }
            }
        }
        DependencyGraph dependencyGraph = buildDependencyGraph();
        List<MetaNode> sorted = ParseUtils.topoSort(dependencyGraph);

        for (MetaNode node : sorted) {
            try {
                resolveDependencies(node);
            } catch (InvalidIncludeException e) {
                node.addException(e);
            }
        }
        validate(parsedNodes);
        for (MetaNode node : mHaveCounts) {
            resolveCounts(node.getCounts());
        }
        return parsedNodes;
    }

    private void validate(ParsedNodes parsedNodes) {
        for (Screen screen : parsedNodes.getScreenNodes()) {
            try {
                PopulatorHost screenContent = screen.getContent();
                if (screenContent != null) {
                    checkIncompatibleSiblingsInTree(screenContent);
                    checkUnnestableNodesInTree(screenContent);
                }
            } catch (Exception e) {
                screen.addException(e);
            }
        }
    }

    private class ExceptionWrapper extends Error {
        ExceptionWrapper(Throwable cause) {
            super(cause);
        }
    }

    private void checkUnnestableNodesInTree(MetaNode node) throws Populator.InvalidPopulatorChildException {
        final CheckSubtreeVisitor checkSubtreeVisitor = new CheckSubtreeVisitor();
        try {
            node.traverse(new MetaNode.MetaNodeVisitor() {
                @Override
                public void visit(PopulatorContainer pc) {
                    if (!(pc.getParent() instanceof Screen)) {
                        checkSubtreeVisitor.root = pc;
                        pc.traverse(checkSubtreeVisitor);
                    }
                }
            });
        } catch (ExceptionWrapper e) {
            throw (Populator.InvalidPopulatorChildException) e.getCause();
        }
    }

    private class CheckSubtreeVisitor extends MetaNode.MetaNodeVisitor {
        PopulatorContainer root;

        @Override
        public void visit(PopulatorContainer pc) {
            if (pc != root) {
                Type rootType = root.getType();
                if (!rootType.canBeAncestorOf(pc.getType())) {
                    //TODO this error message should be in Mirror. We'll move it when refactoring MirrorLib
                    String msg = String.format("<%s> cannot be ancestor of <%s>", rootType.getTagName(),
                            pc.getType().getTagName());
                    throw new ExceptionWrapper(new Populator.InvalidPopulatorChildException(msg));
                }
            }
        }
    }

    private void checkIncompatibleSiblingsInTree(MetaNode node) throws Populator.InvalidPopulatorChildException {
        try {
            node.traverse(new MetaNode.MetaNodeVisitor() {
                @Override
                public void visit(Populator p) {
                    List<PopulatorContainer> epcs = p.getChildren();
                    for (int i = 0; i < epcs.size(); i++) {
                        for (int j = i + 1; j < epcs.size(); j++) {
                            Type typeI = epcs.get(i).getType();
                            Type typeJ = epcs.get(j).getType();
                            if (!typeI.canBeSiblingOf(typeJ))
                                //TODO this error message should be in Mirror. We'll move it when refactoring MirrorLib
                                throw new ExceptionWrapper(new Populator.InvalidPopulatorChildException(
                                        String.format("<%s> and <%s> tags cannot be in the same parent tag.",
                                                typeI.getTagName(), typeJ.getTagName())));
                        }
                    }
                }
            });
        } catch (ExceptionWrapper e) {
            throw (Populator.InvalidPopulatorChildException) e.getCause();
        }
    }

    public static class ParsedNodes {
        private List<Screen> mScreenNodes;
        private List<InvalidMetaNode> mInvalidNodes;

        private ParsedNodes() {
            mScreenNodes = new LinkedList<Screen>();
            mInvalidNodes = new LinkedList<InvalidMetaNode>();
        }

        public List<Screen> getScreenNodes() {
            return mScreenNodes;
        }

        public void addScreenNode(Screen screenNode) {
            mScreenNodes.add(screenNode);
        }

        public List<InvalidMetaNode> getInvalidNodes() {
            return mInvalidNodes;
        }

        public void addInvalidNode(InvalidMetaNode invalidNode) {
            mInvalidNodes.add(invalidNode);
        }
    }
}
