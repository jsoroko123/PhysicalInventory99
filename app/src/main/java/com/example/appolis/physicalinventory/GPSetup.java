package com.example.appolis.physicalinventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class GPSetup extends ActionBarActivity implements View.OnClickListener {

    EditText etWebUrl, etDomain, etUserName, etPassword;
    Button btnValidate, btnReset, btnSave;
    Spinner spnCompany, spnWarehouse;
    Toast toast = null;
    public long lastBackPressTime = 0;
    private static final String METHOD_NAME = "GetCompanyList";
    private static final String SOAP_ACTION = "http://tempuri.org/GetCompanyList";
    private static final String METHOD_NAME2 = "GetWarehouseList";
    private static final String SOAP_ACTION2 = "http://tempuri.org/GetWarehouseList";
    private static ArrayList<String> companyList = new ArrayList<>();
    private static ArrayList<String> warehouseList = new ArrayList<>();
    private static String  company, site;
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
        spnCompany = (Spinner) findViewById(R.id.spinCompany);
        spnWarehouse = (Spinner) findViewById(R.id.spinSite);


        etWebUrl.setText( MainActivity.prefs.getString(MainActivity.Url, ""));
        etDomain.setText( MainActivity.prefs.getString(MainActivity.Domain, ""));
        etUserName.setText( MainActivity.prefs.getString(MainActivity.Username, ""));
        etPassword.setText( MainActivity.prefs.getString(MainActivity.Password, ""));

       if (MainActivity.prefs.getString(MainActivity.Verified, "").equals("True")){
           etWebUrl.setEnabled(false);
           etDomain.setEnabled(false);
           etUserName.setEnabled(false);
           etPassword.setEnabled(false);
           spnCompany.setEnabled(false);
           spnWarehouse.setEnabled(false);
           etWebUrl.setBackground(getResources().getDrawable(R.drawable.text3));
           etDomain.setBackground(getResources().getDrawable(R.drawable.text3));
           etUserName.setBackground(getResources().getDrawable(R.drawable.text3));
           etPassword.setBackground(getResources().getDrawable(R.drawable.text3));
           btnValidate.setEnabled(false);
           btnSave.setEnabled(false);
          GetCompanyList(MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""), MainActivity.prefs.getString(MainActivity.Url, ""),true);
            GetWarehouseList(MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""), MainActivity.prefs.getString(MainActivity.Company, ""));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                   android.R.layout.simple_spinner_item, companyList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

           ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                   android.R.layout.simple_spinner_item, warehouseList);
           adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

           spnCompany.setAdapter(adapter);
           spnWarehouse.setAdapter(adapter2);

           int spinnerPosition1 = adapter.getPosition(MainActivity.prefs.getString(MainActivity.Company, ""));
            spnCompany.setSelection(spinnerPosition1);
           int spinnerPosition2 = adapter2.getPosition(MainActivity.prefs.getString(MainActivity.Site, ""));
            spnWarehouse.setSelection(spinnerPosition2);


       }

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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, companyList);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, warehouseList);
        switch(view.getId()){

            case R.id.btnWebUrl:
               boolean isValid =  GetCompanyList(etDomain.getText().toString(), etUserName.getText().toString(), etPassword.getText().toString(), etWebUrl.getText().toString(), false);
                if(isValid){


                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCompany.setAdapter(adapter);

                    spnCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            GetWarehouseList(etDomain.getText().toString(), etUserName.getText().toString(), etPassword.getText().toString(), adapterView.getItemAtPosition(i).toString());
                            spnWarehouse.setAdapter(adapter2);
                            company = adapterView.getItemAtPosition(i).toString();

                            spnWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    site = adapterView.getItemAtPosition(i).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else {
                    spnCompany.setAdapter(null);
                    spnWarehouse.setAdapter(null);

                }
                break;

            case R.id.btnWebReset:



                if (lastBackPressTime < System.currentTimeMillis() - 3000) {
                    CustomToast("Press Edit/Reset again to reset all settings.", R.color.encode_view);
                    etWebUrl.setEnabled(true);
                    etDomain.setEnabled(true);
                    etUserName.setEnabled(true);
                    etPassword.setEnabled(true);
                    spnCompany.setEnabled(true);
                    spnWarehouse.setEnabled(true);
                    etWebUrl.setBackground(getResources().getDrawable(R.drawable.text));
                    etDomain.setBackground(getResources().getDrawable(R.drawable.text));
                    etUserName.setBackground(getResources().getDrawable(R.drawable.text));
                    etPassword.setBackground(getResources().getDrawable(R.drawable.text));
                    btnValidate.setEnabled(true);
                    btnSave.setEnabled(true);
                    toast.show();
                    lastBackPressTime = System.currentTimeMillis();
                } else  {
                    etWebUrl.setEnabled(true);
                    etDomain.setEnabled(true);
                    etUserName.setEnabled(true);
                    etPassword.setEnabled(true);
                    spnCompany.setEnabled(true);
                    spnWarehouse.setEnabled(true);
                    etWebUrl.setBackground(getResources().getDrawable(R.drawable.text));
                    etDomain.setBackground(getResources().getDrawable(R.drawable.text));
                    etUserName.setBackground(getResources().getDrawable(R.drawable.text));
                    etPassword.setBackground(getResources().getDrawable(R.drawable.text));
                    btnValidate.setEnabled(true);
                    btnSave.setEnabled(true);
                    etWebUrl.setText("");
                    etDomain.setText("");
                    etUserName.setText("");
                    etPassword.setText("");
                    spnCompany.setAdapter(null);
                    spnWarehouse.setAdapter(null);
                    SharedPreferences.Editor ed = MainActivity.prefs.edit();
                    ed.putString(MainActivity.Url,"");
                    ed.putString(MainActivity.Username,"");
                    ed.putString(MainActivity.Password,"");
                    ed.putString(MainActivity.Domain,"");
                    ed.putString(MainActivity.Company,"");
                    ed.putString(MainActivity.Site,"");
                    ed.putString(MainActivity.Verified, "False");
                }
            break;
            case R.id.btnWebSave:
                String w = etWebUrl.getText().toString();
                String u = etUserName.getText().toString();
                String p = etPassword.getText().toString();
                String d = etDomain.getText().toString();
                String c = company;
                String s = site;

                SharedPreferences.Editor ed = MainActivity.prefs.edit();
                ed.putString(MainActivity.Url,w);
                ed.putString(MainActivity.Username,u);
                ed.putString(MainActivity.Password,p);
                ed.putString(MainActivity.Domain,d);
                ed.putString(MainActivity.Company,c);
                ed.putString(MainActivity.Site,s);

                ed.apply();


                etWebUrl.setEnabled(false);
                etDomain.setEnabled(false);
                etUserName.setEnabled(false);
                etPassword.setEnabled(false);
                spnCompany.setEnabled(false);
                spnWarehouse.setEnabled(false);
                etWebUrl.setBackground(getResources().getDrawable(R.drawable.text3));
                etDomain.setBackground(getResources().getDrawable(R.drawable.text3));
                etUserName.setBackground(getResources().getDrawable(R.drawable.text3));
                etPassword.setBackground(getResources().getDrawable(R.drawable.text3));
                btnValidate.setEnabled(false);
                btnSave.setEnabled(false);
                CustomToast("Settings saved.", R.color.encode_view);
                break;
        }
    }
        public boolean GetCompanyList(String Domain, String UserName, String Password, String Url, boolean firstRun){

            companyList.clear();
            boolean isValid = true;
            SharedPreferences.Editor ed = MainActivity.prefs.edit();
            ed.putString(MainActivity.Verified, "False");
            ed.apply();
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
            HttpTransportSE androidHttpTransport = new HttpTransportSE(Url);

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                if (response.getPropertyCount() > 0){
                    isValid = true;
                    SharedPreferences.Editor ed2 = MainActivity.prefs.edit();
                    ed2.putString(MainActivity.Verified, "True");
                    ed2.apply();
                    if(!firstRun) {
                        CustomToast("Connection successfully validated.", R.color.encode_view);
                    }
                }
                else {
                    if(!firstRun) {
                        DisplayError("Please validate user credentials.");
                    }
                    isValid = false;
                }
                for(int i=0;i<response.getPropertyCount();i++){

                    Object property = response.getProperty(i);
                    SoapObject info = (SoapObject) property;
                    String cID = info.getProperty("CompanyID").toString();
                    companyList.add(cID.trim());
                }
                } catch (Exception e) {

                    isValid = false;
                    e.printStackTrace();
                    DisplayError(e.getMessage());
                }
            if(isValid) {
                companyList.add(0, "---Select Company---");
            }
             return isValid;

        }

    @Override
    public void onBackPressed() {

            super.onBackPressed();
        overridePendingTransition(R.anim.slide_in2, R.anim.slide_out2);
    }


    public void GetWarehouseList(String Domain, String UserName, String Password, String Company){
        if(!Company.equals("---Select Company---")) {
            warehouseList.clear();
            SoapObject request = new SoapObject(MainFragment.NAMESPACE, METHOD_NAME2);
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

            CasePI = new PropertyInfo();
            CasePI.setName("company");
            CasePI.setValue(Company);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.prefs.getString(MainActivity.Url, ""));

            try {
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                for (int i = 0; i < response.getPropertyCount(); i++) {

                    Object property = response.getProperty(i);
                    SoapObject info = (SoapObject) property;
                    String wID = info.getProperty("WarehouseID").toString();
                    warehouseList.add(wID.trim());

                }


            } catch (Exception e) {

                e.printStackTrace();
                DisplayError(e.getMessage());
            }

            warehouseList.add(0,"---Select Warehouse---");
        }
    }


    public void CustomToast(String message, int color){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(18);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toastTV.setTextColor(getResources().getColor(color));
        toast.show();
    }




    public void DisplayError(String message){
        AlertDialog alertDialog1 = new AlertDialog.Builder(
                this).create();

        alertDialog1.setTitle("Error!");
        alertDialog1.setMessage(message);
        alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog1.show();
    }
}
