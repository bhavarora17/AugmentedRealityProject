package app.com.augmentedrealitytraining.training.ar.slideshow;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.training.ar.TrainingActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SimplePhotoGalleryListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SimplePhotoGalleryListFragment#} factory method to
 * create an instance of this fragment.
 */
public class SimplePhotoGalleryListFragment extends BaseFragment implements AbsListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<PhotoItem>> {
    protected OnFragmentInteractionListener mListener;
    protected AbsListView mListView;
    protected PhotoAdapter mAdapter;
    protected ArrayList<PhotoItem> mPhotoListItem;
    protected TextView mEmptyTextView;
    protected ProgressDialog mLoadingProgressDialog;

    /**
     * Required empty constructor
     */
    public SimplePhotoGalleryListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an empty loader and pre-initialize the photo list items as an empty list.
        Context context = getActivity().getBaseContext();
        // Set up empty mAdapter
        mPhotoListItem = new ArrayList<>();
        mAdapter = new PhotoAdapter(context,
                R.layout.photo_item,
                mPhotoListItem, false);
        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_simple_photo_gallery_list, container, false);
        // Set the mAdapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        (mListView).setAdapter(mAdapter);
        // Show the empty text / message.
        resolveEmptyText();
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        return view;
    }

    /**
     * Used to show a generic empty text warning. Override in inheriting classes.
     */
    protected void resolveEmptyText() {
        if (mAdapter.isEmpty()) {
            mEmptyTextView.setVisibility(View.VISIBLE);
            setEmptyText();
        } else {
            mEmptyTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
// Show a progress dialog.
            mLoadingProgressDialog = new ProgressDialog(getActivity());
            mLoadingProgressDialog.setMessage("Loading Photos...");
            mLoadingProgressDialog.setCancelable(true);
            mLoadingProgressDialog.show();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        cancelProgressDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelProgressDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelProgressDialog();
    }

    /**
     * This is only triggered when the user selects a single photo.
     *
     * @param parent the parent
     * @param view view
     * @param position of the photo selected
     * @param id of the photo selected
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Tell the share builder to add the photo to the share operation.
//            PhotoItem photoListItem = this.mAdapter.getItem(position);
//            String imagePath = photoListItem.getThumbnailUri().getPath();
            mListener.onFragmentInteraction(TrainingActivity.SELECT_PHOTO_ACTION);
            resetFragmentState();
        }
    }

    /**
     * Used when hitting the back button to reset the mFragment UI state
     */
    protected void resetFragmentState() {
        // Clear view state
        getActivity().invalidateOptionsMenu();
        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText() {
        mEmptyTextView.setText("No Photos!");
    }

    /**
     * Loader Handlers for loading the photos in the background.
     */
    @Override
    public Loader<List<PhotoItem>> onCreateLoader(int id, Bundle args) {
// This is called when a new Loader needs to be created. This
// sample only has one Loader with no arguments, so it is simple.
        return new PhotoGalleryAsyncLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<PhotoItem>> loader, List<PhotoItem> data) {
// Set the new data in the mAdapter.
        mPhotoListItem.clear();
        for (int i = 0; i < data.size(); i++) {
            PhotoItem item = data.get(i);
            mPhotoListItem.add(item);
        }
        mAdapter.notifyDataSetChanged();
        resolveEmptyText();
        cancelProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader<List<PhotoItem>> loader) {
// Clear the data in the mAdapter.
        mPhotoListItem.clear();
        mAdapter.notifyDataSetChanged();
        resolveEmptyText();
        cancelProgressDialog();
    }

    /**
     * Save cancel for the progress loader
     */
    private void cancelProgressDialog() {
        if (mLoadingProgressDialog != null) {
            if (mLoadingProgressDialog.isShowing()) {
                mLoadingProgressDialog.cancel();
            }
        }
    }


}