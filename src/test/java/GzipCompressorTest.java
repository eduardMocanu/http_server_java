import compressions.GzipCompressor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;

public class GzipCompressorTest {

    @Test
    void classicTest() throws IOException {
        byte[] original = "Ana are mere".getBytes();
        byte[] compressed = GzipCompressor.compress(original);
        byte[] decompressed = GzipCompressor.decompress(compressed);

        assertArrayEquals(original, decompressed);
        assertNotEquals(original.length, compressed.length);

    }

    @Test
    void emptyArrayTest() throws IOException {
        assertArrayEquals(new byte[0], GzipCompressor.decompress(GzipCompressor.compress(new byte[0])));
    }

    @Test
    void nonCompressedToDecompressTest() throws IOException {
        byte[] notCompressed = "ana are mere".getBytes();
        assertThrows(IOException.class, () -> GzipCompressor.decompress(notCompressed));
    }


}
