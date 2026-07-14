package com.inzila.mapping.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inzila.mapping.R;
import com.inzila.mapping.database.AppDatabase;
import com.inzila.mapping.database.SavedLocation;
import com.inzila.mapping.databinding.FragmentMapBinding;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LocationCallback locationCallback;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFrag == null) {
            mapFrag = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.map_container, mapFrag)
                    .commit();
        }
        mapFrag.getMapAsync(this);

        binding.btnMyLocation.setOnClickListener(v -> moveToCurrentLocation());
        binding.btnDirections.setOnClickListener(v -> getDirections());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        startLocationUpdates();
        loadMarkersOnMap();

        // Tapping a saved-location pin fills the "To" field
        googleMap.setOnMarkerClickListener(marker -> {
            SavedLocation loc = (SavedLocation) marker.getTag();
            if (loc != null) {
                String coords = loc.latitude + "," + loc.longitude;
                binding.etTo.setText(coords);
                Toast.makeText(requireContext(),
                        "\"" + loc.name + "\" set as destination", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                currentLocation = result.getLastLocation();
            }
        };

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private void moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Location permission needed", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
            } else {
                Toast.makeText(requireContext(), "Acquiring GPS signal…", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDirections() {
        String from = binding.etFrom.getText() != null
                ? binding.etFrom.getText().toString().trim() : "";
        String to = binding.etTo.getText() != null
                ? binding.etTo.getText().toString().trim() : "";

        if (to.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Enter a destination or tap a saved pin on the map", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder uri = new StringBuilder("https://www.google.com/maps/dir/?api=1");

        if (!from.isEmpty()) {
            // User typed an origin address
            uri.append("&origin=").append(Uri.encode(from));
        } else if (currentLocation != null) {
            // Use live GPS coordinates as origin
            uri.append("&origin=")
               .append(String.format(Locale.US, "%.6f,%.6f",
                       currentLocation.getLatitude(), currentLocation.getLongitude()));
        }
        // If no origin at all, Google Maps defaults to the device's current location

        uri.append("&destination=").append(Uri.encode(to));
        uri.append("&travelmode=driving");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Fallback: open in browser
            intent.setPackage(null);
            startActivity(intent);
        }

        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focused = requireActivity().getCurrentFocus();
        if (imm != null && focused != null) {
            imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
        }
    }

    public void loadMarkersOnMap() {
        if (googleMap == null) return;
        executor.execute(() -> {
            List<SavedLocation> locs = AppDatabase.getDatabase(requireContext())
                    .locationDao().getAll();
            requireActivity().runOnUiThread(() -> {
                if (googleMap == null) return;
                googleMap.clear();
                for (SavedLocation loc : locs) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(loc.latitude, loc.longitude))
                            .title(loc.name)
                            .snippet(String.format(Locale.US,
                                    "%.0f m geo-fence  •  tap to set as destination", loc.radius)));
                    if (marker != null) marker.setTag(loc);
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMarkersOnMap();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        executor.shutdown();
        binding = null;
    }
}
