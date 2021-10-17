package com.example.yacupmobilea.tracker;

public interface IAudioSource {
    public interface OnFrameCaptured {
        void onFrame(float[] data);
    }

    void start();

    void stop();

    void setOnFrameCapturedListener(OnFrameCaptured onFrameCapturedListener);

}
