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
import android.widget.EditText;
import android.widget.TextView;

import com.organon.helda.R;
import com.organon.helda.app.HeldaApp;
import com.organon.helda.app.data.NetworkManager;
import com.organon.helda.app.fragments.SettingsFragment;
import com.organon.helda.app.services.ServiceHelper;
import com.organon.helda.app.services.WorkerService;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.usecases.workerLogin.WorkerLoginResponseMessage;


public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    HeldaApp app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        app = (HeldaApp)getApplication();

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        final EditText user = findViewById(R.id.username);
        final EditText pass = findViewById(R.id.password);
        final Button submit = findViewById(R.id.submit);
        final Button logout = findViewById(R.id.logout);
        final Button scan = findViewById(R.id.scanStart);
        final TextView errorMessage = findViewById(R.id.errorMessage);

        scan.setVisibility(View.GONE);
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString();
                String password = pass.getText().toString();

                NetworkManager.getInstance(MenuActivity.this);

                new WorkerService(HeldaApp.getContext()).workerLogin(username, password, new ServiceHelper.Listener<WorkerLoginResponseMessage>() {
                    @Override
                    public void onComplete(WorkerLoginResponseMessage response) {
                        if (response.workerID == -1) {
                            errorMessage.setText("Inicio de sesión incorrecta");
                        } else {
                            app.workerID = ""+response.workerID;
                            scan.setVisibility(View.VISIBLE);
                            logout.setVisibility(View.VISIBLE);
                            errorMessage.setVisibility(View.GONE);
                            submit.setVisibility(View.GONE);
                            user.setVisibility(View.GONE);
                            pass.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        logout.setVisibility(View.GONE);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.workerID = "-1";

                logout.setVisibility(View.GONE);
                scan.setVisibility(View.GONE);
                user.setVisibility(View.VISIBLE);
                pass.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {

        final Button logout = findViewById(R.id.logout);
        final Button scan = findViewById(R.id.scanStart);
        final EditText user = findViewById(R.id.username);
        final EditText pass = findViewById(R.id.password);
        final Button submit = findViewById(R.id.submit);
        final TextView errorMessage = findViewById(R.id.errorMessage);
        errorMessage.setVisibility(View.GONE);

        if (app.workerID.equals("-1")) {
            logout.setVisibility(View.GONE);
            scan.setVisibility(View.GONE);
            user.setVisibility(View.VISIBLE);
            pass.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        } else {
            scan.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);
            pass.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        }

        super.onResume();
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
