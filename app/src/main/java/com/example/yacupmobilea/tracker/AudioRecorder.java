package com.example.yacupmobilea.tracker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.Arrays;

/**
 * Get raw audio input from phone mic
 */
public class AudioRecorder implements IAudioSource {
    private OnFrameCaptured onFrameCapturedListener;
    private AudioRecord audioRecorder;
    private int readSize;
    private float[] floatBuffer;
    private boolean isRunning = false;

    public AudioRecorder() {
        this.audioRecorder = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                44100,
                AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_FLOAT,
                AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_FLOAT));
        this.readSize = 512;
        this.floatBuffer = new float[readSize];
    }

    public void setOnFrameCapturedListener(OnFrameCaptured onFrameCapturedListener) {
        this.onFrameCapturedListener = onFrameCapturedListener;
    }

    public void start() {
        this.audioRecorder.startRecording();
        isRunning = true;
        Thread readingThread = new Thread(() -> {
            while (isRunning) {
                int data = audioRecorder.read(floatBuffer, 0, readSize, AudioRecord.READ_BLOCKING);
                if (data == readSize) {
                    if (onFrameCapturedListener != null) {
                        onFrameCapturedListener.onFrame(Arrays.copyOf(floatBuffer, readSize));
                    }
                }
            }
        });
        readingThread.start();
    }

    public void stop() {
        this.isRunning = false;
        this.audioRecorder.stop();
    }

}
