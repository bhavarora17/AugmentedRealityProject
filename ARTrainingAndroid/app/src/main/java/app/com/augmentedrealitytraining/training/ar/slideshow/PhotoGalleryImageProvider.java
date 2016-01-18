package app.com.augmentedrealitytraining.training.ar.slideshow;

/**
 * Created by bhavya on 10/17/15.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.com.augmentedrealitytraining.Constants;


public class PhotoGalleryImageProvider {
    // Consts
    public static final int IMAGE_RESOLUTION = 15;
    // Buckets where we are fetching images from.
    public static final String CAMERA_IMAGE_BUCKET_NAME = Constants.SCREENSHOT_DIRECTORY;
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Fetch both full sized images and thumbnails via a single query.
     * Returns all images not in the Camera Roll.
     *
     * @param context
     * @return
     */
    public static List<PhotoItem> getAlbumThumbnails(Context context) throws IOException {
        File dir = new File(Constants.SCREENSHOT_DIRECTORY);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };

        File[] filelist = dir.listFiles(fileFilter);

        ArrayList<PhotoItem> result = new ArrayList<PhotoItem>(filelist.length);

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit=shre.edit();


        for (int i = 0; i < filelist.length; i++) {
            Uri thumbnailUri = Uri.parse(filelist[i].getAbsolutePath());
            Uri fullImageUri = Uri.parse(filelist[i].getAbsolutePath().replace("thumbnails/",""));
            PhotoItem newItem = new PhotoItem(thumbnailUri, fullImageUri);

            edit.putString("imagepath"+String.valueOf(i),String.valueOf(fullImageUri));
            edit.commit();

            result.add(newItem);
        }

        return result;
    }

    /**
     * Get the path to the full image for a given thumbnail.
     */
    private static Uri uriToFullImage(Cursor thumbnailsCursor, Context context) {
        String imageId = thumbnailsCursor.getString(thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
// Request image related to this thumbnail
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor imagesCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filePathColumn, MediaStore.Images.Media._ID + "=?", new String[]{imageId}, null);
        if (imagesCursor != null && imagesCursor.moveToFirst()) {
            int columnIndex = imagesCursor.getColumnIndex(filePathColumn[0]);
            String filePath = imagesCursor.getString(columnIndex);
            imagesCursor.close();
            return Uri.parse(filePath);
        } else {
            imagesCursor.close();
            return Uri.parse("");
        }
    }

    /**
     * Render a thumbnail photo and scale it down to a smaller size.
     *
     * @param path
     * @return
     */
    private static Bitmap bitmapFromPath(String path) {
        File imgFile = new File(path);
        Bitmap imageBitmap = null;
        if (imgFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = IMAGE_RESOLUTION;
            imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        }
        return imageBitmap;
    }

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }
}