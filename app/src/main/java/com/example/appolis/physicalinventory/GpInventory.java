package com.example.appolis.physicalinventory;

import java.util.ArrayList;

/**
 * Created by Jeffery on 9/13/2014.
 */
public class GpInventory {
    public  String CountID;
    public  String ItemNumber;
    public  String UOM;
    public  String LotNumber;
    public  String Qty;
    public  String BinNumber;
    public  String DateCreated;

    public static ArrayList<GpInventory> GPInventoryList = new ArrayList<>();



    public GpInventory(String countID, String itemNumber, String uom, String lotNumber, String qty, String date, String bin){

        this.CountID = countID.trim();
        this.ItemNumber=itemNumber.trim();
        this.UOM = uom.trim();
        this.LotNumber = lotNumber.trim();
        this.Qty = qty.trim();
        this.DateCreated = date.trim();
        this.BinNumber = bin.trim();

    }

    public String getItemNumber() {
        return ItemNumber;
    }

    public void setItemNumber(String itemNumber) {
        ItemNumber = itemNumber;
    }

    public String getLotNumber() {
        return LotNumber;
    }

    public void setLotNumber(String lotNumber) {
        LotNumber = lotNumber;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getDateCreated() { return DateCreated; }

    public void setDateCreated(String dateCreated) { DateCreated = dateCreated; }

    public String getBinNumber() {
        return BinNumber;
    }

    public void setBinNumber(String binNumber) {
        BinNumber = binNumber;
    }

    public String getCountID() {
        return CountID;
    }

    public void setCountID(String countID) {
        CountID = countID;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public static ArrayList<GpInventory> getGPInventoryList() {
        return  GPInventoryList;
    }

    public static void setGPInventoryList(ArrayList<GpInventory> arrList) {
        GPInventoryList = arrList;
    }

    public static void cleatGPInventoryList() {
        GPInventoryList.clear();
    }



}
