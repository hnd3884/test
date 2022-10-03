package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTwipsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTControl;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;

public class CTObjectImpl extends CTPictureBaseImpl implements CTObject
{
    private static final long serialVersionUID = 1L;
    private static final QName CONTROL$0;
    private static final QName DXAORIG$2;
    private static final QName DYAORIG$4;
    
    public CTObjectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTControl getControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTControl ctControl = (CTControl)this.get_store().find_element_user(CTObjectImpl.CONTROL$0, 0);
            if (ctControl == null) {
                return null;
            }
            return ctControl;
        }
    }
    
    @Override
    public boolean isSetControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTObjectImpl.CONTROL$0) != 0;
        }
    }
    
    @Override
    public void setControl(final CTControl ctControl) {
        this.generatedSetterHelperImpl((XmlObject)ctControl, CTObjectImpl.CONTROL$0, 0, (short)1);
    }
    
    @Override
    public CTControl addNewControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTControl)this.get_store().add_element_user(CTObjectImpl.CONTROL$0);
        }
    }
    
    @Override
    public void unsetControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTObjectImpl.CONTROL$0, 0);
        }
    }
    
    @Override
    public BigInteger getDxaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTObjectImpl.DXAORIG$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    @Override
    public STTwipsMeasure xgetDxaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTObjectImpl.DXAORIG$2);
        }
    }
    
    @Override
    public boolean isSetDxaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTObjectImpl.DXAORIG$2) != null;
        }
    }
    
    @Override
    public void setDxaOrig(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTObjectImpl.DXAORIG$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTObjectImpl.DXAORIG$2);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    @Override
    public void xsetDxaOrig(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTObjectImpl.DXAORIG$2);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTObjectImpl.DXAORIG$2);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    @Override
    public void unsetDxaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTObjectImpl.DXAORIG$2);
        }
    }
    
    @Override
    public BigInteger getDyaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTObjectImpl.DYAORIG$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    @Override
    public STTwipsMeasure xgetDyaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTObjectImpl.DYAORIG$4);
        }
    }
    
    @Override
    public boolean isSetDyaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTObjectImpl.DYAORIG$4) != null;
        }
    }
    
    @Override
    public void setDyaOrig(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTObjectImpl.DYAORIG$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTObjectImpl.DYAORIG$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    @Override
    public void xsetDyaOrig(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTObjectImpl.DYAORIG$4);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTObjectImpl.DYAORIG$4);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    @Override
    public void unsetDyaOrig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTObjectImpl.DYAORIG$4);
        }
    }
    
    static {
        CONTROL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "control");
        DXAORIG$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dxaOrig");
        DYAORIG$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dyaOrig");
    }
}
