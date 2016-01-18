package app.com.augmentedrealitytraining.training.ar.slideshow;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.training.ar.TrainingActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SimpleAndroidImagePickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SimpleAndroidImagePickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleAndroidImagePickerFragment extends BaseFragment implements Button.OnClickListener {
    // Code for our image picker select action.
    private static final int IMAGE_PICKER_SELECT = 999;
    // Reference to our image view we will use
    private ImageView mSelectedImage;
    // Reference to picker button.
    private Button mPickPhotoButton;

    /**
     * Default empty constructor.
     */
    public SimpleAndroidImagePickerFragment() {
        super();
    }

    /**
     * Static factory method
     *
     * @param sectionNumber
     * @return
     */
    public static SimpleAndroidImagePickerFragment newInstance(int sectionNumber) {
        SimpleAndroidImagePickerFragment fragment = new SimpleAndroidImagePickerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * OnCreateView fragment override
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_simple_android_image_picker, container, false);
// Set the image view
        mSelectedImage = (ImageView) view.findViewById(R.id.imageViewFullSized);
        mPickPhotoButton = (Button) view.findViewById(R.id.button);
// Set OnItemClickListener so we can be notified on button clicks
        mPickPhotoButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_PICKER_SELECT);
    }

    /**
     * Photo Selection result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            TrainingActivity activity = (TrainingActivity) getActivity();
            Bitmap bitmap = getBitmapFromCameraData(data, activity);
            mSelectedImage.setImageBitmap(bitmap);
        }
    }

    /**
     * Scale the photo down and fit it to our image views.
     * <p>
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath) {
// Get the dimensions of the View
        int targetW = mSelectedImage.getWidth();
        int targetH = mSelectedImage.getHeight();
// Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
// Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
// Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        mSelectedImage.setImageBitmap(bitmap);
    }

    /**
     * Use for decoding camera response data.
     *
     * @param data
     * @param context
     * @return
     */
    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }
}