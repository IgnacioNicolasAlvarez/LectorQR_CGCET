package github.nisrulz.projectqreader.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

import github.nisrulz.projectqreader.MainActivity;
import github.nisrulz.projectqreader.R;
import github.nisrulz.qreader.ConexionWebService;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ViewGroup radioGroup = findViewById(R.id.radioB);
        final LoginActivity x = this;
        final ArrayList<Evento> listaEvento = new ArrayList<>();
        final int[] indicePresionado = new int[1];
        final String[] arrayAcceso = new String[2];

        Thread nt = new Thread() {
            @Override
            public void run() {

                try {
                    SoapObject respuestaWS = ConexionWebService.getInstancia().getEventosHoy();
                    for (int i = 0; i < respuestaWS.getPropertyCount(); i++) {

                        String tema = respuestaWS.getProperty(i).toString().split("=")[1].split(";")[0];
                        String fecha = respuestaWS.getProperty(i).toString().split("=")[2].split(";")[0];
                        String turno = respuestaWS.getProperty(i).toString().split("=")[3].split(";")[0];
                        String codigo = respuestaWS.getProperty(i).toString().split("=")[4].split(";")[0];
                        String conCosto = respuestaWS.getProperty(i).toString().split("=")[5].split(";")[0];

                        Evento e = new Evento(tema, fecha, turno, codigo, conCosto);
                        listaEvento.add(e);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                for (final Evento evento : listaEvento) {

                    final RadioButton radioButton = new RadioButton(x);
                    radioButton.setId(listaEvento.indexOf(evento));

                    radioButton.setText("Evento: " + evento.getCodigo() +
                            "\nFecha: " + evento.getFecha() +
                            "\nTurno: " + evento.getTurno());

                    radioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            indicePresionado[0] = radioButton.getId();
                            loginButton.setEnabled(true);
                        }
                    });
                    radioGroup.addView(radioButton);
                }
            }
        };

        nt.start();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = usernameEditText.getText().toString();
                final String pass = passwordEditText.getText().toString();

                if (user == null || pass == null) {

                } else {

                    Thread nt = new Thread() {
                        @Override
                        public void run() {

                            try {

                                SoapObject respuestaWS = ConexionWebService.getInstancia().getVerificarContraseña(user, pass);
                                for (int i = 1; i < respuestaWS.getPropertyCount(); i++) {
                                    arrayAcceso[0] = respuestaWS.getProperty(0).toString();
                                    arrayAcceso[1] = respuestaWS.getProperty(1).toString();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (arrayAcceso[0] != null) {

                                        if (arrayAcceso[0].equals("aprobado")) {

                                            SharedPreferences prefs =
                                                    getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);

                                            SharedPreferences.Editor sesionUsuario = prefs.edit();

                                            sesionUsuario.putString("apynom", arrayAcceso[1]);
                                            sesionUsuario.putString("turno", listaEvento.get(indicePresionado[0]).getTurno());
                                            sesionUsuario.putString("fecha", listaEvento.get(indicePresionado[0]).getFecha());

                                            sesionUsuario.commit();

                                            cargarLector();
                                        } else {

                                            Toast.makeText(LoginActivity.this, "Número de DNI, o contraseña erroneas." +
                                                    " Vuelva a intentarlo.", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Ha ocurrido un problema con la red." +
                                                " Vuelva a intentarlo.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    };
                    nt.start();
                }
            }
        });

    }

    public void cargarLector() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
