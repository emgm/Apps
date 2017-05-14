package project.apps;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button btnSolicitar;
    private EditText etNombre, etTelefono, etDireccionRecogida, etDireccionEntrega;
    private Spinner spinner;


    DatabaseReference db;
    DatabaseReference mensajeRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar my_ToolBar = (Toolbar) findViewById(R.id.myToolBar);

        setSupportActionBar(my_ToolBar); // se da soporte para todas las versiones

        //Elemento button

        btnSolicitar = (Button) findViewById(R.id.btnSolicitar);

        btnSolicitar.setOnClickListener(this);

        // fin Elemento button

        //Elemento EditText

        etNombre = (EditText) findViewById(R.id.etNombre);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        etDireccionRecogida = (EditText) findViewById(R.id.etDireccionRecogida);
        etDireccionEntrega = (EditText) findViewById(R.id.etDireccionEntrega);

        //Fin elemento EditText

        //Conexion a la base de datos

        db = FirebaseDatabase.getInstance().getReference();

        mensajeRef = db.child("pedidos");

        //Elemento Spinner

        spinner = (Spinner) findViewById(R.id.spinner);

        String[] rol = {"Seleccione", "Domicilio", "Compra y/o Envio"};

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rol));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // vacio

            }
        });

        //Fin Elemento Spinner

    }

    // Metodo para las acciones del boton

    @Override
    public void onClick(View v) {


        switch (v.getId()) {


            case R.id.btnSolicitar:

                ArrayList datos = new ArrayList();
                ArrayList mail = new ArrayList();

                datos.add(spinner.getSelectedItem().toString());
                datos.add(etNombre.getText().toString());

                datos.add(etTelefono.getText().toString());

                datos.add(etDireccionRecogida.getText().toString());
                datos.add(etDireccionEntrega.getText().toString());


                String resultado =  validarCampos(datos);
                String resultado2 = isOnline();

                if(resultado.equals("") && resultado2.equals("")){

                    mensajeRef = db.child("pedidos").push();

                    mensajeRef.child("Direccion Recogida").setValue(datos.get(3));
                    mensajeRef.child("Nombre").setValue(datos.get(1));
                    mensajeRef.child("Servicio").setValue(datos.get(0));
                    mensajeRef.child("Telefono").setValue(datos.get(2));
                    mensajeRef.child("Direccion Entrega").setValue(datos.get(4));
                    mensajeRef.child("Estado").setValue("Por asignar mensajero");
                    mensajeRef.child("Mensajero").setValue("No asignado");
                    mensajeRef.child("Observaciones").setValue("Ninguna");
                    mensajeRef.child("Activo").setValue("SI");

                    resultado = "En tres minutos un representante confirmara su solicitud.";

                    spinner.setSelection(0);
                    etNombre.setText(null);
                    etTelefono.setText(null);

                    etDireccionRecogida.setText(null);
                    etDireccionEntrega.getText().clear();

                    etDireccionEntrega.setFocusable(false);

                    etDireccionEntrega.setFocusableInTouchMode(true);

                    alertMensaje(resultado, resultado2);


                }else{

                  alertMensaje(resultado, resultado2);

                }

                break;
        }

    }//Fin metodo acciones del boton


    // Metodo para validar campos

    public String validarCampos(ArrayList datos){

        String resul="";

        for (int i = 0; i < datos.size(); i++) {

            if (datos.get(i).equals("") || datos.get(i).equals("Seleccione")) {

                resul = "Por favor diligencie todos los campos.";

            }


        }

        return resul;
    }

    //Verificar Conexion a internet

    public String isOnline(){

        String enabled = "";

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable())) {

            enabled = "Revise su conexión de internet.";

        }

        return enabled;
    }


    //Metodo para mostar mensajes de alertas

    public void alertMensaje(String resultado, String resultado2){



        new AlertDialog.Builder(this)

                .setTitle("Aviso")
                .setMessage(resultado + "\n" + "\n" + resultado2)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Some stuff to do when ok got clicked
                    }
                })
                .show();

    }




}




