package app.com.augmentedrealitytraining.training.ar;

import android.app.Application;
import android.preference.PreferenceManager;

import com.adobe.libs.scan.ASImageHandler;
import com.adobe.libs.scan.context.ASContext;

import org.artoolkit.ar.base.assets.AssetHelper;

import java.io.File;

import app.com.augmentedrealitytraining.Constants;

public class ARApplication extends Application {

    private static Application sInstance;

    // Load the native libraries.
    static {
        System.loadLibrary("c++_shared");

        // ARToolKit v5.1.0 and later depend on libcurl.
        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("curl");

        System.loadLibrary("nftSimpleNative");

        System.loadLibrary("AdobeScan");
    }

    // Anywhere in the application where an instance is required, this method
    // can be used to retrieve it.
    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ((ARApplication) sInstance).initializeInstance();
    }

    // Here we do one-off initialisation which should apply to all activities
    // in the application.
    protected void initializeInstance() {

        PreferenceManager.setDefaultValues(this, org.artoolkit.ar.base.R.xml.preferences, false);
        ASContext.register(this, "app.com.augmentedrealitytraining");
        ASImageHandler.setImagesCacheFolder(Constants.SCREENSHOT_DIRECTORY);

        // Unpack assets to cache directory so native library can read them.
        // N.B.: If contents of assets folder changes, be sure to increment the
        // versionCode integer in the AndroidManifest.xml file.
        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(getInstance(), "Data");
        assetHelper.cacheAssetFolder(getInstance(), "DataNFT");

        //Create the directory for screenshots.
        createPath(new File(Constants.SCREENSHOT_DIRECTORY));

        // Create the directory for the thumbnails.
        createPath(new File(Constants.SCREENSHOT_THUMBNAILS_DIRECTORY));
    }

    /**
     * Creates the path specified as parameter if it doesn't exist. Does nothing if the path already exists.
     */
    private static void createPath(File path) {
        if (!path.exists()) {
            path.mkdirs();
        }
    }
}
