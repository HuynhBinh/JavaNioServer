package server;

import example.Application;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by jjenkov on 19-10-2015.
 */
public class SocketAccepter implements Runnable
{
    private ServerSocketChannel serverSocketChannel = null;

    public SocketAccepter()
    {
    }

    public void run()
    {
        try
        {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(Application.PORT));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }


        while (true)
        {
            try
            {
                SocketChannel socketChannel = this.serverSocketChannel.accept();

                System.out.println("Socket accepted: " + socketChannel);

                //todo check if the queue can even accept more sockets.
                Application.socketQueue.add(new Socket(socketChannel));

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

    }
}
