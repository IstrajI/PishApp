package com.example.uladzislau_nikitsin.pishapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder>{

    private List<DeviceModel> devices;
    private DevicesClickLister devicesClickLister;

    DevicesAdapter() {
        this.devices = new ArrayList<>();
    }

    @Override
    public DevicesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.descriptionTextView.setText(devices.get(position).getName());
        holder.ipTextView.setText(devices.get(position).getIp());
        holder.portTextView.setText(devices.get(position).getPort());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void update(final List<DeviceModel> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
        notifyDataSetChanged();
    }

    public void add(final DeviceModel device) {
        Log.d("pishpish", "yes");
        this.devices.add(device);
        for (DeviceModel deviceModel: devices) {
            Log.d("device", ""+device.getName());
        }
        notifyDataSetChanged();
    }

    public void remove(final DeviceModel device) {
        devices.remove(device);
        notifyDataSetChanged();
    }

    public DeviceModel getItemByPosition(final int position) {
        return devices.get(position);
    }

    public void setDevicesClickLister(DevicesClickLister devicesClickLister) {
        this.devicesClickLister = devicesClickLister;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView descriptionTextView;
        public TextView ipTextView;
        public TextView portTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.text_view_device_item_description);
            ipTextView = (TextView) itemView.findViewById(R.id.text_view_device_item_ip);
            portTextView = (TextView) itemView.findViewById(R.id.text_view_device_item_port);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(final View v) {
            devicesClickLister.onItemClick(v, getAdapterPosition(), DevicesAdapter.this);
        }
    }

    interface DevicesClickLister {
        void onItemClick(final View view, final int position, final RecyclerView.Adapter adapter);
    }
}
