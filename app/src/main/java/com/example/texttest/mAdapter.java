package com.example.texttest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class mAdapter extends RecyclerView.Adapter<mAdapter.MyHolder>{

    DisplayMetrics dm;
    int width;

    private List<BookInfo> list = null;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private OnItemLongClickListener Longlistener;
    private int length;
    Context context;

    public mAdapter(Context context, List<BookInfo> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        length = list.size();
        list.add(new BookInfo(length,null, null, 0));
        dm =context.getResources().getDisplayMetrics();
        width = dm.widthPixels;
    }

    public interface OnItemLongClickListener{
        void onItemClick(View itemView, int position);
    }

    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setOnItemLongClicklistener (OnItemLongClickListener listener){
        this.Longlistener = listener;
    }

    public void refresh(List<BookInfo> list) {
        this.list = list;
        length = list.size();
        list.add(new BookInfo(length,null, null, 0));
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item,parent,false);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = width/20*7;
        view.setLayoutParams(lp);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        BookInfo bookInfo = list.get(position);
        if (position == length){
            holder.book_name.setText("");
            holder.book_progress.setText("");
//            holder.background.setBackground(context.getDrawable(R.drawable.dialog_conrer));
            holder.type.setBackground(context.getDrawable(R.drawable.ic_add_black_24dp));
        } else {
            holder.type.setBackground(null);
//            holder.background.setBackground(context.getDrawable(R.drawable.content_default));
            holder.book_name.setText(bookInfo.getName());
            holder.book_progress.setText(String.valueOf((int)bookInfo.getProgress()) + "%");
        }
        //设置item点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Longlistener.onItemClick(v,position);
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView book_name,book_progress;
        RelativeLayout background;
        ImageView type;
        public MyHolder(View itemView) {
            super(itemView);
            book_name = itemView.findViewById(R.id.book_name);
            book_progress = itemView.findViewById(R.id.book_progress);
            background = itemView.findViewById(R.id.item_change);
            type = itemView.findViewById(R.id.book_type);
        }
    }
}
