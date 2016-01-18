package app.com.augmentedrealitytraining.training.nonar;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.imr.IMRData;
import app.com.augmentedrealitytraining.imr.IMRQuestion;

public class QuizActivity extends FragmentActivity {

    private String TAG="QUIZEActivity";
    private String json;
    private JSONArray contentArray;
    private static int mCurrentIndex=0; //initialize to 0
    private IMRQuestion currentQuestion;
    private String myAnswer="";
    private TextView titleView;
    private RadioGroup questionView;
    private Button submitButton;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //mCurrentIndex=0;
        setContentView(R.layout.activity_quize);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        titleView=(TextView)findViewById(R.id.quizeTitle);
        questionView=(RadioGroup)findViewById(R.id.radioGroup);
        submitButton=(Button)findViewById(R.id.submitButton);
        nextButton=(ImageButton)findViewById(R.id.nextButton);
        previousButton=(ImageButton)findViewById(R.id.previousButton);
        editText=(EditText)findViewById(R.id.editText);

        questionView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioId = group.getCheckedRadioButtonId();
                RadioButton button = (RadioButton) findViewById(radioId);
                myAnswer = button.getText().toString();
                Log.d(TAG, myAnswer);
            }
        });



        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex!=0) mCurrentIndex=(mCurrentIndex-1)% IMRData.get(QuizActivity.this).getQuestionList().size();
                updateQuestion();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex=(mCurrentIndex+1)%IMRData.get(QuizActivity.this).getQuestionList().size();
                updateQuestion();
            }
        });


        String apiURL="https://imr-api.azurewebsites.net/";
        GetJsonTask myTask=new GetJsonTask();
        String[]params={apiURL+"/Question","Question"};
        if(IMRData.get(this).isQuestionListEmpty())
            myTask.execute(params);
        else
            updateQuestion();


    }

    public void onResponseReceived(String result, String caller) {
        // TODO Auto-generated method stub
        this.json = result;
        if(caller.equals("Question")) {
            Log.d(TAG,json);
           try {
               contentArray = new JSONArray(json);
               for(int i=0;i<contentArray.length();i++)
               {
                   JSONObject object=contentArray.getJSONObject(i);
                   String hint="";
                   JSONArray hintArray=object.getJSONArray("Hints");
                   if(hintArray.length()>0) {
                       JSONObject hintsObject = hintArray.getJSONObject(0);
                       hint = hintsObject.getString("Description");
                   }

                   JSONObject typeObject=object.getJSONObject("QuestionType");
                   String type=typeObject.getString("Name");

                   int id=object.getInt("QA_Id");
                   String title=object.getString("Title");
                   String question=object.getString("Question");
                   String answer=object.getString("Answer");
                   String[] questions=question.split("\r\n");
                   //Log.d(TAG,question);
                   IMRQuestion aq=new IMRQuestion(id,hint,type,title,questions,answer);
                   IMRData.get(this).addQustion(aq);
               }
               updateQuestion();
           }catch (Exception e)
           {
               e.printStackTrace();
           }

        }

    }

    public void updateQuestion()
    {
        IMRQuestion question=IMRData.get(this).getQuestionList().get(mCurrentIndex);
        currentQuestion=question;
       // Log.d(TAG,question.getTitle());
        titleView=(TextView)findViewById(R.id.quizeTitle);
        titleView.setText(question.getTitle());
        String[] qs=question.getQuestion();
        questionView.removeAllViews();
        if(question.getQuestionType().equals("YesNo Qeustion")||question.getQuestionType().equals("Multiple Choice")) {
            editText.setVisibility(View.INVISIBLE);
            for (int i = 0; i < qs.length; i++) {
                RadioButton rButton=new RadioButton(this);
                rButton.setText(qs[i]);
                questionView.addView(rButton);
            }

            submitButton.setText("Submit");
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentQuestion = IMRData.get(QuizActivity.this).getQuestionList().get(mCurrentIndex);
                    Log.d(TAG, "Submit," + currentQuestion.getAnswer());
                    if (currentQuestion.getAnswer().equals(myAnswer)) {
                        Toast.makeText(QuizActivity.this, "Congratulations.Your answer is right.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(QuizActivity.this, "Your answer is not right. The right answer is " + currentQuestion.getAnswer(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        else if(question.getQuestionType().equals("Fill in the blank"))
        {
            for (int i = 0; i < qs.length; i++) {
                RadioButton rButton=new RadioButton(this);
                rButton.setText(qs[i]);
                rButton.setFocusable(false);
                questionView.addView(rButton);
            }
            editText.setVisibility(View.VISIBLE);

            submitButton.setText("Submit");
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAnswer = editText.getText().toString();
                    currentQuestion = IMRData.get(QuizActivity.this).getQuestionList().get(mCurrentIndex);
                    Log.d(TAG, "Submit," + currentQuestion.getAnswer());
                    if (currentQuestion.getAnswer().equals(myAnswer)) {
                        Toast.makeText(QuizActivity.this, "Congratulations.Your answer is right.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(QuizActivity.this, "Your answer is not right. The right answer is " + currentQuestion.getAnswer(), Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
        else//other
        {
            editText.setVisibility(View.INVISIBLE);
            submitButton.setText("Start ARView");
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    public class GetJsonTask extends AsyncTask<String, Integer, String> {

        // public TaskIF delegate = null;
        private String caller;

        private static final String TAG = "GetJsonTask";

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
            Log.d(TAG, result);
            onResponseReceived(result, caller);
        }


    }
}
