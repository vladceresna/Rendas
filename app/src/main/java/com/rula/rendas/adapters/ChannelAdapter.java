package com.rula.rendas.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rula.etime.EasyTime;
import com.rula.etime.TimeData;
import com.rula.rendas.R;
import com.rula.rendas.obj.Channel;
import com.rula.rendas.obj.Chat;
import com.rula.rendas.obj.Message;

import java.util.List;

public class ChannelAdapter extends BaseAdapter {
    private List<Channel> channels;
    private Context context;
    private String myid;
    String TAG = "Tag";

    public ChannelAdapter(Context context, List<Channel> channels, String myid) {
        this.myid = myid;
        this.channels = channels;
        this.context = context;
        Log.d("Tag", "StartAdapt");
    }
    @Override
    public int getCount() {
        Log.d(TAG, "getCount: ");
        return channels.size();
    }

    @Override
    public Object getItem(int i) {
        Log.d(TAG, "getItem: ");
        return channels.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.d(TAG, "getItemId: ");
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            Log.d(TAG, "getView: ");
            view = LayoutInflater.from(context).
                    inflate(R.layout.chat_layout, viewGroup, false);
        }
        Log.d("Tag", "Adapt");
        Channel channel = (Channel) getItem(i);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView lmess = (TextView) view.findViewById(R.id.lmess);
        TextView time = (TextView) view.findViewById(R.id.time);

        name.setText(channel.getName());

        if (channel.getMessages() != null) {
            Message lastmess = new Message("", myid, "0");
            for (Message message : channel.getMessages().values()) {
                if (Long.parseLong(message.getTime()) > Long.parseLong(lastmess.getTime())) {
                    lastmess = message;
                }
            }
            lmess.setText(lastmess.getMessage());

            TimeData td = new EasyTime().getConvTime(Long.parseLong(lastmess.getTime()));
            time.setText(td.M() +"."+ td.D() +" "+ td.H() +":"+ td.m());

        }else{
            time.setText("");
            lmess.setText("");
        }



        return view;
    }
}
