package com.wintone.utils;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;

public class Utils {
    public static int[] convertYUV420_NV21toARGB8888(byte[] data, int width, int height) {
        int size = width * height;
        int offset = size;
        int[] pixels = new int[size];
        int i = 0;
        int k = 0;
        while (i < size) {
            int y2 = data[i + 1] & MotionEventCompat.ACTION_MASK;
            int y3 = data[width + i] & MotionEventCompat.ACTION_MASK;
            int y4 = data[(width + i) + 1] & MotionEventCompat.ACTION_MASK;
            int u = (data[offset + k] & MotionEventCompat.ACTION_MASK) - 128;
            int v = (data[(offset + k) + 1] & MotionEventCompat.ACTION_MASK) - 128;
            pixels[i] = convertYUVtoARGB(data[i] & MotionEventCompat.ACTION_MASK, u, v);
            pixels[i + 1] = convertYUVtoARGB(y2, u, v);
            pixels[width + i] = convertYUVtoARGB(y3, u, v);
            pixels[(width + i) + 1] = convertYUVtoARGB(y4, u, v);
            if (i != 0 && (i + 2) % width == 0) {
                i += width;
            }
            i += 2;
            k += 2;
        }
        return pixels;
    }

    private static int convertYUVtoARGB(int y, int u, int v) {
        int r = y + (u * 1);
        int g = y - ((int) ((0.344f * ((float) v)) + (0.714f * ((float) u))));
        int b = y + (v * 1);
        if (r > MotionEventCompat.ACTION_MASK) {
            r = MotionEventCompat.ACTION_MASK;
        } else if (r < 0) {
            r = 0;
        }
        if (g > MotionEventCompat.ACTION_MASK) {
            g = MotionEventCompat.ACTION_MASK;
        } else if (g < 0) {
            g = 0;
        }
        if (b > MotionEventCompat.ACTION_MASK) {
            b = MotionEventCompat.ACTION_MASK;
        } else if (b < 0) {
            b = 0;
        }
        return ((ViewCompat.MEASURED_STATE_MASK | (r << 16)) | (g << 8)) | b;
    }
}