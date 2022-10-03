package com.sun.xml.internal.ws.encoding;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;

public class DataSourceStreamingDataHandler extends StreamingDataHandler
{
    public DataSourceStreamingDataHandler(final DataSource ds) {
        super(ds);
    }
    
    @Override
    public InputStream readOnce() throws IOException {
        return this.getInputStream();
    }
    
    @Override
    public void moveTo(final File file) throws IOException {
        final InputStream in = this.getInputStream();
        final OutputStream os = new FileOutputStream(file);
        try {
            final byte[] temp = new byte[8192];
            int len;
            while ((len = in.read(temp)) != -1) {
                os.write(temp, 0, len);
            }
            in.close();
        }
        finally {
            if (os != null) {
                os.close();
            }
        }
    }
    
    @Override
    public void close() throws IOException {
    }
}
