package com.example.appolis.physicalinventory;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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


public  class MainFragment extends Fragment implements View.OnClickListener {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static int id = 0;
    public boolean hasErrors = false;
    public static final String NAMESPACE = "http://tempuri.org/";
    private static final String SOAP_ACTION = "http://tempuri.org/GetItem";
    private static final String METHOD_NAME = "GetItem";
    private static final String SOAP_ACTION2 = "http://tempuri.org/GetItemUOM";
    private static final String METHOD_NAME2 = "GetItemUOM";
    private static final String SOAP_ACTION4 = "http://tempuri.org/InsertItemCount";
    private static final String METHOD_NAME4 = "InsertItemCount";
    private static final String SOAP_ACTION5 = "http://tempuri.org/UpdateItemCount";
    private static final String METHOD_NAME5 = "UpdateItemCount";
    public static ArrayList<GpInventory> gpList = new ArrayList<>();
    public static FirstFragment f = new FirstFragment();
    public static MainFragment newInstance(String message){
         MainFragment f = new MainFragment();
         Bundle bdl = new Bundle(1);
         bdl.putString(EXTRA_MESSAGE, message);
         f.setArguments(bdl);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        MainActivity.etItem = (EditText) rootView.findViewById(R.id.et_item);
        MainActivity.btn1 = (Button) rootView.findViewById(R.id.button1);
        MainActivity.etLot = (EditText) rootView.findViewById(R.id.et_lot);
        MainActivity.etQty = (EditText) rootView.findViewById(R.id.et_qty);
        MainActivity.btn2 = (Button) rootView.findViewById(R.id.button2);
        MainActivity.tvItemDesc = (TextView) rootView.findViewById(R.id.item_description);
        MainActivity.btnGo1 = (Button) rootView.findViewById(R.id.button1);
        MainActivity.btnGo2 = (Button) rootView.findViewById(R.id.button2);
        MainActivity.btnGo3 = (Button) rootView.findViewById(R.id.btnGo3);

        MainActivity.btnClear = (Button) rootView.findViewById(R.id.button3);
        MainActivity.btnNextCount = (Button) rootView.findViewById(R.id.button4);
        MainActivity.btnClearAll = (Button) rootView.findViewById(R.id.button12);
        MainActivity.spinner = (Spinner) rootView.findViewById(R.id.spinner2);
        MainActivity.ll = (LinearLayout) rootView.findViewById(R.id.llBar);
        MainActivity.llSection1 = (LinearLayout) rootView.findViewById(R.id.llsection1);
        MainActivity.llSection2 = (LinearLayout) rootView.findViewById(R.id.llsection2);
        MainActivity.llMainFrag = (LinearLayout) rootView.findViewById(R.id.rlayout);
        MainActivity.btnGo1.setOnClickListener(this);
        MainActivity.btnGo2.setOnClickListener(this);
        MainActivity.btnGo3.setOnClickListener(this);
        MainActivity.btnClear.setOnClickListener(this);
        MainActivity.btnNextCount.setOnClickListener(this);
        MainActivity.btnClearAll.setOnClickListener(this);
        return rootView;
    }

