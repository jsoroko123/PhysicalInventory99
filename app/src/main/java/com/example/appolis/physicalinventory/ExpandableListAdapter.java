package com.example.appolis.physicalinventory;
import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ParentRow> parentList;
    private ArrayList<ParentRow> originalList;

    public ExpandableListAdapter(Context context, ArrayList<ParentRow> continentList) {
        this.context = context;
        this.parentList = new ArrayList<>();
        this.parentList.addAll(continentList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(continentList);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ChildRow> countryList = parentList.get(groupPosition).getChildList();
        return countryList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        ChildRow child = (ChildRow) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.listrow_details, null);
        }



        TextView lot = (TextView) view.findViewById(R.id.code);

        TextView date = (TextView) view.findViewById(R.id.name);
        TextView count = (TextView) view.findViewById(R.id.population);
        lot.setText(child.getLotNumber().trim());
        lot.setTag(child.getCountID().trim());
        count.setText(child.getQty().trim());
        date.setText(child.getUOM().trim());

        return view;
    }



    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<ChildRow> countryList = parentList.get(groupPosition).getChildList();
        return countryList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        ParentRow parentRow = (ParentRow) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.listrow_group, null);
        }

        TextView lot = (TextView) view.findViewById(R.id.tv_lotnumber);
        lot.setText(parentRow.getLotNumber().trim());
        TextView inv = (TextView) view.findViewById(R.id.tv_inventory);
        inv.setText(parentRow.getInventory().trim());
        TextView count = (TextView) view.findViewById(R.id.tv_counted);
        count.setText(parentRow.getCounted().trim());


        return view;
    }




    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }






}