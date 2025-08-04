import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HandleClients implements Runnable{
    private Socket client;

    HandleClients(Socket client){
        this.client = client;
    }

    @Override
    public void run(){

        try{
            OutputStream out = client.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String requestLine = bufferedReader.readLine();
            InputStream in = client.getInputStream();

            System.out.println(requestLine);

            if(requestLine != null && !requestLine.isEmpty()){
                String[] parts = requestLine.split(" ");
                String operation = parts[0];
                String path = parts[1];
                Map<String, String> map = new HashMap<>();
                String i;
                try {
                    while(((i = bufferedReader.readLine()) != null) && !i.isEmpty()){
                        if(i.startsWith("User-Agent:")){
                            map.put("User-Agent", i.substring("User-Agent:".length()).trim());
                        }
                        if(i.startsWith("Host:")){
                            map.put("Host", i.substring("Host:".length()).trim());
                        }
                        if(i.startsWith("Accept:")){
                            map.put("Accept", i.substring("Accept:".length()).trim());
                        }
                        if(i.startsWith("Content-Type:")){
                            map.put("Content-Type", i.substring("Content-Type:".length()).trim());
                        }
                        if(i.startsWith("Content-Length:")){
                            map.put("Content-Length", i.substring("Content-Length:".length()).trim());
                        }
                    }
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }

                if (path.startsWith("/echo/")){
                    String body = path.substring(6);
                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: " + body.length() + "\r\n" +
                            "\r\n"+
                            body;
                    System.out.println(body);
                    out.write(response.getBytes());
                    out.flush();
                    System.out.println("Accepted");
                } else if (path.equals("/user-agent")) {
                    String value = map.get("User-Agent");
                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n"+
                            "Content-Length: " + value.length() + "\r\n"+
                            "\r\n"
                            + value;
                    out.write(response.getBytes());
                    out.flush();
                    System.out.println(value + " sent");
                } else if (path.startsWith("/files/")) {
                    if(operation.equals("GET")){
                        String fileName = path.substring("/files/".length()).trim();
                        String pathToFile = ".\\"+fileName;
                        File file = new File(pathToFile);
                        if(file.exists() && file.isFile()){
                            byte[] body = Files.readAllBytes(file.toPath());
                            String response = "HTTP/1.1 200 OK\r\n"+
                                    "Content-Type: application/octet-stream\r\n"+
                                    "Content-Length: " + body.length + "\r\n"+
                                    "\r\n";

                            out.write(response.getBytes());
                            out.write(body);
                            out.flush();
                            System.out.println("Accepted");
                        }else{
                            String body = "404 Not Found";
                            String response = "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Type: text/plain\r\n" +
                                    "Content-Length: " + body.length() + "\r\n" +
                                    "\r\n" +
                                    body;
                            out.write(response.getBytes());
                            out.flush();
                            System.out.println("not accepted");
                        }
                    }
                    else if(operation.equals("POST")){
                        System.out.println("here");
                        String fileName = path.substring("/files/".length()).trim();
                        int nrBytes = Integer.parseInt(map.get("Content-Length"));
                        char[] rawData = new char[nrBytes];
                        int bytesRead = 0;
                        System.out.println("rigth before");
                        bufferedReader.read(rawData, bytesRead, nrBytes);
                        System.out.println("right after");
                        //System.out.println(value);
                        System.out.println(rawData.length);
                        //continue here

                    }
                    
                } else{
                    String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                    out.write(response.getBytes());
                    out.flush();
                    System.out.println("Not accepted");
                }
            }else{
                String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                out.write(response.getBytes());
                out.flush();
                //System.out.println("No request line");
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

}
