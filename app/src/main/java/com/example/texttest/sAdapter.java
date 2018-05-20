package com.example.texttest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class sAdapter extends BaseAdapter {
    private Context context;
    private List<BookInfo> list;
    private LayoutInflater inflater;
    public sAdapter(Context context, List<BookInfo> list){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyHolder holder = null;
        BookInfo book = list.get(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_item,null);
            holder = new MyHolder();
            holder.ssize = convertView.findViewById(R.id.size);
            holder.sname = convertView.findViewById(R.id.name);
            holder.spath = convertView.findViewById(R.id.path);
            holder.button = convertView.findViewById(R.id.add_book);
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }
        String size;
        if (book.getProgress() > 1024){
            size = book.getProgress() / 1024 + "";
            size = size.substring(0,size.lastIndexOf(".")+2) + "M";
        } else {
            size = book.getProgress() + "";
            size = size.substring(0,size.lastIndexOf(".")+2) + "K";
        }

        holder.ssize.setText(size);
        holder.sname.setText("\uD83D\uDCD6\t\t" + book.getName());
        holder.spath.setText(book.getPath());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDb(position);
            }
        });
        return convertView;
    }

    private void addDb(int position) {
        File f=new File(list.get(position).getPath());
        if (f.exists()){
            DbAction dbAction = new DbAction(context);
            dbAction.insert(list.get(position).getName(), list.get(position).getPath(),0);
            Toast.makeText(context,"加入成功",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"文件不存在",Toast.LENGTH_SHORT).show();
        }

    }
    public void refresh(List<BookInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class MyHolder{
        TextView sname,spath,ssize;
        Button button;
    }
}
