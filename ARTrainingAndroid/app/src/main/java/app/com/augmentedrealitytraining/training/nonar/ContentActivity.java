package app.com.augmentedrealitytraining.training.nonar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.training.ar.ContentFragment;
import app.com.augmentedrealitytraining.training.nonar.QuizActivity;


public class ContentActivity extends FragmentActivity {
    private String TAG="ContentActivity";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragmentContainer);
        if(fragment==null)
        {
            fragment=new ContentFragment();
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.quizzes)
        {
            Intent intent=new Intent(this,QuizActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
