package com.organon.helda.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.organon.helda.app.data.HttpPlanGateway;
import com.organon.helda.app.data.InMemoryPlanGateway;
import com.organon.helda.app.data.NetworkManager;
import com.organon.helda.app.services.PlanService;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.R;
import com.organon.helda.core.entities.Task;

import java.io.IOException;


public class BarcodeReaderActivity extends AppCompatActivity {

    private static final String MODEL = "1PW2A4LKNQ78FKD0";
    private static final String LOCALE = "es";
    public static final int PERMISSIONS_CAMERA_REQUEST = 2;
    BarcodeDetector detector;
    CameraSource cameraSource;
    SurfaceView cameraView;
    SurfaceHolder cameraHolder;
    String detectedBarcodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        NetworkManager.getInstance(this);
        TextView txtView = (TextView) findViewById(R.id.textView);
        detector = new BarcodeDetector.Builder(getApplicationContext()).build();
        if(!detector.isOperational()){
            txtView.setText("Could not set up the detector!");
            return;
        }
        cameraView = (SurfaceView)findViewById(R.id.cameraSurface);
        cameraHolder = cameraView.getHolder();
        scanBarcode();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView) findViewById(R.id.textView);
                            textView.setText(detectedBarcodes);
                        }
                    });

                    PlanService.getPlan(MODEL,LOCALE,new HttpPlanGateway(), new PlanService.Listener()
                    {
                        @Override
                        public void onComplete (Object response){
                            Intent intent = new Intent(BarcodeReaderActivity.this, DisassemblyActivity.class);
                            intent.putExtra("currentPlan", (Plan) response);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

}
