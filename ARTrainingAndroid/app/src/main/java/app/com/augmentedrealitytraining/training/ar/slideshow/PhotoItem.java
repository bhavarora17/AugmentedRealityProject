package app.com.augmentedrealitytraining.training.ar.slideshow;

/**
 * Created by bhavya on 10/17/15.
 */

import android.net.Uri;
import android.util.Log;


public class PhotoItem {
    // Ivars.
    private Uri thumbnailUri;
    private Uri fullImageUri;

    public PhotoItem(Uri thumbnailUri, Uri fullImageUri) {
        this.thumbnailUri = thumbnailUri;
        this.fullImageUri = fullImageUri;
    }

    /**
     * Getters and setters
     */
    public Uri getThumbnailUri() {
        Log.d("bhavya", "thumbnailUri");

        return thumbnailUri;

    }

    public void setThumbnailUri(Uri thumbnailUri) {
        Log.d("bhavya", "thumbnailUri");
        this.thumbnailUri = thumbnailUri;
    }

    public Uri getFullImageUri() {
        Log.d("bhavya", "FullImageUri");

        return fullImageUri;
    }

    public void setFullImageUri(Uri fullImageUri) {
        this.fullImageUri = fullImageUri;
    }
}