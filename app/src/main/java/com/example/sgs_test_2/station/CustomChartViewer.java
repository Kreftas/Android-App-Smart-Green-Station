package com.example.sgs_test_2.station;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Cartesian;
import com.anychart.core.Chart;
import com.anychart.enums.HoverMode;
import com.anychart.enums.TooltipPositionMode;
import com.example.sgs_test_2.R;
import com.example.sgs_test_2.RealMainActivity;
import com.example.sgs_test_2.database.Database;

import Utility.MockupFactory;


public class CustomChartViewer extends Fragment {

    private String dataField;
    private String dataType;
    private String station;
    private int id;
    private TextView chartTitle, chartDescription;

    /**
     * Default constructor
     */
    public CustomChartViewer() {
    }

    public CustomChartViewer(int id, String dataField, String dataType, String station) {
        this.dataField = dataField;
        this.id = id;
        this.dataType = dataType;
        this.station = station;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chart_view, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack("TextViewer", 0);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        chartTitle = view.findViewById(R.id.chart_title);
        chartDescription = view.findViewById(R.id.char_description);

        AnyChartView anyChartView = view.findViewById(R.id.chart_view);
        anyChartView.setChart(createChart());


        return view;
    }

    private Chart createChart() {
        Cartesian cartesian = AnyChart.column();

        fetchData(cartesian);
        cartesian.animation(true);
        cartesian.title().padding(10d, 0d, 0d, 45d);

        cartesian.yScale().alignMaximum();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        cartesian.background().fill(sp.getBoolean("nightDayMode", false) ? "#1c1b22" : "#ffffff");

        chartTitle.setText(MockupFactory.getDataDescription(dataType));
        chartDescription.setText(getText(dataType));


        cartesian.padding(10d, 30d, 20d, 10d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.yAxis(0).title(MockupFactory.getUnit(dataType));

        cartesian.legend().align();


        cartesian.title().fontSize(20);
        cartesian.xAxis(0).title().fontSize(17);
        cartesian.yAxis(0).title().fontSize(17);
        cartesian.xAxis(0).labels().fontSize(17);
        cartesian.yAxis(0).labels().fontSize(17);

        cartesian.title().fontColor("#1693c5");
        cartesian.xAxis(0).title().fontColor("#1693c5");
        cartesian.yAxis(0).title().fontColor("#1693c5");
        cartesian.xAxis(0).labels().fontColor("#1693c5");
        cartesian.yAxis(0).labels().fontColor("#1693c5");

        return cartesian;
    }

    private String getText(String dataType) {
        switch (dataType) {
            case "PM2.5":
                return getString(R.string.pm2_5_text) + " " + station + ".";
            case "PM10":
                return getString(R.string.pm10_text) + " " + station + ".";
            case "Light":
                return getString(R.string.light_text) + " " + station + ".";
            case "Temperature":
                return getString(R.string.temp_text) + " " + station + ".";
            default:
                return "No data";
        }
    }

    private void fetchData(Cartesian cartesian) {
        RealMainActivity mainActivity = (RealMainActivity) requireActivity();
        Database db = mainActivity.getDatabaseObject();
        db.populateChart(id, dataField, cartesian);
    }

}