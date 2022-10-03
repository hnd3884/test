package com.adventnet.customview.table;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.customview.service.ServiceConfiguration;

public class TableModelServiceConfiguration implements ServiceConfiguration
{
    private static final String CLASS_NAME;
    private static Logger OUT;
    private String serviceName;
    private String baseResourceTableName;
    
    public TableModelServiceConfiguration() {
        this.serviceName = "TABLEMODEL_SERVICE";
        this.baseResourceTableName = null;
    }
    
    @Override
    public String getServiceName() {
        return this.serviceName;
    }
    
    public String getBaseTableName() {
        return this.baseResourceTableName;
    }
    
    public void setBaseTableName(final String baseResourceTableName) {
        this.baseResourceTableName = baseResourceTableName;
        TableModelServiceConfiguration.OUT.log(Level.FINER, " BaseResourceTableName set as : {0}", baseResourceTableName);
    }
    
    static {
        CLASS_NAME = TableModelServiceConfiguration.class.getName();
        TableModelServiceConfiguration.OUT = Logger.getLogger(TableModelServiceConfiguration.CLASS_NAME);
    }
}
