package com.me.ems.framework.common.api.utils;

import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.File;
import javax.ws.rs.core.StreamingOutput;

public class FileStreamingOutput implements StreamingOutput
{
    private File file;
    
    public FileStreamingOutput(final File file) {
        this.file = file;
    }
    
    public void write(final OutputStream output) throws IOException, WebApplicationException {
        try (final FileInputStream input = new FileInputStream(this.file)) {
            int bytes;
            while ((bytes = input.read()) != -1) {
                output.write(bytes);
            }
        }
        catch (final Exception e) {
            throw new WebApplicationException((Throwable)e);
        }
        finally {
            if (output != null) {
                output.close();
            }
        }
    }
}
