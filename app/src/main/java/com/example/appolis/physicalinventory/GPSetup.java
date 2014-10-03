package com.example.appolis.physicalinventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//dhdhn
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class GPSetup extends ActionBarActivity implements View.OnClickListener {

    EditText etWebUrl, etDomain, etUserName, etPassword;
    Button btnValidate, btnReset, btnSave;
    private static final String METHOD_NAME = "GetCompanyList";
    private static final String SOAP_ACTION = "http://tempuri.org/GetCompanyList";

    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpsetup_layout);
        btnValidate = (Button) findViewById(R.id.btnWebUrl);
        btnValidate.setOnClickListener(this);
        btnReset = (Button) findViewById(R.id.btnWebReset);
        btnReset.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btnWebSave);
        btnSave.setOnClickListener(this);
        etWebUrl = (EditText) findViewById(R.id.et_webUrl);
        etDomain = (EditText) findViewById(R.id.etwebDomain);
        etUserName = (EditText) findViewById(R.id.et_webUsername);
        etPassword = (EditText) findViewById(R.id.et_webPassword);

        etWebUrl.setText( MainActivity.prefs.getString(MainActivity.Url, ""));
        etDomain.setText( MainActivity.prefs.getString(MainActivity.Domain, ""));
        etUserName.setText( MainActivity.prefs.getString(MainActivity.Username, ""));
        etPassword.setText( MainActivity.prefs.getString(MainActivity.Password, ""));
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.goback) {

           super.finish();
            overridePendingTransition(R.anim.slide_in2, R.anim.slide_out2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.btnWebUrl:

               boolean isValid =  GetCompanyList(MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""));
                if(isValid){

                    btnValidate.setBackground(getResources().getDrawable(R.drawable.button_style_green));
                    btnValidate.setText("Validated");
                }
                else {
                    btnValidate.setBackground(getResources().getDrawable(R.drawable.button_style));
                    btnValidate.setText("Validate");

                }
                break;

            case R.id.btnWebReset:
            break;
            case R.id.btnWebSave:
                String w = etWebUrl.getText().toString();
                String u = etUserName.getText().toString();
                String p = etPassword.getText().toString();
                String d = etDomain.getText().toString();

                SharedPreferences.Editor ed = MainActivity.prefs.edit();
                ed.putString(MainActivity.Url,w);
                ed.putString(MainActivity.Username,u);
                ed.putString(MainActivity.Password,p);
                ed.putString(MainActivity.Domain,d);
                ed.commit();
                break;
        }
    }
        public boolean GetCompanyList(String Domain, String UserName, String Password){
            boolean isValid = true;
            MainFragment mf = new MainFragment();
            ArrayList<String> itemUOM = new ArrayList<>();
            SoapObject request = new SoapObject(MainFragment.NAMESPACE, METHOD_NAME);
            PropertyInfo CasePI = new PropertyInfo();
            CasePI.setName("Domain");
            CasePI.setValue(Domain);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            CasePI = new PropertyInfo();
            CasePI.setName("UserName");
            CasePI.setValue(UserName);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            CasePI = new PropertyInfo();
            CasePI.setName("Password");
            CasePI.setValue(Password);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE( MainActivity.prefs.getString(MainActivity.Url, ""));

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                if (request.getPropertyCount() > 0){
                    isValid = true;
                }



            } catch (Exception e) {
                isValid = false;
                e.printStackTrace();
                DisplayError(e.getMessage());
            }

         return isValid;
        }


    public void DisplayError(String message){
        AlertDialog alertDialog1 = new AlertDialog.Builder(
                this).create();

        alertDialog1.setTitle("Error");
        alertDialog1.setMessage(message);
        alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog1.show();
    }
}
