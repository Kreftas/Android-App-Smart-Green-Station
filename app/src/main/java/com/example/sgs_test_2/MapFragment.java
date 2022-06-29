package com.example.sgs_test_2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.sgs_test_2.station.TextViewer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Objects;

import Utility.MockupFactory;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener {
    private GoogleMap mMap;
    private RealMainActivity activity;
    private HashMap<Integer, Marker> markerCollection;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private HashMap<Integer, String> map;
    private LatLng firstZoom;
    private final MockupFactory mockupFactory = new MockupFactory();
    private Marker myActiveMarker;
    private FusedLocationProviderClient provider;
    private boolean firstMapLoad = false;
    private final boolean QR_code = true;
    private boolean lock = false;
    private SharedPreferences sp;

    public MapFragment() {
        firstMapLoad = true;
    }

    public MapFragment(LatLng newZoom) {
        firstZoom = newZoom;
        lock = true;
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Put Google map in fragment
        View view = inflater.inflate(R.layout.fragment_map_test, container, false);
        //Create GoogleMap object
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // DO NOT DO ANYTHING HERE!!
                mMap.getUiSettings().setMapToolbarEnabled(false);
                if (myActiveMarker != null) {
                    myActiveMarker.hideInfoWindow();
                }

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        activity = (RealMainActivity) this.getActivity();

        provider = LocationServices.getFusedLocationProviderClient(activity);

        map = activity.getDatabaseObject().getStationDescription();//The station descriptions

        // When QR-scanner is finished and closed the data from the QR-code will be returned and handled here.
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        // fetches ID from QR-scanner
                        String id = result.getData().getStringExtra("id");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TextViewer(mockupFactory.getName(Integer.parseInt(id)), Integer.parseInt(id), QR_code), "TextViewer").addToBackStack("TextViewer").commit();
                    } else {
                        activity.getSupportFragmentManager().popBackStack("MapFragment", 0);
                    }
                });
        return view;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        styleMapJSON(mMap);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);

        // mMap.getUiSettings().setMapToolbarEnabled(true);

        // Enable CurrentLocation Button for highlighting the device location.

        // Just for safety precaution in case of emergency for the user
        mMap.setOnMyLocationButtonClickListener(() -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment(), "MapFragment").addToBackStack("MapFragment").commit();
            return false;
        });


//Loads Shared preferences


        //Setup a shared preference listener in case of them change
        SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, key) -> styleMapJSON(mMap);

        sp.registerOnSharedPreferenceChangeListener(listener);
        activity = (RealMainActivity) this.getActivity();

        ImageView button = requireView().findViewById(R.id.imageButton);
        button.setOnClickListener(view -> {
            //trigger for new Intent in order to start Camera.
            launchCameraClass();
            // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        });

        if (activity.checkPermission()) {
            mMap.setMyLocationEnabled(true);
        }
        TextView textViewer = getView().findViewById(R.id.progressBar2);

        textViewer.setOnClickListener(view -> {
            markerCollection = activity.getDatabaseObject().addMarkersToMap(mMap, this);
            if (!activity.isNetworkConnected()) {
                activity.showInternetError();
            }
        });

        if (!activity.isNetworkConnected() || activity.getDatabaseObject().getFetchCount() == 6) {
            textViewer.setVisibility(View.VISIBLE);
        }

        if (firstMapLoad) {
            this.getDeviceLocation();
            firstMapLoad = false;

            if (!activity.isNetworkConnected()) {
                activity.showInternetError();
            }
        } else if (firstZoom != null && lock) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstZoom, 12.0f));
            lock = false;
            if (!activity.isNetworkConnected()) {
                activity.showInternetError();
            }
        }

        markerCollection = activity.getDatabaseObject().addMarkersToMap(mMap, this);
    }

    private void styleMapJSON(GoogleMap googleMap) {
        int style = sp.getBoolean("nightDayMode", false) ? R.raw.style_night : R.raw.style_retro;
        if (getContext() == null) return;
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.getContext(), style));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        mMap.getUiSettings().setMapToolbarEnabled(true);
        marker.setSnippet(mockupFactory.getType((Integer) marker.getTag()));
        setMyLastShownInfoWindow(marker);
        return false;
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                new TextViewer(
                        map.get(marker.getTag()), (Integer) marker.getTag(), QR_code
                ), "TextViewer").addToBackStack("TextViewer").commit();
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
    }

    //Launches the QR-scanner, utilizing the units Camera.
    public void launchCameraClass() {
        if (activity.getDatabaseObject().databaseInitialized()) {
            Intent i = new Intent(activity, Camera.class);
            if (activity.getDatabaseObject().getStationIds() != null) {
                i.putExtra("stations", activity.getDatabaseObject().getStationIds());
            }
            someActivityResultLauncher.launch(i);
        }
    }

    private void setMyLastShownInfoWindow(Marker marker) {
        this.myActiveMarker = marker;
    }

    private void getDeviceLocation() {
        try {
            if (activity.checkPermission()) {
                Task<Location> locationResult = provider.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location location = task.getResult();
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng,
                                    12.0f);
                            mMap.moveCamera(update);
                        }
                    }
                });
            } else {
                LatLng currentLatLng = new LatLng(59, 14.3);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng,
                        5.9f);
                mMap.moveCamera(update);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }
}