package com.example.gabriel.app.sunshine;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView)rootview.findViewById(R.id.listview);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Gabriel");
        arrayList.add("Abdul");
        arrayList.add("Luis");
        arrayList.add("Tania");
        arrayList.add("Kike");
        arrayList.add("William");

        ArrayAdapter adapter  = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);


        return rootview;
    }

}
