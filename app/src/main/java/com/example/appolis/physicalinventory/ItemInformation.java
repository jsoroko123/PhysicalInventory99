package com.example.appolis.physicalinventory;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffery on 9/10/2014.
 */
    public class ItemInformation {

    public static String itemNumber;
    public static String itemDescription;
    public static String stdCost;
    public static String currCost;
    public static String itemShWt;
    public static String pUom;
    public static String SUom;
    public static String uoMSchedule;
    public static String site;

    public static String getSite() {
        return site;
    }

    public static void setSite(String site) {
        ItemInformation.site = site;
    }

    public static String getLotTrackingInd() {
        return LotTrackingInd;
    }

    public static void setLotTrackingInd(String lotTrackingInd) {
        LotTrackingInd = lotTrackingInd;
    }

    public static String LotTrackingInd;
    public static ArrayList<Pair> itemUOM = new ArrayList<>();

    public ItemInformation(){

    }

    public static String getItemNumber() {
        return itemNumber;
    }

    public static void setItemNumber(String itemNmbr) {
        itemNumber = itemNmbr.trim();
    }

    public static String getItemDescription() {
        return itemDescription;
    }

    public static void setItemDescription(String itemDesc) {
        itemDescription = itemDesc.trim();
    }

    public static String getStdCost() {
        return stdCost;
    }

    public static void setStdCost(String stdCst) {
        stdCost = stdCst.trim();
    }

    public static String getCurrCost() {
        return currCost;
    }

    public static void setCurrCost(String currCst) {
        currCost = currCst.trim();
    }

    public static String getItemShWt() {
        return itemShWt;
    }

    public static void setItemShWt(String itemSh) {
        itemShWt = itemSh.trim();
    }

    public static String getpUom() {
        return pUom;
    }

    public static void setpUom(String pUom1) {
        pUom = pUom1.trim();
    }

    public static String getSUom() {
        return SUom;
    }

    public static void setSUom(String SUom1) {
        SUom = SUom1.trim();
    }

    public static String getUoMSchedule() {
        return uoMSchedule;
    }

    public static void setUoMSchedule(String uoMSchedule1) {
        uoMSchedule = uoMSchedule1.trim();
    }

    public static void ClearItemDetails()
    {
        setItemNumber("");
        setItemDescription("");
        setStdCost("");
        setCurrCost("");
        setItemShWt("");
        setpUom("");
        setSUom("");
        setUoMSchedule("");
        setSite("");
    }

    public static ArrayList<Pair> getItemUOMList() {
        return itemUOM;
    }

    public static void setItemUOmList(ArrayList<Pair> arrList) {
        itemUOM = arrList;
    }

    public static void clearItemUOmList() {
        itemUOM.clear();
    }

    public static String GetBaseQuantity(String qtyCounted, String bcf ){
        String qtyConverted;
        double quantity = Double.valueOf(qtyCounted) * Double.valueOf(bcf);
        qtyConverted = String.valueOf(quantity);

        return qtyConverted;


    }

    public static String GetVariance(String Counted, String Inventory){
        String varianceConverted;
        double variance = Double.valueOf(Counted) - Double.valueOf(Inventory);
        varianceConverted = String.valueOf(variance);
        return varianceConverted;

    }



}

