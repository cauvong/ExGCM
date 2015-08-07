package com.example.vynv.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vynv.myapplication.R;
import com.example.vynv.myapplication.objects.ContactPhone;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by vynv on 8/7/15.
 */
public class ContactPhoneAdapter extends BaseAdapter {
    private Context mContext;
    private List<ContactPhone> mContactPhone;
    private LayoutInflater mInflater;

    public ContactPhoneAdapter(Context context, ArrayList<ContactPhone> contactPhone) {
        mContext = context;
        mContactPhone = contactPhone;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("xxx", "" + mContactPhone.size());
    }

    @Override
    public int getCount() {
        return mContactPhone.size();
    }

    @Override
    public Object getItem(int position) {
        return mContactPhone.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.contact_phone_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactPhone contactPhone= mContactPhone.get(position);
        holder.txtNamePeople.setText(contactPhone.getPhoneNamePeople());
        holder.txtNumberPhone.setText(contactPhone.getPhoneNumber());
        return convertView;
    }
    class ViewHolder {
        public TextView txtNamePeople;
        public TextView txtNumberPhone;
        ViewHolder(View convertView) {
            txtNamePeople = (TextView) convertView.findViewById(R.id.txtNamePeople);
            txtNumberPhone = (TextView) convertView.findViewById(R.id.txtNumberPhone);
        }
    }
}

