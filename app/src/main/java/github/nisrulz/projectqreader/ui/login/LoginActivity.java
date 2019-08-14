package github.nisrulz.projectqreader.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.ksoap2.serialization.SoapObject;

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
        final EditText dateEditText = findViewById(R.id.date);
        final Button loginButton = findViewById(R.id.login);
        final ToggleButton toogleTurno = findViewById(R.id.selectorTurno);
        final String[] textoToogle = new String[1];
        final String[] arrayAcceso = new String[2];

        toogleTurno.setChecked(false);
        toogleTurno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    textoToogle[0] = "M";
                } else {
                    textoToogle[0] = "T";
                }
            }
        });

        loginButton.setEnabled(true);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = usernameEditText.getText().toString();
                final String pass = passwordEditText.getText().toString();
                final String turno = textoToogle[0];
                final String fecha = dateEditText.getText().toString();

                if (user == null || pass == null || turno == null || fecha == null) {

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

                                            sesionUsuario.putString("turno", turno);
                                            sesionUsuario.putString("fecha", fecha);
                                            sesionUsuario.putString("apynom", arrayAcceso[1]);
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
