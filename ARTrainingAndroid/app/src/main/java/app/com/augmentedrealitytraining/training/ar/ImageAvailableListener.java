package app.com.augmentedrealitytraining.training.ar;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.media.ThumbnailUtils;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import app.com.augmentedrealitytraining.Constants;
import app.com.augmentedrealitytraining.utils.FileUtils;

/**
 * Takes a screenshot of the view and stores in to a location on the device.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ImageAvailableListener implements ImageReader.OnImageAvailableListener {

    private String screenShotImageName;
    private MediaProjection mediaProjection;
    private int mWidth;
    private int mHeight;

    public ImageAvailableListener(MediaProjection mediaProjection, String screenShotImageName, int mWidth, int mHeight) {
        this.screenShotImageName = screenShotImageName;
        this.mediaProjection = mediaProjection;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = null;
        Bitmap bitmap = null;

        try {
            Thread.sleep(1000);
            image = reader.acquireLatestImage();
            if (image != null) {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;

                // create bitmap
                bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);

                FileUtils.saveScreenShot(bitmap, screenShotImageName);

                if (mediaProjection != null) {
                    mediaProjection.stop();
                    CharSequence sequence = "ScreenShot saved at: " + Constants.SCREENSHOT_DIRECTORY;
                    Toast.makeText(ARApplication.getInstance(), sequence, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }

            if (image != null) {
                image.close();
            }
        }
    }
}
