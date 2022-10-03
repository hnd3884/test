package javax.activation;

import java.io.OutputStream;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.awt.datatransfer.DataFlavor;

class DataSourceDataContentHandler implements DataContentHandler
{
    private DataSource ds;
    private DataFlavor[] transferFlavors;
    private DataContentHandler dch;
    
    public DataSourceDataContentHandler(final DataContentHandler dch, final DataSource ds) {
        this.ds = null;
        this.transferFlavors = null;
        this.dch = null;
        this.ds = ds;
        this.dch = dch;
    }
    
    public Object getContent(final DataSource dataSource) throws IOException {
        if (this.dch != null) {
            return this.dch.getContent(dataSource);
        }
        return dataSource.getInputStream();
    }
    
    public Object getTransferData(final DataFlavor dataFlavor, final DataSource dataSource) throws UnsupportedFlavorException, IOException {
        if (this.dch != null) {
            return this.dch.getTransferData(dataFlavor, dataSource);
        }
        if (dataFlavor.equals(this.getTransferDataFlavors()[0])) {
            return dataSource.getInputStream();
        }
        throw new UnsupportedFlavorException(dataFlavor);
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        if (this.transferFlavors == null) {
            if (this.dch != null) {
                this.transferFlavors = this.dch.getTransferDataFlavors();
            }
            else {
                (this.transferFlavors = new DataFlavor[1])[0] = new ActivationDataFlavor(this.ds.getContentType(), this.ds.getContentType());
            }
        }
        return this.transferFlavors;
    }
    
    public void writeTo(final Object o, final String s, final OutputStream outputStream) throws IOException {
        if (this.dch != null) {
            this.dch.writeTo(o, s, outputStream);
            return;
        }
        throw new UnsupportedDataTypeException("no DCH for content type " + this.ds.getContentType());
    }
}
