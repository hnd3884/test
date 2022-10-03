package com.microsoft.sqlserver.jdbc.dataclassification;

public class SensitivityProperty
{
    private Label label;
    private InformationType informationType;
    
    public SensitivityProperty(final Label label, final InformationType informationType) {
        this.label = label;
        this.informationType = informationType;
    }
    
    public Label getLabel() {
        return this.label;
    }
    
    public InformationType getInformationType() {
        return this.informationType;
    }
}
