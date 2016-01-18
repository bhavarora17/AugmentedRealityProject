package app.com.augmentedrealitytraining.training.ar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adobe.libs.scan.ASImage;
import com.adobe.libs.scan.ASImageHandler;
import com.adobe.libs.scan.ASMultipleImagePreviewActivity;
import com.adobe.libs.scan.ASSingleImageEnhanceActivity;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.training.ar.slideshow.BaseFragment;
import app.com.augmentedrealitytraining.training.ar.slideshow.HorizontalPhotoGalleryFragment;
import app.com.augmentedrealitytraining.utils.FileUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageViewFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private FragmentManager fragmentManager;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageViewFragment newInstance(String param1, String param2) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment;
    }

    public ImageViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        fragmentManager = getFragmentManager();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_image_view, container, false);

        ImageButton closeButton = (ImageButton) v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.ImageFrame, new BaseFragment()).commit();
            }
        });

        ImageButton deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteImageTask().execute();
            }
        });

        ImageButton editButton = (ImageButton) v.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ASImageHandler.getImageList().clear();
                ASImageHandler.addImage(mParam1, ASImage.IMAGE_SOURCE.GALLERY);
                TrainingActivity trainingActivity = (TrainingActivity) getActivity();
                Intent intent = new Intent(trainingActivity, ASSingleImageEnhanceActivity.class);
                intent.putExtra(ASMultipleImagePreviewActivity.IMAGE_INDEX_KEY, 0);
                trainingActivity.startActivityForResult(intent, TrainingActivity.EDIT_REQUEST_CODE);
            }
        });

        reLayoutFragment(v);
        return v;
    }

    public void reLayoutFragment(final View rootView) {
        if(rootView != null) {
            ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        final View v = rootView.findViewById(R.id.photo_container);

                        Bitmap bitmap;

                        BitmapFactory.Options thumbOpts = new BitmapFactory.Options();
                        thumbOpts.inSampleSize = 1;
                        bitmap = BitmapFactory.decodeFile(mParam1, thumbOpts);

                        if(bitmap != null) {
                            ImageView img;
                            img = (ImageView) v.findViewById(R.id.image);

                            int width2 = rootView.getWidth();
                            int height2 = v.getHeight();

                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();

                            float xScale = (float) width2 / width;
                            float yScale = (float) height2 / height;
                            float scale = (xScale <= yScale) ? xScale : yScale;

                            Matrix matrix = new Matrix();
                            matrix.postScale(scale, scale);

                            Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                            img.setImageBitmap(scaledBitmap);

                            final LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.photo_toolbar);
                            ViewGroup.LayoutParams params = layout.getLayoutParams();
                            params.width = scaledBitmap.getWidth();
                            layout.setLayoutParams(params);

                            params = v.getLayoutParams();
                            params.width = scaledBitmap.getWidth();
                            v.setLayoutParams(params);
                        } else {
                            fragmentManager.beginTransaction().replace(R.id.ImageFrame, new BaseFragment()).commit();
                        }
                    }
                });
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private class DeleteImageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            FileUtils.deleteFileFromStorage(mParam1);
            FileUtils.deleteFileFromStorage(FileUtils.getPathToThumbnailDirectory(mParam1));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getFragmentManager().beginTransaction().replace(R.id.ImageFrame, new BaseFragment()).commit();
            getFragmentManager().beginTransaction().replace(R.id.container, new BaseFragment()).commit();
            getFragmentManager().beginTransaction().replace(R.id.container, HorizontalPhotoGalleryFragment.newInstance(1)).commit();
        }
    }
}
