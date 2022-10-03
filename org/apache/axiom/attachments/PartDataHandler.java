package org.apache.axiom.attachments;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.DataSource;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import javax.activation.DataHandler;

class PartDataHandler extends DataHandler implements DataHandlerExt
{
    private final PartImpl part;
    private DataSource dataSource;
    
    public PartDataHandler(final PartImpl part) {
        super(new PartDataSource(part));
        this.part = part;
    }
    
    @Override
    public DataSource getDataSource() {
        if (this.dataSource == null) {
            this.dataSource = this.part.getDataSource();
            if (this.dataSource == null) {
                this.dataSource = super.getDataSource();
            }
        }
        return this.dataSource;
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException {
        this.part.writeTo(os);
    }
    
    public InputStream readOnce() throws IOException {
        return this.part.getInputStream(false);
    }
    
    public void purgeDataSource() throws IOException {
        this.part.releaseContent();
    }
    
    public void deleteWhenReadOnce() throws IOException {
        this.purgeDataSource();
    }
}
