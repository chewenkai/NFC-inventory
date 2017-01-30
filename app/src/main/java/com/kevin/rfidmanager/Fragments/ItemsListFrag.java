package com.kevin.rfidmanager.Fragments;

/**
 * Created by Kevin on 2017/1/26.
 */

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevin.rfidmanager.Adapter.ItemListAdaper;
import com.kevin.rfidmanager.R;
import com.kevin.rfidmanager.Utils.DatabaseUtil;

public class ItemsListFrag extends android.support.v4.app.Fragment {
    private RecyclerView recyclerView;
    private ItemListAdaper itemListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_list_layout, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recycle_item_list);
        itemListAdapter = new ItemListAdaper(getActivity(), DatabaseUtil.queryItems(getActivity()));
        recyclerView.setAdapter(itemListAdapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);// First param is number of columns and second param is orientation i.e Vertical or Horizontal
        recyclerView.setLayoutManager(gridLayoutManager);// Attach the layout manager to the recycler view
        recyclerView.setHasFixedSize(true);
    }
}