        @Override
        public void onClick(View view) {
            try {
                switch (view.getId()) {
                    case R.id.button1:
                        if (!MainActivity.etItem.getText().toString().isEmpty()) {
                            boolean hasResults = GetItem(MainActivity.etItem.getText().toString(), MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""));
                            if (hasResults) {
                                FirstFragment.childList.clear();
                                FirstFragment.theParentList.clear();
                                FirstFragment.ItemLotCounts.clear();
                                MainActivity.pager.setOnTouchListener(null);
                                MainActivity.etItem.setText(MainActivity.etItem.getText().toString());
                                MainActivity.tvItemDesc.setText(ItemInformation.getItemDescription());
                                MainActivity.tvItemNumber.setText("Item: " + ItemInformation.getItemNumber());
                                MainActivity.tvItemDescription.setText(ItemInformation.getItemDescription());
                                MainActivity.tvStdCost.setText(ItemInformation.getStdCost());
                                MainActivity.tvCurrCost.setText(ItemInformation.getCurrCost());
                                MainActivity.tvItemShWt.setText(ItemInformation.getItemShWt());
                                MainActivity.tvPUom.setText(ItemInformation.getpUom());
                                MainActivity.tvSUom.setText(ItemInformation.getSUom());
                                MainActivity.tvUOMSchedule.setText(ItemInformation.getUoMSchedule());
                                MainActivity.etItem.setEnabled(false);
                                MainActivity.btn1.setEnabled(false);
                                MainActivity.btn2.setEnabled(true);
                                MainActivity.etLot.setEnabled(true);
                                MainActivity.etLot.requestFocus();
                                MainActivity.etItem.setBackground(getResources().getDrawable(R.drawable.text2));
                                MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text));
                                MainActivity.llSection2.setBackground(getResources().getDrawable(R.drawable.blue_button));
                                MainActivity.llSection1.setBackground(getResources().getDrawable(R.drawable.gray_button2));
                                GetItemUOM(ItemInformation.getUoMSchedule(), MainActivity.prefs.getString(MainActivity.Domain, ""), MainActivity.prefs.getString(MainActivity.Username, ""), MainActivity.prefs.getString(MainActivity.Password, ""));
                                DisplayUOMSpinner();
                                FirstFragment.Counted.clear();
                                FirstFragment.Inventory.clear();
                                FirstFragment.LotNumber.clear();
                                f.GetItemCounts(MainActivity.etItem.getText().toString());
                                f.ListGpVsCounted(MainActivity.etItem.getText().toString());
                                FirstFragment.listAdapter = new ExpandableListAdapter(getActivity(), FirstFragment.theParentList);
                                FirstFragment.myList.setAdapter(FirstFragment.listAdapter);
                                FirstFragment.myList.invalidateViews();
                            } else {

                                    CustomToast("Invalid Item Number.", R.color.red);
                                    MainActivity.etItem.setText("");
                            }
                        } else {
                            CustomToast("Please Enter or Scan the Item Number", R.color.red);
                        }

