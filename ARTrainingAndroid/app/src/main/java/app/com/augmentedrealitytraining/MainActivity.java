package app.com.augmentedrealitytraining;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import app.com.augmentedrealitytraining.login.LoginActivity;
import app.com.augmentedrealitytraining.login.ProfileActivity;
import app.com.augmentedrealitytraining.training.ar.TrainingActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });

        Button arButton = (Button) findViewById(R.id.artraining);
        arButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arButtonClick();
            }
        });
    }

    public void arButtonClick()
    {
        Intent intent=new Intent(this,TrainingActivity.class);
        startActivity(intent);
    }
    public void buttonClick()
    {
        Intent intent=new Intent(this, app.com.augmentedrealitytraining.training.nonar.TrainingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.log_in)
        {
            Boolean isLoggedIn = getSharedPreferences("ARTraining", Context.MODE_PRIVATE).getBoolean("Logged In",false);

            if(isLoggedIn){
                Intent intent=new Intent(this, ProfileActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
