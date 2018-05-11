package com.organon.helda.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.organon.helda.R;
import com.organon.helda.app.fragments.SettingsFragment;
import com.organon.helda.app.utils.Utils;

public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Button scan = findViewById(R.id.scanStart);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String serverIP =  sharedPref.getString("serverIP","");
                final String port = sharedPref.getString("port","");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(Utils.isValidIPV4(serverIP) && Utils.isValidPort(port)){
                            Intent intent = new Intent(MenuActivity.this, BarcodeReaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            Utils.resetSystemProperties(sharedPref);
                            startActivity(intent);
                        } else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView error = findViewById(R.id.errorText);
                                    error.setText("IP/Port del servidor inválido");
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .addToBackStack("settings")
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
