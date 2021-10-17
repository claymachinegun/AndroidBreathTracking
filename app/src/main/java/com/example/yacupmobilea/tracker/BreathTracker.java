package com.example.yacupmobilea.tracker;

import org.jtransforms.fft.FloatFFT_1D;

public class BreathTracker implements IAudioSource.OnFrameCaptured {

    private DetectorSignal lastSignal;
    private BreathingInterval currentInterval = null;
    private BreathStageListener onBreathIntervalChanged;

    public interface OnValueObtainedHandler {
        void onValue(float value);
    }

    private OnValueObtainedHandler onValueObtained;

    public void setOnBreathIntervalChanged(BreathStageListener onBreathIntervalChanged) {
        this.onBreathIntervalChanged = onBreathIntervalChanged;
    }

    protected void processSignal(DetectorSignal newSignal) {
        if (lastSignal == newSignal) return;
        if (currentInterval == null) {
            currentInterval = new BreathingInterval();
            currentInterval.setType(newSignal);
            currentInterval.setStart(System.currentTimeMillis());
        }

        if (lastSignal == DetectorSignal.BREATH_OUT && newSignal == DetectorSignal.BREATH_IN) {
            return;
        }
        if (lastSignal == DetectorSignal.BREATH_IN && newSignal == DetectorSignal.BREATH_OUT) {
            currentInterval.setType(newSignal);
            return;
        }

        if (onBreathIntervalChanged != null) {
            BreathingInterval newInterval = new BreathingInterval();
            newInterval.setType(currentInterval.getType());
            newInterval.setStart(currentInterval.getStart());
            newInterval.setEnd(System.currentTimeMillis());
            onBreathIntervalChanged.onNewInterval(newInterval);
        }
        currentInterval.setEnd(System.currentTimeMillis());
        currentInterval = new BreathingInterval();
        currentInterval.setType(newSignal);
        currentInterval.setStart(System.currentTimeMillis());

        lastSignal = newSignal;
    }


    public void setOnValueObtained(OnValueObtainedHandler onValueObtained) {
        this.onValueObtained = onValueObtained;
    }

    private IAudioSource audioSource;
    private int minFreqIdx;
    private int maxFreqIdx;
    private FloatFFT_1D fourier;
    private ISignalFolder folder;
    private IValueFilter filter;
    private IDetector detector;

    /**
     * Track breathing phases by using IAudioSource
     *
     * @param sampleSize Size of record sample
     * @param minFreqIdx HighPass filter. frequencies lower than minFreqIdx * (sampleRateInHz / sampleSize ) will not be processed
     * @param maxFreqIdx LowPass filter. frequencies higher than minFreqIdx * (sampleRateInHz / sampleSize ) will not be processed
     */
    public BreathTracker(IAudioSource audioSource, ISignalFolder folder,
                         IValueFilter filter, IDetector detector,
                         int sampleSize, int minFreqIdx,
                         int maxFreqIdx) {
        this.fourier = new FloatFFT_1D(sampleSize);
        this.audioSource = audioSource;
        this.detector = detector;
        this.minFreqIdx = minFreqIdx;
        this.maxFreqIdx = maxFreqIdx;
        this.folder = folder;
        this.filter = filter;
    }

    public void start() {
        lastSignal = DetectorSignal.SILENCE;
        this.audioSource.setOnFrameCapturedListener(this);
        this.audioSource.start();

    }

    public void stop() {
        this.audioSource.stop();
        this.audioSource.setOnFrameCapturedListener(null);
    }

    private float[] fftToMagnitudes(float[] fft) {
        float[] result = new float[maxFreqIdx - minFreqIdx];
        int id = 0;
        for (int i = minFreqIdx * 2; i < maxFreqIdx * 2; i += 2) {
            result[id++] = (float) Math.sqrt(fft[i] * fft[i] + fft[i + 1] * fft[i + 1]);
        }
        return result;
    }


    @Override
    public void onFrame(float[] sample) {
        fourier.realForward(sample);
        float[] magnitudes = fftToMagnitudes(sample);
        float value = folder.fold(magnitudes);
        value = filter.next(value);
        if (this.onValueObtained != null) {
            this.onValueObtained.onValue(value);
        }
        DetectorSignal signal = detector.getSignal(value);
        processSignal(signal);
    }
}
