package http;


import server.IMessageReader;
import server.IMessageReaderFactory;

/**
 * Created by jjenkov on 18-10-2015.
 */
public class HttpMessageReaderFactory implements IMessageReaderFactory {

    public HttpMessageReaderFactory() {
    }

    public IMessageReader createMessageReader() {
        return new HttpMessageReader();
    }
}
