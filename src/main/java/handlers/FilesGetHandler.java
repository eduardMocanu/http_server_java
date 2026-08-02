package handlers;

import exceptions.InexistentFile;
import exceptions.InvalidFileName;
import exceptions.InvalidHeader;
import http.HttpRequest;
import http.HttpResponse;
import utils.Utils;

import javax.swing.text.Utilities;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FilesGetHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            File file = Utils.extractFile(request.getPath());
            byte[] fileBody = Files.readAllBytes(file.toPath());
            return HttpResponse.ok(fileBody);
        }catch (IOException e){
            return HttpResponse.internalServerError("The wanted file encountered a problem");
        }catch (InexistentFile e){
            return HttpResponse.notFound("The wanted file is not found");
        }catch (InvalidFileName e){
            return HttpResponse.unauthorized("The wanted file name is not valid");
        }
    }
}
