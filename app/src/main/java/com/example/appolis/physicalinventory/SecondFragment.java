package com.example.appolis.physicalinventory;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public  class SecondFragment  extends Fragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     *
     */

    private static final String[] statuses = {"--Select--","EA","BAG50"};

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static SecondFragment newInstance(String message)

    {
        SecondFragment f = new SecondFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);

        return f;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_details, container, false);
        MainActivity.tvItemNumber = (TextView) rootView.findViewById(R.id.section_label);
        MainActivity.tvItemDescription = (TextView) rootView.findViewById(R.id.tvItemDesc2);
        MainActivity.tvStdCost = (TextView) rootView.findViewById(R.id.tvStndCost2);
        MainActivity.tvCurrCost = (TextView) rootView.findViewById(R.id.tvCurrCost2);
        MainActivity.tvItemShWt = (TextView) rootView.findViewById(R.id.tvShip2);
        MainActivity.tvPUom = (TextView) rootView.findViewById(R.id.tvPuom2);
        MainActivity.tvSUom = (TextView) rootView.findViewById(R.id.tvSuom2);
        MainActivity.tvUOMSchedule = (TextView) rootView.findViewById(R.id.tvuom2);

        MainActivity.tvItemNumber.setText("Item: "+ItemInformation.getItemNumber());
        MainActivity.tvItemDescription.setText(ItemInformation.getItemDescription());
        MainActivity.tvStdCost.setText(ItemInformation.getStdCost());
        MainActivity.tvCurrCost.setText(ItemInformation.getCurrCost());
        MainActivity.tvItemShWt.setText(ItemInformation.getItemShWt());
        MainActivity.tvPUom.setText(ItemInformation.getpUom());
        MainActivity.tvSUom.setText(ItemInformation.getSUom());
        MainActivity.tvUOMSchedule.setText(ItemInformation.getUoMSchedule());
        return rootView;


    }


    @Override
    public void onClick(View view) {

    }

    public static void ClearItemInfo(){

        MainActivity.tvItemNumber.setText("Item: "+ItemInformation.getItemNumber());
        MainActivity.tvItemDescription.setText(ItemInformation.getItemDescription());
        MainActivity.tvStdCost.setText(ItemInformation.getStdCost());
        MainActivity.tvCurrCost.setText(ItemInformation.getCurrCost());
        MainActivity.tvItemShWt.setText(ItemInformation.getItemShWt());
        MainActivity.tvPUom.setText(ItemInformation.getpUom());
        MainActivity.tvSUom.setText(ItemInformation.getSUom());
        MainActivity.tvUOMSchedule.setText(ItemInformation.getUoMSchedule());

    }
}