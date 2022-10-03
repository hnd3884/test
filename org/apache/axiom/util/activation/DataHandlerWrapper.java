package org.apache.axiom.util.activation;

import javax.activation.CommandMap;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.activation.CommandInfo;
import javax.activation.DataSource;
import javax.activation.DataHandler;

public class DataHandlerWrapper extends DataHandler
{
    private final DataHandler parent;
    
    public DataHandlerWrapper(final DataHandler parent) {
        super(EmptyDataSource.INSTANCE);
        this.parent = parent;
    }
    
    @Override
    public CommandInfo[] getAllCommands() {
        return this.parent.getAllCommands();
    }
    
    @Override
    public Object getBean(final CommandInfo cmdinfo) {
        return this.parent.getBean(cmdinfo);
    }
    
    @Override
    public CommandInfo getCommand(final String cmdName) {
        return this.parent.getCommand(cmdName);
    }
    
    @Override
    public Object getContent() throws IOException {
        return this.parent.getContent();
    }
    
    @Override
    public String getContentType() {
        return this.parent.getContentType();
    }
    
    @Override
    public DataSource getDataSource() {
        return this.parent.getDataSource();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this.parent.getInputStream();
    }
    
    @Override
    public String getName() {
        return this.parent.getName();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.parent.getOutputStream();
    }
    
    @Override
    public CommandInfo[] getPreferredCommands() {
        return this.parent.getPreferredCommands();
    }
    
    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this.parent.getTransferData(flavor);
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return this.parent.getTransferDataFlavors();
    }
    
    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return this.parent.isDataFlavorSupported(flavor);
    }
    
    @Override
    public void setCommandMap(final CommandMap commandMap) {
        this.parent.setCommandMap(commandMap);
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException {
        this.parent.writeTo(os);
    }
}
