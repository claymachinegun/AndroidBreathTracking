package com.example.yacupmobilea.ui.main;

import com.example.yacupmobilea.tracker.AudioRecorder;
import com.example.yacupmobilea.tracker.AverageSignalFolder;
import com.example.yacupmobilea.tracker.BreathTracker;
import com.example.yacupmobilea.tracker.BreathingInterval;
import com.example.yacupmobilea.tracker.DetectorSignal;
import com.example.yacupmobilea.tracker.MovingAverageValueFilter;
import com.example.yacupmobilea.tracker.SimpleDetector;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("mm:ss.SS");

    public ObservableBoolean isSessionStarted = new ObservableBoolean(false);
    public ObservableBoolean isPermissionsGranted = new ObservableBoolean(false);
    public ObservableBoolean isShareButtonVisible = new ObservableBoolean(false);

    public ObservableInt track = new ObservableInt(0);

    public ObservableArrayList<BreathCycleRow> rows = new ObservableArrayList<>();

    private BreathTracker tracker;
    private MovingAverageValueFilter filter;
    private SimpleDetector detector;
    private BreathingInterval previousInterval;

    public MainViewModel() {
        filter = new MovingAverageValueFilter(0.085f);
        detector = new SimpleDetector(0.1f, .4f);

        this.tracker = new BreathTracker(new AudioRecorder(), new AverageSignalFolder(), filter, detector, 512, 1, 12);
        this.tracker.setOnBreathIntervalChanged((interval) -> {
            if (interval.getType() == DetectorSignal.SILENCE) {
                return;
            }
            if (previousInterval == null) {
                previousInterval = interval;
                return;
            }

            if (previousInterval.getType() != interval.getType()) {
                BreathCycleRow row = new BreathCycleRow();
                if (previousInterval.getType() == DetectorSignal.BREATH_IN) {
                    row.setBold(true);
                    row.setDescription(String.format("%d. %s -> %s - %s",
                            rows.size() / 2 + 1,
                            previousInterval.getType().getReadableName(),
                            interval.getType().getReadableName(),
                            dateFormatter.format(new Date(interval.getStart() - previousInterval.getEnd()))));
                } else {
                    row.setDescription(String.format("\t%s -> %s - %s",
                            previousInterval.getType().getReadableName(),
                            interval.getType().getReadableName(),
                            dateFormatter.format(new Date(interval.getStart() - previousInterval.getEnd()))
                    ));
                }
                rows.add(row);
            }
            previousInterval = interval;
        });
        this.tracker.setOnValueObtained((value) -> {
            track.set((int) (value * 255.0f));
        });

    }

    public String getSessionLog() {
        StringBuilder builder = new StringBuilder();
        builder.append("Hi, this is my session today:\n\n");
        for (BreathCycleRow row : rows) {
            if (row.isBold()) {
                builder.append(row.getDescription());
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    public void startStopSession() {
        isSessionStarted.set(!isSessionStarted.get());
        if (isSessionStarted.get()) {
            isShareButtonVisible.set(false);
            previousInterval = null;
            rows.clear();
            tracker.start();
        } else {
            if (rows.size() > 1) {
                isShareButtonVisible.set(true);
            }
            tracker.stop();
            track.set(0);
        }

    }


}