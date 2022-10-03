package org.apache.xmlbeans.impl.inst2xsd;

public class Inst2XsdOptions
{
    public static final int DESIGN_RUSSIAN_DOLL = 1;
    public static final int DESIGN_SALAMI_SLICE = 2;
    public static final int DESIGN_VENETIAN_BLIND = 3;
    private int _design;
    public static final int SIMPLE_CONTENT_TYPES_SMART = 1;
    public static final int SIMPLE_CONTENT_TYPES_STRING = 2;
    private int _simpleContentTypes;
    public static final int ENUMERATION_NEVER = 1;
    public static final int ENUMERATION_NOT_MORE_THAN_DEFAULT = 10;
    private int _enumerations;
    private boolean _verbose;
    
    public Inst2XsdOptions() {
        this._design = 3;
        this._simpleContentTypes = 1;
        this._enumerations = 10;
        this._verbose = false;
    }
    
    public int getDesign() {
        return this._design;
    }
    
    public void setDesign(final int designType) {
        if (designType != 1 && designType != 2 && designType != 3) {
            throw new IllegalArgumentException("Unknown value for design type.");
        }
        this._design = designType;
    }
    
    public boolean isUseEnumerations() {
        return this._enumerations > 1;
    }
    
    public int getUseEnumerations() {
        return this._enumerations;
    }
    
    public void setUseEnumerations(final int useEnumerations) {
        if (useEnumerations < 1) {
            throw new IllegalArgumentException("UseEnumerations must be set to a value bigger than 1");
        }
        this._enumerations = useEnumerations;
    }
    
    public int getSimpleContentTypes() {
        return this._simpleContentTypes;
    }
    
    public void setSimpleContentTypes(final int simpleContentTypes) {
        if (simpleContentTypes != 1 && simpleContentTypes != 2) {
            throw new IllegalArgumentException("Unknown value for simpleContentTypes.");
        }
        this._simpleContentTypes = simpleContentTypes;
    }
    
    public boolean isVerbose() {
        return this._verbose;
    }
    
    public void setVerbose(final boolean verbose) {
        this._verbose = verbose;
    }
}
