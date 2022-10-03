package com.sun.mail.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public abstract class handler_base implements DataContentHandler
{
    protected abstract ActivationDataFlavor[] getDataFlavors();
    
    protected Object getData(final ActivationDataFlavor aFlavor, final DataSource ds) throws IOException {
        return this.getContent(ds);
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        final ActivationDataFlavor[] adf = this.getDataFlavors();
        if (adf.length == 1) {
            return new DataFlavor[] { adf[0] };
        }
        final DataFlavor[] df = new DataFlavor[adf.length];
        System.arraycopy(adf, 0, df, 0, adf.length);
        return df;
    }
    
    @Override
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        final ActivationDataFlavor[] adf = this.getDataFlavors();
        for (int i = 0; i < adf.length; ++i) {
            if (adf[i].equals(df)) {
                return this.getData(adf[i], ds);
            }
        }
        return null;
    }
}
