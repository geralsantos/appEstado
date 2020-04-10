package com.example.myapplication.ui.donaciones;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.myapplication.Config.Config;
import com.example.myapplication.Interface.ApiService;
import com.example.myapplication.R;
import com.example.myapplication.ui.tareo_actividades.TareoActividadesFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Context c;
    public Dialog d;
    public Button yes, no;
    public Integer id;
    public Spinner spncompfamiliar_0;
    public Spinner spncompfamiliar_1;
    public Spinner spncompfamiliar_2;
    public Spinner spncompfamiliar_3;
    public Spinner spncompfamiliar_4;

    public String[] composicion = {"0 - 11","12 - 17","18 - 29","30 - 59","60+"};
    SharedPreferences preferences = getContext().getSharedPreferences("datosCompFami",getContext().MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();

    private ApiService servicio = Config.getRetrofit().create(ApiService.class);

    public CustomDialogClass(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    public void NumerosCompFamiliar(){
        List<Integer> spinnerArray =  new ArrayList<Integer>();
        for (int i = 0; i <= 70; i++) {
            spinnerArray.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>( getContext(), R.layout.spinner_text_color, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spncompfamiliar_0.setAdapter(adapter);
        spncompfamiliar_1.setAdapter(adapter);
        spncompfamiliar_2.setAdapter(adapter);
        spncompfamiliar_3.setAdapter(adapter);
        spncompfamiliar_4.setAdapter(adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.comp_familiar_dialog);
        spncompfamiliar_0 = (Spinner) findViewById(R.id.spn_comp_familiar_0) ;
        spncompfamiliar_1 = (Spinner) findViewById(R.id.spn_comp_familiar_1) ;
        spncompfamiliar_2 = (Spinner) findViewById(R.id.spn_comp_familiar_2) ;
        spncompfamiliar_3 = (Spinner) findViewById(R.id.spn_comp_familiar_3) ;
        spncompfamiliar_4 = (Spinner) findViewById(R.id.spn_comp_familiar_4) ;
        Log.d("dddd",spncompfamiliar_0.getSelectedItemPosition()+"asd");
        this.NumerosCompFamiliar();

        preferences = getContext().getSharedPreferences("datosCompFami",getContext().MODE_PRIVATE);

        String compareValue = "some value";

        spncompfamiliar_0.setSelection(preferences.getInt("compFam_0",0));
        spncompfamiliar_1.setSelection(preferences.getInt("compFam_1",0));
        spncompfamiliar_2.setSelection(preferences.getInt("compFam_2",0));
        spncompfamiliar_3.setSelection(preferences.getInt("compFam_3",0));
        spncompfamiliar_4.setSelection(preferences.getInt("compFam_4",0));

        yes = (Button) findViewById(R.id.btn_yes);
        //no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        //no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                modificar();
                //c.finish();
                break;
          /*  case R.id.btn_no:
                cancel();
                break;*/
            default:
                break;
        }

    }

    public void modificar(){


        Config.Mensaje(getContext(),"Se ha actualizado la composición familiar");
        cancel();
    }
}