package example;

import server.Socket;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Huynh Binh PC on 8/16/2016.
 */
public class Application
{
    public static  Queue<Socket> socketQueue = new ArrayBlockingQueue(1024); //move 1024 to ServerConfig

    public static int PORT = 9999;

    public static AtomicInteger NextSocketId = new AtomicInteger(16 * 1024); //start incoming socket ids from 16K - reserve bottom ids for pre-defined sockets (servers).

}
