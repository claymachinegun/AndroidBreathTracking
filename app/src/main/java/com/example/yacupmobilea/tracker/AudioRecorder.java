package com.example.yacupmobilea.tracker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Get raw audio input from phone mic
 */
public class AudioRecorder implements IAudioSource {
    private volatile OnFrameCaptured onFrameCapturedListener;
    private final AudioRecord audioRecorder;
    private final int readSize;
    private final ExecutorService executorService;
    private Future currentRecordSession;

    public AudioRecorder() {
        this.audioRecorder = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                44100,
                AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_FLOAT,
                AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_FLOAT));
        this.readSize = 512;
        this.executorService = Executors.newSingleThreadExecutor();
        this.audioRecorder.setPositionNotificationPeriod(1);
    }

    public void setOnFrameCapturedListener(OnFrameCaptured onFrameCapturedListener) {
        this.onFrameCapturedListener = onFrameCapturedListener;
    }

    public void start() {
        if (currentRecordSession != null) {
            stop();
        }
        currentRecordSession = executorService.submit(() -> {
            audioRecorder.startRecording();
            while (!Thread.currentThread().isInterrupted()) {
                float[] floatBuffer = new float[readSize];
                int dataSize = audioRecorder.read(floatBuffer, 0, readSize, AudioRecord.READ_BLOCKING);
                if (dataSize == readSize) {
                    OnFrameCaptured listener = onFrameCapturedListener;
                    if (listener != null) {
                        listener.onFrame(floatBuffer);
                    }
                }
            }
            audioRecorder.stop();
        });

    }

    public void stop() {
        if (this.currentRecordSession != null) {
            this.currentRecordSession.cancel(true);
            this.currentRecordSession = null;
        }
    }

    public void cleanUp() {
        stop();
        executorService.shutdown();
        audioRecorder.release();
    }

}
