package ru.rzn.gmyasoedov.collage;

import android.content.Context;
import android.graphics.Bitmap;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * class for work with file
 */
public class FileManager {
    private static final String TAG = FileManager.class.getSimpleName();
    protected static final String FILE_EXT = "png";
    protected static final String FILE_EXT_DELIMITER = ".";
    private static FileManager instance = new FileManager(CollageApplication.getContext());
    private File cacheDir;

    private FileManager(Context context) {
        cacheDir = createDirectory(StorageUtils.getCacheDirectory(context));
    }

    public static FileManager getInstance() {
        return instance;
    }

    public File getFile(String url) {
        return getFile(url, FILE_EXT);
    }

    public File getFile(String filename, String ext) {
        if (ext != null) {
            filename = filename + FILE_EXT_DELIMITER + ext;
        }
        return new File(cacheDir, filename);
    }

    public void clearCacheDirectory() {
        clearDirectory(cacheDir);
    }

    public void clearDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                clearDirectory(f);
            }
            f.delete();
        }
    }

    public static File createDirectory(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    public File getCacheDir() {
        return cacheDir;
    }

    public File fileToCache(String name, String ext) throws IOException {
        String path = getCacheDir() + File.separator + name + FILE_EXT_DELIMITER + ext;
        File file = new File(path);
        return file;
    }

    public File saveBitmapToFile(Bitmap bitmap, String name) throws IOException {
        File file = null;
        FileOutputStream outputStream = null;
        try {
            file = fileToCache(name, FILE_EXT);
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            bitmap.recycle();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return file;
    }
}