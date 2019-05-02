package com.hmproductions.relaysync;

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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class RelayRecyclerAdapter extends RecyclerView.Adapter<RelayRecyclerAdapter.RelayViewHolder> {

    private List<Relay> relayList;
    private Context context;
    private RelayClickListener listener;

    interface RelayClickListener {
        void onUpButtonClick(int position);

        void onDownButtonClick(int position);
    }

    public RelayRecyclerAdapter(List<Relay> relayList, Context context, RelayClickListener listener) {
        this.relayList = relayList;
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
        Relay currentRelay = relayList.get(position);

        holder.positionTextView.setText(String.valueOf(currentRelay.getPosition()));

        if (currentRelay.getLoadCurrent() != -1)
            holder.maxLoadCurrentEdiText.setText(String.valueOf(currentRelay.getLoadCurrent()));
        else
            holder.maxLoadCurrentEdiText.setText("");

        if (currentRelay.getMaxFaultCurrent() != -1)
            holder.maxFaultCurrentEditText.setText(String.valueOf(currentRelay.getMaxFaultCurrent()));
        else
            holder.maxFaultCurrentEditText.setText("");

        if (currentRelay.getMinFaultCurrent() != -1)
            holder.minFaultCurrentEditText.setText(String.valueOf(currentRelay.getMinFaultCurrent()));
        else
            holder.minFaultCurrentEditText.setText("");
    }

    @Override
    public int getItemCount() {
        if (relayList == null) return 0;
        return relayList.size();
    }

    public void itemsChanged(List<Relay> newList, int positionStart, int count) {
        relayList = newList;
        notifyItemRangeChanged(positionStart, count);
    }

    public void insertAtLast(List<Relay> newList) {
        relayList = newList;
        notifyItemInserted(newList.size() - 1);
    }

    public List<Relay> getUpdatedList() {
        if (relayList == null) return new ArrayList<>();
        return relayList;
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

        RelayViewHolder(@NonNull View itemView) {
            super(itemView);

            positionTextView = itemView.findViewById(R.id.positionTextView);
            maxFaultCurrentEditText = itemView.findViewById(R.id.maxFaultCurrentEditText);
            minFaultCurrentEditText = itemView.findViewById(R.id.minFaultCurrentEditText);
            maxLoadCurrentEdiText = itemView.findViewById(R.id.maxLoadCurrentEditText);
            upButton = itemView.findViewById(R.id.upButton);
            downButton = itemView.findViewById(R.id.downButton);

            maxLoadCurrentEdiText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    relayList.get(getAdapterPosition()).setLoadCurrent(getFormattedInteger(s.toString()));
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
                    relayList.get(getAdapterPosition()).setMinFaultCurrent(getFormattedInteger(s.toString()));
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
                    relayList.get(getAdapterPosition()).setMaxFaultCurrent(getFormattedInteger(s.toString()));
                }
            });

            upButton.setOnClickListener(v -> listener.onUpButtonClick(getAdapterPosition()));
            downButton.setOnClickListener(v -> listener.onDownButtonClick(getAdapterPosition()));
        }
    }
}
