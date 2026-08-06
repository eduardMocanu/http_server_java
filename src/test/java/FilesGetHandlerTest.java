import handlers.FilesGetHandler;
import http.HttpRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.Utils;
import static org.junit.jupiter.api.Assertions.*;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FilesGetHandlerTest {

    private static final String filePath = "./src/test/java/testFiles/filesGetHandlerTestFile.txt";
    private static final FilesGetHandler fileGetHandler = new FilesGetHandler("./src/test/java/testFiles/");

    @BeforeAll
    static public void createFile() throws IOException {
        File file = new File(filePath);
        var created = file.createNewFile();
        FileWriter fw = new FileWriter(file);
        fw.write("Ana are mere");
        fw.close();
        if (created){
            System.out.println("The file was successfully created (file get handler tests)");
        }else{
            System.out.println("The file was not created (file get handler tests)");
        }
    }

    @Test
    public void fileNotFoundTest() throws IOException {
        HttpRequest request = Utils.parseRawRequest("GET /files/adada HTTP/1.1\r\n\r\n\r\n");

        var response = fileGetHandler.handle(request);

        assertEquals(404, response.getResponseCode());
    }

    @Test
    public void invalidFileNameTest() throws IOException {
        HttpRequest request = Utils.parseRawRequest("GET /files/../../feaawd HTTP/1.1\r\n\r\n\r\n");

        var response = fileGetHandler.handle(request);

        assertEquals(401, response.getResponseCode());
    }

    @Test
    public void validFileNameTest() throws IOException {
        HttpRequest request = Utils.parseRawRequest("GET /files/filesGetHandlerTestFile HTTP/1.1\r\n\r\n\r\n");

        var response = fileGetHandler.handle(request);

        assertEquals(200, response.getResponseCode());
        assertArrayEquals("Ana are mere".getBytes(), response.getBody());
    }

    @Test
    public void hasCompressionTest() throws IOException {
        HttpRequest request = Utils.parseRawRequest("GET /files/dada HTTP/1.1\r\n" +
                "Accept-Encoding: gzip\r\n\r\n");

        assertTrue(fileGetHandler.hasCompression(request));
    }

    @Test
    public void notHasCompressionTest() throws IOException {
        HttpRequest request = Utils.parseRawRequest("GET /files/dada HTTP/1.1\r\n\r\n\r\n");

        assertFalse(fileGetHandler.hasCompression(request));
    }

    @AfterAll
    static public void destroyFile(){
        File file = new File(filePath);
        var deleted = file.delete();
        if (! deleted){
            System.out.println("Problem on file deletion of get handler tests");
        }
        else{
            System.out.println("Deleted successfully the get handler test file");
        }
    }

}
