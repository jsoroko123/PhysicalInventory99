package com.example.appolis.physicalinventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
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
        private final String SOAP_ACTION5 = "http://tempuri.org/InsertGPVariance";
        private final String METHOD_NAME5 = "InsertGPVariance";

        public static ArrayList<String> LotNumber = new ArrayList<String>();
        public static ArrayList<String> Inventory = new ArrayList<String>();
        public static ArrayList<String> Counted = new ArrayList<String>();
        public static TextView ciItemNumber, ciLotNumber, ciUom,ciQty, ciDate, ciSite;
       Button btnGP;
        public static String itemNum, lotNum, uom, qty, datePart, timePart;

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
            MainActivity.htvItemNum = (TextView) rootView.findViewById(R.id.tvffItem);
            MainActivity.htvBaseUom = (TextView) rootView.findViewById(R.id.tvffUom);
            MainActivity.htvSite = (TextView) rootView.findViewById(R.id.tvffSite);
            btnGP = (Button) rootView.findViewById(R.id.btnSyncGP);
            MainActivity.htvItemNum.setText(ItemInformation.getItemNumber());
            MainActivity.htvBaseUom.setText(ItemInformation.getSUom());
            btnGP.setOnClickListener(this);
            MainActivity.htvSite.setText(MainActivity.prefs.getString(MainActivity.Site, ""));
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
                                GetItemCounts(MainActivity.etItem.getText().toString(), MainActivity.prefs.getString(MainActivity.Company, ""), MainActivity.prefs.getString(MainActivity.Site, ""));
                                ListGpVsCounted(MainActivity.etItem.getText().toString(), MainActivity.prefs.getString(MainActivity.Company, ""), MainActivity.prefs.getString(MainActivity.Site, ""));
                                listAdapter = new ExpandableListAdapter(getActivity(),  FirstFragment.theParentList);
                                myList.setAdapter(FirstFragment.listAdapter);
                                myList.invalidateViews();
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.pager.setCurrentItem(1);
                                MainActivity.btnNextCount.setText("Update Count");
                                if(ItemInformation.getLotTrackingInd().equals("1")) {
                                    MainActivity.etLot.setText(lotNum);
                                    MainActivity.etQty.setText(qty);
                                    MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text));
                                    MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text3));
                                    MainActivity.etLot.setEnabled(true);
                                    MainActivity.btn2.setEnabled(true);
                                    MainActivity.btnGo3.setEnabled(false);
                                }else{
                                    MainActivity.etLot.setText("");
                                    MainActivity.etQty.setText(qty);
                                    MainActivity.etLot.setEnabled(false);
                                    MainActivity.btn2.setEnabled(false);
                                    MainActivity.btnGo3.setEnabled(true);
                                    MainActivity.etQty.setEnabled(true);
                                    MainActivity.etQty.requestFocus();
                                    MainActivity.etLot.setBackground(getResources().getDrawable(R.drawable.text2));
                                    MainActivity.etQty.setBackground(getResources().getDrawable(R.drawable.text));
                                    MainActivity.etLot.setHint("Lot Tracking N/A");
                                }

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
                ciSite = (TextView) inflatedView.findViewById(R.id.ci_location);
                ciItemNumber.setText(itemNum);
                ciLotNumber.setText(lotNum);
                ciUom.setText(uom);
                ciQty.setText(qty);
                ciDate.setText(datePart);
                ciSite.setText(MainActivity.prefs.getString(MainActivity.Site, ""));

                builder.show();

                return false;
            }
        });
        return rootView;
    }

         public void ListGpVsCounted(String ItemNumber, String Company, String Site){
             if(!MainActivity.etItem.getText().toString().isEmpty()) {
                 itemNum = ItemNumber;
                 SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                 PropertyInfo CasePI = new PropertyInfo();
                 CasePI.setName("ItemNumber");
                 CasePI.setValue(ItemNumber);
                 CasePI.setType(String.class);
                 request.addProperty(CasePI);

                 CasePI = new PropertyInfo();
                 CasePI.setName("company");
                 CasePI.setValue(Company);
                 CasePI.setType(String.class);
                 request.addProperty(CasePI);

                 CasePI = new PropertyInfo();
                 CasePI.setName("siteID");
                 CasePI.setValue(Site);
                 CasePI.setType(String.class);
                 request.addProperty(CasePI);

                 SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                         SoapEnvelope.VER11);
                 envelope.dotNet = true;
                 envelope.setOutputSoapObject(request);
                 HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.prefs.getString(MainActivity.Url, ""));

                 try {
                     androidHttpTransport.call(SOAP_ACTION, envelope);
                     SoapObject response = (SoapObject) envelope.getResponse();
                     String parentlotNum;
                     String parentinvent;
                     String parentassign;

                     for (int i = 0; i < response.getPropertyCount(); i++) {

                         Object property = response.getProperty(i);
                         SoapObject info = (SoapObject) property;
                         if (ItemInformation.getLotTrackingInd().equals("1")) {
                             parentlotNum = info.getProperty("LotNumber").toString().trim();
                         } else {
                             parentlotNum = "Lot Tracking N/A";
                         }
                         parentinvent = info.getProperty("Inventory").toString().trim();
                         parentassign = info.getProperty("Counted").toString().trim();
                         childList = new ArrayList<ChildRow>();
                         for (int a = 0; a < ItemLotCounts.size(); a++) {
                             if (ItemInformation.getLotTrackingInd().equals("1")) {
                                 if (parentlotNum.toUpperCase().equals(ItemLotCounts.get(a).getLotNumber().toUpperCase())) {
                                     ChildRow ch = new ChildRow(ItemLotCounts.get(a).getCountID(), ItemNumber, ItemLotCounts.get(a).getUOM(), ItemLotCounts.get(a).getLotNumber(), ItemLotCounts.get(a).getQty(), ItemLotCounts.get(a).getDateCreated(), ItemLotCounts.get(a).getBinNumber());
                                     childList.add(ch);
                                 }
                             } else {
                                 ChildRow ch = new ChildRow(ItemLotCounts.get(a).getCountID(), ItemNumber, ItemLotCounts.get(a).getUOM(), "Lot Tracking N/A", ItemLotCounts.get(a).getQty(), ItemLotCounts.get(a).getDateCreated(), ItemLotCounts.get(a).getBinNumber());
                                 childList.add(ch);

                             }
                         }


                         ParentRow p = new ParentRow(parentlotNum, parentinvent, parentassign, childList);
                         theParentList.add(p);
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                     String a = e.getMessage();
                     DisplayError(a);
                 }
             }
         }

         public void GetItemCounts(String ItemNumber, String Company, String Site){
            SoapObject request2 = new SoapObject(NAMESPACE, METHOD_NAME2);
            PropertyInfo CasePI2 = new PropertyInfo();
             CasePI2.setName("ItemNumber");
             CasePI2.setValue(ItemNumber);
             CasePI2.setType(String.class);
             request2.addProperty(CasePI2);

             CasePI2 = new PropertyInfo();
             CasePI2.setName("company");
             CasePI2.setValue(Company);
             CasePI2.setType(String.class);
             request2.addProperty(CasePI2);

             CasePI2 = new PropertyInfo();
             CasePI2.setName("siteID");
             CasePI2.setValue(Site);
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
                    String newTime = newDate[1];
                    String[] timePart = newTime.split("\\.");




                    GpInventory gp = new GpInventory(countID, ItemNumber, uom, lotNum, qty, datePart+"   "+timePart[0], bin);
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
                    if(ItemInformation.getLotTrackingInd().equals("1")) {
                        lotNum = info2.getProperty("LotNumber").toString();
                    } else{
                        lotNum = "Lot Tracking N/A";
                    }
                    uom = info2.getProperty("Uom").toString();
                    qty = info2.getProperty("Qty").toString();
                    String date = info2.getProperty("DateCreated").toString();
                    String[] newDate = date.split("T");
                    datePart = newDate[0];
                    String time = newDate[1];


                }
            } catch (Exception e) {
                e.printStackTrace();
                String a= e.getMessage();
                DisplayError(a);
            }


        }

        public void InsertGPVariance(String domain,String UserName, String Password,  String company, String Site, String itemNumber, String qty, String LotNumber, String sUom){
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);
            PropertyInfo CasePI = new PropertyInfo();
            CasePI.setName("Domain");
            CasePI.setValue(domain);
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
            CasePI.setValue(company);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            CasePI = new PropertyInfo();
            CasePI.setName("sWarehouseName");
            CasePI.setValue(Site);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            CasePI = new PropertyInfo();
            CasePI.setName("sItemNumber");
            CasePI.setValue(itemNumber);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            CasePI = new PropertyInfo();
            CasePI.setName("dQty");
            CasePI.setValue(qty);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            CasePI = new PropertyInfo();
            CasePI.setName("sLotNumber");
            CasePI.setValue(LotNumber);
            CasePI.setType(String.class);
            request.addProperty(CasePI);

            CasePI = new PropertyInfo();
            CasePI.setName("sSellingUOM");
            CasePI.setValue(sUom);
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
                DisplayError(e.getMessage());

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
            switch (view.getId()) {
                case R.id.btnSyncGP:
                    for(int i=0;i<theParentList.size();i++) {
                        String a = theParentList.get(i).getCounted().toString();
                        String b = theParentList.get(i).getInventory().toString();
                        String c = theParentList.get(i).getLotNumber();
                        String d = ItemInformation.getItemNumber();
                        String e = ItemInformation.getSUom();
                        String f = MainActivity.prefs.getString(MainActivity.Domain, "");
                        String g = MainActivity.prefs.getString(MainActivity.Username, "");
                        String h = MainActivity.prefs.getString(MainActivity.Password, "");
                        String j = MainActivity.prefs.getString(MainActivity.Company, "");
                        String k = MainActivity.prefs.getString(MainActivity.Site, "");
                        InsertGPVariance(f, g, h, j, k, d, ItemInformation.GetVariance(a,b), c, e);

                        }
                    CustomToast("GP Synchronization Successful.",R.color.encode_view);
            }
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