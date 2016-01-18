package app.com.augmentedrealitytraining.training.ar;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.libs.scan.ASImage;
import com.adobe.libs.scan.ASImageHandler;
import com.adobe.libs.scan.ASSingleImageEnhanceActivity;

import org.artoolkit.ar.base.camera.CameraPreferencesActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import app.com.augmentedrealitytraining.Constants;
import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.training.HelpActivity;
import app.com.augmentedrealitytraining.training.ar.slideshow.BaseFragment;
import app.com.augmentedrealitytraining.training.ar.slideshow.HorizontalPhotoGalleryFragment;
import app.com.augmentedrealitytraining.utils.FileUtils;

@SuppressLint("NewApi")
public class TrainingActivity extends Activity implements BaseFragment.OnFragmentInteractionListener, ImageViewFragment.OnFragmentInteractionListener {
    private static final String TAG = "nftSimple";
    private static final String VIDEO_URL = "tnRJaHZH9lo";
    private static final String POPUP_TEXT = "Test PopUp Window For AR, Move your finger in the window to zoom in and zoom out!";
    public static final int IMAGE_REQUEST_CODE = 100;
    public static final int EDIT_REQUEST_CODE = 200;

    public static final int SELECT_PHOTO_ACTION = 0;

    private static final int NUMBER_OF_MENU_ITEMS = 4;

    static TextView textView;
    static String content;
    private static Context nContext;
    private static DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private GLSurfaceView glView;
    private CameraSurface camSurface;
    private FrameLayout mainLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageButton createNote;
    private ImageButton slideShow;
    private float createNoteButtonBaseYCoordinate;

    String imagePathFull;
    //TextView functions
    // public static native String nativeGetString(String s);

    private int mWidth;
    private int mHeight;
    private int _xDelta;
    private int _yDelta;
    private static DrawerItemClickListener drawerItemClickListener;
    private static int count = 0;

    // Lifecycle functions.
    public static native boolean nativeCreate(Context ctx);

    private static FragmentManager fm;

    public static native boolean nativeStart();

    public static native boolean nativeStop();

    public static native boolean nativeDestroy();

    public static native boolean nativeVideoAvailable();

    // Camera functions.
    public static native boolean nativeVideoInit(int w, int h, int cameraIndex, boolean cameraIsFrontFacing);

    public static native void nativeVideoFrame(byte[] image);

    // OpenGL functions.
    public static native void nativeSurfaceCreated();

    public static native void nativeSurfaceChanged(int w, int h);

    public static native void nativeDrawFrame();

    // Other functions.
    public static native void nativeDisplayParametersChanged(int orientation, int w, int h, int dpi); // 0 = portrait, 1 = landscape (device rotated 90 degrees ccw), 2 = portrait upside down, 3 = landscape reverse (device rotated 90 degrees cw).

    public static native void nativeSetInternetState(int state);

    //called by JNI, The parameter is passed by JNI
    public static void showInformation(String description) {
        //System.out.println(description);
        //  String description="qq";
        //Log.i(TAG, description);
        //Log.i(TAG, content);
        content = description;
        mHandler.sendMessage(mHandler.obtainMessage(0, content));
    }

