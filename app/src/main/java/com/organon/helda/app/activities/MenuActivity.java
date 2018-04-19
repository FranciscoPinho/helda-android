package com.organon.helda.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.organon.helda.R;
import com.organon.helda.app.utils.Utils;

public class MenuActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button scan = findViewById(R.id.scanStart);
        sharedPref = MenuActivity.this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText serverIPedit = findViewById(R.id.serverIP);
                final String serverIP = serverIPedit.getText().toString();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(Utils.isValidIPV4(serverIP)){
                            editor.putString("serverIP",serverIP.trim());
                            System.clearProperty("serverIP");
                            System.setProperty("serverIP",serverIP.trim());
                            editor.commit();
                            Intent intent = new Intent(MenuActivity.this, BarcodeReaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView error = findViewById(R.id.errorText);
                                    error.setText("IP del servidor inválido");
                                }
                            });
                        }
                    }
                });


            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        if(sharedPref.contains("serverIP")){
            EditText serverIPedit = findViewById(R.id.serverIP);
            System.setProperty("serverIP",sharedPref.getString("serverIP",""));
            serverIPedit.setText(sharedPref.getString("serverIP",""));
        }
    }
}
