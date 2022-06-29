package com.example.sgs_test_2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class AboutSGSFragment extends Fragment {

    TextView email_button, phone_button, address_button;

    public AboutSGSFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_sgs, container, false);
        email_button = v.findViewById(R.id.email_button);
        phone_button = v.findViewById(R.id.phone_button);
        address_button = v.findViewById(R.id.address_button);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack("MapFragment", 0);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        email_button.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO).setData(Uri.parse("mailto:info@smartgreenstation.se"));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "There is no client installed.", Toast.LENGTH_SHORT).show();
            }
        });

        phone_button.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+46722569321"));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "There is no client installed.", Toast.LENGTH_SHORT).show();
            }

        });

        address_button.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/R%C3%B6nnblomsgatan+1,+212+16+Malm%C3%B6/@55.6017364,13.0295543,17z/data=!3m1!4b1!4m5!3m4!1s0x4653a3d8da697577:0xa83f95f4b10b455b!8m2!3d55.6017334!4d13.031743"));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "There is no client installed.", Toast.LENGTH_SHORT).show();
            }

        });


        return v;
    }

}
