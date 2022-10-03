package com.adventnet.persistence.migration;

import java.util.Properties;
import java.io.File;

public class DBParamsChanges
{
    private String dbName;
    private File productOldFile;
    private File productNewFile;
    private File customerFile;
    private Properties mergedProps;
    private boolean propsChangePreference;
    
    public String getDBName() {
        return this.dbName;
    }
    
    public void setDBName(final String dbName) {
        this.dbName = dbName;
    }
    
    public File getProductOldFile() {
        return this.productOldFile;
    }
    
    public void setProductOldFile(final File productOldFile) {
        this.productOldFile = productOldFile;
    }
    
    public File getProductNewFile() {
        return this.productNewFile;
    }
    
    public void setProductNewFile(final File productNewFile) {
        this.productNewFile = productNewFile;
    }
    
    public File getCustomerFile() {
        return this.customerFile;
    }
    
    public void setCustomerFile(final File customerFile) {
        this.customerFile = customerFile;
    }
    
    public Properties getMergedProps() {
        return this.mergedProps;
    }
    
    public void setMergedProps(final Properties mergedProps) {
        this.mergedProps = mergedProps;
    }
    
    public boolean getPropsChangePreference() {
        return this.propsChangePreference;
    }
    
    public void setPropsChangePreference(final boolean propsChangePreference) {
        this.propsChangePreference = propsChangePreference;
    }
}
