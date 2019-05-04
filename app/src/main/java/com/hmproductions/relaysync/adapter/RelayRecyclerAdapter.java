package com.hmproductions.relaysync.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hmproductions.relaysync.R;
import com.hmproductions.relaysync.data.Relay;

import java.text.DecimalFormat;
import java.util.List;

public class RelayRecyclerAdapter extends RecyclerView.Adapter<RelayRecyclerAdapter.RelayViewHolder> {

    private Context context;
    private List<Relay> relayList;

    public RelayRecyclerAdapter(Context context, List<Relay> relayList) {
        this.context = context;
        this.relayList = relayList;
        
        relayList.add(0, new Relay(-1.0, -1.0, -1.0, -1));
    }

    @NonNull
    @Override
    public RelayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RelayViewHolder(LayoutInflater.from(context).inflate(R.layout.relay_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RelayViewHolder holder, int position) {
        Relay currentRelay = relayList.get(position);
        DecimalFormat formatter = new DecimalFormat("#.###");
        
        if(currentRelay.getCt() < 0) {
            holder.ctRatioTextView.setText(context.getString(R.string.ct_ratio));
            holder.tmsTextView.setText(context.getString(R.string.tms));
            holder.psmTextView.setText(context.getString(R.string.psm));
            holder.topTextView.setText(context.getString(R.string.top));

            holder.topTextView.setTypeface(null, Typeface.BOLD);
            holder.psmTextView.setTypeface(null, Typeface.BOLD);
            holder.tmsTextView.setTypeface(null, Typeface.BOLD);
            holder.ctRatioTextView.setTypeface(null, Typeface.BOLD);
            return;
        }

        String temp = currentRelay.getCt() + ":1";
        holder.ctRatioTextView.setText(temp);

        holder.tmsTextView.setText(formatter.format(currentRelay.getTms()));
        holder.psmTextView.setText(formatter.format(currentRelay.getPsm()));

        temp = formatter.format(currentRelay.getTop()) + " s";
        holder.topTextView.setText(temp);
    }

    @Override
    public int getItemCount() {
        if(relayList == null) return 0;
        return relayList.size();
    }

    class RelayViewHolder extends RecyclerView.ViewHolder {

        TextView tmsTextView, psmTextView, topTextView, ctRatioTextView;

        RelayViewHolder(@NonNull View itemView) {
            super(itemView);

            tmsTextView = itemView.findViewById(R.id.tmsTextView);
            psmTextView = itemView.findViewById(R.id.psmTextView);
            topTextView = itemView.findViewById(R.id.topTextView);
            ctRatioTextView = itemView.findViewById(R.id.ctRatioTextView);
        }
    }
}
