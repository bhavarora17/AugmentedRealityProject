package app.com.augmentedrealitytraining.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.com.augmentedrealitytraining.R;
import app.com.augmentedrealitytraining.imr.dal.UserDal;
import app.com.augmentedrealitytraining.imr.pojo.User;


public class RegisterFragment extends Fragment {
    private UserRegisterTask mTask;
    private EditText rPasswordView;
    private EditText rEnterPasswordView;
    private EditText rEmailView;
    private EditText rFirstName;
    private EditText rLastName;
    private View mProgressView;

    private String TAG="RegisterFragment";
    public RegisterFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.w("Class", "Too");
        super.onCreate(savedInstanceState);

        setRetainInstance(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w("Class", "Here Three");
        View rootView = inflater.inflate(R.layout.register_login, container, false);

        getActivity().getActionBar().setTitle("Register");

        rFirstName = (EditText) rootView.findViewById(R.id.firstname);
        rLastName = (EditText)rootView.findViewById(R.id.lastname);
        rEmailView=(EditText)rootView.findViewById(R.id.email_signup_text);
        rPasswordView = (EditText)rootView.findViewById(R.id.password_register);
        rEnterPasswordView = (EditText)rootView.findViewById(R.id.password_confirmation);
        mProgressView=rootView.findViewById(R.id.register_progress);

        Button mNewUserButton = (Button) rootView.findViewById(R.id.new_user_button);
        mNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getActivity().getApplicationContext(), "Registration Coming Soon!!", Toast.LENGTH_SHORT).show();
                Log.d("RegisterFragment", rPasswordView.getText().toString() + "," + rEnterPasswordView.getText().toString());
                boolean cancel = false;

                if (rEmailView.getText().toString().isEmpty()) {
                    cancel = true;
                    rEmailView.setError("The email can't be empty.");
                    //   Toast.makeText(getActivity().getApplicationContext(), "The Email or password can't be empty.", Toast.LENGTH_SHORT)
                    //           .show();
                }
                if(!isEmailValid(rEmailView.getText().toString()))
                {
                    cancel=true;
                    rEmailView.setError("This email is not valid.");
                }
                if (rPasswordView.getText().toString().isEmpty()) {
                    cancel = true;
                    rPasswordView.setError("The password can't be empty.");
                }
                if (!isPasswordValid(rPasswordView.getText().toString())) {
                    cancel = true;
                    rPasswordView.setError("The password is too short.");
                }
                if (!rPasswordView.getText().toString().equals(rEnterPasswordView.getText().toString()) ||
                        rEnterPasswordView.getText().toString().isEmpty()) {
                    cancel = true;
                    rEnterPasswordView.setError("The confirmation password is not the same.");
                    // Toast.makeText(getActivity().getApplicationContext(), "The password is not the same.", Toast.LENGTH_SHORT)
                    //         .show();
                }
                if (!cancel) {
                    User user = new User();
                    user.setEmail(rEmailView.getText().toString());
                    user.setPassword(rPasswordView.getText().toString());
                    user.setFirstName(rFirstName.getText().toString());
                    user.setLastName(rLastName.getText().toString());

                    showProgress(true);
                    mTask = new UserRegisterTask(user);
                    mTask.execute((Void) null);

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Can't register.Check your input", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        return rootView;
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private User newUser;


        UserRegisterTask(User user) {
            newUser=user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            try{

                //check if this user is already registered
                User user = UserDal.getUserByEmail(newUser.getEmail());
                if (user == null) {
                    //then register this user
                    UserDal.insertUser(newUser);
                    getActivity().getSharedPreferences("ARTraining",Context.MODE_PRIVATE).edit().putString("email",newUser.getEmail()).commit();
                    getActivity().getSharedPreferences("ARTraining", Context.MODE_PRIVATE).edit().putString("firstname", newUser.getFirstName()).commit();
                    getActivity().getSharedPreferences("ARTraining", Context.MODE_PRIVATE).edit().putString("lastname", newUser.getLastName()).commit();
                    return true;
                }
                //if the email already registered
                else
                {
                    return false;
                }

            } catch (Exception e)
            {
                return false;
            }

            // TODO: register the new account here.
            //  return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mTask = null;
            if (success) {
                //getPreferences(Context.MODE_PRIVATE).edit().putBoolean("Logged In",true).commit();
                getActivity().getSharedPreferences("ARTraining",Context.MODE_PRIVATE).edit().putBoolean("Logged In",true).commit();
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                Log.d(TAG, "IS LOGGEDIN");
                // invalidateOptionsMenu();
                //finish();
            } else {
                Log.d(TAG, "FAIL TO REGISTER");
                new AlertDialog.Builder(getActivity()).setTitle("Failed to register").
                        setMessage("This email is already registered.").setPositiveButton("OK", null)
                        .show();
                getActivity().getSharedPreferences("ARTraining", Context.MODE_PRIVATE).edit().putBoolean("Logged In", false).commit();

            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
          mTask = null;
        }
    }
}
