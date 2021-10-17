package com.example.yacupmobilea.tracker;

/**
 * Filters signal to reduce noise affection
 */
public class MovingAverageValueFilter implements IValueFilter {
    private float previousValue = 0.0f;
    private boolean isInitialized = false;
    private float coefficient;

    public MovingAverageValueFilter(float coefficient) {
        this.coefficient = coefficient;
    }

    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
        isInitialized = false;
    }

    @Override
    public float next(float newValue) {
        if (!isInitialized) {
            previousValue = newValue;
            isInitialized = true;
            return newValue;
        }
        float value = previousValue * (1.0f - coefficient) + newValue * coefficient;
        previousValue = value;
        return value;
    }
}
