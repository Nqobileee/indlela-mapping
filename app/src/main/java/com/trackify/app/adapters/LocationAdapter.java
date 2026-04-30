package com.trackify.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trackify.app.database.SavedLocation;
import com.trackify.app.databinding.ItemLocationBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    public interface OnDeleteListener {
        void onDelete(SavedLocation location);
    }

    public interface OnNavigateListener {
        void onNavigate(SavedLocation location);
    }

    private List<SavedLocation> locations = new ArrayList<>();
    private final OnDeleteListener deleteListener;
    private final OnNavigateListener navigateListener;

    public LocationAdapter(OnDeleteListener deleteListener, OnNavigateListener navigateListener) {
        this.deleteListener = deleteListener;
        this.navigateListener = navigateListener;
    }

    public void setLocations(List<SavedLocation> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLocationBinding binding = ItemLocationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedLocation loc = locations.get(position);
        holder.binding.tvName.setText(loc.name);
        holder.binding.tvCoords.setText(String.format(Locale.US, "%.5f, %.5f", loc.latitude, loc.longitude));
        holder.binding.tvRadius.setText(String.format(Locale.US, "Geo-fence radius: %.0f m", loc.radius));
        holder.binding.btnDelete.setOnClickListener(v -> deleteListener.onDelete(loc));
        holder.binding.btnNavigate.setOnClickListener(v -> navigateListener.onNavigate(loc));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemLocationBinding binding;

        ViewHolder(ItemLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
