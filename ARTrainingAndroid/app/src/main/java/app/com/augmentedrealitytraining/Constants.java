package app.com.augmentedrealitytraining;

import android.os.Environment;

import java.io.File;

/**
 * Created by Anmol on 10/9/2015.
 */
public class Constants {

    public static final String SCREENSHOT_DIRECTORY = Environment.getExternalStorageDirectory()
            + File.separator + "AR_Pictures";
    public static final String THUMBNAILS = File.separator + "thumbnails";
    public static final String SCREENSHOT_THUMBNAILS_DIRECTORY = SCREENSHOT_DIRECTORY + THUMBNAILS;
    public static final String IMAGE_EXTENSION = ".jpeg";
    public static final int THUMBSIZE = 64;

}
