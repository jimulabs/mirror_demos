package com.jimulabs.mirrorlib;

import java.util.Map;

/**
 * This class is called into by modified external dex files to
 * get resource ids. This lets us replace inlined resource ids
 * that may be out of date with calls to these methods that use
 * the most recent external resources and are always up-to-date.
 * <p/>
 * It's important that the context static variable gets set
 * before any external code that could call into here gets run.
 * <p/>
 * Created by matt on 2013-10-08.
 */
public class MirrorResources {

    public static int getAnim(String name) {
        return getResourceId("anim", name);
    }

    public static int getAnimator(String name) {
        return getResourceId("animator", name);
    }

    public static int getArray(String name) {
        return getResourceId("array", name);
    }

    public static int getAttr(String name) {
        return getResourceId("attr", name);
    }

    public static int getBool(String name) {
        return getResourceId("bool", name);
    }

    public static int getColor(String name) {
        return getResourceId("color", name);
    }

    public static int getDimen(String name) {
        return getResourceId("dimen", name);
    }

    public static int getDrawable(String name) {
        return getResourceId("drawable", name);
    }

    public static int getFraction(String name) {
        return getResourceId("fraction", name);
    }

    public static int getId(String name) {
        return getResourceId("id", name);
    }

    public static int getInteger(String name) {
        return getResourceId("integer", name);
    }

    public static int getInterpolator(String name) {
        return getResourceId("interpolator", name);
    }

    public static int getLayout(String name) {
        return getResourceId("layout", name);
    }

    public static int getMenu(String name) {
        return getResourceId("menu", name);
    }

    public static int getMipMap(String name) {
        return getResourceId("mipmap", name);
    }

    public static int getPlurals(String name) {
        return getResourceId("plurals", name);
    }

    public static int getRaw(String name) {
        return getResourceId("raw", name);
    }

    public static int getString(String name) {
        return getResourceId("string", name);
    }

    public static int getStyle(String name) {
        return getResourceId("style", name);
    }

    public static int getXml(String name) {
        return getResourceId("xml", name);
    }

    public static int[] getStyleableArray(String name) {
//        if (name.equals("ActionBarWindow")) {
//            return new int[] { 0x7f010050, 0x7f010051, 0x7f010052 };
//        }

        Map<String, int[]> attrMap = Refresher.currentModel().getAttrMap();
        int[] attrs = attrMap.get(name);
        if (attrs != null) {
            return attrs;
        } else {
            throw new IllegalArgumentException("No such styleable array: " + name);
        }
    }

    public static int getResourceId(String type, String name) {
        String packageName = Refresher.packageName();
        int id = Refresher.getResources().getIdentifier(name, type, packageName);

        return id;
    }
}
