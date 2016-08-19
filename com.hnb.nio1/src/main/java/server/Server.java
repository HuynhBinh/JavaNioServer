package server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jjenkov on 24-10-2015.
 */
public class Server {

    private SocketAccepter  socketAccepter  = null;
    private SocketProcessor socketProcessor = null;

    private IMessageReaderFactory messageReaderFactory = null;
    private IMessageProcessor     messageProcessor = null;

    public Server(IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor)
    {
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;
    }

    public void start() throws IOException
    {
        this.socketAccepter  = new SocketAccepter();


        MessageBuffer readBuffer  = new MessageBuffer();
        MessageBuffer writeBuffer = new MessageBuffer();

        this.socketProcessor = new SocketProcessor(readBuffer, writeBuffer,  this.messageReaderFactory, this.messageProcessor);

        Thread accepterThread  = new Thread(this.socketAccepter);
        Thread processorThread = new Thread(this.socketProcessor);

        accepterThread.start();
        processorThread.start();
    }


}
