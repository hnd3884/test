package sun.awt.datatransfer;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class TransferableProxy implements Transferable
{
    protected final Transferable transferable;
    protected final boolean isLocal;
    
    public TransferableProxy(final Transferable transferable, final boolean isLocal) {
        this.transferable = transferable;
        this.isLocal = isLocal;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return this.transferable.getTransferDataFlavors();
    }
    
    @Override
    public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
        return this.transferable.isDataFlavorSupported(dataFlavor);
    }
    
    @Override
    public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        Object o = this.transferable.getTransferData(dataFlavor);
        if (o != null && this.isLocal && dataFlavor.isFlavorSerializedObjectType()) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ClassLoaderObjectOutputStream classLoaderObjectOutputStream = new ClassLoaderObjectOutputStream(byteArrayOutputStream);
            classLoaderObjectOutputStream.writeObject(o);
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            try {
                o = new ClassLoaderObjectInputStream(byteArrayInputStream, classLoaderObjectOutputStream.getClassLoaderMap()).readObject();
            }
            catch (final ClassNotFoundException ex) {
                throw (IOException)new IOException().initCause(ex);
            }
        }
        return o;
    }
}
