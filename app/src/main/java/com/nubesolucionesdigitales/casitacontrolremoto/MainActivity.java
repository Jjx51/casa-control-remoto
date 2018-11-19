package com.nubesolucionesdigitales.casitacontrolremoto;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonHabitacionUno;
    Button buttonHabitacionDos;
    Button buttonJardin;
    Button buttonEntrada;
    Button buttonSala;
    Button buttonCocina;
    Button buttonWc;
    Button buttonCuartoLavado;
    Button buttonDesconectar;
    Button buttonAbrirPuerta;
    Button buttonReiniciar;

    TextView textViewEstadoPuerta;
    TextView textViewEstadoVentana;
    /**/
    boolean habitacionUnoPrendido=false;
    boolean habitacionDosPrendido=false;
    boolean jardinPrendido=false;
    boolean entradaPrendido=false;
    boolean salaPrendido=false;
    boolean cocinaPrendido=false;
    boolean wcPrendido=false;
    boolean cuartoLavadoPrendido=false;

    boolean puertaAbierta =false;
    boolean ventanaAbierta=false;

    /*CODIGO PARA CONTROLAR BLUETOOTH*/
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /*variables de Firebase*/
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceHabitacion1=firebaseDatabase.getReference("habitacion1");
    DatabaseReference databaseReferenceHabitacion2=firebaseDatabase.getReference("habitacion2");
    DatabaseReference databaseReferenceCocina=firebaseDatabase.getReference("cocina");
    DatabaseReference databaseReferenceCuartoLavado=firebaseDatabase.getReference("cuartoLavado");
    DatabaseReference databaseReferenceEntrada=firebaseDatabase.getReference("entrada");
    DatabaseReference databaseReferenceJardin=firebaseDatabase.getReference("jardin");
    DatabaseReference databaseReferenceSala=firebaseDatabase.getReference("sala");
    DatabaseReference databaseReferencePuerta =firebaseDatabase.getReference("puerta");
    DatabaseReference databaseReferenceWc=firebaseDatabase.getReference("wc");
    DatabaseReference databaseReferenceVentana=firebaseDatabase.getReference("ventana");

    /**/
    EditText editTextRed;
    EditText editTextGreen;
    EditText editTextBlue;
    Button buttonRgb;

    /**/
    String bluetoothSala="A";
    String bluetoothCuartoLavado="F";
    String bluetoothWc="C";
    String bluetoothCocina="B";
    String bluetoothEntrada="G";
    String bluetoothJardin="H";
    String bluetoothHabitacionDos="E";
    String bluetoothHabitacionUno="D";
    String bluetoothPuerta="I";
    String bluetoothRGB="B";
    String bluetoothReiniciar="X";
    /*Se manda esta variable para verificar el estado de la ventana*/
    String bluetoothChecarEstadoVentana="W";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        obtenerVariables();
        establecerClickListener();
        cargarEstadosFirebase();

        /**/
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceListActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device
        new ConnectBT().execute(); //Call the class to connect
        recibirVariableBluetooth();
    }

    private void establecerClickListener() {
        buttonHabitacionUno.setOnClickListener(this);
        buttonHabitacionDos.setOnClickListener(this);
        buttonJardin.setOnClickListener(this);
        buttonEntrada.setOnClickListener(this);
        buttonSala.setOnClickListener(this);
        buttonCocina.setOnClickListener(this);
        buttonCuartoLavado.setOnClickListener(this);
        buttonWc.setOnClickListener(this);

        buttonDesconectar.setOnClickListener(this);
        buttonAbrirPuerta.setOnClickListener(this);
        buttonReiniciar.setOnClickListener(this);

        buttonRgb.setOnClickListener(this);
    }

    private void obtenerVariables() {
        buttonHabitacionUno=findViewById(R.id.buttonHabitacionUno);
        buttonHabitacionDos=findViewById(R.id.buttonHabitacionDos);
        buttonJardin=findViewById(R.id.buttonJardin);
        buttonEntrada=findViewById(R.id.buttonEntrada);
        buttonSala=findViewById(R.id.buttonSala);
        buttonCocina=findViewById(R.id.buttonCocina);
        buttonWc=findViewById(R.id.buttonWc);
        buttonCuartoLavado=findViewById(R.id.buttonCuartoLavado);

        buttonDesconectar=findViewById(R.id.buttonDesconectar);

        textViewEstadoPuerta =findViewById(R.id.textViewEstadoPuerta);
        buttonAbrirPuerta =findViewById(R.id.buttonAbrirPuerta);

        buttonReiniciar=findViewById(R.id.buttonReiniciar);

        editTextRed=findViewById(R.id.editTextRed);
        editTextGreen=findViewById(R.id.editTextGreen);
        editTextBlue=findViewById(R.id.editTextBlue);
        buttonRgb=findViewById(R.id.buttonEnviarRgb);

        textViewEstadoVentana=findViewById(R.id.textViewEstadoVentana);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.buttonHabitacionUno:
                iluminarHabitacionUno();
                break;
            case R.id.buttonHabitacionDos:
                iluminarHabitacionDos();
                break;
            case R.id.buttonJardin:
                iluminarJardin();
                break;
            case R.id.buttonEntrada:
                iluminarEntrada();
                break;
            case R.id.buttonCocina:
                iluminarCocina();
                break;
            case R.id.buttonWc:
                iluminarWc();
                break;
            case R.id.buttonCuartoLavado:
                iluminarCuartoLavado();
                break;
            case R.id.buttonSala:
                iluminarSala();
                break;
            case R.id.buttonDesconectar:
                desconectar();
                break;
            case R.id.buttonAbrirPuerta:
                abrirPuerta();
                break;
            case R.id.buttonReiniciar:
                reiniciar();
                break;
            case R.id.buttonEnviarRgb:
                mandarRgb();
                break;

        }
    }

    private void reiniciar() {
        /**/
        habitacionUnoPrendido=false;
        habitacionDosPrendido=false;
        jardinPrendido=false;
        entradaPrendido=false;
        salaPrendido=false;
        cocinaPrendido=false;
        wcPrendido=false;
        cuartoLavadoPrendido=false;
        puertaAbierta =false;
        ventanaAbierta=false;

        /**/
        databaseReferenceSala.setValue(0);
        databaseReferenceCuartoLavado.setValue(0);
        databaseReferenceWc.setValue(0);
        databaseReferenceCocina.setValue(0);
        databaseReferenceEntrada.setValue(0);
        databaseReferenceJardin.setValue(0);
        databaseReferenceHabitacion1.setValue(0);
        databaseReferenceHabitacion2.setValue(0);
        databaseReferencePuerta.setValue(0);
        databaseReferenceVentana.setValue(0);

        /**/
        buttonSala.setBackground(getDrawable(R.drawable.salaoscuro));
        buttonCuartoLavado.setBackground(getDrawable(R.drawable.cocheraoscuro));
        buttonWc.setBackground(getDrawable(R.drawable.wcoscuro));
        buttonCocina.setBackground(getDrawable(R.drawable.cocinaoscuro));
        buttonEntrada.setBackground(getDrawable(R.drawable.entradaoscuro));
        buttonJardin.setBackground(getDrawable(R.drawable.jardinoscuro));
        buttonHabitacionUno.setBackground(getDrawable(R.drawable.habitacion1oscuro));
        buttonHabitacionDos.setBackground(getDrawable(R.drawable.habitacion2oscuro));

        textViewEstadoPuerta.setText("Cerrada");
        buttonAbrirPuerta.setText("Abrir Puerta");
        textViewEstadoVentana.setText("Cerrada");


        mandarVariableBluetooth(bluetoothReiniciar);

        Toast.makeText(this, "Se han reiniciado las variables", Toast.LENGTH_SHORT).show();

    }

    private void iluminarSala() {
        if (salaPrendido){
            /*Se apaga*/
            salaPrendido=false;
            buttonSala.setBackground(getDrawable(R.drawable.salaoscuro));
            databaseReferenceSala.setValue(0);
            mandarVariableBluetooth(bluetoothSala);
        }else {
            /*Se prende*/
            salaPrendido = true;
            buttonSala.setBackground(getDrawable(R.drawable.sala));
            databaseReferenceSala.setValue(1);
            mandarVariableBluetooth(bluetoothSala);
        }
    }

    private void iluminarCuartoLavado() {
        if (cuartoLavadoPrendido){
            /*Se apaga*/
            cuartoLavadoPrendido=false;
            buttonCuartoLavado.setBackground(getDrawable(R.drawable.cocheraoscuro));
            databaseReferenceCuartoLavado.setValue(0);
            mandarVariableBluetooth(bluetoothCuartoLavado);

        }else {
            cuartoLavadoPrendido=true;
            buttonCuartoLavado.setBackground(getDrawable(R.drawable.cochera));
            databaseReferenceCuartoLavado.setValue(1);
            mandarVariableBluetooth(bluetoothCuartoLavado);
        }
    }

    private void iluminarWc() {
        if (wcPrendido){
            /*Se apaga*/

            wcPrendido=false;
            buttonWc.setBackground(getDrawable(R.drawable.wcoscuro));
            databaseReferenceWc.setValue(0);
            mandarVariableBluetooth(bluetoothWc);

        }else {
            wcPrendido=true;
            buttonWc.setBackground(getDrawable(R.drawable.wc));
            databaseReferenceWc.setValue(1);
            mandarVariableBluetooth(bluetoothWc);
        }
    }

    private void iluminarCocina() {
        if (cocinaPrendido){
            /*Se apaga*/

            cocinaPrendido=false;
            buttonCocina.setBackground(getDrawable(R.drawable.cocinaoscuro));
            databaseReferenceCocina.setValue(0);
            mandarVariableBluetooth(bluetoothCocina);

        }else {
            cocinaPrendido=true;
            buttonCocina.setBackground(getDrawable(R.drawable.cocina));
            databaseReferenceCocina.setValue(1);
            mandarVariableBluetooth(bluetoothCocina);
        }
    }

    private void iluminarEntrada() {
        if (entradaPrendido){
            /*Se apaga*/
            entradaPrendido=false;
            buttonEntrada.setBackground(getDrawable(R.drawable.entradaoscuro));
            databaseReferenceEntrada.setValue(0);
            mandarVariableBluetooth(bluetoothEntrada);

        }else {
            entradaPrendido=true;
            buttonEntrada.setBackground(getDrawable(R.drawable.entrada));
            databaseReferenceEntrada.setValue(1);
            mandarVariableBluetooth(bluetoothEntrada);
        }
    }

    private void iluminarJardin() {
        if (jardinPrendido){
            /*Se apaga*/
            jardinPrendido=false;
            buttonJardin.setBackground(getDrawable(R.drawable.jardinoscuro));
            databaseReferenceJardin.setValue(0);
            mandarVariableBluetooth(bluetoothJardin);

        }else {
            jardinPrendido=true;
            buttonJardin.setBackground(getDrawable(R.drawable.jardin));
            databaseReferenceJardin.setValue(1);
            mandarVariableBluetooth(bluetoothJardin);
        }
    }

    private void iluminarHabitacionDos() {
        if (habitacionDosPrendido){
            /*Se apaga*/
            habitacionDosPrendido=false;
            buttonHabitacionDos.setBackground(getDrawable(R.drawable.habitacion2oscuro));
            databaseReferenceHabitacion2.setValue(0);
            mandarVariableBluetooth(bluetoothHabitacionDos);

        }else {
            /*Se apaga*/
            habitacionDosPrendido=true;
            buttonHabitacionDos.setBackground(getDrawable(R.drawable.habitacion2));
            databaseReferenceHabitacion2.setValue(1);
            mandarVariableBluetooth(bluetoothHabitacionDos);
        }
    }

    private void iluminarHabitacionUno() {
        if (habitacionUnoPrendido){
            /*Se apaga*/
            habitacionUnoPrendido=false;
            buttonHabitacionUno.setBackground(getDrawable(R.drawable.habitacion1oscuro));
            databaseReferenceHabitacion1.setValue(0);
            mandarVariableBluetooth(bluetoothHabitacionUno);
        }else {
            habitacionUnoPrendido = true;
            buttonHabitacionUno.setBackground(getDrawable(R.drawable.habitacion1));
            databaseReferenceHabitacion1.setValue(1);
            mandarVariableBluetooth(bluetoothHabitacionUno);
        }
    }

    private void abrirPuerta(){
        if (puertaAbierta){
            /*Se cierra*/
            puertaAbierta =false;
            textViewEstadoPuerta.setText("Cerrada");
            buttonAbrirPuerta.setText("Abrir puerta");
            databaseReferencePuerta.setValue(0);
            mandarVariableBluetooth(bluetoothPuerta);
        }else {
            puertaAbierta = true;
            textViewEstadoPuerta.setText("Abierta");
            buttonAbrirPuerta.setText("Cerrar puerta");
            databaseReferencePuerta.setValue(1);
            mandarVariableBluetooth(bluetoothPuerta);
        }

    }

    private void mandarRgb() {
        Boolean valoresCorrectos=false;
        final String red;
        final String green;
        final String blue;

        /*Se recuepran las variables de los edittext*/
        red=editTextRed.getText().toString();
        green=editTextGreen.getText().toString();
        blue=editTextBlue.getText().toString();

        /*Se pasan las variables a int para comprobar que estan dentro del rango de 0 a 255*/
        int redInt;
        int greenInt;
        int blueInt;

        if (!red.equals("") && !green.equals("") && !blue.equals("")){
            redInt=Integer.parseInt(red);
            greenInt=Integer.parseInt(green);
            blueInt=Integer.parseInt(blue);

            if (redInt>=0 &&redInt<=255){
                if (greenInt>=0 &&greenInt<=255){
                    if (blueInt>=0 &&blueInt<=255){
                        valoresCorrectos=true;
                    }
                }
            }

        }


        if (valoresCorrectos) {
            /*Mandar Bluetooth*/
            mandarVariableBluetooth(bluetoothRGB);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mandarVariableBluetooth(red);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mandarVariableBluetooth(green);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mandarVariableBluetooth(blue);
                                }
                            },500);
                        }
                    },500);
                }
            },500);



            Toast.makeText(this, "RGB mandado", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Los valores del RGB no son correctos", Toast.LENGTH_SHORT).show();
        }
    }


    /*CODIGO PARA CONTROLAR BLUETOOTH*/
    private void desconectar() {
        if (btSocket!=null){ //If the btSocket is busy
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
        finish(); //return to the first layout

    }

    /*Ejemplos para mandar variables por medio del blueetooh
    * Aqui se debe de acomodar para cada metodo*/

    private void mandarVariableBluetooth(String variable){
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(variable.getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void recibirVariableBluetooth(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (btSocket!=null)
                {
                    try
                    {
                        String variableRecibida="";
                        btSocket.getInputStream().read(variableRecibida.getBytes());

                        if (variableRecibida.equals("0")){
                            textViewEstadoVentana.setText("Cerrada");
                            databaseReferenceVentana.setValue(0);
                        }else if (variableRecibida.equals("1")){
                            textViewEstadoVentana.setText("Abierta");
                            databaseReferenceVentana.setValue(1);
                        }else{
                            Toast.makeText(getBaseContext(), "Error recibiendo variable", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Start the thread
        t.start();
    }

    private void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".toString().getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".toString().getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*CONECTAR EL BLUETOOTH*/

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Conectando...", "Porfavor espere!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(MainActivity.this, "Fallo Conexión. Intente otra vez", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Conectado exitosamente", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    /*Lee una unica vez al entrar en la actividad para cargar los estados iniciales de las variables desde Firebase*/

    private void cargarEstadosFirebase() {
        databaseReferenceCocina.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int estadoCocina=dataSnapshot.getValue(Integer.class);
                if (estadoCocina==1){
                    buttonCocina.setBackground(getDrawable(R.drawable.cocina));
                    cocinaPrendido=true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**/

        databaseReferenceCuartoLavado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    buttonCuartoLavado.setBackground(getDrawable(R.drawable.cochera));
                    cuartoLavadoPrendido=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceEntrada.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    buttonEntrada.setBackground(getDrawable(R.drawable.entrada));
                    entradaPrendido=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceHabitacion1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    buttonHabitacionUno.setBackground(getDrawable(R.drawable.habitacion1));
                    habitacionUnoPrendido=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceHabitacion2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    buttonHabitacionDos.setBackground(getDrawable(R.drawable.habitacion2));
                    habitacionDosPrendido=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceJardin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    buttonJardin.setBackground(getDrawable(R.drawable.jardin));
                    jardinPrendido=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceSala.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    buttonSala.setBackground(getDrawable(R.drawable.sala));
                    salaPrendido=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceWc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    buttonWc.setBackground(getDrawable(R.drawable.wc));
                    wcPrendido=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferencePuerta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    puertaAbierta =true;
                    textViewEstadoPuerta.setText("Abierta");
                    buttonAbrirPuerta.setText("Cerrar puerta");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferenceVentana.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class)==1){
                    ventanaAbierta=true;
                    textViewEstadoVentana.setText("Abierta");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
