package com.organon.helda.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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

        Button settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .addToBackStack("setttings")
                        .commit();
            }
        });
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
