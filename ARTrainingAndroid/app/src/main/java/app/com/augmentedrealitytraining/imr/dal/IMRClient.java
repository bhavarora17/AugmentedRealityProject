package app.com.augmentedrealitytraining.imr.dal;

import android.content.Context;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

import app.com.augmentedrealitytraining.training.ar.ARApplication;

class IMRClient {
    private static IMRClient singleton = new IMRClient(ARApplication.getInstance().getApplicationContext());
    private MobileServiceClient mClient;

    private IMRClient(Context context) {
        try {
            mClient = new MobileServiceClient(
                    "https://artraining.azure-mobile.net/",
                    "sCcjHMlgEvrCubCcOglXiGubbIvEsg80",
                    context);
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getName(), "IMR is not reachable");
        }
    }

    public static MobileServiceClient getImrClient() {
        return singleton.mClient;
    }
}
