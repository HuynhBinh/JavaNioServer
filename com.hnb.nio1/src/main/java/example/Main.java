package example;


import http.HttpMessageReaderFactory;
import server.IMessageProcessor;
import server.Message;
import server.Server;
import java.io.IOException;


/**
 * Created by jjenkov on 19-10-2015.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 38\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body>Hellllo!</body></html>";


       // String httpResponse = "Hellooo";

        byte[] httpResponseBytes = httpResponse.getBytes("UTF-8");

        IMessageProcessor messageProcessor = (mess, writeProxy) -> {
            System.out.println("Message Received from socket: " + mess.socketId);

            Message response = writeProxy.getMessage();
            response.socketId = mess.socketId;
            response.writeToMessage(httpResponseBytes);

            writeProxy.enqueue(response);
        };

        Server server = new Server(new HttpMessageReaderFactory(), messageProcessor);

        server.start();

    }


}
