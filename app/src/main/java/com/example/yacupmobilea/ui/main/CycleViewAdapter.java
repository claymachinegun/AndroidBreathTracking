package com.example.yacupmobilea.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.yacupmobilea.databinding.BreathCycleRowBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CycleViewAdapter extends RecyclerView.Adapter<CycleViewHolder> {
    private ArrayList<BreathCycleRow> rows = new ArrayList<>();

    public CycleViewAdapter(ArrayList<BreathCycleRow> rows) {
        this.rows = rows;
    }

    @NonNull
    @Override
    public CycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BreathCycleRowBinding binding = BreathCycleRowBinding.inflate(inflater, parent, false);
        return new CycleViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CycleViewHolder holder, int position) {
        BreathCycleRow row = rows.get(position);
        holder.binding.setCycle(row);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }
}
