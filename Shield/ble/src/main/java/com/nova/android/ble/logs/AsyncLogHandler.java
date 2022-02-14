package com.nova.android.ble.logs;

import com.google.gson.Gson;
import com.nova.android.ble.logs.logentities.LogEntity;

import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class AsyncLogHandler implements Runnable {

    private static String TAG = "[Nova][AsyncLogHandler]";

    private BlockingQueue<LogEntity> blockingQueue = new LinkedBlockingDeque<LogEntity>();

    AsyncLogHandler(BlockingQueue<LogEntity> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {

        try {
            while (true) {
                LogEntity logEntity = this.blockingQueue.take();
                if (BleLogger.getInstance().shouldWriteToTempFile()) {
                    this.writeLog(logEntity);
                }
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            return;
        }
    }

    private void writeLog(LogEntity logEntity) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(BleLogger.getOrCreateFile(BleLogger.LOG_FILE), true);
            fileOutputStream.write(new Gson().toJson((Object) logEntity).getBytes());
            fileOutputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
