package app.com.augmentedrealitytraining.training.ar.slideshow;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by bhavya on 10/17/15.
 */

public class PhotoGalleryAsyncLoader extends AsyncTaskLoader<List<PhotoItem>> {
    // Persistent list of photo list item
    private List<PhotoItem> mPhotoListItems;

    /**
     * Constructor
     *
     * @param context
     */
    public PhotoGalleryAsyncLoader(Context context) {
        super(context);
    }

    /**
     * Matches code in MediaProvider.computeBucketValues.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    /**
     * Load photo album image items in the background
     * <p>
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<PhotoItem> loadInBackground() {
        final Context context = getContext();
        List<PhotoItem> photos = null;
        try {
            photos = PhotoGalleryImageProvider.getAlbumThumbnails(context);
        } catch (Exception e) {

        }
        return photos;
    }

    /**
     * Called when there is new data to deliver to the client. The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<PhotoItem> newPhotoListItems) {
        if (isReset()) {
        // An async query came in while the loader is stopped. We
        // don't need the result.
            if (newPhotoListItems != null) {
                onReleaseResources(newPhotoListItems);
            }
        }
        List<PhotoItem> oldPhotos = mPhotoListItems;
        mPhotoListItems = newPhotoListItems;
        if (isStarted()) {
        // If the Loader is currently started, we can immediately
        // deliver its results.
            super.deliverResult(newPhotoListItems);
        }
        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldPhotos != null) {
            onReleaseResources(oldPhotos);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mPhotoListItems != null) {
        // If we currently have a result available, deliver it immediately.
            deliverResult(mPhotoListItems);
        } else {
        // If the data has changed since the last time it was loaded
        // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<PhotoItem> photoListItems) {
        super.onCanceled(photoListItems);
        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(photoListItems);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mPhotoListItems != null) {
            onReleaseResources(mPhotoListItems);
            mPhotoListItems = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<PhotoItem> photoListItems) {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }
}