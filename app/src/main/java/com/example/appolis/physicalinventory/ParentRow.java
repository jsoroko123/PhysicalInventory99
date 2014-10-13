package com.example.appolis.physicalinventory;

import java.util.ArrayList;

public class ParentRow {

    private String lotNumber;
    private String inventory;
    private String counted;
    public boolean isChecked;

    public  boolean isChecked() {
        return isChecked;
    }

    public  void setChecked(boolean isChecked2) {
        isChecked = isChecked2;
    }

    private ArrayList<ChildRow> children = new ArrayList<ChildRow>();

    public ParentRow(String lot, String inv, String count, boolean checked, ArrayList<ChildRow> childList) {
        super();
        this.lotNumber = lot;
        this.inventory = inv;
        this.counted = count;
        this.isChecked = checked;
        this.children = childList;

    }

    public ParentRow() {
        super();


    }
    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getCounted() {
        return counted;
    }

    public void setCounted(String counted) {
        this.counted = counted;
    }

    public ArrayList<ChildRow> getChildList() {
        return children;
    }
    public void setChildList(ArrayList<ChildRow> childList) {
        this.children = childList;
    };


}