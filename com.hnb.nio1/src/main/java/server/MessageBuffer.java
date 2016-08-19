package server;

/**
 * A shared buffer which can contain many messages inside. A message gets a section of the buffer to use. If the
 * message outgrows the section in size, the message requests a larger section and the message is copied to that
 * larger section. The smaller section is then freed again.
 *
 *
 * Created by jjenkov on 18-10-2015.
 */
public class MessageBuffer
{

    public static int KB = 1024;

    private static final int CAPACITY_SMALL  =   4  * KB; // 4kb
    private static final int CAPACITY_MEDIUM = 128  * KB; // 128kb
    private static final int CAPACITY_LARGE  = 1024 * KB; // 1024kb

    //package scope (default) - so they can be accessed from unit tests.
    private byte[]  smallMessageBuffer  = new byte[1024 *   CAPACITY_SMALL];   //1024 x   4KB messages =  4MB.   1024 small messages
    private byte[]  mediumMessageBuffer = new byte[128  *   CAPACITY_MEDIUM];  // 128 x 128KB messages = 16MB.  128 medium messages
    private byte[]  largeMessageBuffer  = new byte[128  *   CAPACITY_LARGE];   //  16 *   1MB messages = 16MB.   16 large messages

    private QueueIntFlip smallMessageBufferFreeBlocks  = new QueueIntFlip(1024); // 1024 free sections
    private QueueIntFlip mediumMessageBufferFreeBlocks = new QueueIntFlip(128);  // 128  free sections
    private QueueIntFlip largeMessageBufferFreeBlocks  = new QueueIntFlip(128);   // 16   free sections

    //todo make all message buffer capacities and block sizes configurable
    //todo calculate free block queue sizes based on capacity and block size of buffers.

    public MessageBuffer()
    {
        //add all free sections to all free section queues.
        for(int i=0; i<smallMessageBuffer.length; i+= CAPACITY_SMALL)
        {
            this.smallMessageBufferFreeBlocks.put(i);
        }

        for(int i=0; i<mediumMessageBuffer.length; i+= CAPACITY_MEDIUM)
        {
            this.mediumMessageBufferFreeBlocks.put(i);
        }

        for(int i=0; i<largeMessageBuffer.length; i+= CAPACITY_LARGE)
        {
            this.largeMessageBufferFreeBlocks.put(i);
        }
    }

    public Message getMessage()
    {
        int nextFreeSmallBlock = this.smallMessageBufferFreeBlocks.take();

        if(nextFreeSmallBlock == -1) return null;

        Message message = new Message(this);       //todo get from Message pool - caps memory usage.

        message.sharedArray = this.smallMessageBuffer;
        message.capacity    = CAPACITY_SMALL;
        message.offset      = nextFreeSmallBlock;
        message.length      = 0;

        return message;
    }

    public boolean expandMessage(Message message)
    {
        if(message.capacity == CAPACITY_SMALL)
        {
            return moveMessage(message, this.smallMessageBufferFreeBlocks, this.mediumMessageBufferFreeBlocks, this.mediumMessageBuffer, CAPACITY_MEDIUM);
        }
        else if(message.capacity == CAPACITY_MEDIUM)
        {
            return moveMessage(message, this.mediumMessageBufferFreeBlocks, this.largeMessageBufferFreeBlocks, this.largeMessageBuffer, CAPACITY_LARGE);
        }
        else
        {
            return false;
        }
    }

    private boolean moveMessage(Message message, QueueIntFlip srcBlockQueue, QueueIntFlip destBlockQueue, byte[] dest, int newCapacity)
    {
        int nextFreeBlock = destBlockQueue.take();

        if(nextFreeBlock == -1)
            return false;

        System.arraycopy(message.sharedArray, message.offset, dest, nextFreeBlock, message.length);

        srcBlockQueue.put(message.offset); //free smaller block after copy

        message.sharedArray = dest;
        message.offset      = nextFreeBlock;
        message.capacity    = newCapacity;
        return true;
    }
}
