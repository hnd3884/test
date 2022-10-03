package org.apache.xmlbeans.impl.schema;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.QNameSet;
import java.util.Map;
import org.apache.xmlbeans.SchemaAttributeModel;

public class SchemaAttributeModelImpl implements SchemaAttributeModel
{
    private Map attrMap;
    private QNameSet wcSet;
    private int wcProcess;
    private static final SchemaLocalAttribute[] EMPTY_SLA_ARRAY;
    
    public SchemaAttributeModelImpl() {
        this.attrMap = new LinkedHashMap();
        this.wcSet = null;
        this.wcProcess = 0;
    }
    
    public SchemaAttributeModelImpl(final SchemaAttributeModel sam) {
        this.attrMap = new LinkedHashMap();
        if (sam == null) {
            this.wcSet = null;
            this.wcProcess = 0;
        }
        else {
            final SchemaLocalAttribute[] attrs = sam.getAttributes();
            for (int i = 0; i < attrs.length; ++i) {
                this.attrMap.put(attrs[i].getName(), attrs[i]);
            }
            if (sam.getWildcardProcess() != 0) {
                this.wcSet = sam.getWildcardSet();
                this.wcProcess = sam.getWildcardProcess();
            }
        }
    }
    
    @Override
    public SchemaLocalAttribute[] getAttributes() {
        return (SchemaLocalAttribute[])this.attrMap.values().toArray(SchemaAttributeModelImpl.EMPTY_SLA_ARRAY);
    }
    
    @Override
    public SchemaLocalAttribute getAttribute(final QName name) {
        return this.attrMap.get(name);
    }
    
    public void addAttribute(final SchemaLocalAttribute attruse) {
        this.attrMap.put(attruse.getName(), attruse);
    }
    
    public void removeProhibitedAttribute(final QName name) {
        this.attrMap.remove(name);
    }
    
    @Override
    public QNameSet getWildcardSet() {
        return (this.wcSet == null) ? QNameSet.EMPTY : this.wcSet;
    }
    
    public void setWildcardSet(final QNameSet set) {
        this.wcSet = set;
    }
    
    @Override
    public int getWildcardProcess() {
        return this.wcProcess;
    }
    
    public void setWildcardProcess(final int proc) {
        this.wcProcess = proc;
    }
    
    static {
        EMPTY_SLA_ARRAY = new SchemaLocalAttribute[0];
    }
}
