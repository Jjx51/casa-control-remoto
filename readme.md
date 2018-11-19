# Casa a control remoto

## Introducción
La siguiente aplicación móvil sirve para controlar de forma remota una casa por medio de bluetooth, en la aplicación se muestran las habitaciones de una casa de forma oscura si uno presiona sobre cualquiera de los cuartos se encenderá la luz , cuando se prende o se apaga la luz de un cuarto este mandará una señal a un dispositivo bluetooth para que a partir de esta señal el dispositivo bluetooth sepa que es lo que tiene que hacer al mismo tiempo que enviá una señal al bluetooth la aplicación se conecta a Firebase para cambiar los estados de las habitaciones para que por medio de una página web se pueda observar el comportamiento que esta teniendo la casa.

## Clases principales
La aplicación móvil cuenta con 3 Actividades:
### SplashActivity
Esta clase unicamente mostrará una imagen principal por unos segundos y luego desaparecerá.

### DeviceListActivity
Esta clase mostrará los dispositivos bluetooths vinculados anteriormente , seleccionaremos el dispositivo con el cual nos comunicaremos y nos mandará a la actividad Main.

### MainActivity
Esta clase va a mantener toda la lógica del programa, aquí recuperará los datos del bluetooth de la vista anterior para poder crear el canal de comunicación , también aquí se vinculan los botones para que cuando el usuario aplique un botón en la aplicación esta mande una señal por bluetooth y al mismo tiempo se conecta a Firebase.

### Funcionamiento de código
El código cuenta con las siguientes habitaciones:
* Habitación 1
* Habitación 2
* Jardín
* Entrada
* Sala
* Cocina
* Wc
* Cuarto de lavado
Ademas de contar con dos variables especiales más que son:
* Puerta
* Ventana
Desde la aplicación se manda otra señal para poder abrir una puerta al bluetooth y la aplicación esta a la espera de una variable para saber el estado de la ventana para unicamente cambiar un texto (Abierta/Cerrada).

Al contar con 8 habitaciones mucho del código que se encuentra es análogo.
Se tiene una variable boolean por cada habitación para saber si se encuentre prendido o apagado la luz.
Se lee por primera vez todos los valores desde Firebase para colocar las habitaciones prendidas y apagadas correspondientemente.
Conforme se van prendiendo o apagando se van cambiando los valores en Firebase.

También cuenta con una función RGB para poder mandar los tres colores, estos se activan primero mandando una señal "B" y después los valores de los colores de forma correspondiente al RGB con valores entre 0 y 255 con un intervalo de .5 segundos entre cada envío.


## Función para enviar al bluetooth
Para poder controlar de manera fácil el bluetooth el codigo de MainActivity conoce una función la cual es la siguiente: 
	
~~~~
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
	
~~~~

Solamente hace falta pasarle a la función el string que nosotros vamos a querer mandar al bluetooth.

## Función para recibir del bluetooth
Para poder recibir valores desde el bluetooth tenemos la siguiente función:
	
~~~~
    private void recibirVariableBluetooth(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (btSocket!=null){

                    try{
                        String variableRecibida="";
                        btSocket.getInputStream().read(variableRecibida.getBytes());

                    }catch (IOException e){
                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // Inicia el hilo
        t.start();
    }
~~~~

Como podemos darnos cuenta para recibir valores del bluetooth colocamos este procedimiento dentro de un hilo para no afectar al funcionamiento principal de la aplicación.