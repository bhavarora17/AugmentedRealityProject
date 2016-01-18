package app.com.augmentedrealitytraining.training.ar.slideshow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import app.com.augmentedrealitytraining.R;

/**
 * Created by bhavya on 10/17/15.
 */
public class PhotoAdapter extends ArrayAdapter<PhotoItem> {
    // Ivars.
    private Context context;
    private int resourceId;

    public PhotoAdapter(Context context, int resourceId,
                        List<PhotoItem> items, boolean useList) {
        super(context, resourceId, items);
        this.context = context;
        this.resourceId = resourceId;
    }

    /**
     * The "ViewHolder" pattern is used for speed.
     * <p>
     * Reference: http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
     */
    private class ViewHolder {
        ImageView photoImageView;
    }

    /**
     * Populate the view holder with data.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        PhotoItem photoItem = getItem(position);
        View viewToUse = null;
// This block exists to inflate the photo list item conditionally based on whether
// we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            viewToUse = mInflater.inflate(resourceId, null);
            holder.photoImageView = (ImageView) viewToUse.findViewById(R.id.imageView);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }
// Set the thumbnail
        holder.photoImageView.setImageURI(photoItem.getThumbnailUri());
        return viewToUse;
    }
}
