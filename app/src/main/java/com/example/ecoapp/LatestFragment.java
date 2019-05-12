package com.example.ecoapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


public class LatestFragment extends DialogFragment {

    private static final String ARG_DATE = "DATE";
    private static final String ARG_PARTNER = "PARTNER";
    private static final String ARG_VALUE = "VALUE";

    private String date;
    private String partner;
    private int value;

    private TextView dateView;
    private TextView valueView;
    private TextView partnerView;

    public LatestFragment() {
    }

    public static LatestFragment newInstance(String date, int value, String partner) {
        LatestFragment fragment = new LatestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_PARTNER, partner);
        args.putInt(ARG_VALUE, value);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString(ARG_DATE);
            partner = getArguments().getString(ARG_PARTNER);
            value = getArguments().getInt(ARG_VALUE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_latest, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        this.dateView = view.findViewById(R.id.dateView);
        this.valueView = view.findViewById(R.id.valueView);
        this.partnerView = view.findViewById(R.id.partnerView);
        this.dateView.setText(getString(R.string.date_transaction) +" " + this.date);
        this.valueView.setText(getString(R.string.value_transaction) +" " + this.value);
        this.partnerView.setText(getString(R.string.partner_transaction)+" " +this.partner);
    }


}
