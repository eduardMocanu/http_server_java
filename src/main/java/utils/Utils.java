package utils;

import exceptions.InexistentFile;
import exceptions.InvalidFileName;
import exceptions.MalformedRequest;
import http.HttpRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public abstract class Utils {

    static public File extractFile(String path, String baseDir) {
        String filePath = extractFilePath(path, baseDir);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new InexistentFile("The wanted file doesn't exist");
        }
        return file;
    }

    static public File extractFile(String path) {
        return extractFile(path, "../files/");
    }

    static public String extractFilePath(String path){
        return extractFilePath(path, "../files/");
    }

    static public String extractFilePath(String path, String baseDir){
        String fileName = path.substring("/files/".length()).trim().toLowerCase() + ".txt";
        if (fileName.contains("..") || fileName.contains("\\") || fileName.contains("/")){
            throw new InvalidFileName("The wanted file name is not valid");
        }

        return baseDir + fileName;
    }

    static public void bodySizeMatchesLength(byte[] body, int contentLength){
        if (body.length != contentLength){
            throw new  MalformedRequest("Content length doesn't match the body size");
        }
    }

    static public HttpRequest parseRawRequest(String rawRequest) throws IOException{
        return HttpRequest.parse(new BufferedReader(new StringReader(rawRequest)));
    }
}
