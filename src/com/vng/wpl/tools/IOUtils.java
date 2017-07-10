package com.vng.wpl.tools;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by steven on 10/07/2017.
 */
public final class IOUtils {

    private IOUtils() {}

    public static void closeSafely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignored exception
            }
        }
    }
}