    private static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String s = (String) msg.obj;
            String[] array = s.split("/");
            if (s.length() > 0) {
                String show = "This is the " + array[array.length - 1] + " scene.";
                textView.setText(show);
                textView.setBackgroundColor(Color.GRAY);
                mDrawerLayout.openDrawer(Gravity.LEFT);
                drawerItemClickListener.setVideoURL("JyzJdtOw5hk");
                drawerItemClickListener.setPopUpText("This is the " + array[array.length - 1] + " scene.");
                drawerItemClickListener.changeTextView();
            } else {
                textView.setText("Nothing Tracked");
            }

        }

    };

    @Override
    public void startActivityForResult(Intent intent, int requestCode){
        if(!TrainingActivity.nativeVideoAvailable()) {
            CharSequence sequence = "Please wait for the camera preview";
            Toast.makeText(ARApplication.getInstance(), sequence, Toast.LENGTH_SHORT).show();
            return;
        }

        super.startActivityForResult(intent, requestCode);
    }

    /**
     * Called when the activity is first created.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean needActionBar = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (!ViewConfiguration.get(this).hasPermanentMenuKey()) needActionBar = true;
            } else {
                needActionBar = true;
            }
        }
        if (needActionBar) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 18)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
            // Android version < 18 -> set orientation fullSensor
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
        updateNativeDisplayParameters();


        setContentView(R.layout.activity_ar);

        TrainingActivity.nativeCreate(this);
        createAndInitializeDrawer();


        createNote = (ImageButton) findViewById(R.id.createNote);
        createNoteButtonBaseYCoordinate = createNote.getY();

        nContext = this;
        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //createNote.setAnimation(rAnim);
                if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                    return;
                }

                Notes fragment1 = new Notes(new Intent(nContext, Notes.class));
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);

                if (count % 2 == 0) {
                    if (count == 2) {
                        count = 0;
                    }

                    fragmentTransaction.replace(R.id.notes, fragment1);
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    findViewById(R.id.notes).getLayoutParams().height = size.y/2;
                    findViewById(R.id.notes).getLayoutParams().width = size.x/2;
                    fragmentTransaction.commit();
                    count++;

                } else {

                    if (Notes.inNote)
                    {
                        if (getFragmentManager().findFragmentById(R.id.edit) instanceof EditActivity) {
                            final EditActivity ea = (EditActivity) getFragmentManager().findFragmentById(R.id.edit);
                            ea.onBackPressed();
                        }
//                        fragmentTransaction
//                                .replace(R.id.edit, new BaseFragment()).commit();
                        fragmentTransaction
                                .replace(R.id.notes, new BaseFragment()).commit();

                        Notes.inNote = false;
                    }
                    else
                    {
                        fragmentTransaction
                                .replace(R.id.notes, new BaseFragment()).commit();

                    }
                    count++;
                }
            }
        });

        /*FragmentManager fm = getFragmentManager();
        ARTrainingImageFragment targetFragment = null;
        targetFragment = new ARTrainingImageFragment();
//        targetFragment.show(fm, "Dialog Fragment");
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.image_frame, targetFragment).commit();*/
    }

    @Override
    public void onStart() {
        super.onStart();

        mainLayout = (FrameLayout) this.findViewById(R.id.arLayout);

        TrainingActivity.nativeStart();
    }

    @SuppressWarnings("deprecation") // FILL_PARENT still required for API level 7 (Android 2.1)
    @Override
    public void onResume() {
        super.onResume();

        // Update info on whether we have an Internet connection.
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        nativeSetInternetState(isConnected ? 1 : 0);

        // In order to ensure that the GL surface covers the camera preview each time onStart
        // is called, remove and add both back into the FrameLayout.
        // Removing GLSurfaceView also appears to cause the GL surface to be disposed of.
        // To work around this, we also recreate GLSurfaceView. This is not a lot of extra
        // work, since Android has already destroyed the OpenGL context too, requiring us to
        // recreate that and reload textures etc.

        // Create the camera view.
        camSurface = new CameraSurface(this);

        // Create/recreate the GL view.
        glView = new GLSurfaceView(this);
        //glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Do we actually need a transparent surface? I think not, (default is RGB888 with depth=16) and anyway, Android 2.2 barfs on this.
        glView.setRenderer(new Renderer());
        glView.setZOrderMediaOverlay(true); // Request that GL view's SurfaceView be on top of other SurfaceViews (including CameraPreview's SurfaceView).

        //create another overlay
        textView = new TextView(this);
        textView.setText("Nothing Tracked.");
        textView.setGravity(Gravity.BOTTOM);
        textView.setTextSize(15);

        mainLayout.addView(camSurface, new LayoutParams(128, 128));
        mainLayout.addView(glView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        //mainLayout.addView(v,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        addContentView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (glView != null) {
            glView.onResume();
        }

        final WebView webView1 = drawerItemClickListener.getWebView();
        if (webView1 != null) {
            webView1.onResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glView != null) glView.onPause();

        // System hardware must be release in onPause(), so it's available to
        // any incoming activity. Removing the CameraPreview will do this for the
        // camera. Also do it for the GLSurfaceView, since it serves no purpose
        // with the camera preview gone.

        mainLayout.removeView(glView);
        mainLayout.removeView(camSurface);

        textView.setText(" ");

        //pause webview to pause the running video if the activity is paused.
        final WebView webView1 = drawerItemClickListener.getWebView();
        if (webView1 != null) {
            webView1.onPause();
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        TrainingActivity.nativeStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        TrainingActivity.nativeDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // We won't use the orientation from the config, as it only tells us the layout type
        // and not the actual orientation angle.
        //int nativeOrientation;
        //int orientation = newConfig.orientation; // Only portrait or landscape.
        //if (orientation == Configuration.ORIENTATION_LANSCAPE) nativeOrientation = 0;
        //else /* orientation == Configuration.ORIENTATION_PORTRAIT) */ nativeOrientation = 1;
        updateNativeDisplayParameters();

        if(getFragmentManager().findFragmentById(R.id.ImageFrame) instanceof  ImageViewFragment) {
            final ImageViewFragment f = (ImageViewFragment) getFragmentManager().findFragmentById(R.id.ImageFrame);
            if (f != null && f.isVisible())
                f.reLayoutFragment(f.getView());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, CameraPreferencesActivity.class));
            return true;
        } else if (item.getItemId() == R.id.help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * we are create the listeners for taking the screenshot here.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_REQUEST_CODE) {
            MediaProjection mediaProjection =
                    drawerItemClickListener.getProjectionManager().getMediaProjection(resultCode, data);

            if (mediaProjection != null) {
                this.onPause();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int density = metrics.densityDpi;
                int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                mWidth = size.x;
                mHeight = size.y;

                ImageReader imageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
                String screenShotImageName = Constants.SCREENSHOT_DIRECTORY +
                        File.separator + new Date() + Constants.IMAGE_EXTENSION;
                imageReader.setOnImageAvailableListener(new ImageAvailableListener(mediaProjection,
                        screenShotImageName, mWidth, mHeight), drawerItemClickListener.getImageHandler());
                mediaProjection.createVirtualDisplay("screencap", mWidth, mHeight, density,
                        flags, imageReader.getSurface(), null, drawerItemClickListener.getImageHandler());
            }
        } else if(requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK ) {
            if(data.getBooleanExtra(ASSingleImageEnhanceActivity.CHANGED_KEY, false)) {
                if(data.getBooleanExtra(ASSingleImageEnhanceActivity.ROTATED_KEY, false)) {
                    ASImage image = ASImageHandler.getImageAt(0);
                    Bitmap bitmap = image.getProcessedThumbnailBitmap();
                    Matrix matrix = new Matrix();
                    matrix.postRotate(data.getIntExtra(ASSingleImageEnhanceActivity.ROTATED_ANGLE_KEY, 0));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    image.setProcessedBitmap(rotatedBitmap);
                }

                new EditImageTask().execute();
            }
        }
    }

    private class EditImageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            getFragmentManager().beginTransaction().replace(R.id.container, HorizontalPhotoGalleryFragment.newInstance(1)).commit();
        }

        @Override
        protected void onPreExecute() {
            getFragmentManager().beginTransaction().replace(R.id.ImageFrame, new BaseFragment()).commit();
            getFragmentManager().beginTransaction().replace(R.id.container, new BaseFragment()).commit();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ASImage image = ASImageHandler.getImageAt(0);
            Bitmap bitmap = image.getProcessedThumbnailBitmap();
            FileUtils.saveScreenShot(bitmap, Constants.SCREENSHOT_DIRECTORY +
                    File.separator + image.getImageName());
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }


    /**
     * Initializes the drawer for the ARView activity.
     */
    private void createAndInitializeDrawer() {

        ObjectDrawerItem[] drawerItems;

        drawerItems = addDrawerItems();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        mDrawerLayout.openDrawer(Gravity.LEFT);
        DrawerItemCustomAdapter drawerItemCustomAdapter =
                new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItems);
        addDrawerToggleListener();

        mDrawerList.setAdapter(drawerItemCustomAdapter);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        drawerItemClickListener = new DrawerItemClickListener(mDrawerLayout, layoutInflater, getFragmentManager(), mDrawerList, this, VIDEO_URL, POPUP_TEXT);
        mDrawerList.setOnItemClickListener(drawerItemClickListener);
    }

    private ObjectDrawerItem[] addDrawerItems() {

        ObjectDrawerItem[] drawerItems = new ObjectDrawerItem[NUMBER_OF_MENU_ITEMS];

        drawerItems[0] = new ObjectDrawerItem(R.drawable.ic_video_library_black_24dp, "Videos");
        drawerItems[1] = new ObjectDrawerItem(R.drawable.ic_video_library_black_24dp, "Description");
        drawerItems[2] = new ObjectDrawerItem(R.drawable.ic_camera_enhance_black_24dp, "ScreenShot");
        drawerItems[3] = new ObjectDrawerItem(R.drawable.ic_camera_enhance_black_24dp, "SlideShow");
        return drawerItems;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    /**
     * the grabber controller for the drawer appearing on the ARview.
     */
    private void addDrawerToggleListener() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_view_headline_white_36dp, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    public void inflatePicture(String imagePath) throws FileNotFoundException {

        imagePathFull = imagePath;

        FragmentManager fm = getFragmentManager();
        ImageViewFragment targetFragment = null;
        targetFragment = ImageViewFragment.newInstance(imagePath, "abc");

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.ImageFrame, targetFragment).commit();

    }

    private void updateNativeDisplayParameters() {
        Display d = getWindowManager().getDefaultDisplay();
        int orientation = d.getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        int dpi = dm.densityDpi;
        nativeDisplayParametersChanged(orientation, w, h, dpi);
    }

    public void onFragmentInteraction(Uri uri) {
        Log.d("ARTraining", "shownInteraction");

    }

    public void onFragmentInteraction(String id) {
        Log.d("ARTraining", "shownInteraction");
    }

    public void onFragmentInteraction(int actionId) {
        Log.d("ARTraining", "shownInteraction");
    }

}

