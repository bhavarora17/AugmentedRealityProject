package app.com.augmentedrealitytraining.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.augmentedrealitytraining.MainActivity;
import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.imr.IMRData;
import app.com.augmentedrealitytraining.imr.dal.CompleteTrainingDal;
import app.com.augmentedrealitytraining.imr.pojo.CompleteTraining;


public class ProfileActivity extends Activity {

    private TextView nameText;
    private TextView emailText;
    private ListView listView;
    private TextView completeText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        nameText=(TextView)findViewById(R.id.profileName);
        emailText=(TextView)findViewById(R.id.profileEmail);
        listView=(ListView)findViewById(R.id.completeList);
        completeText=(TextView)findViewById(R.id.completeText);

        String email=getSharedPreferences("ARTraining", Context.MODE_PRIVATE).getString("email","");
        String firstname=getSharedPreferences("ARTraining",Context.MODE_PRIVATE).getString("firstname","");
        String lastname=getSharedPreferences("ARTraining",Context.MODE_PRIVATE).getString("lastname","");
        nameText.setText(firstname+" "+lastname);
        emailText.setText(email);

        getCompeleteTask task=new getCompeleteTask(email);
        task.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int idd = item.getItemId();
        //log out
        if(idd==R.id.ProfileLogout)
        {
            getSharedPreferences("ARTraining",Context.MODE_PRIVATE).edit().putBoolean("Logged In",false).commit();
            getSharedPreferences("ARTraining",Context.MODE_PRIVATE).edit().putBoolean("admin",false).commit();
            getSharedPreferences("ARTraining",Context.MODE_PRIVATE).edit().putString("email", "").commit();
            getSharedPreferences("ARTraining",Context.MODE_PRIVATE).edit().putString("firstname","").commit();
            getSharedPreferences("ARTraining",Context.MODE_PRIVATE).edit().putString("lastname", "").commit();

            //to main page
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        if(idd==R.id.ProfileResetpawd)
        {
            Intent intent=new Intent(this,ResetPasswordActivity.class);
            startActivity(intent);
            return true;
        }

        if(idd==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    public void getListView(ArrayList<String> result)
    {
        int size=result.size();
        String s="";
        if(size==0)
            s="You haven't completed any training yet.";
        else
        {
            s="You have completed "+size+" trainings: ";
        }
        completeText.setText(s);
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, result));

    }
    public class getCompeleteTask extends AsyncTask<Void,Void,ArrayList<String>> {

        private String TAG="getCompleteTask";
        private ArrayList<String> list=new ArrayList<String>();
        private String email;

        public getCompeleteTask(String e)
        {
            email=e;
        }

        @Override
        protected ArrayList<String> doInBackground(Void ...params)
        {
            try{
                List results = CompleteTrainingDal.getCompleteTrainingsByEmail(email);
                if (!results.isEmpty())
                {
                    for (int i = 0; i < results.size(); i++)
                    {
                        CompleteTraining training = (CompleteTraining) results.get(i);
                        String title = training.getTrainingTitle();
                        list.add(title);
                    }
                }

            }catch (Exception e)
            {
                e.printStackTrace();

            }
            return list;
        }

        @Override
        protected  void onPostExecute(ArrayList<String> result)
        {
            for(int i=0;i<result.size();i++)
            {
                Log.d(TAG, email + "," + result.get(i));
                IMRData.get(ProfileActivity.this).addComplete(result.get(i));
                IMRData.get(ProfileActivity.this).updateMediaList(result.get(i));
            }
            getListView(result);



        }
    }
}
