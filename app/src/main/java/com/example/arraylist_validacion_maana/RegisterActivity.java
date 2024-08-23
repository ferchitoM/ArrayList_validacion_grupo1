package com.example.arraylist_validacion_maana;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    EditText nombres;
    EditText apellidos;
    EditText fechaNacimiento;
    EditText email;
    EditText documento;
    EditText contraseña;
    EditText confirmarContraseña;
    Button   botonRegistrar;

    ArrayList<Validacion> lista;

    //Textos de error
    TextView nombres_error;
    TextView apellidos_error;
    TextView fechaNacimiento_error;
    TextView email_error;
    TextView documento_error;
    TextView contraseña_error;
    TextView confirmarContraseña_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inicializarAtributos();

        //Al hacer clic sobre el campo de fecha de nacimiento se abre el selector de fecha
        fechaNacimiento.setFocusableInTouchMode(false);
        fechaNacimiento.setOnClickListener(v -> {
            seleccionarFecha();
        });

        botonRegistrar.setOnClickListener(v -> {

           boolean todoOK = validarCampos();

           if(todoOK) {
               Intent intent = new Intent(this, MainActivity.class);
               startActivity(intent);
           }
        });

    }

    private void inicializarAtributos(){

        nombres                     = findViewById(R.id.nombres);
        apellidos                   = findViewById(R.id.apellidos);
        fechaNacimiento             = findViewById(R.id.fechaNacimiento);
        email                       = findViewById(R.id.email);
        documento                   = findViewById(R.id.documento);
        contraseña                  = findViewById(R.id.contraseña);
        confirmarContraseña         = findViewById(R.id.confirmarContraseña);
        botonRegistrar              = findViewById(R.id.botonRegistrar);

        //Textos de error
        nombres_error               = findViewById(R.id.nombres_error);
        apellidos_error             = findViewById(R.id.apellidos_error);
        fechaNacimiento_error       = findViewById(R.id.fechaNacimiento_error);
        email_error                 = findViewById(R.id.email_error);
        documento_error             = findViewById(R.id.documento_error);
        contraseña_error            = findViewById(R.id.contraseña_error);
        confirmarContraseña_error   = findViewById(R.id.confirmarContraseña_error);

        lista = new ArrayList<>();

        //Expresiones regulares que se utilizan para verificar que los datos contengan solo los caracteres permitidos
        String exNombres = "^[a-zA-ZÀ-ÿ\\s]+$";
        String exCorreos = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

        //Llenamos la lista con la configuración de cada dato solicitado en el formulario
        lista.add(new Validacion(documento,             "",7, 10,  documento_error,            false));
        lista.add(new Validacion(nombres,               exNombres,  3, 50,  nombres_error,              true));
        lista.add(new Validacion(apellidos,             exNombres,  3, 50,  apellidos_error,            true));
        lista.add(new Validacion(fechaNacimiento,       "",0, 0,   fechaNacimiento_error,      true));
        lista.add(new Validacion(email,                 exCorreos,  0, 50,  email_error,                true));
        lista.add(new Validacion(contraseña,            "",6, 10,  contraseña_error,           true));
        lista.add(new Validacion(confirmarContraseña,   "",6, 10,  confirmarContraseña_error,  true));
    }

    private boolean validarCampos() {

        boolean valido = true;

        for(Validacion elemento: lista){

            String texto = elemento.campo.getText().toString();

            //Si es requerido validamos
            if(elemento.requerido){

                //Si el campo está vacío mostramos un mensaje de error
                if(texto.isEmpty()){
                    elemento.mostrarMensajeError("Campo requerido");
                    valido = false;
                }
                else elemento.ocultaError();

            }
            //Si el campo tiene texto validamos las restricciones
            if(texto.length() > 0) {

                if(elemento.expresion != "" && !texto.matches(elemento.expresion)){
                    elemento.mostrarMensajeError("Formato incorrecto");
                    valido = false;
                }
                else elemento.ocultaError();

                //Validamos el tamaño del texto
                if(elemento.min != 0 && texto.length() < elemento.min){
                    elemento.mostrarMensajeError("El campo debe tener al menos " + elemento.min + " caracteres");
                    valido = false;
                }
                else if(elemento.max != 0 && texto.length() > elemento.max){
                    elemento.mostrarMensajeError("El campo debe tener como máximo " + elemento.max + " caracteres");
                    valido = false;
                }
                else elemento.ocultaError();
            }

        }

        String c1 = contraseña.getText().toString();
        String c2 = confirmarContraseña.getText().toString();

        //Si la contraseña y la confirmación no están vacías validamos que sean iguales
        if(!c1.isEmpty() && !c2.isEmpty()) {
            //Validamos que sean iguales
            if (!c1.equals(c2)) {
                confirmarContraseña_error.setText("Las contraseñas no coinciden");
                confirmarContraseña_error.setVisibility(TextView.VISIBLE);
                valido = false;
            } else {
                confirmarContraseña_error.setVisibility(TextView.INVISIBLE);
            }
        }

        //Retorna true si no se encontró ningún error
        return valido;
    }


    //Clase personalizada para validar los campos
    public class Validacion {

        public EditText campo;
        public String   expresion;
        public int      min;
        public int      max;
        public TextView campoError;
        public boolean  requerido;

        public Validacion(EditText campo, String expresion, int min, int max, TextView campoError, boolean requerido){
            this.campo      = campo;
            this.expresion  = expresion;
            this.min        = min;
            this.max        = max;
            this.campoError = campoError;
            this.requerido  = requerido;
        }

        public void mostrarMensajeError(String mensaje){
            campoError.setVisibility(TextView.VISIBLE);
            campoError.setText("*" + mensaje);
        }

        public void ocultaError(){
            campoError.setVisibility(TextView.INVISIBLE);
        }
    }

    private void seleccionarFecha() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // mes +1 porque Enero es cero
                final String selectedDate = day + "/" + (month + 1) + "/" + year;
                fechaNacimiento.setText(selectedDate);
            }
        });

        newFragment.show(this.getSupportFragmentManager(), "Selector de fecha");
    }

    //Clase predeterminada para seleccionar fechas
    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }

    }

}