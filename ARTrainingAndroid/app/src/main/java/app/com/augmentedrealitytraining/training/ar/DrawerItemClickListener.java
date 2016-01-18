package app.com.augmentedrealitytraining.training.ar;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.training.ar.slideshow.BaseFragment;
import app.com.augmentedrealitytraining.training.ar.slideshow.HorizontalPhotoGalleryFragment;

/**
 * Created by Anmol on 10/9/2015.
 */
public class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private LayoutInflater layoutInflater;
    private FragmentManager fragmentManager;
    private ListView mDrawerList;
    private TrainingActivity trainingActivity;
    private WebView webView;
    private MediaProjectionManager mProjectionManager;
    private Handler mImageHandler;
    private View popUpViewVideo;
    private View popUpViewText;
    private String videoURL;
    PopupWindow videoPopup;
    PopupWindow textPopUp;
    private String popUpText;

    private static int count = 0;

    public DrawerItemClickListener(DrawerLayout mDrawerLayout,
                                   LayoutInflater layoutInflater, FragmentManager fragmentManager,
                                   ListView mDrawerList, TrainingActivity trainingActivity, String videoUrl, String popupText) {
        this.mDrawerLayout = mDrawerLayout;
        this.layoutInflater = layoutInflater;
        this.fragmentManager = fragmentManager;
        this.mDrawerList = mDrawerList;
        this.trainingActivity = trainingActivity;
        this.videoURL = videoUrl;
        this.popUpText = popupText;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {

        switch (position) {
            case 0:
                loadAndPlayVideo();
                break;
            case 1:
                if (popUpViewText == null) {
                    popUpViewText = layoutInflater.inflate(R.layout.fragment_text, null);
                    TextView textView = (TextView) popUpViewText.findViewById(R.id.textView);
                    textView.setText(getPopUpText());
                    textPopUp = popupWindow(popUpViewText, false);
                }

                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 2:
                takeScreenShot();
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 3:
                mDrawerLayout.closeDrawer(mDrawerList);
                showScreenShotSlideshow();
                break;
            default:
                break;
        }
    }

    private void showScreenShotSlideshow() {
        FragmentManager fm = fragmentManager;
        BaseFragment targetFragment;
        targetFragment = HorizontalPhotoGalleryFragment.newInstance(1);


        FragmentTransaction fragmentTransaction = fm.
                beginTransaction();

        if (count % 2 == 0) {

            if (count == 2) {
                count = 0;
            }

            count++;
            fragmentTransaction
                    .replace(R.id.container, targetFragment);
            fragmentTransaction.commit();

        } else {
            count++;

            fragmentTransaction
                    .replace(R.id.container, new BaseFragment()).commit();

        }
    }

    public void changeTextView() {
        if (textPopUp != null && textPopUp.isShowing()) {
            TextView textView = (TextView) popUpViewText.findViewById(R.id.textView);
            textView.setText(getPopUpText());
            textPopUp.setContentView(textView);
        }
    }

    private void loadAndPlayVideo() {
        if (popUpViewVideo == null) {
            popUpViewVideo = layoutInflater.inflate(R.layout.fragment_video, null);
            webView = (WebView) popUpViewVideo.findViewById(R.id.webview2);
            WebSettings webSettings = webView.getSettings();
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setJavaScriptEnabled(true);
            String html = getHTML(getVideoURL());

            webView.loadData(html, "text/html", "utf-8");
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                }
            });

            videoPopup = popupWindow(popUpViewVideo, true);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @NonNull
    private String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    private String getPopUpText() {
        return popUpText;
    }

    public void setPopUpText(String popUpText) {
        this.popUpText = popUpText;
    }

    public String getHTML(String videoId) {

        String html =
                "<iframe class=\"youtube-player\" "
                        + "style=\"border: 0; width: 100%; height: 95%;"
                        + "padding:0px; margin:0px\" "
                        + "id=\"ytplayer\" type='application/x-shockwave-flash' allowscriptaccess='always' allowfullscreen='true' "
                        + "src=\"http://www.youtube.com/embed/" + videoId
                        + "?fs=0\" frameborder=\"0\" " + "allowfullscreen autobuffer "
                        + "controls onclick=\"this.play()\">\n" + "</iframe>\n";

        return html;
    }

    private PopupWindow popupWindow(final View popUpView, final boolean isVideoPopup) {
        final PopupWindow popupWindow = new PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final Display defaultDisplay = trainingActivity.getWindowManager().getDefaultDisplay();
        popupWindow.setWidth(defaultDisplay.getWidth()/2);
        popupWindow.setHeight(defaultDisplay.getHeight()/2);

        View titlebar = popUpView.findViewById(R.id.titlebar);
        titlebar.setOnTouchListener(new View.OnTouchListener() {

            private int dx = 0;
            private int dy = 0;
            int mPosX = 20;
            int mPosY = 50;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        dx = (int) (mPosX - motionEvent.getRawX());
                        dy = (int) (mPosY - motionEvent.getRawY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mPosX = (int) (motionEvent.getRawX() + dx);
                        mPosY = (int) (motionEvent.getRawY() + dy);
                        popupWindow.update(mPosX, mPosY, -1, -1);
                        break;
                }
                return true;
            }
        });
        ImageView close = (ImageView) popUpView.findViewById(R.id.closeIV);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogBoxAndClosePopup(isVideoPopup, popupWindow);

            }

        });

        popupWindow.showAtLocation(mDrawerLayout, 1, 0, 0);
        popUpView.setOnTouchListener(new View.OnTouchListener() {
            int orgX = 0, orgY = 0;
            int offsetX, offsetY;

            int orgWidth, orgHeight;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        orgX = (int) event.getX();
                        orgY = (int) event.getY();

                        orgWidth = v.getMeasuredWidth();
                        orgHeight = v.getMeasuredHeight();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        offsetX = (int) event.getX() - orgX;
                        offsetY = (int) event.getY() - orgY;
                        int deviceDefaultHeight = defaultDisplay.getHeight();
                        int deviceDefaultWidth = defaultDisplay.getWidth();
                        int width = orgWidth + offsetX;
                        int height = orgHeight + offsetY;
                        if (height < deviceDefaultHeight / 3.5) {
                            height = deviceDefaultHeight/3;
                        } else {
                            if (height > deviceDefaultHeight) {
                                height = deviceDefaultHeight;
                            }
                        }
                        if (width < deviceDefaultWidth/3) {
                            width = deviceDefaultWidth/2;
                        } else if (width > deviceDefaultWidth) {
                            width = deviceDefaultWidth;
                        }
                        popupWindow.update(
                                width,
                                height);
                        break;

                }
                return true;
            }

        });
        return popupWindow;
    }

    private void showDialogBoxAndClosePopup(final boolean isVideoPopup, final PopupWindow popupWindow) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(trainingActivity);

        // set the message to display
        alertbox.setMessage("Do you really want to quit?");

        alertbox.setCancelable(false);
        if (isVideoPopup) {
            webView.onPause();
        }
        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                if (isVideoPopup) {
                    webView.destroy();
                    popUpViewVideo = null;
                    popupWindow.dismiss();
                    Toast.makeText(trainingActivity, "Video Closed", Toast.LENGTH_LONG).show();
                } else {
                    popUpViewText = null;
                    popupWindow.dismiss();
                    Toast.makeText(trainingActivity, "Description Closed", Toast.LENGTH_LONG).show();
                }
            }
        });
        // set a negative/no button and create a listener
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {

            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                if (isVideoPopup) webView.onResume();
            }
        });
        alertbox.show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void takeScreenShot() {
        // call for the projection manager
        mProjectionManager = (MediaProjectionManager) trainingActivity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mImageHandler = new Handler();
                Looper.loop();
            }
        }.start();

        trainingActivity.startActivityForResult(mProjectionManager.createScreenCaptureIntent(), TrainingActivity.IMAGE_REQUEST_CODE);
    }

    public Handler getImageHandler() {
        return mImageHandler;
    }

    public MediaProjectionManager getProjectionManager() {
        return mProjectionManager;
    }

    public WebView getWebView() {
        return webView;
    }


}
