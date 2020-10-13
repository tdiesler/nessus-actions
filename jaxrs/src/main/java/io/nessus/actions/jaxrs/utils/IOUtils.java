package io.nessus.actions.jaxrs.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A utility class for IO operations.
 *
 * @author Thomas.Diesler@jboss.com
 * @since 16-Sep-2010
 */
public final class IOUtils {

    // Hide ctor
    private IOUtils() {
    }

    public static long copyStream(InputStream input, OutputStream output) throws IOException {
        return copyStream(input, output, 8024);
    }

    public static long copyStream(InputStream input, OutputStream output, int buffersize) throws IOException {
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        long count=0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void safeClose(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            // ignore
        }
    }
}
