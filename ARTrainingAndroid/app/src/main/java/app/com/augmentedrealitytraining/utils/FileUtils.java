package app.com.augmentedrealitytraining.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import app.com.augmentedrealitytraining.Constants;

/**
 * Created by saurabh on 11/27/15.
 */
public class FileUtils {

    /**
     * Saves the bitmap and its associated thumbnail in the screenshot directory.
     *
     * @param bitmap bitmap of the image
     * @param screenShotImageName screenShot Name
     */

    public static void saveScreenShot(Bitmap bitmap, String screenShotImageName) {
        FileUtils.saveBitmapToStorage(bitmap, screenShotImageName);
        FileUtils.saveBitmapToStorage(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(String.valueOf(new File(screenShotImageName)))
                , Constants.THUMBSIZE
                , Constants.THUMBSIZE)
                , FileUtils.getPathToThumbnailDirectory(screenShotImageName));
    }

    /**
     * Returns the corresponding thumbnail absolute file path from the screenshot absolute file path.
     */
    public static String getPathToThumbnailDirectory(String screenShotImageName) {
        File file = new File(screenShotImageName);
        return file.getParent() + Constants.THUMBNAILS + File.separator + file.getName();
    }

    /**
     * Saves the bitmap to the location specified by the location.
     */
    public static void saveBitmapToStorage(Bitmap bitmap, String location) {
        try {
            File file = new File(location);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    /**
     * Deletes the file specified by the location.
     */
    public static void deleteFileFromStorage(String location) {
        File file = new File(location);
        file.delete();
    }
}
