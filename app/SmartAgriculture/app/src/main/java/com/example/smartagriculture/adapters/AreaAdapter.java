package com.example.smartagriculture.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartagriculture.R;
import com.example.smartagriculture.models.AreaModel;

import java.util.List;

public class AreaAdapter extends BaseAdapter {

    List<AreaModel> areas;

    public AreaAdapter(List<AreaModel> areas) {
        this.areas = areas;
    }

    @Override
    public int getCount() {
        return areas.size();
    }

    @Override
    public Object getItem(int position) {
        return areas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_area_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.imgView);
            viewHolder.textName = convertView.findViewById(R.id.text_area);
            viewHolder.textDeviceType = convertView.findViewById(R.id.text_devicetype);
            viewHolder.textDeviceId = convertView.findViewById(R.id.text_deviceid);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        AreaModel area = areas.get(position);
        viewHolder.textDeviceType.setText(area.getDeviceType());
        viewHolder.imageView.setImageResource(R.drawable.ic_area);
        viewHolder.textName.setText(area.getName());
        viewHolder.textDeviceId.setText(area.getDeviceId());

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textDeviceType;
        TextView textDeviceId;
        TextView textName;
    }
}
