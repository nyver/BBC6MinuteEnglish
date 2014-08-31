package com.nyver.bbclearningenglish.helper;

import android.content.Context;
import android.os.Environment;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.exception.StorageException;

import java.io.File;

public class StorageHelper {
    public static final String DIRECTORY_NAME = "episodes";

    /**
     * Check if external storage available and writeable
     *
     * @return boolean
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Check if external storage available and readable
     *
     * @return
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getPath(Context context)
    {
        return new File(context.getExternalFilesDir(null), DIRECTORY_NAME);
    }

    public static File getPath(Context context, String fileName)
    {
        return new File(context.getExternalFilesDir(null), DIRECTORY_NAME + File.separator + fileName);
    }

    public static void createStorageIfNotExists(final Context context) throws StorageException
    {
        if (isExternalStorageReadable()) {
            File path = getPath(context);
            if (!path.exists()) {
                if (isExternalStorageWritable()) {
                    if (!path.mkdir()) {
                        throw new StorageException(String.format(context.getString(R.string.error_can_not_create_directory), path.getAbsolutePath()));
                    }
                } else {
                    throw new StorageException(context.getString(R.string.error_external_storage_is_not_writeable));
                }
            }
        } else {
            throw new StorageException(context.getString(R.string.error_external_storage_is_not_writeable));
        }
    }
}
