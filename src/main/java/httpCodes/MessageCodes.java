package httpCodes;

import exceptions.InvalidResponseCode;

import java.util.Map;

public abstract class MessageCodes {
    static final private Map<Integer, String> messages = Map.of(
            200,"OK",
            404,"Not Found",
            400,"Bad Request",
            411,"Length Required",
            201,"Created",
            405,"Method Not Allowed",
            500, "Internal Server Error"
    );

    static public String messageForResponseCode(int code){
        if (messages.containsKey(code)){
            return messages.get(code);
        }
        throw new InvalidResponseCode("Invalid code");
    }
}
