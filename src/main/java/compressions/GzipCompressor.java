package compressions;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

abstract public class GzipCompressor {

    static public byte[] compress(byte[] initialByteData) throws IOException {

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(os);
            gzip.write(initialByteData);
            gzip.close();
            return os.toByteArray();

    }

    static public byte[] decompress(byte[] compressedBody) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressedBody))) {
            gzip.transferTo(os);
        }
        return os.toByteArray();
    }

}