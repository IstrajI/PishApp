package com.example.uladzislau_nikitsin.pishapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder>{

    private List<DeviceModel> devices;

    @Override
    public DevicesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.descriptionTextView.setText(devices.get(position).);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descriptionTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.text_view_device_item_description);
        }
    }
}
