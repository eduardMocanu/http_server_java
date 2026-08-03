package handlers;

import exceptions.InexistentFile;
import exceptions.InvalidFileName;
import exceptions.InvalidHeader;
import exceptions.MalformedRequest;
import http.HttpRequest;
import http.HttpResponse;
import utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FilesPostHandler implements RequestHandler{

    private final String baseDir;

    public FilesPostHandler(String baseDir){
        this.baseDir = baseDir;
    }

    public  FilesPostHandler (){
        this.baseDir = "../files";
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            String filePath = Utils.extractFilePath(request.getPath());
            int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
            Utils.bodySizeMatchesLength(request.getBody(), contentLength);

            File file = new File(filePath);
            boolean createdNewFile = file.createNewFile();
            FileWriter fw = new FileWriter(filePath);
            String fileData = new String(request.getBody());
            fw.write(fileData);
            fw.close();
            if (createdNewFile){
                return HttpResponse.created("The file was created");
            }
            return HttpResponse.ok("The file was updated");

        }catch (IOException e){
            return HttpResponse.internalServerError("The wanted file encountered a problem");
        }catch (InexistentFile e){
            return HttpResponse.notFound("The wanted file is not found");
        }catch (InvalidFileName e){
            return HttpResponse.unauthorized("The wanted file name is not valid");
        }catch (InvalidHeader e){
            return HttpResponse.lengthRequired("The file length is required");
        }catch(MalformedRequest e){
            return HttpResponse.badRequest("The request body doesn't match the length of the content length header");
        }
    }
}
