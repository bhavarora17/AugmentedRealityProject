package app.com.augmentedrealitytraining.training.ar;

/**
 * Created by Anmol on 9/19/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.augmentedrealitytraining.R;

/**
 * Created by Anmol on 9/10/2015.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {

    private Context context;
    private int resource;
    private ObjectDrawerItem[] objects;

    public DrawerItemCustomAdapter(Context context, int resource, ObjectDrawerItem[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem;
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        listItem = layoutInflater.inflate(resource, parent, false);

        ImageView imageView = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textView = (TextView) listItem.findViewById(R.id.textViewName);

        ObjectDrawerItem folder = objects[position];
        imageView.setImageResource(folder.icon);
        textView.setText(folder.name);

        return listItem;
    }
}

