package javax.activation;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

class DataHandlerDataSource implements DataSource
{
    DataHandler dataHandler;
    
    public DataHandlerDataSource(final DataHandler dataHandler) {
        this.dataHandler = null;
        this.dataHandler = dataHandler;
    }
    
    public String getContentType() {
        return this.dataHandler.getContentType();
    }
    
    public InputStream getInputStream() throws IOException {
        return this.dataHandler.getInputStream();
    }
    
    public String getName() {
        return this.dataHandler.getName();
    }
    
    public OutputStream getOutputStream() throws IOException {
        return this.dataHandler.getOutputStream();
    }
}
