package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ThreadUtils {

    public static void log(String message, Object ...args){
        String formattedMessage = String.format(message, args);
        System.out.printf("%s: %s: %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss SSS")), tname(), formattedMessage);
    }

    public static String tname(){
        return Thread.currentThread().getName();
    }

}
