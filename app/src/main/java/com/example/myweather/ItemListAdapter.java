package com.example.myweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder>{

    private final Activity mParentActivity;
    private final ArrayList<HashMap<String, String>> mValues;
    private final boolean mTwoPane;

    ItemListAdapter(Activity parent, ArrayList<HashMap<String, String>> items, boolean twoPane)
    {
        mValues=items;
        mParentActivity=parent;
        mTwoPane=twoPane;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_list_content, parent, false);

        final ViewHolder retViewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener(){
                @Override
            public void onClick(View view)
        {
            int position = retViewHolder.getAdapterPosition();
            if(mTwoPane){
                Bundle arguments = new Bundle();
                arguments.putString("date", mValues.get(position).get("date"));
                arguments.putString("city", mValues.get(position).get("city"));
                WeatherDetailFragment fragment = new WeatherDetailFragment();
                fragment.setArguments(arguments);
                ((AppCompatActivity) mParentActivity).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, fragment)
                        .commit();
            }
            else{
                Context context = view.getContext();
                Intent intent = new Intent(context, WeatherDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("city",mValues.get(position).get("city") );
                extras.putString("date", mValues.get(position).get("date"));
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        }
        });
        return retViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        HashMap<String, String> song = new HashMap<>();
        song=mValues.get(position);
        holder.date.setId(position);
        holder.temp.setId(position);
        holder.icon.setId(position);

        holder.date.setText(song.get("date"));
        holder.temp.setText(song.get("temp"));
        String iconUrl = "http://openweathermap.org/img/w/" + song.get("icon") + ".png";
        Picasso.with(mParentActivity).load(iconUrl).into(holder.icon);

    }

    @Override
    public int getItemCount(){return mValues.size();}

    public void clear() {
        int size = mValues.size();
        mValues.clear();
        notifyItemRangeRemoved(0, size);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final TextView temp;
        final ImageView icon;

        ViewHolder(View view)
        {
            super(view);
            date = view.findViewById(R.id.textView8);
            temp = view.findViewById(R.id.textView10);
            icon = view.findViewById(R.id.imageView);
        }
    }
}
