package javax.sql.rowset.spi;

import javax.sql.RowSetWriter;
import javax.sql.RowSetReader;

class ProviderImpl extends SyncProvider
{
    private String className;
    private String vendorName;
    private String ver;
    private int index;
    
    ProviderImpl() {
        this.className = null;
        this.vendorName = null;
        this.ver = null;
    }
    
    public void setClassname(final String className) {
        this.className = className;
    }
    
    public String getClassname() {
        return this.className;
    }
    
    public void setVendor(final String vendorName) {
        this.vendorName = vendorName;
    }
    
    @Override
    public String getVendor() {
        return this.vendorName;
    }
    
    public void setVersion(final String ver) {
        this.ver = ver;
    }
    
    @Override
    public String getVersion() {
        return this.ver;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    @Override
    public int getDataSourceLock() throws SyncProviderException {
        int dataSourceLock;
        try {
            dataSourceLock = SyncFactory.getInstance(this.className).getDataSourceLock();
        }
        catch (final SyncFactoryException ex) {
            throw new SyncProviderException(ex.getMessage());
        }
        return dataSourceLock;
    }
    
    @Override
    public int getProviderGrade() {
        int providerGrade = 0;
        try {
            providerGrade = SyncFactory.getInstance(this.className).getProviderGrade();
        }
        catch (final SyncFactoryException ex) {}
        return providerGrade;
    }
    
    @Override
    public String getProviderID() {
        return this.className;
    }
    
    @Override
    public RowSetReader getRowSetReader() {
        RowSetReader rowSetReader = null;
        try {
            rowSetReader = SyncFactory.getInstance(this.className).getRowSetReader();
        }
        catch (final SyncFactoryException ex) {}
        return rowSetReader;
    }
    
    @Override
    public RowSetWriter getRowSetWriter() {
        RowSetWriter rowSetWriter = null;
        try {
            rowSetWriter = SyncFactory.getInstance(this.className).getRowSetWriter();
        }
        catch (final SyncFactoryException ex) {}
        return rowSetWriter;
    }
    
    @Override
    public void setDataSourceLock(final int dataSourceLock) throws SyncProviderException {
        try {
            SyncFactory.getInstance(this.className).setDataSourceLock(dataSourceLock);
        }
        catch (final SyncFactoryException ex) {
            throw new SyncProviderException(ex.getMessage());
        }
    }
    
    @Override
    public int supportsUpdatableView() {
        int supportsUpdatableView = 0;
        try {
            supportsUpdatableView = SyncFactory.getInstance(this.className).supportsUpdatableView();
        }
        catch (final SyncFactoryException ex) {}
        return supportsUpdatableView;
    }
}
