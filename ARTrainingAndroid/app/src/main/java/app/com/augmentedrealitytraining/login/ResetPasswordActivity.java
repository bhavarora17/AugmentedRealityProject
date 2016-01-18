package app.com.augmentedrealitytraining.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.imr.dal.UserDal;
import app.com.augmentedrealitytraining.imr.pojo.User;


public class ResetPasswordActivity extends Activity{

    private String TAG="ResetPasswordActivity";
    private EditText oldPasswd;
    private EditText newPasswd;
    private EditText confirmation;
    private Button resetButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpasswd);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        oldPasswd=(EditText)findViewById(R.id.resetpwd_oldPwd);
        newPasswd=(EditText)findViewById(R.id.resetpwd_newPwd);
        confirmation=(EditText)findViewById(R.id.resetpwd_confirmation);
        resetButton=(Button)findViewById(R.id.resetpwd_button);


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldp=oldPasswd.getText().toString();
                String newp=newPasswd.getText().toString();
                String confirm=confirmation.getText().toString();
                if(confirm.equals(newp))
                {
                    ResetPwdTask pwdTask=new ResetPwdTask(oldp,newp);
                    pwdTask.execute();

                }
                else
                {
                    confirmation.setError("The confirmation password is not the same");
                }
            }
        });

    }

    public void showDialog(boolean flag)
    {
        if(flag)
        {
            new AlertDialog.Builder(this).setTitle("Success").
                    setMessage("The password is reset successfully. ").setPositiveButton("OK",null)
                    .show();
        }
        else
        {
            new AlertDialog.Builder(this).setTitle("Error").setMessage("Failed to reset the password. Please check your input and try again.")
                    .setPositiveButton("OK",null).show();
        }
    }


    public class ResetPwdTask extends AsyncTask<Void,Void,Boolean>
    {
        private String oldpwd;
        private String newpwd;

        public ResetPwdTask(String oldpass,String newpass)
        {
            oldpwd=oldpass;
            newpwd=newpass;
        }

        @Override
        protected Boolean doInBackground(Void ...params)
        {
            try{
                String email=getSharedPreferences("ARTraining", Context.MODE_PRIVATE).getString("email","");
                User user = UserDal.getUserByEmail(email);

                if (user != null)
                {
                    user = UserDal.getUserByPassword(oldpwd);
                    if (user != null)
                    {
                        //reset the password to database
                        user.setPassword(newpwd);
                        UserDal.updateUser(user);
                        return true;

                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }

            }catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showDialog(success);
        }

    }


}
