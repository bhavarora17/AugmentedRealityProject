package app.com.augmentedrealitytraining.login;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.imr.dal.UserDal;


public class ForgotFragment extends Fragment {
    private String TAG="forgot password";
    private EditText mEmailView;
    private Button mFindButton;
    private forgotPasswordTask mTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_forgot, container, false);
        getActivity().getActionBar().setTitle("Forgot Password");
        mEmailView=(EditText)rootView.findViewById(R.id.forgot_email);
        mFindButton=(Button)rootView.findViewById(R.id.find_button);

        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Email: " + mEmailView.getText().toString());
                mTask = new forgotPasswordTask(mEmailView.getText().toString());
                mTask.execute((Void) null);
            }
        });
        return rootView;
    }

    public void showDialog(boolean flag)
    {
        if(flag)
        {
            new AlertDialog.Builder(getActivity()).setTitle("Success").
                    setMessage("The email is sent successfully. Please check your email. ").setPositiveButton("OK",null)
                    .show();
        }
    }
   public void sendEmail(String email,String password)
   {
       Log.d(TAG,"SEND EMAIL:");
       try {

           String host = "smtp.gmail.com";
           String body = "Dear user, \n"+"\n"+"Your password is "+password+". Please change your password after logging in. Thank you. \n"+
                  "\n"+ "AR Training Team";
           String subject = "Find Password";
           //Set the properties
           Properties props = new Properties();
           props.setProperty("mail.smtp.ssl.enable","true");
           // Set the session here
           Session session = Session.getDefaultInstance(props);
           MimeMessage msg = new MimeMessage(session);
           msg.setSubject(subject);
           msg.setText(body);
           Log.d(TAG,body);
           msg.setFrom(new InternetAddress("customerService@ARTraining.com", "AR Training Team"));
           msg.addRecipient(Message.RecipientType.TO,
                   new InternetAddress(email));
           Transport t = session.getTransport("smtps");
           Log.d(TAG,"CONNECT:");
           t.connect(host, "findbrutusosu@gmail.com", "FindMe12!");
           Log.d(TAG,"SEND TO RECIPIENTS");
           t.sendMessage(msg, msg.getAllRecipients());
           t.close();
       }catch (Exception e)
       {
           e.printStackTrace();
       }
       Log.d(TAG,"SEND");
   }


    public class forgotPasswordTask extends AsyncTask<Void,Void,Boolean>
    {
        private String email;
        private String password;

        forgotPasswordTask(String s)
        {
            email=s;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                return (UserDal.getUserByEmail(email) != null);
            } catch (Exception e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mTask = null;
            if (success) {
                Log.d(TAG, "The email is found");
                //send email
                //sendEmail(email,password);
                SendMailTask mailTask=new SendMailTask(email,password);
                mailTask.execute();
                showDialog(true);
               // Toast.makeText(getActivity(),
               //         "Email is sent successfully", Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(getActivity(),"The email is not found",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }

    private class SendMailTask extends AsyncTask<Void, Void, Void> {
        private String memail;
        private String mpassword;

        public SendMailTask(String email,String password)
        {
            memail=email;
            mpassword=password;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            Log.d(TAG, "ONPOSTEXECUTE");

        }

        @Override
        protected Void doInBackground(Void ...params) {
            try {
                Log.d(TAG, "EMAIL TASK DOINBACKGROUND");
               // Transport.send(messages[0]);
                sendEmail(memail,mpassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
