package server;

/**
 * Same as QueueFillCount, except that QueueFlip uses a flip flag to keep track of when the internal writePos has
 * "overflowed" (meaning it goes back to 0). Other than that, the two implementations are very similar in functionality.
 *
 * One additional difference is that QueueFlip has an available() method, where this is a public variable in
 * QueueFillCount.
 *
 * Created by jjenkov on 18-09-2015.
 */
public class QueueIntFlip {

    public int[] elements = null;

    public int capacity = 0;
    public int writePos = 0;
    public int readPos  = 0;
    public boolean flipped = false;

    public QueueIntFlip(int capacity)
    {
        this.capacity = capacity;
        this.elements = new int[capacity]; //todo get from TypeAllocator ?
    }

    public void reset()
    {
        this.writePos = 0;
        this.readPos  = 0;
        this.flipped  = false;
    }

    public int available()
    {
        if(!flipped)
        {
            return writePos - readPos;
        }

        return capacity - readPos + writePos;
    }

    public int remainingCapacity()
    {
        if(!flipped)
        {
            return capacity - writePos;
        }
        return readPos - writePos;
    }

    public boolean put(int element)
    {
        if(!flipped)
        {
            if(writePos == capacity)
            {
                writePos = 0;
                flipped = true;

                if(writePos < readPos)
                {
                    elements[writePos++] = element;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                elements[writePos++] = element;
                return true;
            }
        }
        else
        {
            if(writePos < readPos )
            {
                elements[writePos++] = element;
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public int take()
    {
        if(!flipped)
        {
            if(readPos < writePos)
            {
                return elements[readPos++];
            }
            else
            {
                return -1;
            }
        }
        else
        {
            if(readPos == capacity)
            {
                readPos = 0;
                flipped = false;

                if(readPos < writePos)
                {
                    return elements[readPos++];
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                return elements[readPos++];
            }
        }
    }
}
