import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;

public class HandleClients implements Runnable {
    private Socket client;

    HandleClients(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

        try {
            OutputStream out = client.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String requestLine;
            boolean ok = true;

            while (!client.isClosed() && ok) {
                requestLine = bufferedReader.readLine();
                System.out.println(requestLine);

                if (requestLine != null && ! requestLine.isEmpty()) {
                    String[] parts = requestLine.split(" ");
                    String operation = parts[0];
                    String path = parts[1];
                    Map<String, String> map = new HashMap<>();
                    String i;
                    try {
                        while (((i = bufferedReader.readLine()) != null) && ! i.isEmpty()) {
                            if (i.startsWith("User-Agent:")) {
                                map.put("User-Agent", i.substring("User-Agent:".length()).trim());
                            }
                            if (i.startsWith("Host:")) {
                                map.put("Host", i.substring("Host:".length()).trim());
                            }
                            if (i.startsWith("Accept:")) {
                                map.put("Accept", i.substring("Accept:".length()).trim());
                            }
                            if (i.startsWith("Content-Type:")) {
                                map.put("Content-Type", i.substring("Content-Type:".length()).trim());
                            }
                            if (i.startsWith("Content-Length:")) {
                                map.put("Content-Length", i.substring("Content-Length:".length()).trim());
                            }
                            if (i.startsWith("Accept-Encoding:")) {
                                map.put("Accept-Encoding", i.substring("Accept-Encoding:".length()).trim());
                            }
                            if (i.startsWith("Connection:")) {
                                map.put("Connection", i.substring("Connection:".length()).trim());
                                if (map.get("Connection").equals("close")) {
                                    ok = false;
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    String contentTypeText = "text/plain";
                    String contentEncoding = (map.containsKey("Accept-Encoding") && map.get("Accept-Encoding").contains("gzip")) ? "gzip" : "";
                    String connection = (map.containsKey("Connection") && map.get("Connection").contains("close")) ? "close" : "";
                    if (path.startsWith("/echo/")) {
                        String body = path.substring(6);
                        sendResponse(out, "200", "OK", contentTypeText, "", connection, body.getBytes());
                        System.out.println("Accepted");

                    } else if (path.equals("/user-agent")) {
                        String value = map.get("User-Agent");
                        sendResponse(out, "200", "OK", contentTypeText, "", connection, value.getBytes());

                    } else if (path.startsWith("/files/")) {

                        if (operation.equals("GET")) {
                            String fileName = path.substring("/files/".length()).trim() + ".txt";
                            String pathToFile = ".\\" + fileName;
                            File file = new File(pathToFile);
                            if (fileName.contains("..") || fileName.contains("\\") || fileName.contains("/")) {
                                sendResponse(out, "400", "Bad Request", contentTypeText, "", connection, "folder traversal attempted".getBytes());
                            }else{
                                if (file.exists() && file.isFile()) {
                                    byte[] body = Files.readAllBytes(file.toPath());
                                    sendResponse(out, "200", "OK", "application/octet-stream", contentEncoding, connection, gzipCompress(body));
                                } else {
                                    sendResponse(out, "404", "Not Found", contentTypeText, "", connection, "Not found".getBytes());
                                }
                            }

                        } else if (operation.equals("POST")) {
                            String fileName = path.substring("/files/".length()).trim() + ".txt";
                            try {
                                if (fileName.contains("..") || fileName.contains("\\") || fileName.contains("/")) {
                                    sendResponse(out, "400", "Bad Request", contentTypeText, "", connection, "folder traversal attempted".getBytes());
                                }
                                else{
                                    if(!map.containsKey("Content-Length")){
                                        sendResponse(out, "411", "Length Required", contentTypeText, "", connection, "Content-Length missing".getBytes());
                                    }else{
                                        int nrBytes = Integer.parseInt(map.get("Content-Length"));
                                        char[] rawData = new char[nrBytes];
                                        bufferedReader.read(rawData, 0, nrBytes);
                                        System.out.println(rawData.length);
                                        String fileData = new String(rawData);
                                        System.out.println(fileData);
                                        File createdFile = new File(fileName);

                                        if (createdFile.createNewFile()) {
                                            sendResponse(out, "201", "Created", contentTypeText, "", connection, "created".getBytes());
                                        } else {
                                            sendResponse(out, "200", "OK", contentTypeText, "", connection, "file already exists".getBytes());
                                        }

                                        FileWriter fileWriter = new FileWriter(fileName);
                                        fileWriter.write(fileData);
                                        fileWriter.close();
                                    }
                                }


                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                sendResponse(out, "400", "Bad Request", contentTypeText, "", connection, ("Request had a problem " + e.getMessage()).getBytes());
                            }
                        }

                    } else {
                        sendResponse(out, "405", "Method Not Allowed", contentTypeText, "", connection, "Bad endpoint".getBytes());
                        System.out.println("Not accepted");
                    }
                } else {
                    sendResponse(out, "404", "Not found", "text/plain", "", "close", "No request line received".getBytes());
                    System.out.println("Not accepted");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private byte[] gzipCompress(byte[] initialBytedData) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(os);
            gzip.write(initialBytedData);
            gzip.close();
            return os.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new byte[0];
        }
    }

    private void sendResponse(OutputStream out, String code, String message, String contentType, String contentEncoding, String connection, byte[] body){
        String response = "HTTP/1.1 " + code + " " + message + "\r\n"+
                "Content-Type: " + contentType + "\r\n"+
                "Content-Length: " + body.length + "\r\n";
        if(!contentEncoding.isEmpty()){
            response += "Content-Encoding: " + contentEncoding + "\r\n";
        }
        if(!connection.isEmpty()){
            response += "Connection: " + connection + "\r\n";
        }
        response += "\r\n";
        try{
            out.write(response.getBytes());
            out.write(body);
            out.flush();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

}