                        break;
                    case R.id.button2:
                        if (!MainActivity.etLot.getText().toString().isEmpty()) {
                            MainActivity.etLot.setText(MainActivity.etLot.getText().toString());
                            MainActivity.etLot.setEnabled(false);
                            MainActivity.btn2.setEnabled(false);
                            MainActivity.btnGo3.setEnabled(true);
                            MainActivity.etQty.setEnabled(true);
                            MainActivity.etQty.requestFocus();
                            MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text2));
                            MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text));
                        } else {
                            CustomToast("Please Enter or Scan the lot Number", R.color.red);
                        }


                        break;
                    case R.id.btnGo3:
                        if (!MainActivity.etQty.getText().toString().isEmpty()) {
                            MainActivity.etQty.setText(MainActivity.etQty.getText().toString());
                            MainActivity.etQty.setEnabled(false);
                            MainActivity.btnGo3.setEnabled(false);
                            MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text2));
                            MainActivity.ll.setBackgroundColor(getResources().getColor(R.color.orange));
                            MainActivity.m.setEnabled(false);
                            MainActivity.m.setIcon(getResources().getDrawable(R.drawable.barcode_icon_no));
                            MainActivity.btnNextCount.setEnabled(true);
                            MainActivity.llSection1.setBackground(getResources().getDrawable(R.drawable.gray_button2));
                            MainActivity.llSection2.setBackground(getResources().getDrawable(R.drawable.gray_button));
                        } else {
                            CustomToast("Please Enter or Scan Qty Counted", R.color.red);
                        }
                        break;
                    case R.id.button3:
                        if (!MainActivity.etItem.isEnabled()) {
                            if (!MainActivity.etLot.getText().toString().isEmpty()) {
                                Animation animationFadeIn2 = AnimationUtils.loadAnimation(getActivity(), R.anim.new_anim2);
                                MainActivity.llSection2.startAnimation(animationFadeIn2);
                                MainActivity.etLot.setText("");
                                MainActivity.etQty.setText("");
                                MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text));
                                MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text3));
                                MainActivity.etLot.setEnabled(true);
                                MainActivity.btnGo3.setEnabled(false);
                                MainActivity.btn2.setEnabled(true);
                                MainActivity.llSection2.setBackground(getResources().getDrawable(R.drawable.blue_button));
                                MainActivity.btnNextCount.setEnabled(false);
                                MainActivity.m.setEnabled(true);
                                MainActivity.m.setIcon(getResources().getDrawable(R.drawable.barcode_icon));
                                MainActivity.etLot.requestFocus();
                                MainActivity.ll.setBackgroundColor(getResources().getColor(R.color.org));
                            }
                        }
                        break;
                    case R.id.button4:
                        if (MainActivity.btnNextCount.getText().toString().contains("Update")) {
                            String a = MainActivity.etLot.getTag().toString();
                            UpdateItemCount(Integer.valueOf(a), MainActivity.spinner.getSelectedItem().toString(), MainActivity.etLot.getText().toString(), MainActivity.etQty.getText().toString());
                        } else {
                            InsertItemCount(MainActivity.etItem.getText().toString(), MainActivity.spinner.getSelectedItem().toString(), MainActivity.etLot.getText().toString(), MainActivity.etQty.getText().toString());
                            Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.new_anim);
                            MainActivity.llSection2.startAnimation(animationFadeIn);
                            MainActivity.etLot.setText("");
                            MainActivity.etQty.setText("");
                            MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text));
                            MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text3));
                            MainActivity.etLot.setEnabled(true);
                            MainActivity.btnGo3.setEnabled(false);
                            MainActivity.btn2.setEnabled(true);
                            MainActivity.llSection2.setBackground(getResources().getDrawable(R.drawable.blue_button));
                            MainActivity.btnNextCount.setEnabled(false);
                            MainActivity.m.setEnabled(true);
                            MainActivity.m.setIcon(getResources().getDrawable(R.drawable.barcode_icon));
                            MainActivity.etLot.requestFocus();
                            MainActivity.ll.setBackgroundColor(getResources().getColor(R.color.org));
                            FirstFragment.Counted.clear();
                            FirstFragment.Inventory.clear();
                            FirstFragment.LotNumber.clear();
                            f.ListGpVsCounted(MainActivity.etItem.getText().toString());
                            FirstFragment.listAdapter.notifyDataSetChanged();
                            Toast toast = Toast.makeText(getActivity(), "Item Lot Successfully Counted.", Toast.LENGTH_SHORT);
                            LinearLayout toastLayout = (LinearLayout) toast.getView();
                            TextView toastTV = (TextView) toastLayout.getChildAt(0);
                            toastTV.setTextSize(18);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toastTV.setTextColor(getResources().getColor(R.color.encode_view));
                            toastTV.setBackground(getResources().getDrawable(R.drawable.gray_button));

                            FirstFragment.theParentList.clear();
                            FirstFragment.childList.clear();
                            FirstFragment.ItemLotCounts.clear();
                            f.GetItemCounts(MainActivity.etItem.getText().toString());
                            f.ListGpVsCounted(MainActivity.etItem.getText().toString());
                            FirstFragment.listAdapter = new ExpandableListAdapter(getActivity(), FirstFragment.theParentList);
                            FirstFragment.myList.setAdapter(FirstFragment.listAdapter);
                            FirstFragment.myList.invalidateViews();


                            toast.show();
                        }
                        break;
                    case R.id.button12:
                        if (!MainActivity.etItem.getText().toString().isEmpty()) {
                            Animation animationFadeIn2 = AnimationUtils.loadAnimation(getActivity(), R.anim.new_anim2);
                            MainActivity.llMainFrag.startAnimation(animationFadeIn2);
                            ItemInformation ii = new ItemInformation();
                            ii.ClearItemDetails();
                            SecondFragment.ClearItemInfo();
                            MainActivity.etItem.setText("");
                            MainActivity.etLot.setText("");
                            MainActivity.etQty.setText("");
                            MainActivity.tvItemDesc.setText("");
                            MainActivity.spinner.setAdapter(null);
                            MainActivity.etItem.setBackground(getResources().getDrawable(R.drawable.text));
                            MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text3));
                            MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text3));
                            MainActivity.etItem.setEnabled(true);
                            MainActivity.etLot.setEnabled(false);
                            MainActivity.etLot.setEnabled(false);
                            MainActivity.btn1.setEnabled(true);
                            MainActivity.btnGo3.setEnabled(false);
                            MainActivity.btn2.setEnabled(false);
                            MainActivity.llSection1.setBackground(getResources().getDrawable(R.drawable.blue_button2));
                            MainActivity.llSection2.setBackground(getResources().getDrawable(R.drawable.gray_button));
                            MainActivity.btnNextCount.setEnabled(false);
                            MainActivity.m.setEnabled(true);
                            MainActivity.m.setIcon(getResources().getDrawable(R.drawable.barcode_icon));
                            MainActivity.etItem.requestFocus();
                            MainActivity.ll.setBackgroundColor(getResources().getColor(R.color.org));
                            FirstFragment.Counted.clear();
                            FirstFragment.Inventory.clear();
                            FirstFragment.LotNumber.clear();
                            FirstFragment.theParentList.clear();
                            FirstFragment.childList.clear();
                            FirstFragment.ItemLotCounts.clear();
                            FirstFragment.listAdapter = new ExpandableListAdapter(getActivity(), FirstFragment.theParentList);
                            FirstFragment.myList.setAdapter(FirstFragment.listAdapter);
                            FirstFragment.myList.invalidateViews();
                            MainActivity.btnNextCount.setText("Complete Count");
                        }

                        break;

                    default:
                        //code..
                        break;

                }
            }catch (Exception e){
                e.printStackTrace();
                    DisplayError(e.getMessage());
            }
    }

    public boolean GetItem(String ItemNumber, String Domain, String UserName, String Password){
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        PropertyInfo CasePI = new PropertyInfo();
        CasePI.setName("ItemNumber");
        CasePI.setValue(ItemNumber);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
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

        boolean hasResults = false;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE( MainActivity.prefs.getString(MainActivity.Url, ""));
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                if (response.getPropertyCount() > 0) {
                    hasResults = true;
                }
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

        }
        catch (Exception e) {
            e.printStackTrace();
            String a= e.getMessage();
            DisplayError(a);
            hasErrors = true;
        }

        return hasResults;
    }

    public void GetItemUOM(String uomSchedule, String Domain, String UserName, String Password){
        ItemInformation.clearItemUOmList();
        ArrayList<String> itemUOM = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
        PropertyInfo CasePI = new PropertyInfo();
        CasePI.setName("sUOMSchedule");
        CasePI.setValue(uomSchedule);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
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
            androidHttpTransport.call(SOAP_ACTION2, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();

            for(int i=0;i<response.getPropertyCount();i++){

                Object property = response.getProperty(i);
                SoapObject info = (SoapObject) property;
                String userName = info.getProperty("Uofm").toString();
                itemUOM.add(userName.trim());

            }
        } catch (Exception e) {

            e.printStackTrace();

        }

        ItemInformation.setItemUOmList(itemUOM);
    }

    public void UpdateItemCount(int countID,  String uom, String lotNumber, String qty){
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);
        PropertyInfo CasePI = new PropertyInfo();
        CasePI.setName("CountID");
        CasePI.setValue(countID);
        CasePI.setType(int.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("Uom");
        CasePI.setValue(uom);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("LotNumber");
        CasePI.setValue(lotNumber);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("Qty");
        CasePI.setValue(qty);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE( MainActivity.prefs.getString(MainActivity.Url, ""));

        try {
            androidHttpTransport.call(SOAP_ACTION5, envelope);


        } catch (Exception e) {
            e.printStackTrace();
            String a= e.getMessage();
            DisplayError(a);
            hasErrors = true;
        }
    }

    public void InsertItemCount(String itemNumber,  String uom, String lotNumber, String qty){
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);
        PropertyInfo CasePI = new PropertyInfo();
        CasePI.setName("ItemNumber");
        CasePI.setValue(itemNumber);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("Uom");
        CasePI.setValue(uom);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("LotNumber");
        CasePI.setValue(lotNumber);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        CasePI = new PropertyInfo();
        CasePI.setName("Qty");
        CasePI.setValue(qty);
        CasePI.setType(String.class);
        request.addProperty(CasePI);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE( MainActivity.prefs.getString(MainActivity.Url, ""));

        try {
            androidHttpTransport.call(SOAP_ACTION4, envelope);


        } catch (Exception e) {
            e.printStackTrace();
            String a= e.getMessage();
            DisplayError(a);
            hasErrors = true;
        }

    }

    public void DisplayUOMSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, ItemInformation.getItemUOMList());
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

    public void CustomToast(String message, int color){
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(18);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toastTV.setTextColor(getResources().getColor(color));
        toast.show();
    }


    public void DisplayError(String message){
        AlertDialog alertDialog1 = new AlertDialog.Builder(
                getActivity()).create();

        alertDialog1.setTitle("Error");
        alertDialog1.setMessage(message);
        alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog1.show();
    }


}



