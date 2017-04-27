package com.example.AnReaderDemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ScanAdapter extends BaseAdapter {
    private List<ScanReport> list;
    private LayoutInflater inflater;

    public ScanAdapter(Context context, List<ScanReport> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScanReport scanfReport = (ScanReport) this.getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.xml.scan_record_list_tittle,
                    null);
            viewHolder.mTextData = (TextView) convertView
                    .findViewById(R.id.tv_scanfData);
            viewHolder.mTextFindCnt = (TextView) convertView
                    .findViewById(R.id.tv_scanfCnt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextData.setText(scanfReport.getDataStr());
        viewHolder.mTextFindCnt.setText(scanfReport.getFindCnt() + "");
        return convertView;
    }

    public static class ViewHolder {
        public TextView mTextData;
        public TextView mTextFindCnt;
    }

}