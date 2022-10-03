package com.microsoft.sqlserver.jdbc.dataclassification;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class SensitivityClassification
{
    private List<Label> labels;
    private List<InformationType> informationTypes;
    private List<ColumnSensitivity> columnSensitivities;
    
    public SensitivityClassification(final List<Label> labels, final List<InformationType> informationTypes, final List<ColumnSensitivity> columnSensitivity) {
        this.labels = new ArrayList<Label>(labels);
        this.informationTypes = new ArrayList<InformationType>(informationTypes);
        this.columnSensitivities = new ArrayList<ColumnSensitivity>(columnSensitivity);
    }
    
    public List<Label> getLabels() {
        return this.labels;
    }
    
    public List<InformationType> getInformationTypes() {
        return this.informationTypes;
    }
    
    public List<ColumnSensitivity> getColumnSensitivities() {
        return this.columnSensitivities;
    }
}
