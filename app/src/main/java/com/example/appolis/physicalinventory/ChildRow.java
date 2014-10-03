package com.example.appolis.physicalinventory;

public class ChildRow {

    public  String CountID;
    public  String ItemNumber;
    public  String UOM;
    public  String LotNumber;
    public  String Qty;
    public  String BinNumber;
    public  String DateCreated;

    public ChildRow(String countID, String itemNumber, String uom, String lotNumber, String qty, String date, String bin) {
        super();
        this.CountID = countID.trim();
        this.ItemNumber=itemNumber.trim();
        this.UOM = uom.trim();
        this.LotNumber = lotNumber.trim();
        this.Qty = qty.trim();
        this.DateCreated = date.trim();
        this.BinNumber = bin.trim();

    }

    public String getCountID() {
        return CountID;
    }

    public void setCountID(String countID) {
        CountID = countID;
    }

    public String getItemNumber() {
        return ItemNumber;
    }

    public void setItemNumber(String itemNumber) {
        ItemNumber = itemNumber;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
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

    public String getBinNumber() {
        return BinNumber;
    }

    public void setBinNumber(String binNumber) {
        BinNumber = binNumber;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }
}