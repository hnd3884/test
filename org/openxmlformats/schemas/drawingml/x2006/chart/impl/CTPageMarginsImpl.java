package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageMargins;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPageMarginsImpl extends XmlComplexContentImpl implements CTPageMargins
{
    private static final long serialVersionUID = 1L;
    private static final QName L$0;
    private static final QName R$2;
    private static final QName T$4;
    private static final QName B$6;
    private static final QName HEADER$8;
    private static final QName FOOTER$10;
    
    public CTPageMarginsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public double getL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.L$0);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.L$0);
        }
    }
    
    public void setL(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.L$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.L$0);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetL(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.L$0);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.L$0);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.R$2);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.R$2);
        }
    }
    
    public void setR(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.R$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.R$2);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetR(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.R$2);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.R$2);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.T$4);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.T$4);
        }
    }
    
    public void setT(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.T$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.T$4);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetT(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.T$4);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.T$4);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.B$6);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.B$6);
        }
    }
    
    public void setB(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.B$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.B$6);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetB(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.B$6);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.B$6);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
        }
    }
    
    public void setHeader(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.HEADER$8);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetHeader(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.HEADER$8);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
        }
    }
    
    public void setFooter(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.FOOTER$10);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetFooter(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.FOOTER$10);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    static {
        L$0 = new QName("", "l");
        R$2 = new QName("", "r");
        T$4 = new QName("", "t");
        B$6 = new QName("", "b");
        HEADER$8 = new QName("", "header");
        FOOTER$10 = new QName("", "footer");
    }
}
