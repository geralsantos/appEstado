package com.example.myapplication.ui.donaciones;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Config.Config;
import com.example.myapplication.Interface.ApiService;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.model.clsTipoDocumento;
import com.example.myapplication.data.model.clsCentros;
import com.example.myapplication.data.model.clsTipoCaptura;
import com.example.myapplication.data.model.clsCompFamiliar;
import com.example.myapplication.data.model.clsEstadoEntrega;
import com.example.myapplication.data.model.clsDonaciones;
import com.example.myapplication.ui.gallery.GalleryFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.myapplication.config;
import com.example.myapplication.ui.donaciones.CustomDialogClass;
import com.google.android.material.chip.ChipDrawable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract class TextChangedListener<T> implements TextWatcher {
    private T target;

    public TextChangedListener(T target) {
        this.target = target;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        this.onTextChanged(target, s);
    }

    public abstract void onTextChanged(T target, Editable s);
}
public class DonacionesFragment extends Fragment {

    config conf = new config();
    private DonacionesViewModel mViewModel;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;


    ImageButton btn_camara,btn_camara_benef;
    Button btn_verfotos,btn_verfotos2;
    String TipoFoto="";
    ImageView id_img;
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean permissionToRecordAccepted = false;

    static String actionActive = "";
    static Intent intentData;
    Bitmap imageBitmap;

    Spinner spinner_listado_tipodoc;
    Spinner spinner_listado_tipocaptura;
    Spinner spinner_listado_estadoentrega;

    //Spinner spinner_geo_montaje_listado_incidencia_proyecto;
    EditText edittext_numerodocumento;
    EditText edittext_apellidopaterno;
    EditText edittext_apellidomaterno;
    EditText edittext_nombres;
    EditText edittext_direccion;
    EditText edittext_observacion;
    EditText edittext_centro;
    LinearLayout edittext_comp_familiar;

    Button btn_grabar;
    private ApiService servicio = Config.getRetrofit().create(ApiService.class);
    clsTipoCaptura item =null;

    List<clsTipoCaptura> rowTipoCaptura=null;
    List<String> spnArrTipoCaptura = null;
    String[] TipoCaptura_array = null;
    Integer[] id_TipoCaptura_array = null;

    List<clsTipoDocumento> rowTipoDocumento=null;
    List<String> spnArrTipoDocumento = null;
    String[] TipoDocumento_array = null;
    Integer[] id_TipoDocumento_array = null;

    List<clsCentros> rowCentro=null;
    List<String> spnArrCentro = null;
    String[] Centro_array = null;
    Integer[] id_Centro_array = null;

    List<clsCompFamiliar> rowCompFamiliar=null;
    List<String> spnArrCompFamiliar = null;
    String[] CompFamiliar_array = null;
    Integer[] id_CompFamiliar_array = null;

    List<clsEstadoEntrega> rowEstadoEntrega=null;
    List<String> spnArrEstadoEntrega = null;
    String[] EstadoEntrega_array = null;
    Integer[] id_EstadoEntrega_array = null;

    LocationManager locationManager;
    LocationListener locationListener;
    private static  final int REQUEST_LOCATION=1;
    String latitude="",longitude="";

    boolean isFotoSaving=false;
    SharedPreferences sharedPreferences = null;
    SharedPreferences preferences = null;
    SharedPreferences.Editor editor=null;

    Integer usuario_id=null;
    Integer ubigeo_id=null;
    ProgressBar loadingProgressBar=null;
    LocationListener listener;
    Handler handler = new Handler(); // declared before onCreate
    Runnable myRunnable = null;
    LinearLayout llChips;

