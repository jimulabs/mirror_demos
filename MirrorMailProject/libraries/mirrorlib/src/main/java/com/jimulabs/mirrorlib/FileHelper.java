package com.jimulabs.mirrorlib;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by lintonye on 2013-07-03.
 */
public class FileHelper {
    public static void unzip(File file, File outputDir) throws IOException {
        validateZipFile(file);
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(
                file));
        unzip(is, outputDir);
    }

    public static void unzip(InputStream is, File targetDir) throws IOException {
        targetDir.mkdirs();
        ZipInputStream zis = new ZipInputStream(is);
        try {
            ZipEntry entry;
            byte[] buffer = new byte[2048];
            while ((entry = zis.getNextEntry()) != null) {
                File target = new File(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    target.mkdirs();
                } else {
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(target));
                    int count;
                    while ((count = zis.read(buffer, 0, buffer.length)) > 0) {
                        outputStream.write(buffer, 0, count);
                    }
                    outputStream.close();
                }
                zis.closeEntry();
            }
        } finally {
            zis.close();
        }
    }

    private static void validateZipFile(File bundle) throws ZipException,
            IOException {
        ZipFile zip = null;
        try {
            zip = new ZipFile(bundle);
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
    }

    public static File getInternalDataRoot(Context context) {
//        File dir  = new File(context.getFilesDir(), "resources");
//        FileUtils.forceMkdir(dir);
//        return dir;
        return context.getFilesDir();
    }

    public static File getCleanTempDir(Context context) throws IOException {
        File dir = new File(context.getCacheDir(), "resources");
        FileUtils.forceMkdir(dir);
        FileUtils.cleanDirectory(dir);
        return dir;
    }
}
