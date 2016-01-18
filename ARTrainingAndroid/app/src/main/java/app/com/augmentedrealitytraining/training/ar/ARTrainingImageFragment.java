package app.com.augmentedrealitytraining.training.ar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.training.ar.slideshow.BaseFragment;

/**
 * Created by Anmol on 11/16/2015.
 */
public class ARTrainingImageFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View imageView = inflater.inflate(R.layout.fragment_image, container, false);
        /*ImageView imageView1 = (ImageView) imageView.findViewById(R.id.image);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
        imageView1.setLayoutParams(layoutParams);*/
        return imageView;
    }

}
