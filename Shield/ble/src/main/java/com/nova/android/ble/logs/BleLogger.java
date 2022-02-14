package com.nova.android.ble.logs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;


import com.nova.android.ble.logs.logentities.LogEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BleLogger {

    public static final String DIR_LOG = "BleLogs";
    public static final String LOG_FILE = "ble.log";
    private static String TAG = "[Nova][Logger]";
    private static BleLogger INSTANCE;
    private final BlockingQueue<LogEntity> blockingQueue;
    private boolean isLogging = false;
    private Context context;
    private String filename;
    private ArrayList<LogEntity> activeLogEntityList = new ArrayList();
    private boolean writeToFile;

    public BleLogger(boolean writeToTempFile, Context context) {
        this.blockingQueue = new LinkedBlockingQueue<LogEntity>();
        this.writeToFile = writeToTempFile;
        this.context = context;
        new Thread(new AsyncLogHandler(this.blockingQueue)).start();
    }

    public BleLogger(BlockingQueue<LogEntity> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public static void init(Context context, boolean writeToTempFile) {
        if (writeToTempFile && context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            writeToTempFile = false;
        }
        if (INSTANCE == null) {
            INSTANCE = new BleLogger(writeToTempFile, context);
        }
    }

    public static BleLogger getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Logger must be initialized before trying to reference it.");
        }
        return INSTANCE;
    }

    public static File getOrCreateFile(String fileName) {
        try {
            String string = BleLogger.getInstance().getContext().getExternalCacheDir().getAbsolutePath() + "/" + DIR_LOG;
            File file = new File(string + "/" + fileName);
            if (!file.exists()) {
                File file2 = new File(string);
                file2.mkdirs();
                file.createNewFile();
                new FileWriter(file).close();
                file.setLastModified(System.currentTimeMillis());
            }
            return file;
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return null;
        }
    }

    public static boolean isLogging() {
        return BleLogger.getInstance().isLogging;
    }

    public static synchronized void log(LogEntity logEntity) {
        try {

            if (INSTANCE != null && BleLogger.isLogging()) {
                try {
                    BleLogger.getInstance().blockingQueue.put(logEntity);
                    BleLogger.getInstance().getActiveLogEntityList().add(logEntity);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static synchronized void startLogs() {
        BleLogger.getInstance().isLogging = true;
    }

    public boolean shouldWriteToTempFile() {
        return this.writeToFile;
    }

    public ArrayList<LogEntity> getActiveLogEntityList() {
        return this.activeLogEntityList;
    }

    public void setActiveLogEntityList(ArrayList<LogEntity> logEntityList) {
        this.activeLogEntityList = logEntityList;
    }


    public String getFileName() {
        return this.filename;
    }

    public Context getContext() {
        return this.context;
    }

    public static synchronized void clearLogs() {
        BleLogger.getInstance().setActiveLogEntityList(new ArrayList<LogEntity>());
        BleLogger.empty(LOG_FILE, new byte[0]);

    }

    private static boolean empty(String string, byte[] arrby) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(BleLogger.getOrCreateFile(string));
            fileOutputStream.write(arrby);
            fileOutputStream.close();
            return true;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return false;
        }
    }

}
