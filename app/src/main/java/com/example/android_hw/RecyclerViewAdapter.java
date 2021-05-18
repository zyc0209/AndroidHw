package com.example.android_hw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private OnItemClick onItemClick;
    private List<scandata> arrayList = new ArrayList<>();
    private Button btn_more;
    private Activity activity;

    public RecyclerViewAdapter(Activity activity) {
        this.activity = activity;
    }
    public void OnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }


    public void clearDevice(){
        this.arrayList.clear();
        notifyDataSetChanged();
    }

    public void addDevice(List<scandata> arrayList){
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvAddress,tvRssi;
        Button btn_more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_DeviceName);
            tvAddress = itemView.findViewById(R.id.textView_Address);
            tvRssi = itemView.findViewById(R.id.textView_Rssi);
            btn_more = itemView.findViewById(R.id.btn_more);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scandata,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(arrayList.get(position).getDeviceName());
        holder.tvAddress.setText("裝置位址："+arrayList.get(position).getAddress());
        holder.tvRssi.setText("訊號強度："+arrayList.get(position).getRssi());
        holder.btn_more.setOnClickListener(v -> {
            onItemClick.onItemClick(arrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    interface OnItemClick{
        void onItemClick(scandata selectedDevice);
    }


}