package com.example.yacupmobilea.tracker;

/**
 * Represents breathing time interval
 */
public class BreathingInterval {
    private DetectorSignal type;
    private long start;
    private long end;

    @Override
    public String toString() {
        return String.format("%s %d : %d", type.name(), start, end);
    }

    public void setType(DetectorSignal type) {
        this.type = type;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public DetectorSignal getType() {
        return type;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
