package com.example.yacupmobilea.tracker;

/**
 * Folds magnitudes range to one value
 */
public class AverageSignalFolder implements ISignalFolder {
    public float fold(float[] values) {
        if (values.length == 0) {
            return 0;
        }
        float sum = 0;
        for (float item : values) {
            sum += item;
        }
        return sum / values.length;
    }
}
