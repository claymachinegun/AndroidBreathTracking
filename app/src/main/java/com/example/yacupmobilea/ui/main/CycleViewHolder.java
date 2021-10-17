package com.example.yacupmobilea.ui.main;

import android.view.View;

import com.example.yacupmobilea.databinding.BreathCycleRowBinding;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class CycleViewHolder extends RecyclerView.ViewHolder {
    BreathCycleRowBinding binding;

    public CycleViewHolder(View v) {
        super(v);
        binding = DataBindingUtil.bind(v);
    }
}
