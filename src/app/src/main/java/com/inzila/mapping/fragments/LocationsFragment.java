package com.inzila.mapping.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.LocationServices;
import com.inzila.mapping.adapters.LocationAdapter;
import com.inzila.mapping.database.AppDatabase;
import com.inzila.mapping.database.SavedLocation;
import com.inzila.mapping.databinding.FragmentLocationsBinding;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationsFragment extends Fragment {

    private FragmentLocationsBinding binding;
    private LocationAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new LocationAdapter(this::deleteLocation, this::navigateToLocation);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        AppDatabase.getDatabase(requireContext()).locationDao().getAllLive()
                .observe(getViewLifecycleOwner(), locations -> {
                    adapter.setLocations(locations);
                    binding.emptyView.setVisibility(locations.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.recyclerView.setVisibility(locations.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }

    private void deleteLocation(SavedLocation location) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Location")
                .setMessage("Remove \"" + location.name + "\" and its geo-fence?")
                .setPositiveButton("Delete", (d, w) -> {
                    executor.execute(() -> {
                        AppDatabase.getDatabase(requireContext()).locationDao().delete(location);
                    });
                    removeGeofence(location);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeGeofence(SavedLocation location) {
        LocationServices.getGeofencingClient(requireContext())
                .removeGeofences(Collections.singletonList(location.geofenceId));
    }

    private void navigateToLocation(SavedLocation loc) {
        String uri = "https://www.google.com/maps/dir/?api=1&destination="
                + loc.latitude + "," + loc.longitude + "&travelmode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
        binding = null;
    }
}
