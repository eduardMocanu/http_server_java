package handlers;

import compressions.GzipCompressor;
import exceptions.InexistentFile;
import exceptions.InvalidFileName;
import exceptions.InvalidHeader;
import http.HttpRequest;
import http.HttpResponse;
import utils.Utils;

import javax.swing.*;
import javax.swing.text.Utilities;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class FilesGetHandler implements RequestHandler {

    private final String baseDir;

    public FilesGetHandler(String baseDir) {
        this.baseDir = baseDir;
    }

    public FilesGetHandler() {
        this("../files/");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            File file = Utils.extractFile(request.getPath(), baseDir);
            byte[] fileBody = Files.readAllBytes(file.toPath());
            HashMap<String, String> extraHeaders = new HashMap<>();
            if (hasCompression(request)){
                fileBody = GzipCompressor.compress(fileBody);
                extraHeaders.put("Content-Encoding", "gzip");
            }
            return HttpResponse.ok(fileBody, extraHeaders);
        }catch (IOException e){
            return HttpResponse.internalServerError("The wanted file encountered a problem");
        }catch (InexistentFile e){
            return HttpResponse.notFound("The wanted file is not found");
        }catch (InvalidFileName e){
            return HttpResponse.unauthorized("The wanted file name is not valid");
        }
    }

    public boolean hasCompression(HttpRequest request){
        try {
            return request.getHeader("Accept-Encoding").contains("gzip");
        }catch (InvalidHeader e){
            return false;
        }
    }
}
