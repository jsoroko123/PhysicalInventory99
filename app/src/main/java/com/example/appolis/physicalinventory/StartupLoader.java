package com.example.appolis.physicalinventory;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public  class StartupLoader extends AsyncTask<String, String, String> {
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String SOAP_ACTION = "http://tempuri.org/GetItem";
    private static final String METHOD_NAME = "GetItem";
    private String resp;
    @Override
    protected String doInBackground(String... params) {
        publishProgress("Loading contents..."); // Calls onProgressUpdate()
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        PropertyInfo CasePI = new PropertyInfo();
        CasePI.setName("ItemNumber");
        CasePI.setValue(MainActivity.etItem.getText().toString());
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("Domain");
        CasePI.setValue(MainActivity.prefs.getString(MainActivity.Domain, ""));
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("UserName");
        CasePI.setValue(MainActivity.prefs.getString(MainActivity.Username, ""));
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("Password");
        CasePI.setValue(MainActivity.prefs.getString(MainActivity.Password, ""));
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
            for (int i = 0; i < response.getPropertyCount(); i++) {

                Object property = response.getProperty(i);
                SoapObject info = (SoapObject) property;
                ItemInformation ii = new ItemInformation();
                ii.setItemNumber(info.getProperty("itemNumber").toString());
                ii.setItemDescription(info.getProperty("itemDescription").toString());
                ii.setStdCost(info.getProperty("stdCost").toString());
                ii.setCurrCost(info.getProperty("currCost").toString());
                ii.setItemShWt(info.getProperty("itemShWt").toString());
                ii.setpUom(info.getProperty("pUom").toString());
                ii.setSUom(info.getProperty("SUom").toString());
                ii.setUoMSchedule(info.getProperty("uoMSchedule").toString());

            }


        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
        }
        return resp;
    }

    /**
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        // In this example it is the return value from the web service
        ItemInformation ii = new ItemInformation();
        Log.d(ii.getItemNumber(), ii.getStdCost());
    }

    /**
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        // Things to be done before execution of long running operation. For
        // example showing ProgessDialog
    }

    @Override
    protected void onProgressUpdate(String... text) {

        // Things to be done while execution of long running operation is in
        // progress. For example updating ProgessDialog
    }
}