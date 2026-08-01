package compressions;

public class GzipCompressor {
}
//TODO:
//private byte[] gzipCompress(byte[] initialBytedData) {
//    try {
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        GZIPOutputStream gzip = new GZIPOutputStream(os);
//        gzip.write(initialBytedData);
//        gzip.close();
//        return os.toByteArray();
//    } catch (Exception e) {
//        System.out.println(e.getMessage());
//        return new byte[0];
//    }
//}