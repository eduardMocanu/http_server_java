
import compressions.GzipCompressor;
import handlers.FilesPostHandler;
import http.HttpRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import utils.Utils;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

public class FilesPostHandlerTest {

    private static final String baseDir = "./src/test/java/testFiles/";
    private static final FilesPostHandler filesPostHandler = new FilesPostHandler(baseDir);

    @Test
    public void validFileUpdatedPostTest() throws IOException {
        String path = "/files/testFileUpdated";
        String body = "Ana are mere";
        File file = new File(Utils.extractFilePath(path, baseDir));
        var status = file.createNewFile();
        HttpRequest request = Utils.parseRawRequest(Utils.buildPostRequest(path, body));

        var response = filesPostHandler.handle(request);

        assertEquals(200, response.getResponseCode());
        assertArrayEquals(Files.readAllBytes(file.toPath()), body.getBytes());
    }


    @Test
    public void validFileCreatedPostTest() throws IOException {
        String path = "/files/testFileCreated";
        String body = "Ana are mere";
        HttpRequest request = Utils.parseRawRequest(Utils.buildPostRequest(path, body));

        var response = filesPostHandler.handle(request);
        File file = Utils.extractFile(path, baseDir);

        assertEquals(201, response.getResponseCode());
        assertArrayEquals(Files.readAllBytes(file.toPath()), body.getBytes());

    }

    @Test
    public void GzipEncodingvalidFileCreatedPostTest() throws IOException {
        String path = "/files/testFileCreatedGzip";
        String body = "Ana are mere";
        HttpRequest request = Utils.parseRawRequest(Utils.buildPostRequestGzipCompressed(path, body));

        var response = filesPostHandler.handle(request);
        File file = Utils.extractFile(path, baseDir);

        assertEquals(201, response.getResponseCode());
        assertArrayEquals(Files.readAllBytes(file.toPath()), body.getBytes());

    }

    @Test
    public void invalidFileNameTest() throws IOException {
        String path = "/files/../../adawdaw";
        String body = "Ana are mere";
        HttpRequest request = Utils.parseRawRequest(Utils.buildPostRequest(path, body));

        var response = filesPostHandler.handle(request);

        assertEquals(401, response.getResponseCode());
    }


    @AfterAll
    public static void destroyFiles(){
        File fileCreated = new File(baseDir + "testfilecreated.txt");
        File fileUpdated = new File(baseDir + "testfileupdated.txt");
        File fileCreatedGzip = new File(baseDir + "testfilecreatedgzip.txt");

        var statusCreated = fileCreated.delete();
        var statusUpdated = fileUpdated.delete();
        var statusCreatedGzip = fileCreatedGzip.delete();
    }
}

