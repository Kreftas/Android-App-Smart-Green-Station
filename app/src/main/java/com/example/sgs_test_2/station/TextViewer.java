package com.example.sgs_test_2.station;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sgs_test_2.MapFragment;
import com.example.sgs_test_2.R;
import com.example.sgs_test_2.RealMainActivity;
import com.example.sgs_test_2.database.Database;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Utility.MockupFactory;

public class TextViewer extends Fragment {
    private View view;
    private TextView runningText, titleText;
    private final Handler mHandler = new Handler();
    private String station;
    private int id;
    public boolean QR_code = false;
    private ArrayList<String> latlng;
    private RealMainActivity activity;

    public TextViewer() {
    }

    public TextViewer(String station, int id, boolean QR_code) {
        this.station = station;
        this.id = id;
        this.QR_code = QR_code;
    }

    public TextViewer(String station, int id) {
        this.station = station;
        this.id = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_text_view, container, false);
        activity = (RealMainActivity) getActivity();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (QR_code) {
                    requireActivity().getSupportFragmentManager().popBackStack("MapFragment", 0);
                } else {
                    requireActivity().getSupportFragmentManager().popBackStack("StationsFragment", 0);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        TextView clickForMoreInfoButton = view.findViewById(R.id.click_for_more_info);

        if (!activity.isNetworkConnected()) {
            activity.showInternetError();
            clickForMoreInfoButton.setText(R.string.click_to_launch_settings_application);
        }

        initDataList(view);

        runningText = view.findViewById(R.id.runningText);
        titleText = view.findViewById(R.id.titleText);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.post(mUpdateUITimerTask);
    }

    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
            switch (id) {
                case 1041270:
                    titleText.setText(station);
                    runningText.setText(text);
                case 1041625:
                    titleText.setText(station);
                    runningText.setText(text);
                case 1046533:
                    titleText.setText(station);
                    runningText.setText(text);
                case 1206486:
                    titleText.setText(station);
                    runningText.setText(text);
                default:
                    titleText.setText(station);
                    runningText.setText(text);
            }

        }
    };

    private void initDataList(View v) {
        ArrayList<String> dataList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        latlng = new ArrayList<>();

        Database db = activity.getDatabaseObject();

        NonScrollableListView dataListView = v.findViewById(R.id.slider_list_view);
        DataListAdapter arrayAdapter = new DataListAdapter(getContext(), dataList, valueList);
        dataListView.setAdapter(arrayAdapter);


        LinearLayout button = view.findViewById(R.id.button_pushme);

        if (!activity.isNetworkConnected()) {
            button.setVisibility(View.INVISIBLE);
            dataList.add("");
            valueList.add("");
        } else {
            db.populateDataList(id, dataList, valueList, arrayAdapter, latlng);
        }

        button.setOnClickListener(view -> {
            if (activity.isNetworkConnected()) {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                new MapFragment(new LatLng(Double.parseDouble(latlng.get(0)), Double.parseDouble(latlng.get(1)))), "MapFragment"
                        )
                        .addToBackStack("MapFragment")
                        .commit();
            }
        });
    }

    public class DataListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final List<String> dataFields;
        private final List<String> values;

        public DataListAdapter(Context context, List<String> dataFields, List<String> values) {
            super(context, R.layout.station_list_item, R.id.main_text_view, dataFields);
            this.context = context;
            this.dataFields = dataFields;
            this.values = values;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View item = layoutInflater.inflate(R.layout.data_list_item, parent, false);
            TextView stationName = item.findViewById(R.id.main_text_view);
            ImageView image = item.findViewById(R.id.image);
            LinearLayout button = item.findViewById(R.id.button_pushme);

            if (!activity.getDatabaseObject().databaseInitialized() || !activity.isNetworkConnected()) {
                stationName.setText(R.string.No_Connection);
                image.setImageResource(R.drawable.sad_smiley);
                button.setOnClickListener(v -> activity.getActivityLauncher().launch(new Intent(android.provider.Settings.ACTION_SETTINGS)));
            } else {
                String text = MockupFactory.getDataDescription(dataFields.get(position));
                String s = values.get(position);
                DecimalFormat decimalFormat = new DecimalFormat("0.#####");
                String result = decimalFormat.format(Double.valueOf(s));
                text = text + "\n" + result + " " + MockupFactory.getUnit(dataFields.get(position));
                stationName.setText(text);
                image.setImageResource(getImage(dataFields.get(position), Double.parseDouble(values.get(position))));
                button.setOnClickListener(view -> requireActivity().getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container, new CustomChartViewer(id, String.valueOf(position), dataFields.get(position), station),
                        "CustomChartViewer").addToBackStack("CustomChartViewer").commit());
            }

            return item;
        }

        private int getImage(String unit, Double value) {
            switch (unit) {
                case "PM2.5":
                    if (value == 12) {
                        return R.drawable.dissatisfied_smiley;
                    } else if (value < 12) {
                        return R.drawable.happy_smiley;
                    } else {
                        return R.drawable.sad_smiley;
                    }
                case "PM10":
                    if (value == 150) {
                        return R.drawable.dissatisfied_smiley;
                    } else if (value < 150) {
                        return R.drawable.happy_smiley;
                    } else {
                        return R.drawable.sad_smiley;
                    }
                case "Light":
                    if (value >= 100 && value <= 400) {
                        return R.drawable.dissatisfied_smiley;
                    } else if (value > 400) {
                        return R.drawable.happy_smiley;
                    } else {
                        return R.drawable.sad_smiley;
                    }
                case "Temperature":
                    if (value >= 0 && value <= 15) {
                        return R.drawable.dissatisfied_smiley;
                    } else if (value > 15) {
                        return R.drawable.happy_smiley;
                    } else {
                        return R.drawable.sad_smiley;
                    }
                default:
                    return R.drawable.dissatisfied_smiley;
            }
        }
    }

}