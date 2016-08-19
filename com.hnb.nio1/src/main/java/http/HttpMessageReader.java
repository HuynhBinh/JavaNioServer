package http;


import server.IMessageReader;
import server.Message;
import server.MessageBuffer;
import server.Socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjenkov on 18-10-2015.
 */
public class HttpMessageReader implements IMessageReader
{

    private MessageBuffer messageBuffer = null;

    private List<Message> completeMessages = new ArrayList<Message>();

    private Message nextMessage = null;

    private ByteBuffer byteBuffer = null;

    public HttpMessageReader()
    {
    }

    public void init(MessageBuffer readMessageBuffer)
    {
        this.messageBuffer = readMessageBuffer;
        this.nextMessage = messageBuffer.getMessage();
        this.nextMessage.metaData = new HttpHeaders();

        byteBuffer = ByteBuffer.allocate(1024 * 1024);
    }

    public void read(Socket socket) throws IOException
    {
        int bytesRead = socket.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.remaining() == 0)
        {
            byteBuffer.clear();
            return;
        }

        this.nextMessage.writeToMessage(byteBuffer);

        int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.sharedArray,
                                                 this.nextMessage.offset,
                                                 this.nextMessage.offset + this.nextMessage.length,
                                                 (HttpHeaders) this.nextMessage.metaData);

        // more than 1 message in the buffer
        if (endIndex != -1)
        {
            Message message = this.messageBuffer.getMessage();
            message.metaData = new HttpHeaders();

            message.writePartialMessageToMessage(nextMessage, endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }

        byteBuffer.clear();
    }

    public List<Message> getMessages()
    {
        return this.completeMessages;
    }

}