    Context contexto;
    View root;
    public void guardar(){

       /* if(true){
            edittext_numerodocumento.setError("Debe llenar este campo");
            Toast.makeText(getActivity(),"LAT: "+latitude+" LONG: "+longitude,Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (edittext_numerodocumento.getText().toString().matches("")){
            edittext_numerodocumento.setError(getString(R.string.campovacio));
        }/*if (edittext_apellidopaterno.getText().toString().matches("")){
            edittext_apellidopaterno.setError(getString(R.string.campovacio));
        }if (edittext_apellidomaterno.getText().toString().matches("")){
            edittext_apellidomaterno.setError(getString(R.string.campovacio));
        }if (edittext_nombres.getText().toString().matches("")){
            edittext_nombres.setError(getString(R.string.campovacio));
        }if (edittext_direccion.getText().toString().matches("")){
            edittext_direccion.setError(getString(R.string.campovacio));
        }*/

        Boolean[] arr = {spinner_listado_tipocaptura.getSelectedItemPosition()==0,
                spinner_listado_tipodoc.getSelectedItemPosition()==0,
                spinner_listado_estadoentrega.getSelectedItemPosition()==0,
                (longitude==""||latitude==""),isFotoSaving};

        String[] arrmsj = {"Seleccione un Tipo de Captura",
                "Seleccione un Tipo de Documento",
                "Seleccione el Estado de Entrega",
                "Habilite el gps y vuelva a entrar a la aplicación",
                "La foto se está guardando, por favor espere..."};
        Log.d("guardarxd",""+arr.length);
        Log.d("guardarxd",""+arr.length);
        for (int i = 0;i<arr.length;i++){
            Log.d("foreach",""+i);
            Log.d("condicion",""+arr[i]);
            if (arr[i]){
                Toast.makeText(getActivity(),arrmsj[i],Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Integer tipo_captura_id = id_TipoCaptura_array[useLoop(TipoCaptura_array,spinner_listado_tipocaptura.getSelectedItem().toString())];
        Integer tipo_documento_id = id_TipoDocumento_array[useLoop(TipoDocumento_array,spinner_listado_tipodoc.getSelectedItem().toString())];
        Integer numero_documento = Integer.valueOf(edittext_numerodocumento.getText().toString());
        String apellidopaterno = edittext_apellidopaterno.getText().toString();
        String apellidomaterno = edittext_apellidomaterno.getText().toString();
        String nombres = edittext_nombres.getText().toString();
        String direccion = edittext_direccion.getText().toString();
        String centro = edittext_centro.getText().toString();
        //Integer centro_id = id_Centro_array[useLoop(Centro_array,spinner_listado_centros.getSelectedItem().toString())];
        Integer composicion_familiar_id=0;
        //Integer composicion_familiar_id = id_CompFamiliar_array[useLoop(CompFamiliar_array,spinner_listado_composicionfamiliar.getSelectedItem().toString())];
        String observacion = edittext_observacion.getText().toString();
        Integer estado_entrega_id = id_EstadoEntrega_array[useLoop(EstadoEntrega_array,spinner_listado_estadoentrega.getSelectedItem().toString())];

        /*Log.d("estado_proyecto_id",estado_proyecto_id+"");
        Log.d("listado_estados_",spinner_geo_montaje_listado_estados_proyecto.getSelectedItem().toString());*/

        String root = conf.getRutaArchivos();
        String carpeta="";
        carpeta = root +"Fotos/";
        File myDir = new File(carpeta);

        ArrayList<String> filePaths = new ArrayList<>();
        final List<MultipartBody.Part> parts = new ArrayList<>();
        long fileSizeInBytes=0;

        final File[] filesFotos= myDir.listFiles();
        if (filesFotos !=null){
            for (File f : filesFotos){
                fileSizeInBytes+= f.length();
                parts.add(prepareFilePart("adjuntos[]",f.getAbsolutePath()));
            }
        }else{
            Toast.makeText(getActivity(),"Debe tomar las fotos respectivas",Toast.LENGTH_SHORT).show();
        }
        if (fileSizeInBytes/1024 > 19990) {
            Toast.makeText(getActivity(), "Ha sobrepasado el límite de tamaño permitido: 20MB. Tus archivos pesan:"+fileSizeInBytes/1024/1024+"Mb, favor de borrar algunos archivos", Toast.LENGTH_LONG).show();
            return;
        }

        Call<clsDonaciones> call = servicio.guardarDonacion(tipo_captura_id,tipo_documento_id,numero_documento,apellidopaterno,apellidomaterno,nombres,direccion,
                centro,composicion_familiar_id,observacion,estado_entrega_id,latitude,longitude,usuario_id,parts);
        btn_grabar.setText("Procesando...");
        btn_grabar.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);
        componenteEnabled(false);
        call.enqueue(new Callback<clsDonaciones>() {
            @Override
            public void onResponse(Call<clsDonaciones> call, Response<clsDonaciones> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "Ha sido registrado con exito. " + response.message(), Toast.LENGTH_LONG).show();
                    if (filesFotos !=null){
                        for (File f : filesFotos){
                            borrarArchivoSubido(f.getAbsolutePath());
                        }
                    }
                }else{
                    Toast.makeText(getActivity(), "El servidor ha retornado un error. " + response.message(), Toast.LENGTH_LONG).show();
                }
                loadingProgressBar.setVisibility(View.GONE);
                componenteEnabled(true);
                resetValues();
            }
            @Override
            public void onFailure(Call<clsDonaciones> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                componenteEnabled(true);
                Toast.makeText(getActivity(), "Ha ocurrido un error. " + t.getMessage(), Toast.LENGTH_LONG).show();
                btn_grabar.setText("GUARDAR");
                btn_grabar.setEnabled(true);
            }
        });
    }
    private void componenteEnabled(Boolean estado){
        btn_grabar.setEnabled(estado);
        spinner_listado_estadoentrega.setEnabled(estado);
        //spinner_listado_composicionfamiliar.setEnabled(estado);
        edittext_centro.setEnabled(estado);
        spinner_listado_tipodoc.setEnabled(estado);
        spinner_listado_tipocaptura.setEnabled(estado);
        edittext_numerodocumento.setEnabled(estado);
        edittext_observacion.setEnabled(estado);
        edittext_direccion.setEnabled(estado);
        edittext_nombres.setEnabled(estado);
        edittext_apellidomaterno.setEnabled(estado);
        edittext_apellidopaterno.setEnabled(estado);

        btn_camara.setEnabled(estado);
        btn_camara_benef.setEnabled(estado);
        btn_verfotos.setEnabled(estado);
        btn_verfotos2.setEnabled(estado);
    }
    private void borrarArchivoSubido(String ruta_imagen){
        try {
            File fdelete = new File(ruta_imagen);
            if (fdelete.exists()) {
                if (fdelete.delete()){

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private MultipartBody.Part prepareFilePart(String partName, String url){

        File file = new File(url);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        return MultipartBody.Part.createFormData(partName, file.getName(),requestBody);
    }
    private void verifyGPSON(){
        locationManager=(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Check gps is enable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Write Function To enable gps
            OnGPS();
        }
        else
        {
            //GPS is already On then
            getLocation();
        }
    }
    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            btn_grabar.setText("Obteniendo GPS...");
            btn_grabar.setEnabled(false);
            handler =  new Handler();
            handler.removeCallbacks(myRunnable);
            myRunnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "No se ha podido identificar su gps", Toast.LENGTH_SHORT).show();
                    locationManager.removeUpdates(listener);
                    btn_grabar.setText("GUARDAR");
                    btn_grabar.setEnabled(true);
                }
            };
            handler.postDelayed(myRunnable,10000);
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat=location.getLatitude();
                    double longi=location.getLongitude();
                    handler.removeCallbacks(myRunnable);

                    latitude=lat+"";
                    longitude=longi+"";
                    btn_grabar.setText("GUARDAR");
                    btn_grabar.setEnabled(true);
                    guardar();
                    locationManager.removeUpdates(listener);
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            };
            locationManager.requestLocationUpdates("gps", 2000, 0, listener);


            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            //latitude="";
            //longitude="";
             if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();
                handler.removeCallbacks(myRunnable);
                latitude=lat+"";
                longitude=longi+"";
                btn_grabar.setText("GUARDAR");
                btn_grabar.setEnabled(true);
                locationManager.removeUpdates(listener);

