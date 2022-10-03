package com.microsoft.sqlserver.jdbc.dataclassification;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class ColumnSensitivity
{
    private List<SensitivityProperty> sensitivityProperties;
    
    public ColumnSensitivity(final List<SensitivityProperty> sensitivityProperties) {
        this.sensitivityProperties = new ArrayList<SensitivityProperty>(sensitivityProperties);
    }
    
    public List<SensitivityProperty> getSensitivityProperties() {
        return this.sensitivityProperties;
    }
}
