package com.adventnet.client.components.table.web;

import java.sql.SQLException;
import java.util.logging.Logger;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Properties;

public class ExportTableViewModel extends ExportTableModel
{
    private Properties dataMaskingConfig;
    
    public ExportTableViewModel() {
        this.dataMaskingConfig = null;
    }
    
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        Object value = null;
        try {
            value = this.ds.getValue(columnIndex + 1);
            if (this.dataMaskingConfig != null && value != null && this.dataMaskingConfig.containsKey(this.getColumnName(columnIndex))) {
                return MetaDataUtil.getPiiValueHandler().getMaskedValue(value, this.dataMaskingConfig.getProperty(this.getColumnName(columnIndex)));
            }
            return super.getValueAt(rowIndex, columnIndex);
        }
        catch (final SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExportTableViewModel.class.getName()).severe("Exception occurred while getting data from the model.");
            return value;
        }
    }
    
    @Override
    public void setPIIColumnConfig(final Properties maskingConfigMap) {
        this.dataMaskingConfig = maskingConfigMap;
    }
    
    @Override
    public Properties getPIIColumnConfig() {
        return this.dataMaskingConfig;
    }
}
