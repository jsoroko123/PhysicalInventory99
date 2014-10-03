package com.example.appolis.physicalinventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

    public  class FirstFragment  extends Fragment implements View.OnClickListener {

        private final String NAMESPACE = "http://tempuri.org/";
        private final String SOAP_ACTION = "http://tempuri.org/GetGPInventoryVsCount";
        private final String METHOD_NAME = "GetGPInventoryVsCount";
        private final String SOAP_ACTION2 = "http://tempuri.org/GetItemCounts";
        private final String METHOD_NAME2 = "GetItemCounts";
        private final String SOAP_ACTION3 = "http://tempuri.org/GetItemCount";
        private final String METHOD_NAME3 = "GetItemCount";
        private final String SOAP_ACTION4 = "http://tempuri.org/DeleteItemCount";
        private final String METHOD_NAME4 = "DeleteItemCount";

        public static ArrayList<String> LotNumber = new ArrayList<String>();
        public static ArrayList<String> Inventory = new ArrayList<String>();
        public static ArrayList<String> Counted = new ArrayList<String>();
        public static TextView ciItemNumber, ciLotNumber, ciUom,ciQty, ciDate;

        public static String itemNum, lotNum, uom, qty, datePart;

        public static ExpandableListAdapter listAdapter;
        public static ExpandableListView myList;
        public static  ArrayList<ChildRow> childList = new ArrayList<>();
        public static ArrayList<ParentRow> theParentList = new ArrayList<>();
        public static ArrayList<GpInventory> ItemLotCounts = new ArrayList<>();
        public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

        public static FirstFragment newInstance(String message) {
            FirstFragment f = new FirstFragment();
            Bundle bdl = new Bundle(1);
            bdl.putString(EXTRA_MESSAGE, message);
            f.setArguments(bdl);

            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_inventory_history, container, false);
            myList = (ExpandableListView) rootView.findViewById(R.id.listView);
            listAdapter = new ExpandableListAdapter(getActivity(), theParentList);
            myList.setAdapter(listAdapter);
            myList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if(groupPosition != previousGroup)
                        myList.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                }
            });

            myList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final TextView tv = ((TextView) v.findViewById(R.id.code));
                GetItemCount(Integer.valueOf(tv.getTag().toString()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View inflatedView = inflater.inflate(R.layout.count_info, null);
                builder.setView(inflatedView)
                        // Add action buttons
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                DeleteItemCount(Integer.valueOf(tv.getTag().toString()));
                                theParentList.clear();
                                childList.clear();
                                ItemLotCounts.clear();
                                GetItemCounts(MainActivity.etItem.getText().toString());
                                ListGpVsCounted(MainActivity.etItem.getText().toString());
                                listAdapter = new ExpandableListAdapter(getActivity(),  FirstFragment.theParentList);
                                myList.setAdapter(FirstFragment.listAdapter);
                                myList.invalidateViews();
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.pager.setCurrentItem(1);
                                MainActivity.btnNextCount.setText("Update Count");
                                MainActivity.etLot.setText(lotNum);
                                MainActivity.etQty.setText(qty);
                                MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text));
                                MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text3));
                                MainActivity.etLot.setEnabled(true);
                                MainActivity.btn2.setEnabled(true);
                                MainActivity.btnGo3.setEnabled(false);
                                MainActivity.etLot.setTag(Integer.valueOf(tv.getTag().toString()));
                                MainActivity.m.setEnabled(true);
                                MainActivity.m.setIcon(getResources().getDrawable(R.drawable.barcode_icon));
                                ArrayAdapter myAdap = (ArrayAdapter) MainActivity.spinner.getAdapter();
                                int spinnerPosition = myAdap.getPosition(uom);
                                MainActivity.spinner.setSelection(spinnerPosition);
                                MainActivity.etLot.requestFocus();
                            }
                        });

                ciItemNumber = (TextView) inflatedView.findViewById(R.id.ci_item);
                ciLotNumber = (TextView) inflatedView.findViewById(R.id.ci_lot);
                ciUom = (TextView) inflatedView.findViewById(R.id.ci_uom);
                ciQty = (TextView) inflatedView.findViewById(R.id.ci_qty);
                ciDate = (TextView) inflatedView.findViewById(R.id.ci_date);

                ciItemNumber.setText(itemNum);
                ciLotNumber.setText(lotNum);
                ciUom.setText(uom);
                ciQty.setText(qty);
                ciDate.setText(datePart);

                builder.show();

                return false;
            }
        });
        return rootView;
    }

         public void ListGpVsCounted(String ItemNumber){
            itemNum = ItemNumber;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            PropertyInfo CasePI = new PropertyInfo();
            CasePI.setName("ItemNumber");
            CasePI.setValue(ItemNumber);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.prefs.getString(MainActivity.Url, ""));

            try {            androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                String parentlotNum;
                String parentinvent;
                String parentassign;

                for(int i=0;i<response.getPropertyCount();i++){

                    Object property = response.getProperty(i);
                    SoapObject info = (SoapObject) property;

                    parentlotNum = info.getProperty("LotNumber").toString().trim();
                    parentinvent = info.getProperty("Inventory").toString().trim();
                    parentassign = info.getProperty("Counted").toString().trim();
                    childList = new ArrayList<ChildRow>();
                    for(int a=0;a<ItemLotCounts.size();a++){

                        if(parentlotNum.toUpperCase().equals(ItemLotCounts.get(a).getLotNumber().toUpperCase())){
                            ChildRow ch = new ChildRow(ItemLotCounts.get(a).getCountID(), ItemNumber, ItemLotCounts.get(a).getUOM(), ItemLotCounts.get(a).getLotNumber(),ItemLotCounts.get(a).getQty(),ItemLotCounts.get(a).getDateCreated(),ItemLotCounts.get(a).getBinNumber());
                            childList.add(ch);
                        }
                    }
                    ParentRow p = new ParentRow(parentlotNum, parentinvent, parentassign, childList);
                    theParentList.add(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String a= e.getMessage();
                DisplayError(a);
            }
         }

         public void GetItemCounts(String ItemNumber){
            SoapObject request2 = new SoapObject(NAMESPACE, METHOD_NAME2);
            PropertyInfo CasePI2 = new PropertyInfo();
            CasePI2.setName("ItemNumber");
            CasePI2.setValue(ItemNumber);
            CasePI2.setType(String.class);
            request2.addProperty(CasePI2);

            SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope2.dotNet = true;
            envelope2.setOutputSoapObject(request2);
            HttpTransportSE androidHttpTransport2 = new HttpTransportSE(MainActivity.prefs.getString(MainActivity.Url, ""));

            try {
                androidHttpTransport2.call(SOAP_ACTION2, envelope2);
                SoapObject response2 = (SoapObject) envelope2.getResponse();

                for(int i=0;i<response2.getPropertyCount();i++){

                    Object property2 = response2.getProperty(i);
                    SoapObject info2 = (SoapObject) property2;
                    String countID = info2.getProperty("CountID").toString();
                    String lotNum = info2.getProperty("LotNumber").toString();
                    String uom = info2.getProperty("Uom").toString();
                    String qty = info2.getProperty("Qty").toString();
                    String bin = info2.getProperty("BinNumber").toString();
                    String date = info2.getProperty("DateCreated").toString();
                    String[] newDate = date.split("T");
                    String datePart = newDate[0];

                    GpInventory gp = new GpInventory(countID, ItemNumber, uom, lotNum, qty, datePart, bin);
                    ItemLotCounts.add(gp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String a= e.getMessage();
                DisplayError(a);
            }
        }

        public void GetItemCount(int CountID){
            SoapObject request2 = new SoapObject(NAMESPACE, METHOD_NAME3);
            PropertyInfo CasePI2 = new PropertyInfo();
            CasePI2.setName("CountID");
            CasePI2.setValue(CountID);
            CasePI2.setType(int.class);
            request2.addProperty(CasePI2);

            SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope2.dotNet = true;
            envelope2.setOutputSoapObject(request2);
            HttpTransportSE androidHttpTransport2 = new HttpTransportSE( MainActivity.prefs.getString(MainActivity.Url, ""));

            try {
                androidHttpTransport2.call(SOAP_ACTION3, envelope2);
                SoapObject response2 = (SoapObject) envelope2.getResponse();

                for(int i=0;i<response2.getPropertyCount();i++){
                    Object property2 = response2.getProperty(i);
                    SoapObject info2 = (SoapObject) property2;
                    lotNum = info2.getProperty("LotNumber").toString();
                    uom = info2.getProperty("Uom").toString();
                    qty = info2.getProperty("Qty").toString();
                    String date = info2.getProperty("DateCreated").toString();
                    String[] newDate = date.split("T");
                    datePart = newDate[0];
                }
            } catch (Exception e) {
                e.printStackTrace();
                String a= e.getMessage();
                DisplayError(a);
            }


        }

        public void DeleteItemCount(int countID){
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);
            PropertyInfo CasePI = new PropertyInfo();
            CasePI.setName("CountID");
            CasePI.setValue(countID);
            CasePI.setType(int.class);
            request.addProperty(CasePI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE( MainActivity.prefs.getString(MainActivity.Url, ""));

            try {
                androidHttpTransport.call(SOAP_ACTION4, envelope);
}           catch (Exception e) {
                e.printStackTrace();
                String a= e.getMessage();
                DisplayError(a);
            }

        }
        @Override
        public void onClick(View view) {

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