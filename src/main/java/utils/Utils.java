package utils;

import exceptions.InexistentFile;
import exceptions.InvalidFileName;
import exceptions.MalformedRequest;

import java.io.File;

public abstract class Utils {

    static public File extractFile(String path){
        String filePath = extractFilePath(path);

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()){
            throw new InexistentFile("The wanted file doesn't exist");
        }
        return file;
    }

    static public String extractFilePath(String path){
        String fileName = path.substring("/files/".length()).trim().toLowerCase() + ".txt";
        if (fileName.contains("..") || fileName.contains("\\") || fileName.contains("/")){
            throw new InvalidFileName("The wanted file name is not valid");
        }

        return "../files/" + fileName;
    }

    static public void bodySizeMatchesLength(byte[] body, int contentLength){
        if (body.length != contentLength){
            throw new  MalformedRequest("Content length doesn't match the body size");
        }
    }

}
