package com.example.yacupmobilea.ui.main;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yacupmobilea.databinding.MainFragmentBinding;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    private MainFragmentBinding binding;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = MainFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment context = this;

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setContext(mViewModel);

        binding.rvRows.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        final CycleViewAdapter adapter = new CycleViewAdapter(mViewModel.rows);
        binding.rvRows.setItemAnimator(null);
        binding.rvRows.setAdapter(adapter);

        binding.btnShare.setOnClickListener((v) -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My traingin session");
            emailIntent.putExtra(Intent.EXTRA_TEXT, mViewModel.getSessionLog());
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            } catch (ActivityNotFoundException ex) {
                Snackbar.make(getView(), "There are no email clients installed", Snackbar.LENGTH_LONG).show();
            }


        });

        mViewModel.rows.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<BreathCycleRow>>() {
            @Override
            public void onChanged(ObservableList<BreathCycleRow> sender) {
                //context.getActivity().runOnUiThread(() -> {
                //    binding.rvRows.getAdapter().notifyDataSetChanged();
                //});
            }

            @Override
            public void onItemRangeChanged(ObservableList<BreathCycleRow> sender, int positionStart, int itemCount) {
                context.getActivity().runOnUiThread(() -> {
                    binding.rvRows.getAdapter().notifyItemRangeChanged(positionStart, itemCount);
                });
            }

            @Override
            public void onItemRangeInserted(ObservableList<BreathCycleRow> sender, int positionStart, int itemCount) {
                context.getActivity().runOnUiThread(() -> {
                    binding.rvRows.getAdapter().notifyItemRangeInserted(positionStart, itemCount);
                    binding.rvRows.scrollToPosition(positionStart);
                });
            }

            @Override
            public void onItemRangeMoved(ObservableList<BreathCycleRow> sender, int fromPosition, int toPosition, int itemCount) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<BreathCycleRow> sender, int positionStart, int itemCount) {
                context.getActivity().runOnUiThread(() -> {
                    binding.rvRows.getAdapter().notifyItemRangeRemoved(positionStart, itemCount);
                });
            }
        });


        Dexter.withContext(getContext())
                .withPermission(
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                mViewModel.isPermissionsGranted.set(true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                showPermissionRequest();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                showPermissionRequest();
            }
        }).check();

    }


    private void showPermissionRequest() {
        mViewModel.isPermissionsGranted.set(false);
        Snackbar
                .make(getView(), "We need permission to record audio so we can track your breath rate", Snackbar.LENGTH_INDEFINITE)
                .setAction("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .show();
    }

}