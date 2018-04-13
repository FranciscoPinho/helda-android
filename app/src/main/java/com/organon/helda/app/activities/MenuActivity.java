package com.organon.helda.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.organon.helda.R;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.entities.Plan;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button scan = findViewById(R.id.scanStart);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText serverIPedit = findViewById(R.id.serverIP);
                String serverIP = serverIPedit.getText().toString();
                if(Utils.isValidIPV4(serverIP)){
                    System.setProperty("serverIP",serverIP.trim());
                    Intent intent = new Intent(MenuActivity.this, BarcodeReaderActivity.class);
                    startActivity(intent);
                }
                else{
                    TextView error = findViewById(R.id.errorText);
                    error.setText("IP del servidor inválido");
                }
            }
        });
    }
}
