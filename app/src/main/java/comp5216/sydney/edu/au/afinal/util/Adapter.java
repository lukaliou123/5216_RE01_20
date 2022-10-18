package comp5216.sydney.edu.au.afinal.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.R;
import comp5216.sydney.edu.au.afinal.ui.search.SearchFragment;

public class Adapter extends BaseAdapter {
    private final ArrayList<EventEntity> events;
    private final Context mContext;


    public Adapter(Context mContext, ArrayList<EventEntity> events){
        this.mContext = mContext;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public EventEntity getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.events_list, viewGroup, false);
            holder = new ViewHolder();
            holder.title = view.findViewById(R.id.title);
            holder.author = view.findViewById(R.id.author);
            holder.time = view.findViewById(R.id.time);
            holder.address = view.findViewById(R.id.address);
            holder.like = view.findViewById(R.id.like);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(events.get(i).getTitle());
        holder.author.setText(events.get(i).getBlogger());
        holder.time.setText(events.get(i).getTimeStamp().toString());
        holder.address.setText(events.get(i).getLocation());
        holder.like.setText(events.get(i).getLikes());

        return view;
    }

    static class ViewHolder{
        TextView title;
        TextView author;
        TextView time;
        TextView address;
        TextView like;
    }
}
