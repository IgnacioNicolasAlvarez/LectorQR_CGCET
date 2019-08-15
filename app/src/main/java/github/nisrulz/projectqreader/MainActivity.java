/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.projectqreader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.ksoap2.serialization.SoapPrimitive;

import github.nisrulz.qreader.ConexionWebService;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class MainActivity extends AppCompatActivity {

    private static final String cameraPerm = Manifest.permission.CAMERA;
    private RelativeLayout currentLayout;
    private SurfaceView mySurfaceView;
    private QREader qrEader;
    boolean hasCameraPermission = false;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasCameraPermission = RuntimePermissionUtil.checkPermissonGranted(this, cameraPerm);
        currentLayout = findViewById(R.id.activity_main);

        Button restartbtn = findViewById(R.id.btn_restart_activity);
        restartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartActivity();
            }
        });

        mySurfaceView = findViewById(R.id.camera_view);

        if (hasCameraPermission) {
            setupQREader();
        } else {
            RuntimePermissionUtil.requestPermission(MainActivity.this, cameraPerm, 100);
        }


    }

    void restartActivity() {
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finish();
    }

    void setupQREader() {

        final String[] auxStrings = new String[1];

        try {
            qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
                @Override
                public void onDetected(final String lecturaQR) {

                    new Thread(new Runnable() {
                        public void run() {

                            qrEader.stop();
                            try {
                                SharedPreferences sesionUsuario = getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
                                final String turno = sesionUsuario.getString("turno", "");
                                final String apynom = sesionUsuario.getString("apynom", "");
                                final String fecha = sesionUsuario.getString("fecha", "");
                                final String codigo = sesionUsuario.getString("codigo", "");
                                final String conCosto = sesionUsuario.getString("conCosto", "");

                                SoapPrimitive s = ConexionWebService.getInstancia().getEscribirAsistencia(lecturaQR, turno, apynom, fecha, codigo, conCosto);
                                auxStrings[0] = s.getValue().toString();

                                switch (auxStrings[0]) {
                                    case "S":
                                        currentLayout.setBackgroundColor(Color.GREEN);
                                        break;
                                    case "NI":
                                        currentLayout.setBackgroundColor(Color.CYAN);
                                        break;
                                    default:
                                        currentLayout.setBackgroundColor(Color.RED);
                                        break;

                                }

                            } catch (Exception ex) {
                                currentLayout.setBackgroundColor(Color.YELLOW);
                                qrEader.releaseAndCleanup();
                                ex.printStackTrace();
                            } finally {
                                qrEader.start();
                            }

                        }

                    }).start();
                }
            }).facing(QREader.BACK_CAM)
                    .enableAutofocus(true)
                    .height(mySurfaceView.getHeight())
                    .width(mySurfaceView.getWidth())
                    .build();
        } catch (Exception ex) {
            currentLayout.setBackgroundColor(Color.RED);
            qrEader.releaseAndCleanup();
            ex.printStackTrace();
        } finally {
            qrEader.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (hasCameraPermission) {
            qrEader.releaseAndCleanup();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasCameraPermission) {
            qrEader.initAndStart(mySurfaceView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == 100) {
            RuntimePermissionUtil.onRequestPermissionsResult(grantResults, new RPResultListener() {
                @Override
                public void onPermissionGranted() {
                    if (RuntimePermissionUtil.checkPermissonGranted(MainActivity.this, cameraPerm)) {
                        restartActivity();
                    }
                }

                @Override
                public void onPermissionDenied() {
                    // do nothing
                }
            });
        }
    }
}
