package app.com.augmentedrealitytraining.training.ar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.imr.IMRData;
import app.com.augmentedrealitytraining.imr.IMRMedia;

public class ContentFragment extends android.support.v4.app.Fragment {
    private String TAG = "ContentFragment";
    public static final String Content_ID = "CONTENT_ID";

    private IMRMedia data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int dataid = (Integer) getActivity().getIntent().getSerializableExtra(Content_ID);
        data = IMRData.get(getActivity()).getMedia(dataid);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_media, parent, false);
        getActivity().getActionBar().setTitle(data.getTitle());
        Log.d(TAG, data.getMediaType());
        Log.d(TAG, data.getDescription());

        if (data.getMediaType().equals("Video")) {

            WebView wv = (WebView) v.findViewById(R.id.web_media);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setPluginState(PluginState.ON);

            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            String html = getHTML(data.getUrl());
            wv.setWebChromeClient(new WebChromeClient() {
            });
            wv.loadDataWithBaseURL("", html, mimeType, encoding, "");
        } else { //Text
            Log.d(TAG, "not video");
            WebView wv = (WebView) v.findViewById(R.id.web_media);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setPluginState(PluginState.ON);
            wv.getSettings().setLoadsImagesAutomatically(true);
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            String html = data.getDescription();
            wv.setWebChromeClient(new WebChromeClient() {
            });
            wv.loadDataWithBaseURL("", html, mimeType, encoding, "");
        }
        return v;

    }

    public String getHTML(String str) {
        String html = "<iframe class=\"youtube-player\" style=\"border: 0; width: 100%; height: 95%; padding:0px; margin:0px\" id=\"ytplayer\" type=\"text/html\" src=\"" + str
                + "?fs=0\" frameborder=\"0\">\n"
                + "</iframe>\n";
        return html;
    }

}
