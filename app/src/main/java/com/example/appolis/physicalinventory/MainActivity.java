package com.example.appolis.physicalinventory;
import com.google.xzing.integration.android.IntentIntegrator;
import com.google.xzing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public static MyPageAdapter pageAdapter;
    public static ViewPager pager;
    public static TextView tvItemDesc;
    public static EditText etItem, etLot, etQty;
    public static Spinner spinner;
    public static Button btn1, btn2, btnClear, btnNextCount;
    public static Button btnGo1, btnGo2, btnGo3, btnClearAll;
    public static LinearLayout ll;
    public static LinearLayout llSection1, llSection2, llMainFrag;
    public static TextView tvItemNumber, tvItemDescription, tvStdCost, tvCurrCost, tvItemShWt, tvPUom, tvSUom, tvUOMSchedule;
    public static TextView htvItemNum, htvBaseUom, htvSite;
    public static List<Fragment> fragments = new ArrayList<>();
    public static MenuItem m;
    public static SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Url = "urlKey";
    public static final String Domain = "domainKey";
    public static final String Username = "userKey";
    public static final String Password = "passKey";
    public static final String Company = "companyKey";
    public static final String Verified = "verifiedKey";
    public static final String Site = "siteKey";
    public static ArrayAdapter<String> adapter;
    public static String URL = null;
    List<Fragment> fList = new ArrayList<>();
    MainFragment mf = new MainFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setEnabled(false);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(1);
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ItemInformation ii = new ItemInformation();
        prefs = getApplicationContext().getSharedPreferences(MyPREFERENCES, 0);
        ii.ClearItemDetails();
        FirstFragment.Counted.clear();
        FirstFragment.Inventory.clear();
        FirstFragment.LotNumber.clear();
        FirstFragment.theParentList.clear();
        FirstFragment.childList.clear();
        FirstFragment.ItemLotCounts.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        m = menu.findItem(R.id.action_settings);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            return true;
        }
        if (id == R.id.settings) {
            startActivity(new Intent(getApplicationContext(), GPSetup.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {

    }


    class MyPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {

            super(fm);

            this.fragments = fragments;

        }

        @Override
        public Fragment getItem(int position) {

            return this.fragments.get(position);

        }


        @Override
        public int getCount() {

            return this.fragments.size();

        }

    }

    private List<Fragment> getFragments() {

        fList.add(FirstFragment.newInstance("Fragment 1"));
        fList.add(MainFragment.newInstance("Fragment 2"));
        fList.add(SecondFragment.newInstance("Fragment 3"));

        return fList;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            if (scanContent != null) {
                if (scanningResult.getFormatName().equals("QR_CODE") && scanContent.contains("|")) {
                    String[] newContent = scanContent.split("\\|");
                    String itemNumber = newContent[0];
                    String uom = newContent[1];
                    int spinnerPosition = 0;
                    String lotNumber = newContent[2];
                    boolean hasResults = mf.GetItem(itemNumber, MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""), MainActivity.prefs.getString(MainActivity.Company, ""));
                    if (hasResults) {

                        mf.GetItemUOM(ItemInformation.getUoMSchedule(), MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""), MainActivity.prefs.getString(MainActivity.Company, ""));
                        DisplayUOMSpinner();
                        etItem.setText(itemNumber);

                        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                        spinnerPosition = myAdap.getPosition(uom);
                        spinner.setSelection(spinnerPosition);
                        tvItemDesc.setText(ItemInformation.getItemDescription());
                        tvItemNumber.setText("Item: " + ItemInformation.getItemNumber());
                        tvItemDescription.setText(ItemInformation.getItemDescription());
                        tvStdCost.setText(ItemInformation.getStdCost());
                        tvCurrCost.setText(ItemInformation.getCurrCost());
                        tvItemShWt.setText(ItemInformation.getItemShWt());
                        tvPUom.setText(ItemInformation.getpUom());
                        tvSUom.setText(ItemInformation.getSUom());
                        tvUOMSchedule.setText(ItemInformation.getUoMSchedule());
                        if (ItemInformation.getLotTrackingInd().equals("1")) {
                            etLot.setText(lotNumber);
                            etItem.setEnabled(false);
                            btn1.setEnabled(false);
                            etLot.setEnabled(false);
                            btn2.setEnabled(false);
                            etItem.setBackground(getResources().getDrawable(R.drawable.text2));
                            etLot.setBackground(getResources().getDrawable(R.drawable.text2));
                            etQty.setBackground(getResources().getDrawable(R.drawable.text));
                            etQty.setEnabled(true);
                            btnGo3.setEnabled(true);
                            llSection1.setBackground(getResources().getDrawable(R.drawable.gray_button2));
                            llSection2.setBackground(getResources().getDrawable(R.drawable.blue_button));
                        } else {
                            MainActivity.etLot.setText(MainActivity.etLot.getText().toString());
                            MainActivity.etLot.setEnabled(false);
                            MainActivity.btn2.setEnabled(false);
                            MainActivity.btnGo3.setEnabled(true);
                            MainActivity.etQty.setEnabled(true);
                            MainActivity.etQty.requestFocus();
                            MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text2));
                            MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text));
                            MainActivity.etLot.setHint("Lot Tracking N/A");

                        }
                        FirstFragment.Counted.clear();
                        FirstFragment.Inventory.clear();
                        FirstFragment.LotNumber.clear();
                        FirstFragment f = new FirstFragment();
                        FirstFragment.theParentList.clear();
                        FirstFragment.childList.clear();
                        FirstFragment.ItemLotCounts.clear();
                        f.GetItemCounts(MainActivity.etItem.getText().toString(), MainActivity.prefs.getString(MainActivity.Company, ""), MainActivity.prefs.getString(MainActivity.Site, ""));
                        f.ListGpVsCounted(MainActivity.etItem.getText().toString(), MainActivity.prefs.getString(MainActivity.Company, ""), MainActivity.prefs.getString(MainActivity.Site, ""));
                        FirstFragment.listAdapter = new ExpandableListAdapter(this, FirstFragment.theParentList);
                        FirstFragment.myList.setAdapter(FirstFragment.listAdapter);
                        FirstFragment.myList.invalidateViews();

                        if (spinnerPosition == -1) {
                            Toast toast = Toast.makeText(this, "Invalid UOM. Please Select From List.", Toast.LENGTH_SHORT);
                            LinearLayout toastLayout = (LinearLayout) toast.getView();
                            TextView toastTV = (TextView) toastLayout.getChildAt(0);
                            toastTV.setTextSize(18);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toastTV.setTextColor(getResources().getColor(R.color.red));
                            toast.show();

                        }

                    } else {
                        Toast toast = Toast.makeText(this, "Invalid Item Number", Toast.LENGTH_SHORT);
                        LinearLayout toastLayout = (LinearLayout) toast.getView();
                        TextView toastTV = (TextView) toastLayout.getChildAt(0);
                        toastTV.setTextSize(18);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toastTV.setTextColor(getResources().getColor(R.color.red));
                        toast.show();
                    }

                } else {
                    if (etItem.isEnabled()) {
                        boolean hasResults = mf.GetItem(scanContent.toString(), MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""), MainActivity.prefs.getString(MainActivity.Company, ""));
                        if (hasResults) {
                            //pager.setOnTouchListener(null);
                            pager.setCurrentItem(1);
                            etItem.setText(scanContent);
                            tvItemDesc.setText(ItemInformation.getItemDescription());
                            tvItemNumber.setText("Item: " + ItemInformation.getItemNumber());
                            tvItemDescription.setText(ItemInformation.getItemDescription());
                            tvStdCost.setText(ItemInformation.getStdCost());
                            tvCurrCost.setText(ItemInformation.getCurrCost());
                            tvItemShWt.setText(ItemInformation.getItemShWt());
                            tvPUom.setText(ItemInformation.getpUom());
                            tvSUom.setText(ItemInformation.getSUom());
                            tvUOMSchedule.setText(ItemInformation.getUoMSchedule());
                            etItem.setEnabled(false);
                            btn1.setEnabled(false);
                            btn2.setEnabled(true);
                            etLot.setEnabled(true);
                            etItem.setBackground(getResources().getDrawable(R.drawable.text2));
                            etLot.setBackground(getResources().getDrawable(R.drawable.text));
                            llSection2.setBackground(getResources().getDrawable(R.drawable.blue_button));
                            llSection1.setBackground(getResources().getDrawable(R.drawable.gray_button2));
                            mf.GetItemUOM(ItemInformation.getUoMSchedule(), MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""), MainActivity.prefs.getString(MainActivity.Company, ""));
                            DisplayUOMSpinner();
                            FirstFragment.Inventory.clear();
                            FirstFragment.LotNumber.clear();
                            FirstFragment f = new FirstFragment();
                            FirstFragment.theParentList.clear();
                            FirstFragment.childList.clear();
                            FirstFragment.ItemLotCounts.clear();
                            f.GetItemCounts(MainActivity.etItem.getText().toString(), MainActivity.prefs.getString(MainActivity.Company, ""), MainActivity.prefs.getString(MainActivity.Site, ""));
                            f.ListGpVsCounted(MainActivity.etItem.getText().toString(), MainActivity.prefs.getString(MainActivity.Company, ""), MainActivity.prefs.getString(MainActivity.Site, ""));
                            FirstFragment.listAdapter = new ExpandableListAdapter(this, FirstFragment.theParentList);
                            FirstFragment.myList.setAdapter(FirstFragment.listAdapter);
                            FirstFragment.myList.invalidateViews();
                        } else {
                            Toast toast = Toast.makeText(this, "Invalid Item Number", Toast.LENGTH_SHORT);
                            LinearLayout toastLayout = (LinearLayout) toast.getView();
                            TextView toastTV = (TextView) toastLayout.getChildAt(0);
                            toastTV.setTextSize(18);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toastTV.setTextColor(getResources().getColor(R.color.red));
                            toast.show();
                        }
                    } else if (etLot.isEnabled()) {
                        pager.setCurrentItem(1);
                        etLot.setText(scanContent);
                        etLot.setEnabled(false);
                        btn2.setEnabled(false);
                        etLot.setBackground(getResources().getDrawable(R.drawable.text2));
                        etQty.setEnabled(true);
                        etQty.setBackground(getResources().getDrawable(R.drawable.text));
                        btnGo3.setEnabled(true);


                    } else if (etQty.isEnabled()) {
                        pager.setCurrentItem(1);
                        etQty.setText(scanContent);
                        etQty.setEnabled(false);
                        etQty.setBackground(getResources().getDrawable(R.drawable.text2));
                        btnGo3.setEnabled(false);
                        ll.setBackgroundColor(getResources().getColor(R.color.orange));
                        m.setEnabled(false);
                        m.setIcon(getResources().getDrawable(R.drawable.barcode_icon_no));
                        btnNextCount.setEnabled(true);
                    }
                }
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();//
        }

    }

    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Press back again to close application.", Toast.LENGTH_LONG);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            super.onBackPressed();
        }
    }

    public void DisplayUOMSpinner() {
        ArrayList<String> u = new ArrayList<>();
        for (int a = 0; a < ItemInformation.getItemUOMList().size(); a++) {
            u.add(ItemInformation.getItemUOMList().get(a).first.toString());

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, u);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MainActivity.spinner.setAdapter(adapter);
        MainActivity.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


     public static class SoapAccessTask extends AsyncTask<String, Void, String> {
         private ProgressDialog dialog;

         public SoapAccessTask(Context context) {
             dialog = new ProgressDialog(context);
         }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Syncing with GP....");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String NAMESPACE = "http://tempuri.org/";
            String SOAP_ACTION5 = "http://tempuri.org/InsertGPVariance";
            String METHOD_NAME5 = "InsertGPVariance";
            String webResponse = "";
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);
                PropertyInfo CasePI = new PropertyInfo();
                CasePI.setName("Domain");
                CasePI.setValue(urls[0]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("UserName");
                CasePI.setValue(urls[1]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("Password");
                CasePI.setValue(urls[2]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("company");
                CasePI.setValue(urls[3]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("sWarehouseName");
                CasePI.setValue(urls[4]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("sItemNumber");
                CasePI.setValue(urls[5]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("dQty");
                CasePI.setValue(urls[6]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("sLotNumber");
                CasePI.setValue(urls[7]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                CasePI = new PropertyInfo();
                CasePI.setName("sSellingUOM");
                CasePI.setValue(urls[8]);
                CasePI.setType(String.class);
                request.addProperty(CasePI);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.prefs.getString(MainActivity.Url, ""));


                request.addProperty(CasePI);

                androidHttpTransport.call(SOAP_ACTION5, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                webResponse = response.toString();
            } catch (Exception e) {

            }
            return webResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

}



