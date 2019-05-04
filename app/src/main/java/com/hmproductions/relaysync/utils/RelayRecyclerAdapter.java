package com.hmproductions.relaysync.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.hmproductions.relaysync.R;
import com.hmproductions.relaysync.data.Bus;

import java.util.ArrayList;
import java.util.List;

public class RelayRecyclerAdapter extends RecyclerView.Adapter<RelayRecyclerAdapter.RelayViewHolder> {

    private List<Bus> busList;
    private Context context;
    private RelayClickListener listener;

    public interface RelayClickListener {
        void onUpButtonClick(int position);

        void onDownButtonClick(int position);
    }

    public RelayRecyclerAdapter(List<Bus> busList, Context context, RelayClickListener listener) {
        this.busList = busList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RelayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RelayViewHolder(LayoutInflater.from(context).inflate(R.layout.relay_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RelayViewHolder holder, int position) {
        Bus currentBus = busList.get(position);

        holder.positionTextView.setText(String.valueOf(currentBus.getPosition()));

        if (currentBus.getLoadCurrent() != -1)
            holder.maxLoadCurrentEdiText.setText(String.valueOf(currentBus.getLoadCurrent()));
        else
            holder.maxLoadCurrentEdiText.setText("");

        if (currentBus.getMaxFaultCurrent() != -1)
            holder.maxFaultCurrentEditText.setText(String.valueOf(currentBus.getMaxFaultCurrent()));
        else
            holder.maxFaultCurrentEditText.setText("");

        if (currentBus.getMinFaultCurrent() != -1)
            holder.minFaultCurrentEditText.setText(String.valueOf(currentBus.getMinFaultCurrent()));
        else
            holder.minFaultCurrentEditText.setText("");
    }

    @Override
    public int getItemCount() {
        if (busList == null) return 0;
        return busList.size();
    }

    public void itemsChanged(List<Bus> newList, int positionStart, int count) {
        busList = newList;
        notifyItemRangeChanged(positionStart, count);
    }

    public void insertAtLast(List<Bus> newList) {
        busList = newList;
        notifyItemInserted(newList.size() - 1);
    }

    public void deleteRelay(List<Bus> newList, int position) {
        busList = newList;
        notifyItemRemoved(position);
    }

    public List<Bus> getUpdatedList() {
        if (busList == null) return new ArrayList<>();
        return busList;
    }

    private int getFormattedInteger(String s) {
        if (s.equals(""))
            return -1;
        else
            return Integer.parseInt(s);
    }

    class RelayViewHolder extends RecyclerView.ViewHolder {

        private TextView positionTextView;
        private EditText maxFaultCurrentEditText, minFaultCurrentEditText, maxLoadCurrentEdiText;
        private ImageButton upButton, downButton;
        ConstraintLayout viewForeground;

        RelayViewHolder(@NonNull View itemView) {
            super(itemView);

            positionTextView = itemView.findViewById(R.id.positionTextView);
            maxFaultCurrentEditText = itemView.findViewById(R.id.maxFaultCurrentEditText);
            minFaultCurrentEditText = itemView.findViewById(R.id.minFaultCurrentEditText);
            maxLoadCurrentEdiText = itemView.findViewById(R.id.maxLoadCurrentEditText);
            upButton = itemView.findViewById(R.id.upButton);
            downButton = itemView.findViewById(R.id.downButton);
            viewForeground = itemView.findViewById(R.id.view_foreground);

            maxLoadCurrentEdiText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    busList.get(getAdapterPosition()).setLoadCurrent(getFormattedInteger(s.toString()));
                }
            });

            minFaultCurrentEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    busList.get(getAdapterPosition()).setMinFaultCurrent(getFormattedInteger(s.toString()));
                }
            });

            maxFaultCurrentEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    busList.get(getAdapterPosition()).setMaxFaultCurrent(getFormattedInteger(s.toString()));
                }
            });

            upButton.setOnClickListener(v -> listener.onUpButtonClick(getAdapterPosition()));
            downButton.setOnClickListener(v -> listener.onDownButtonClick(getAdapterPosition()));
        }
    }
}
