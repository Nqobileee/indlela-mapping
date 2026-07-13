package com.trackify.app.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.trackify.app.R;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trackify.app.database.AppDatabase;
import com.trackify.app.database.SavedLocation;
import com.trackify.app.databinding.FragmentAddLocationBinding;
import com.trackify.app.receivers.GeofenceBroadcastReceiver;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddLocationFragment extends Fragment implements OnMapReadyCallback {

    private FragmentAddLocationBinding binding;
    private GeofencingClient geofencingClient;
    private GoogleMap pickerMap;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        geofencingClient = LocationServices.getGeofencingClient(requireActivity());

        // Set up the tap-to-pin map
        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_picker_container);
        if (mapFrag == null) {
            mapFrag = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.map_picker_container, mapFrag)
                    .commit();
        }
        mapFrag.getMapAsync(this);

        binding.sliderRadius.addOnChangeListener((slider, value, fromUser) ->
                binding.tvRadiusValue.setText(String.format(Locale.US, "%.0f m", value)));

        binding.btnUseCurrentLocation.setOnClickListener(v -> pinCurrentLocation());
        binding.btnSave.setOnClickListener(v -> saveLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        pickerMap = map;
        pickerMap.getUiSettings().setZoomControlsEnabled(true);
        pickerMap.getUiSettings().setCompassEnabled(true);

        // Centre on the device's last known position so the user doesn't start at 0,0
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            pickerMap.setMyLocationEnabled(true);

            LocationServices.getFusedLocationProviderClient(requireActivity())
                    .getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null && pickerMap != null) {
                            pickerMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                        }
                    });
        }

        // Tap on map → drop marker and fill lat/lng fields
        pickerMap.setOnMapClickListener(latLng -> {
            pickerMap.clear();
            pickerMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Pinned location"));
            binding.etLatitude.setText(String.format(Locale.US, "%.6f", latLng.latitude));
            binding.etLongitude.setText(String.format(Locale.US, "%.6f", latLng.longitude));
        });
    }

    private void pinCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.getFusedLocationProviderClient(requireActivity())
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        binding.etLatitude.setText(String.format(Locale.US, "%.6f", location.getLatitude()));
                        binding.etLongitude.setText(String.format(Locale.US, "%.6f", location.getLongitude()));
                        if (pickerMap != null) {
                            pickerMap.clear();
                            pickerMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                            pickerMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                        }
                    } else {
                        Toast.makeText(requireContext(), "GPS signal not ready yet", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLocation() {
        String name    = binding.etName.getText()      != null ? binding.etName.getText().toString().trim()      : "";
        String latStr  = binding.etLatitude.getText()  != null ? binding.etLatitude.getText().toString().trim()  : "";
        String lngStr  = binding.etLongitude.getText() != null ? binding.etLongitude.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Enter a name for this location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latStr.isEmpty() || lngStr.isEmpty()) {
            Toast.makeText(requireContext(), "Tap the map or use GPS to set coordinates", Toast.LENGTH_SHORT).show();
            return;
        }

        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid coordinates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            Toast.makeText(requireContext(), "Coordinates out of valid range", Toast.LENGTH_SHORT).show();
            return;
        }

        float radius = binding.sliderRadius.getValue();

        SavedLocation location = new SavedLocation();
        location.name       = name;
        location.latitude   = lat;
        location.longitude  = lng;
        location.radius     = radius;
        location.geofenceId = name;

        binding.btnSave.setEnabled(false);

        executor.execute(() -> {
            long id = AppDatabase.getDatabase(requireContext()).locationDao().insert(location);
            location.id = (int) id;

            requireActivity().runOnUiThread(() -> {
                addGeofence(location);
                binding.btnSave.setEnabled(true);
                Toast.makeText(requireContext(), "\"" + name + "\" saved!", Toast.LENGTH_SHORT).show();
                clearForm();
            });
        });
    }

    private void addGeofence(SavedLocation location) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(),
                    "Location saved, but geo-fence requires location permission", Toast.LENGTH_LONG).show();
            return;
        }

        Geofence geofence = new Geofence.Builder()
                .setRequestId(location.geofenceId)
                .setCircularRegion(location.latitude, location.longitude, location.radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        geofencingClient.addGeofences(request, getGeofencePendingIntent())
                .addOnSuccessListener(v ->
                        Toast.makeText(requireContext(), "Geo-fence active!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Geo-fence error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(requireContext(), GeofenceBroadcastReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        return PendingIntent.getBroadcast(requireContext(), 0, intent, flags);
    }

    private void clearForm() {
        binding.etName.setText("");
        binding.etLatitude.setText("");
        binding.etLongitude.setText("");
        binding.sliderRadius.setValue(200f);
        if (pickerMap != null) pickerMap.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
        binding = null;
    }
}
