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

                    if (path.startsWith("/echo/")) {
                        String body = path.substring(6);
                        String response;
                        if (map.containsKey("Accept-Encoding") && map.get("Accept-Encoding").contains("gzip")) {
                            byte[] compressedBytes = gzipCompress(body.getBytes());
                            response = "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/plain\r\n" +
                                    "Content-Encoding: gzip\r\n" +
                                    "Content-Length: " + compressedBytes.length + "\r\n" +
                                    "\r\n";
                            System.out.println(body);
                            out.write(response.getBytes());
                            out.write(compressedBytes);
                        } else {
                            response = "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/plain\r\n" +
                                    "Content-Length: " + body.length() + "\r\n" +
                                    "\r\n" +
                                    body;
                            System.out.println(body);
                            out.write(response.getBytes());
                        }
                        out.flush();
                        System.out.println("Accepted");
                    } else if (path.equals("/user-agent")) {
                        String value = map.get("User-Agent");
                        String response;
                        if (map.containsKey("Accept-Encoding") && map.get("Accept-Encoding").contains("gzip")) {
                            byte[] body = gzipCompress(value.getBytes());
                            response = "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/plain\r\n" +
                                    "Content-Length: " + body.length + "\r\n" +
                                    "Content-Encoding: gzip\r\n" +
                                    "\r\n";
                            out.write(response.getBytes());
                            out.write(body);
                        } else {
                            response = "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/plain\r\n" +
                                    "Content-Length: " + value.length() + "\r\n" +
                                    "\r\n"
                                    + value;
                            out.write(response.getBytes());
                        }
                        out.flush();
                    } else if (path.startsWith("/files/")) {
                        if (operation.equals("GET")) {
                            String fileName = path.substring("/files/".length()).trim();
                            String pathToFile = ".\\" + fileName;
                            File file = new File(pathToFile);
                            if (file.exists() && file.isFile()) {
                                byte[] body = Files.readAllBytes(file.toPath());
                                String response;
                                if (map.containsKey("Accept-Encoding") && map.get("Accept-Encoding").contains("gzip")) {
                                    byte[] bodyGzipCompressed = gzipCompress(body);
                                    response = "HTTP/1.1 200 OK\r\n" +
                                            "Content-Type: application/octet-stream\r\n" +
                                            "Content-Length: " + bodyGzipCompressed.length + "\r\n" +
                                            "Content-Encoding: gzip\r\n" +
                                            "\r\n";
                                    out.write(response.getBytes());
                                    out.write(bodyGzipCompressed);
                                } else {
                                    response = "HTTP/1.1 200 OK\r\n" +
                                            "Content-Type: application/octet-stream\r\n" +
                                            "Content-Length: " + body.length + "\r\n" +
                                            "\r\n";
                                    out.write(response.getBytes());
                                    out.write(body);
                                }
                                out.flush();
                                System.out.println("Accepted");
                            } else {
                                String response;
                                String body;
                                body = "404 Not Found";
                                response = "HTTP/1.1 404 Not Found\r\n" +
                                        "Content-Type: text/plain\r\n" +
                                        "Content-Length: " + body.length() + "\r\n" +
                                        "\r\n" +
                                        body;
                                out.write(response.getBytes());
                                out.flush();
                                System.out.println("not accepted");
                            }
                        } else if (operation.equals("POST")) {
                            String fileName = path.substring("/files/".length()).trim() + ".txt";
                            try {
                                if (fileName.contains("..") || fileName.contains("\\") || fileName.contains("/")) {
                                    throw new IllegalArgumentException("Invalid file name: directory traversal attempted");
                                }
                                int nrBytes = Integer.parseInt(map.get("Content-Length"));
                                char[] rawData = new char[nrBytes];
                                bufferedReader.read(rawData, 0, nrBytes);
                                System.out.println(rawData.length);
                                String fileData = new String(rawData);
                                System.out.println(fileData);
                                File createdFile = new File(fileName);

                                String response;
                                if (createdFile.createNewFile()) {
                                    System.out.println("File created " + createdFile.getName());
                                    response = "HTTP/1.1 201 Created\r\n\r\n";
                                } else {
                                    System.out.println("file already exists");
                                    response = "HTTP/1.1 200 OK\r\n\r\n";
                                }
                                FileWriter fileWriter = new FileWriter(fileName);
                                fileWriter.write(fileData);
                                fileWriter.close();
                                out.write(response.getBytes());
                                out.flush();

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                String bodyMessage = "Request had a problem: " + e.getMessage();
                                String response = "HTTP/1.1 400 Bad Request\r\n" +
                                        "Content-Type: text/plain\r\n" +
                                        "Content-Length: " + bodyMessage.length() + "\r\n" +
                                        "\r\n" + bodyMessage;
                                out.write(response.getBytes());
                                out.flush();
                            }
                        }

                    } else {
                        String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                        out.write(response.getBytes());
                        out.flush();
                        System.out.println("Not accepted");
                    }
                } else {
                    String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                    out.write(response.getBytes());
                    out.flush();
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
}
//TO DO - write a sendResponse method that is modular enough to serve for all endpoints