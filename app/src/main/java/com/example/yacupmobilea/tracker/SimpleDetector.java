package com.example.yacupmobilea.tracker;

/**
 * Detect breathing phases by value thesholds. Inhale ususally quieter than exhale
 */
public class SimpleDetector implements IDetector {
    private float breathInThreshold;
    private float breathOutThreshold;

    public float getBreathInThreshold() {
        return breathInThreshold;
    }

    public void setBreathInThreshold(float breathInThreshold) {
        this.breathInThreshold = breathInThreshold;
    }

    public float getBreathOutThreshold() {
        return breathOutThreshold;
    }

    public void setBreathOutThreshold(float breathOutThreshold) {
        this.breathOutThreshold = breathOutThreshold;
    }

    public SimpleDetector(float breathInThreshold, float breathOutThreshold) {
        this.breathInThreshold = breathInThreshold;
        this.breathOutThreshold = breathOutThreshold;
    }

    @Override
    public DetectorSignal getSignal(float value) {
        if (value >= breathOutThreshold) {
            return DetectorSignal.BREATH_OUT;
        } else if (value >= breathInThreshold) {
            return DetectorSignal.BREATH_IN;
        }
        return DetectorSignal.SILENCE;
    }
}
