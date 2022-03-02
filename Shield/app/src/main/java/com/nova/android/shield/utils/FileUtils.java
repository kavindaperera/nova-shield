package com.nova.android.shield.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

    public static List<String> readManufacturerList(Context context, int filename) {
        List<String> values = new LinkedList<>();
        Scanner inp = new Scanner(context.getResources().openRawResource(filename));
        inp.nextLine();
        while (inp.hasNextLine()) {
            values.add(inp.nextLine().split(",")[0].toLowerCase());
        }
        inp.close();
        return values;
    }

    public static List<String> readDeviceList(Context context, int filename) {
        List<String> devices = new LinkedList<>();
        Scanner inp = new Scanner(context.getResources().openRawResource(filename));
        inp.nextLine();
        while (inp.hasNextLine()) {
            devices.add(inp.nextLine().split(",")[1].toLowerCase());
        }
        inp.close();
        return devices;
    }

    public static HashMap<Integer, Integer> readDeviceThresholds(Context context, int filename) {
        HashMap<Integer, Integer> bleThresh = new HashMap<>();
        Scanner inp = new Scanner(context.getResources().openRawResource(filename));
        inp.nextLine();
        int counter = 1;
        while (inp.hasNextLine()) {
            bleThresh.put(counter, (int) Double.parseDouble(inp.nextLine().split(",")[2]));
            counter++;
        }
        inp.close();
        return bleThresh;
    }
}
