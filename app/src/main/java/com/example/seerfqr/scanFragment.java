package com.example.seerfqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class scanFragment extends Fragment {
    public static final int CAMERA = 101;
    private CodeScanner mCodeScanner;
    private boolean isZoonEnabled;
    Double latitude, longitude;
    LocationManager locationManager;
    LocationListener locationListener;

    private static final int REQUEST_LOCATION = 1;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("zoom_enabled", isZoonEnabled);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            isZoonEnabled = savedInstanceState.getBoolean("zoom_enabled", false);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        checkPermission();

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        ActivityCompat.requestPermissions(activity,new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        },REQUEST_LOCATION );


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tasksRef = rootRef.child("Key").push();
        DatabaseReference outRangeRef = rootRef.child("outRange").push();
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {


                activity.runOnUiThread(new Runnable() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void run() {
                        try {
                            String resultText = result.getText();

//
                            String[] modifiedText = resultText.split("=")[1].split(",");


                            Location startPoint = new Location("locationA");
                            startPoint.setLatitude(Double.parseDouble(modifiedText[0]));
                            startPoint.setLongitude(Double.parseDouble(modifiedText[1]));

                            Location endPoint = new Location("locationA");
                            endPoint.setLatitude(latitude);
                            endPoint.setLongitude(longitude);

                            double distance = startPoint.distanceTo(endPoint);

                            Log.d("distance", String.valueOf(latitude));

                            if (distance <= 30.0) {
                                //withing a 10 meter


                                HashMap<String, String> scanCode = new HashMap<>();

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                String currentDate = dateFormat.format(date);
                                scanCode.put("history_item_type", "Pankaj");
                                scanCode.put("history_item_summary", result.getText());
                                scanCode.put("history_item_date", currentDate);


                                AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                                builder.setMessage("Verified")
                                        .setTitle("Verified")
                                        .setIcon(getResources().getDrawable(R.drawable.baseline_check_24))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                Toast.makeText(activity, "Ok", Toast.LENGTH_SHORT).show();

                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                                tasksRef.setValue(scanCode);
                            } else {
                                //out side 10 meter
                                HashMap<String, String> scanCode = new HashMap<>();

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                String currentDate = dateFormat.format(date);
                                scanCode.put("history_item_type", "Pankaj");
                                scanCode.put("history_item_summary", result.getText());
                                scanCode.put("history_item_date", currentDate);
                                outRangeRef.setValue(scanCode);

                                AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                                builder.setMessage("Not Verified")
                                        .setTitle("Not Verified")
                                        .setIcon(getResources().getDrawable(R.drawable.baseline_close_24))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                Toast.makeText(activity, "Ok", Toast.LENGTH_SHORT).show();

                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        } catch (Exception e) {
                            Log.e("Error", "run:smit", e);
                        }


                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCodeScanner.startPreview();

            }
        });


        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

        return root;

    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {
                case CAMERA:
                    checkPermission();
                    break;

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.releaseResources();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        mCodeScanner.stopPreview();
        super.onPause();
    }

}
