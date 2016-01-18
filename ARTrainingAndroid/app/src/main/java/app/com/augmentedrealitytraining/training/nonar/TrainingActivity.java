package app.com.augmentedrealitytraining.training.nonar;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.imr.IMRData;
import app.com.augmentedrealitytraining.imr.IMRMedia;
import app.com.augmentedrealitytraining.imr.dal.CompleteTrainingDal;
import app.com.augmentedrealitytraining.imr.pojo.CompleteTraining;
import app.com.augmentedrealitytraining.training.ar.ContentFragment;


public class TrainingActivity extends ListActivity {
    public insertCompleteTabletTask insertTask;
    private String TAG="TrainingActivity";
    private String json;
    private JSONArray contentArray;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.activity_training);
        String apiURL="https://imr-api.azurewebsites.net/";
        GetJsonTask myTask=new GetJsonTask();
        String[] params={apiURL+"/Media","Media"};
       // String[]geoparams={apiURL+"/Question","Question"};
       if(IMRData.get(this).isMediaListEmpty()){
                myTask.execute(params);}
        else//the data is already loaded to the mdeialist
       {
           setmyListAdapter();
       }
        HashSet<String> set=new HashSet<String>();
        getSharedPreferences("ARTraining", Context.MODE_PRIVATE).edit().putStringSet("read", set);


    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (getListAdapter() != null) ((IMRMediaAdapter) getListAdapter()).notifyDataSetChanged();
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

    public void onResponseReceived(String result, String caller) {
        // TODO Auto-generated method stub
        this.json = result;
        if(caller.equals("Media")) {
            getIMRMediaData();
        }

    }

    public void getIMRMediaData()
    {
        try {
            Log.d("Json Returned", json);
            contentArray = new JSONArray(json);
            addListData(contentArray);
        }catch (Exception e)
        {

        }
    }

    public void addListData(JSONArray contentArray)
    {

        try {
            for (int i = 0; i < contentArray.length(); i++) {
                JSONObject contentObject = contentArray.getJSONObject(i);

                JSONObject mobject=contentObject.getJSONObject("MediaType");
                String mediaType=mobject.getString("TypeName");
                int id=contentObject.getInt("Media_Id");
                String title=contentObject.getString("Title");
                String description=contentObject.getString("Description");
                String url=contentObject.getString("Url");
                String path=contentObject.getString("Path");
                String sequence=contentObject.getString("Seq");
                String place=contentObject.getString("Place");
                IMRMedia imr=new IMRMedia(id,mediaType,title,description,url,path,sequence,place);
                IMRData.get(this).addMedia(imr);
               // IMRData.mediaList.add(imr);
            }
           // ArrayAdapter<IMRMedia> adapter=new ArrayAdapter<IMRMedia>(this,android.R.layout.simple_list_item_1,mediaList);

           // setListAdapter(adapter);
            setmyListAdapter();

        }catch(Exception e){Log.e(TAG, "jsonContent Array failed to parse");}
    }

    public void setmyListAdapter()
    {
        IMRMediaAdapter mediaAdapter=new IMRMediaAdapter(this,IMRData.get(this).getMediaList());
        setListAdapter(mediaAdapter);
    }
    @Override
    public void onListItemClick(ListView l, View v,int position,long id)
    {
        IMRMedia amedia=((IMRMediaAdapter)getListAdapter()).getItem(position);
        //amedia.setRead(true);
        IMRData.get(TrainingActivity.this).updateMediaList(amedia.getTitle());
        boolean loggedin= getSharedPreferences("ARTraining",Context.MODE_PRIVATE).getBoolean("Logged In",false);
        if(loggedin) {
            String email=getSharedPreferences("ARTraining", Context.MODE_PRIVATE).getString("email","");
            Log.d(TAG,email+","+amedia.getTitle());
            CompleteTraining atraining = new CompleteTraining(email, amedia.getTitle());
            insertTask=new insertCompleteTabletTask(atraining);
            insertTask.execute();
        }

        Intent intent=new Intent(this,ContentActivity.class);
        intent.putExtra(ContentFragment.Content_ID,amedia.getId());
        startActivity(intent);
    }
    public class GetJsonTask extends AsyncTask<String, Integer, String> {

        private static final String TAG = "GetJsonTask";
        // public TaskIF delegate = null;
        private String caller;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.d(TAG, "JSON Async start");
            String result = null;
            InputStream is = null;
            this.caller = params[1];
            // Making HTTP request
            try {
                // defaultHttpClient
                String jsonHolder = "{\"ApplicationToken\":\"D5200DE9CB4503E5FC99AEDA827F3BC2\"}";
                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
                HttpPost httpPost = new HttpPost();
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setURI(new URI(params[0]));
                httpPost.setEntity(new StringEntity(jsonHolder));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch(Exception e){
                //Log.d(TAG, e.toString());
            }


            return result;
        }

        protected void onProgressUpdate(Integer num)
        {}

        protected void onPostExecute(String result)
        {
           // Log.d(TAG,result);
            onResponseReceived(result, caller);
        }


    }

    public class insertCompleteTabletTask extends  AsyncTask<Void,Void,Boolean>
    {
        private String TAG="insertCompleteTableTask";
        private CompleteTraining cTraining;

        public insertCompleteTabletTask(CompleteTraining t)
        {
            cTraining=t;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            try{
                boolean exist=false;
                List results = CompleteTrainingDal.getCompleteTrainingsByEmail(cTraining.getEmail());
                Log.d(TAG,cTraining.getEmail()+","+cTraining.getTrainingTitle());
                Log.d(TAG, "result size: " + results.size());
                if (!results.isEmpty())
                {
                    for (int i = 0; i < results.size(); i++)
                    {
                        CompleteTraining t = (CompleteTraining) results.get(i);
                        if(t.getTrainingTitle().equals(cTraining.getTrainingTitle()))
                        {
                            exist=true;
                        }
                    }

                }
                if(!exist)
                {
                    CompleteTrainingDal.insertCompleteTraining(cTraining);
                    return true;
                }
               return true;
            }catch (Exception e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            insertTask=null;
            if(success)
            {
                Log.d(TAG,"INSERT INTO COMPLETE TABLE SUCCESSFULLY");
            }
            else
            {
                Log.d(TAG,"FAIL TO INSERT INTO COMPLETE TABLE");
            }
        }
    }
    public class IMRMediaAdapter extends ArrayAdapter<IMRMedia>
    {
        Activity myActivity;
        public IMRMediaAdapter(Activity act,ArrayList<IMRMedia> m)
        {
            super(act,0,m);
            myActivity=act;
           // Log.d(TAG,"ADAPTER");
        }

        @Override
        public View getView(int position,View convertView, ViewGroup parent)
        {
            if(convertView==null)
            {
                convertView=myActivity.getLayoutInflater().inflate(R.layout.list_item_media,null);
            }
            IMRMedia media=getItem(position);
            TextView textView=(TextView)convertView.findViewById(R.id.mediaListItem);
            textView.setText(media.getTitle());
            ImageView image = (ImageView) convertView.findViewById(R.id.checkIcon);

            if(media.isRead()) {
                image.setImageResource(R.drawable.check_icon);
                image.setVisibility(View.VISIBLE);
            }
            else
            {
                image.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

    }



}