                guardar();
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();
                handler.removeCallbacks(myRunnable);
                latitude=lat+"";
                longitude=longi+"";
                btn_grabar.setText("GUARDAR");
                btn_grabar.setEnabled(true);
                locationManager.removeUpdates(listener);

                guardar();
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();
                handler.removeCallbacks(myRunnable);
                latitude=""+lat;
                longitude=""+longi;
                btn_grabar.setText("GUARDAR");
                btn_grabar.setEnabled(true);
                locationManager.removeUpdates(listener);

                guardar();
            }else{
                 Toast.makeText(getActivity(), "Verifique que su gps funcione correctamente", Toast.LENGTH_SHORT).show();
            }

        }

    }
    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        builder.setMessage("Habilitar GPS").setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public static DonacionesFragment newInstance() {
        return new DonacionesFragment();
    }
    public void onResume(){
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.menu_donaciones));


    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_donaciones, container, false);
        contexto = root.getContext();

        return root;
    }

    private int SpannedLength = 0,chipLength = 4;

    //AppCompatEditText Phone = getView().findViewById(R.id.phone);



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DonacionesViewModel.class);
        // TODO: Use the ViewModel

        llChips = (LinearLayout) getView().findViewById(R.id.llchips);

        loadingProgressBar = (ProgressBar) getView().findViewById(R.id.loading);
        sharedPreferences = getActivity().getSharedPreferences("datosusuario",getActivity().MODE_PRIVATE);
        preferences = getActivity().getSharedPreferences("datosCompFami",getActivity().MODE_PRIVATE);
        editor = preferences.edit();

        usuario_id = sharedPreferences.getInt("usuario_id",10);
        ubigeo_id = sharedPreferences.getInt("ubigeo_id",10);

        Log.d("usuario_id",usuario_id+"");
        spinner_listado_tipodoc = (Spinner) getView().findViewById(R.id.spinner_tipo_documento);
        spinner_listado_tipocaptura = (Spinner) getView().findViewById(R.id.spinner_tipo_captura);
        spinner_listado_estadoentrega = (Spinner) getView().findViewById(R.id.spinner_estado_entrega);

        id_img = (ImageView) getView().findViewById(R.id.id_img);
        edittext_numerodocumento = (EditText) getView().findViewById(R.id.edittext_numerodocumento);
        edittext_apellidopaterno = (EditText) getView().findViewById(R.id.edittext_paterno);
        edittext_apellidomaterno = (EditText) getView().findViewById(R.id.edittext_materno);
        edittext_nombres = (EditText) getView().findViewById(R.id.edittext_nombres);
        edittext_direccion = (EditText) getView().findViewById(R.id.edittext_direccion);
        edittext_observacion = (EditText) getView().findViewById(R.id.edittext_observacion);
        edittext_comp_familiar = (LinearLayout) getView().findViewById(R.id.llchips);
        edittext_centro = (EditText) getView().findViewById(R.id.edittext_centro);

        verifyStoragePermissions(getActivity());
        requestPermissionGPS(getActivity());
        this.listadoTipoCaptura();
        this.listadoTipoDocumento();
        this.listadoEstadoEntrega();

        edittext_comp_familiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialogClass cdd=new CustomDialogClass(contexto);
                cdd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        llChips.removeAllViews();
                        int pos = cdd.spncompfamiliar_0.getSelectedItemPosition();
                        if(pos!=0){
                            addChips(cdd.composicion[0]+": "+pos,180);
                        }

                        pos = cdd.spncompfamiliar_1.getSelectedItemPosition();
                        if(cdd.spncompfamiliar_1.getSelectedItemPosition()!=0){
                            addChips(cdd.composicion[1]+": "+pos,210);
                        }

                        pos = cdd.spncompfamiliar_2.getSelectedItemPosition();
                        if(cdd.spncompfamiliar_2.getSelectedItemPosition()!=0){
                            addChips(cdd.composicion[2]+": "+pos,210);
                        }
                        pos = cdd.spncompfamiliar_3.getSelectedItemPosition();
                        if(cdd.spncompfamiliar_3.getSelectedItemPosition()!=0){
                            addChips(cdd.composicion[3]+": "+pos,210);
                        }

                        pos = cdd.spncompfamiliar_4.getSelectedItemPosition();
                        if(cdd.spncompfamiliar_4.getSelectedItemPosition()!=0){
                            addChips(cdd.composicion[4]+": "+pos,150);
                        }
                        editor.putInt("compFam_0",cdd.spncompfamiliar_0.getSelectedItemPosition());
                        editor.putInt("compFam_1",cdd.spncompfamiliar_1.getSelectedItemPosition());
                        editor.putInt("compFam_2",cdd.spncompfamiliar_2.getSelectedItemPosition());
                        editor.putInt("compFam_3",cdd.spncompfamiliar_3.getSelectedItemPosition());
                        editor.putInt("compFam_4",cdd.spncompfamiliar_4.getSelectedItemPosition());
                        editor.commit();



                    }
                });

                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });


        btn_camara = (ImageButton) getView().findViewById(R.id.btn_camara);
        btn_camara_benef = (ImageButton) getView().findViewById(R.id.btn_camara_benef);
        btn_verfotos=(Button) getView().findViewById(R.id.btn_verfotos);
        btn_verfotos2=(Button) getView().findViewById(R.id.btn_verfotos2);

        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()){
                    TipoFoto = "DNI";
                    if(!FotoExiste(TipoFoto)){
                        llamarIntentCamara();
                    }else{
                        Toast.makeText(getContext(),"Tiene que borrar la foto del DNI que ha tomado con anterioridad",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btn_camara_benef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()){
                    TipoFoto = "BENEFICIARIO";
                    if(!FotoExiste(TipoFoto)){
                        llamarIntentCamara();
                    }else{
                        Toast.makeText(getActivity(),"Tiene que borrar la foto del BENEFICIARIO que ha tomado con anterioridad",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btn_verfotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()){
                    actionActive = "android.media.action.IMAGE_CAPTURE";
                    llamarIntentVerArchivos();
                }
            }
        });
        btn_verfotos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()){
                    actionActive = "android.media.action.IMAGE_CAPTURE";
                    llamarIntentVerArchivos();
                }
            }
        });
        btn_grabar = (Button) getView().findViewById(R.id.btn_grabar);
        btn_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyGPSON();
            }
        });
        this.setErrores();
    }
    public void addChips(String composicionFami,int Width){
        TextView tv = new TextView(getContext());
        tv.setText(composicionFami);
        tv.setWidth(Width);
        tv.setHeight(70);
        tv.setPadding(16,6,5,5);
        tv.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_rounded) );
        tv.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,0,0,0);
        tv.setLayoutParams(params);
        llChips.addView(tv);
    }
    public Boolean FotoExiste(String tipo){
        Boolean response = false;
        String root = conf.getRutaArchivos();
        String carpeta="";
        carpeta = root +"Fotos/";
        File myDir = new File(carpeta);
        final File[] filesFotos= myDir.listFiles();
        if (filesFotos !=null){
            for (File f : filesFotos){
                String currentString = f.getAbsolutePath();
                if(currentString.indexOf(tipo)>=0){
                    response = true;
                    break;
                };
            }
        }
        return response;
    }
    public void requestPermissionGPS(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }

    }

    public void listadoTipoCaptura(){
        Call<List<clsTipoCaptura>> tipoCaptura = servicio.getTipoCaptura(ubigeo_id);
        tipoCaptura.enqueue(new Callback<List<clsTipoCaptura>>() {
            @Override
            public void onResponse(Call<List<clsTipoCaptura>> call, Response<List<clsTipoCaptura>> response) {
                if (response.isSuccessful()){
                    TipoCaptura_array = new String[response.body().size()];
                    id_TipoCaptura_array = new Integer[response.body().size()];
                    spnArrTipoCaptura =  new ArrayList<String>();
                    rowTipoCaptura = new ArrayList<clsTipoCaptura>();
                    spnArrTipoCaptura.add("SELECCIONE UN TIPO DE CAPTURA");
                    Integer i = 0;
                    for (clsTipoCaptura p : response.body()) {
                        spnArrTipoCaptura.add(p.getNombre());
                        TipoCaptura_array[i] = p.getNombre();
                        id_TipoCaptura_array[i] = p.getId();
                        Log.d("id_proyectos_array["+i+"]",p.getId()+"");
                        rowTipoCaptura.add(p);
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), R.layout.spinner_text_color, spnArrTipoCaptura);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_listado_tipocaptura = (Spinner) getView().findViewById(R.id.spinner_tipo_captura);
                    spinner_listado_tipocaptura.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<clsTipoCaptura>> call, Throwable t) {
                Log.d("ERROR TIPOCAPTURA",t.getMessage());
            }
        });
    }
    public void listadoTipoDocumento(){
        Call<List<clsTipoDocumento>> documentos = servicio.getTipoDocumento();
        documentos.enqueue(new Callback<List<clsTipoDocumento>>() {
            @Override
            public void onResponse(Call<List<clsTipoDocumento>> call, Response<List<clsTipoDocumento>> response) {
                if (response.isSuccessful()){
                    TipoDocumento_array = new String[response.body().size()];
                    id_TipoDocumento_array = new Integer[response.body().size()];
                    spnArrTipoDocumento =  new ArrayList<String>();
                    rowTipoDocumento = new ArrayList<clsTipoDocumento>();
                    spnArrTipoDocumento.add("SELECCIONE UN TIPO DE DOCUMENTO");
                    Integer i = 0;
                    for (clsTipoDocumento p : response.body()) {
                        spnArrTipoDocumento.add(p.getNombre());
                        TipoDocumento_array[i] = p.getNombre();
                        id_TipoDocumento_array[i] = p.getId();
                        Log.d("id_proyectos_array["+i+"]",p.getId()+"");
                        rowTipoDocumento.add(p);
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), R.layout.spinner_text_color, spnArrTipoDocumento);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_listado_tipodoc = (Spinner) getView().findViewById(R.id.spinner_tipo_documento);
                    spinner_listado_tipodoc.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<clsTipoDocumento>> call, Throwable t) {
                Log.d("ERROR clsTipoDocumento",t.getMessage());
            }
        });
    }
    public void listadoEstadoEntrega(){
        Call<List<clsEstadoEntrega>> documentos = servicio.getEstadoEntrega();
        documentos.enqueue(new Callback<List<clsEstadoEntrega>>() {
            @Override
            public void onResponse(Call<List<clsEstadoEntrega>> call, Response<List<clsEstadoEntrega>> response) {
                if (response.isSuccessful()){
                    EstadoEntrega_array = new String[response.body().size()];
                    id_EstadoEntrega_array = new Integer[response.body().size()];
                    spnArrEstadoEntrega =  new ArrayList<String>();
                    rowEstadoEntrega = new ArrayList<clsEstadoEntrega>();
                    spnArrEstadoEntrega.add("SELECCIONE UNA ENTREGA");
                    Integer i = 0;
                    for (clsEstadoEntrega p : response.body()) {
                        spnArrEstadoEntrega.add(p.getNombre());
                        EstadoEntrega_array[i] = p.getNombre();
                        id_EstadoEntrega_array[i] = p.getId();
                        Log.d("id_proyectos_array["+i+"]",p.getId()+"");
                        rowEstadoEntrega.add(p);
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), R.layout.spinner_text_color, spnArrEstadoEntrega);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_listado_estadoentrega = (Spinner) getView().findViewById(R.id.spinner_estado_entrega);
                    spinner_listado_estadoentrega.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<clsEstadoEntrega>> call, Throwable t) {
                Log.d("ERROR clsEstadoEntrega",t.getMessage());
            }
        });
    }
    /*public void listadoCentros(){
        Call<List<clsCentros>> centros = servicio.getCentros();
        centros.enqueue(new Callback<List<clsCentros>>() {
            @Override
            public void onResponse(Call<List<clsCentros>> call, Response<List<clsCentros>> response) {
                if (response.isSuccessful()){
                    Centro_array = new String[response.body().size()];
                    id_Centro_array = new Integer[response.body().size()];
                    spnArrCentro =  new ArrayList<String>();
                    rowCentro = new ArrayList<clsCentros>();
                    spnArrCentro.add("SELECCIONE UN TIPO DE DOCUMENTO");
                    Integer i = 0;
                    for (clsCentros p : response.body()) {
                        spnArrCentro.add(p.getNombre());
                        Centro_array[i] = p.getNombre();
                        id_Centro_array[i] = p.getId();
                        Log.d("id_proyectos_array["+i+"]",p.getId()+"");
                        rowCentro.add(p);
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), R.layout.spinner_text_color, spnArrCentro);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_listado_centros = (Spinner) getView().findViewById(R.id.spinner_centros);
                    spinner_listado_centros.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<clsCentros>> call, Throwable t) {
                Log.d("ERROR clsCentros",t.getMessage());
            }
        });
    }
    public void listadoComposicionFamiliar(){
        Call<List<clsCompFamiliar>> documentos = servicio.getComposicionFamiliar();
        documentos.enqueue(new Callback<List<clsCompFamiliar>>() {
            @Override
            public void onResponse(Call<List<clsCompFamiliar>> call, Response<List<clsCompFamiliar>> response) {
                if (response.isSuccessful()){
                    CompFamiliar_array = new String[response.body().size()];
                    id_CompFamiliar_array = new Integer[response.body().size()];
                    spnArrCompFamiliar =  new ArrayList<String>();
                    rowCompFamiliar = new ArrayList<clsCompFamiliar>();
                    spnArrCompFamiliar.add("SELECCIONE UNA COMPOSICIÓN FAMILIAR");
                    Integer i = 0;
                    for (clsCompFamiliar p : response.body()) {
                        spnArrCompFamiliar.add(p.getNombre());
                        CompFamiliar_array[i] = p.getNombre();
                        id_CompFamiliar_array[i] = p.getId();
                        Log.d("id_proyectos_array["+i+"]",p.getId()+"");
                        rowCompFamiliar.add(p);
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), R.layout.spinner_text_color, spnArrCompFamiliar);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_listado_composicionfamiliar = (Spinner) getView().findViewById(R.id.spinner_comp_familiar);
                    spinner_listado_composicionfamiliar.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<clsCompFamiliar>> call, Throwable t) {
                Log.d("ERROR clsCompFamiliar",t.getMessage());
            }
        });
    }*/
    public static Integer useLoop(String[] arr, String targetValue) {
        Integer i = 0;
        Log.d("useLoop targetValue",targetValue);

        for(String s: arr){
            Log.d("useLoop name",s);
            if(s.equals(targetValue))
                return i;

            i++;

        }
        return 0;
    }


    public void llamarIntentVerArchivos( ){

        GalleryFragment galleryF = new GalleryFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,R.animator.exit_to_left,R.animator.exit_to_left);
        fragmentTransaction.add(R.id.content_frame, galleryF);
        switch (actionActive) {
            case "android.media.action.IMAGE_CAPTURE":
                galleryF.mostrarTipoArchivo = "mostrarFotos";
                break;
            /*case "android.media.action.VIDEO_CAPTURE":
                galleryF.mostrarTipoArchivo = "mostrarVideos";
                break;
            case "android.media.action.AUDIO_CAPTURE":
                galleryF.mostrarTipoArchivo = "mostrarAudios";
                break;*/
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                this.verificarCarpetaCreada();
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    private void saveToInternalStorage(){
        String root = conf.getRutaArchivos();
        File myDir,file;
        String fname="";
        Calendar c = Calendar.getInstance(); SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String strDate = sdf.format(c.getTime());
        switch (actionActive){
            case "android.media.action.IMAGE_CAPTURE":
                isFotoSaving=true;
                myDir = new File(root+"Fotos/");
                myDir.mkdirs();
                fname = TipoFoto+"_Image_"+strDate+".jpeg";
                file = new File (myDir, fname);
                if (file.exists ()) file.delete ();
                try {
                    Bitmap imageBitmap = (Bitmap) intentData.getExtras().get("data");
                    FileOutputStream out = new FileOutputStream(file);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    //imageBitmap = ImageUtils.getInstant().getCompressedBitmap(root+"Fotos/"+fname);
                    isFotoSaving=false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }
    private String pictureImagePath = "";
    public void llamarIntentCamara(){
        actionActive = MediaStore.ACTION_IMAGE_CAPTURE;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    public void llamarIntentCamara2(){

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = timeStamp + ".jpeg";

        /*File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);*/
        String storageDir = conf.getRutaArchivos();
        pictureImagePath = storageDir + "Fotos/" + imageFileName;

        File file = new File(pictureImagePath);

        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        //actionActive = MediaStore.ACTION_IMAGE_CAPTURE;
        //Intent takePictureIntent = new Intent(actionActive);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (actionActive){
            case "android.media.action.IMAGE_CAPTURE":
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode!=0 ) {
                  /*  Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");*/
                    verifyStoragePermissions(getActivity());
                   /* if (isStoragePermissionGranted()){
                        */intentData = data;
                        saveToInternalStorage();
                        llamarIntentVerArchivos();/*
                    }*/
                }
                break;

        }

    }
    public void verificarCarpetaCreada(){
        String root = conf.getRutaArchivos();
        File myDir = new File(root+"Fotos/");
        myDir.mkdirs();
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }



    }
    public void resetValues(){
        btn_grabar.setText("GUARDAR");
        btn_grabar.setEnabled(true);
        edittext_observacion.setText("");
        edittext_direccion.setText("");
        edittext_apellidomaterno.setText("");
        edittext_apellidopaterno.setText("");
        edittext_numerodocumento.setText("");
        edittext_centro.setText("");
        edittext_nombres.setText("");
        spinner_listado_tipodoc.setSelection(0);
        spinner_listado_tipocaptura.setSelection(0);
        //spinner_listado_composicionfamiliar.setSelection(0);
        spinner_listado_estadoentrega.setSelection(0);
        editor.clear();
        editor.commit();

    }
    public void setErrores(){
        edittext_numerodocumento.addTextChangedListener(new TextChangedListener<EditText>(edittext_numerodocumento) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if (!target.getText().toString().matches("")){
                    target.setError(null);
                }else{
                    target.setError(getString(R.string.campovacio));
                }
            }
        });
        edittext_apellidopaterno.addTextChangedListener(new TextChangedListener<EditText>(edittext_apellidopaterno) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if (!target.getText().toString().matches("")){
                    target.setError(null);
                }else{
                    target.setError(getString(R.string.campovacio));
                }
            }
        });
        edittext_nombres.addTextChangedListener(new TextChangedListener<EditText>(edittext_nombres) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if (!target.getText().toString().matches("")){
                    target.setError(null);
                }else{
                    target.setError(getString(R.string.campovacio));
                }
            }
        });
        edittext_direccion.addTextChangedListener(new TextChangedListener<EditText>(edittext_direccion) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if (!target.getText().toString().matches("")){
                    target.setError(null);
                }else{
                    target.setError(getString(R.string.campovacio));
                }
            }
        });
    }
}
