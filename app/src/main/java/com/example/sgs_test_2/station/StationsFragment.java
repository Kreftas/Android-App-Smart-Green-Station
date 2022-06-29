package com.example.sgs_test_2.station;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sgs_test_2.R;
import com.example.sgs_test_2.RealMainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utility.MockupFactory;

public class StationsFragment extends Fragment {
    private List<Integer> idList = new ArrayList<>();
    private final MockupFactory mockupFactory = new MockupFactory();

    /**
     * Default constructor
     */
    public StationsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_station_list, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack("MapFragment", 0);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        RealMainActivity activity = (RealMainActivity) this.getActivity();
        NonScrollableListView dataListView = view.findViewById(R.id.slider_list_view);
        List<String> stationList = new ArrayList<>();
        idList = new ArrayList<>();
        StationListAdapter stationListAdapter = new StationListAdapter(getContext(), stationList, idList);
        dataListView.setAdapter(stationListAdapter);
        TextView clickForMoreInfoButton = view.findViewById(R.id.click_for_more_info);

        if (!activity.getDatabaseObject().initializeStations() || !activity.isNetworkConnected()) {
            idList.add(0);
            stationList.add("");
            clickForMoreInfoButton.setText(R.string.click_to_launch_settings_application);
        } else {
            HashMap<Integer, String> list = activity.getDatabaseObject().getStationDescription();//The station descriptions
            for (HashMap.Entry<Integer, String> entry : list.entrySet()) {
                idList.add(entry.getKey());
                stationList.add(entry.getValue());
            }
        }
        stationListAdapter.notifyDataSetChanged();
        dataListView.setOnItemClickListener((AdapterView<?> adapterView, View view1, int i, long l) -> {
            if (activity.getDatabaseObject().databaseInitialized() && activity.isNetworkConnected()) {
                activity.getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        new TextViewer((mockupFactory.getName(idList.get(i))), idList.get(i)), "TextViewer").addToBackStack("TextViewer").commit();
            } else {
                activity.getActivityLauncher().launch(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });


        return view;
    }

    public class StationListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final List<Integer> ids;
        private final RealMainActivity activity = (RealMainActivity) getActivity();

        public StationListAdapter(Context context, List<String> stationNames, List<Integer> ids) {
            super(context, R.layout.station_list_item, R.id.main_text_view, stationNames);
            this.context = context;
            this.ids = ids;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View item = layoutInflater.inflate(R.layout.data_list_item, parent, false);

            TextView stationName = item.findViewById(R.id.main_text_view);

            if (!activity.getDatabaseObject().databaseInitialized() || !activity.isNetworkConnected()) {
                stationName.setText(R.string.No_Connection);
            } else {
                int id = ids.get(position);
                String text = mockupFactory.getName(id) + ": " + mockupFactory.getType(id);
                stationName.setText(text);
            }
            return item;
        }
    }

}
