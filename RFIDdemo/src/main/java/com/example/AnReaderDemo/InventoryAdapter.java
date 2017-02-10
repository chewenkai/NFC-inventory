package com.example.AnReaderDemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class InventoryAdapter extends BaseAdapter {
    private List<InventoryReport> list;
    private LayoutInflater inflater;

    public InventoryAdapter(Context context, List<InventoryReport> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        InventoryReport inventoryReport = (InventoryReport) this
                .getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.inventorylist_tittle, null);
            viewHolder.mTextUid = (TextView) convertView
                    .findViewById(R.id.tv_inventoryUid);
            viewHolder.mTextTagType = (TextView) convertView
                    .findViewById(R.id.tv_inventoryTagType);
            viewHolder.mTextFindCnt = (TextView) convertView
                    .findViewById(R.id.tv_inventoryCnt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTextUid.setText(inventoryReport.getUidStr());
        viewHolder.mTextTagType.setText(inventoryReport.getTagTypeStr());
        viewHolder.mTextFindCnt.setText(inventoryReport.getFindCnt() + "");

        return convertView;
    }

    public static class ViewHolder {
        public TextView mTextUid;
        public TextView mTextTagType;
        public TextView mTextFindCnt;
    }
}
