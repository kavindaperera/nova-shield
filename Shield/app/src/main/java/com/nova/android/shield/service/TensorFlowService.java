package com.nova.android.shield.service;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldApp;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TensorFlowService {

    private static final String TAG = "[Nova][Shield][TensorFlowService]";

    private static Context context;
    private static Interpreter interpreter;

    private static TensorFlowService INSTANCE;

    private TensorFlowService (TensorFlowServiceBuilder builder) throws IOException {
        this.context = builder.context;
        this.interpreter = new Interpreter(loadModelFile(), null);
    }

    public static TensorFlowService getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        throw new IllegalStateException("TensorFlow Service must be initialized before trying to reference it.");
    }

    public static void initialize(Context context) throws IOException {
        Log.i(TAG, "initialize()");
        synchronized (TensorFlowService.class) {
            INSTANCE = new TensorFlowServiceBuilder(context).build();
        }
    }

    private static class TensorFlowServiceBuilder {

        private final Context context;

        TensorFlowServiceBuilder(Context context) {

            if (context == null) {
                throw new IllegalArgumentException("Context  is null.");
            }
            this.context = context.getApplicationContext();
        }

        public TensorFlowService build() throws IOException {
            return new TensorFlowService(this);
        }

    }


    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.context.getAssets().openFd("linear.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }

    public float[] doInference(String string) {
        float[] input = new float[1];
        input[0] = Float.parseFloat(string);
        float[][] output = new float[1][2];
        interpreter.run(input, output);
        return output[0];
    }

}
