package com.organon.helda.app.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.organon.helda.app.data.HttpPlanGateway;
import com.organon.helda.app.data.NetworkManager;
import com.organon.helda.app.services.PlanService;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.R;

import java.io.IOException;


public class BarcodeReaderActivity extends AppCompatActivity {

    public static final int PERMISSIONS_CAMERA_REQUEST = 2;
    BarcodeDetector detector;
    BroadcastReceiver connectivity_receiver;
    CameraSource cameraSource;
    SurfaceView cameraView;
    SurfaceHolder cameraHolder;
    String detectedBarcodes;
    Boolean connectivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        NetworkManager.getInstance(this);
        TextView txtView = findViewById(R.id.textView);
        connectivity= Utils.isNetworkAvailable(this);
        connectivity_receiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connectivity=Utils.isNetworkAvailable(context);
            }
        };
        registerReceiver(connectivity_receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        detector = new BarcodeDetector.Builder(getApplicationContext()).build();
        if(!detector.isOperational()){
            txtView.setText("Could not set up the detector!");
            return;
        }
        cameraView = findViewById(R.id.cameraSurface);
        cameraHolder = cameraView.getHolder();
        scanBarcode();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivity_receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    cameraSource.start(cameraView.getHolder());
                }
                catch(SecurityException e){
                    e.printStackTrace();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            } else {
                finish();
            }
        }
    }

    private void scanBarcode() {
        cameraView.setZOrderMediaOverlay(true);
        cameraSource = new CameraSource.Builder(BarcodeReaderActivity.this, detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(900, 900)
                .build();
        cameraHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(BarcodeReaderActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarcodeReaderActivity.this, new String[]{Manifest.permission.CAMERA},PERMISSIONS_CAMERA_REQUEST);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                        for(int i=0; i<barcodes.size();i++){
                           detectedBarcodes = "Barcode "+i+": "+barcodes.valueAt(i).rawValue+"\n";
                        }

                        if(connectivity) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cameraSource.stop();
                                    TextView textView = findViewById(R.id.textView);
                                    textView.setText(detectedBarcodes);
                                }
                            });
                            PlanService.getPlan("TESTE", "es", new HttpPlanGateway(), new PlanService.Listener() {
                                @Override
                                public void onComplete(Object response) {
                                    if (response == null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    cameraSource.start(cameraView.getHolder());
                                                }
                                                catch(SecurityException e){
                                                    e.printStackTrace();
                                                }
                                                catch(IOException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        return;
                                    }
                                    Intent intent = new Intent(BarcodeReaderActivity.this, DisassemblyActivity.class);
                                    intent.putExtra("currentPlan", (Plan) response);
                                    startActivity(intent);
                                }
                            });

                        }
                        else runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BarcodeReaderActivity.this, R.string.noConnection, Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }

}
