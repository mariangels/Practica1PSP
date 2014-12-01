package com.example.mariangeles.practica1psp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;


public class Principal extends Activity {
    EditText etNombre;
    EditText etURL;
    ImageView img;
    RadioButton publico;
    RadioButton privado;

    File f;
    String dir;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad);

        etNombre=(EditText)findViewById(R.id.etNombre);
        etURL=(EditText)findViewById(R.id.etURL);
        img = (ImageView) findViewById(R.id.imageView);
        publico=(RadioButton) findViewById(R.id.rbPublico);
        privado=(RadioButton) findViewById(R.id.rbPrivado);
    }



    public void descargar(View view){
        Hebra h = new Hebra();
        h.execute();
    }

    private class Hebra extends AsyncTask<String, Objects, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fichero();
            cargarImagen();
        }

        @Override
        protected Bitmap doInBackground(String[] objects) {
            guardarImagen(direccion());
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            verImagen();
            pDialog.dismiss();
        }
    }


    private void fichero(){
        dir=etURL.getText().toString();
        if(extension().equalsIgnoreCase("png") || extension().equalsIgnoreCase("jpg")){
            String nombre=etNombre.getText().toString();
            if(nombre.equalsIgnoreCase("")){
                Tostada("No le has puesto nombre");
                nombre=dir.substring(dir.lastIndexOf("/")+1);
            }
            if (publico.isChecked()) {
                f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"/"+ nombre);
            } else if (privado.isChecked()) {
                f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) +"/"+ nombre);
            }

            etNombre.setText(nombre);
        }else{
            Tostada("Extension incorrecta");
        }
    }

    private void cargarImagen(){
        pDialog = new ProgressDialog(Principal.this);
        pDialog.setMessage("Cargando Imagen");
        pDialog.setCancelable(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    private String extension(){
        return dir.substring(dir.lastIndexOf(".")+1);
    }

    private URL direccion(){
        dir=etURL.getText().toString();
        URL url=null;
        try {
            url=new URL(dir);
        } catch (MalformedURLException e) {
            Tostada("La direccion no es correcta");
        }
        return url;
    }

    private boolean guardarImagen (URL url){
        if(!url.equals(null)) {
            try {
                URLConnection urlCon = url.openConnection();
                InputStream is = urlCon.getInputStream();
                FileOutputStream fos = new FileOutputStream(f);
                byte[] by = new byte[1000];
                int temp = is.read(by);
                while (temp > 0) {
                    fos.write(by, 0, temp);
                    temp = is.read(by);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }else{
            return false;
        }
    }

    private void verImagen(){
        Bitmap imagen = BitmapFactory.decodeFile(f.getAbsolutePath());
        img.setImageBitmap(imagen);
    }

    public void Tostada(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}