package com.manav.smsgrouping;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> list;
    private Context mContext;
    private String notif;

    public ListAdapter(List<Object> list, Context mContext,String notif) {
        this.list = list;
        this.mContext = mContext;
        this.notif=notif;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recview, parent, false);

            return new GeneralViewHolder(v);
        }
        if(viewType==2){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recview, parent, false);
            return new GeneralViewHolder(v);
        }
        else {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.date, parent, false);
            return new DateViewHolder(v2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object listItem = list.get(position);
       if(holder instanceof GeneralViewHolder){
           ((GeneralViewHolder) holder).setData((GeneralItem) listItem);
       }else{
           ((DateViewHolder) holder).setData((DateItem) listItem);

       }
    }

    @Override
    public int getItemCount() {
        return (list != null && list.size() > 0) ? list.size() : 0;
    }



    @Override
    public int getItemViewType(int position) {
        if(((Lister) list.get(position)).getType()==0 && position==1){
            return 2;
        }
        return ((Lister) list.get(position)).getType();
    }

    class GeneralViewHolder extends RecyclerView.ViewHolder {

        TextView ph_no;
        TextView msg;

        public GeneralViewHolder(View itemView) {
            super(itemView);
            ph_no = (TextView) itemView.findViewById(R.id.ph_no);
            msg = (TextView) itemView.findViewById(R.id.msg);
        }

        public void setData(GeneralItem listItem){
           ph_no.setText(listItem.getPojoOfJsonArray().getNumber());
           msg.setText(listItem.getPojoOfJsonArray().getMessage());
        }
    }

    // View holder for general row item
    class DateViewHolder extends RecyclerView.ViewHolder {

        TextView date;


        public DateViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
        }

        public void setData(DateItem data){
         date.setText(data.getDate());
        }
    }


}