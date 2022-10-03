package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import java.util.AbstractList;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.TransformType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TransformTypeImpl extends XmlComplexContentImpl implements TransformType
{
    private static final long serialVersionUID = 1L;
    private static final QName XPATH$0;
    private static final QName ALGORITHM$2;
    
    public TransformTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<String> getXPathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class XPathList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return TransformTypeImpl.this.getXPathArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String xPathArray = TransformTypeImpl.this.getXPathArray(n);
                    TransformTypeImpl.this.setXPathArray(n, s);
                    return xPathArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    TransformTypeImpl.this.insertXPath(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String xPathArray = TransformTypeImpl.this.getXPathArray(n);
                    TransformTypeImpl.this.removeXPath(n);
                    return xPathArray;
                }
                
                @Override
                public int size() {
                    return TransformTypeImpl.this.sizeOfXPathArray();
                }
            }
            return new XPathList();
        }
    }
    
    @Deprecated
    public String[] getXPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(TransformTypeImpl.XPATH$0, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getXPathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(TransformTypeImpl.XPATH$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<XmlString> xgetXPathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class XPathList extends AbstractList<XmlString>
            {
                @Override
                public XmlString get(final int n) {
                    return TransformTypeImpl.this.xgetXPathArray(n);
                }
                
                @Override
                public XmlString set(final int n, final XmlString xmlString) {
                    final XmlString xgetXPathArray = TransformTypeImpl.this.xgetXPathArray(n);
                    TransformTypeImpl.this.xsetXPathArray(n, xmlString);
                    return xgetXPathArray;
                }
                
                @Override
                public void add(final int n, final XmlString xmlString) {
                    TransformTypeImpl.this.insertNewXPath(n).set((XmlObject)xmlString);
                }
                
                @Override
                public XmlString remove(final int n) {
                    final XmlString xgetXPathArray = TransformTypeImpl.this.xgetXPathArray(n);
                    TransformTypeImpl.this.removeXPath(n);
                    return xgetXPathArray;
                }
                
                @Override
                public int size() {
                    return TransformTypeImpl.this.sizeOfXPathArray();
                }
            }
            return new XPathList();
        }
    }
    
    @Deprecated
    public XmlString[] xgetXPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(TransformTypeImpl.XPATH$0, (List)list);
            final XmlString[] array = new XmlString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XmlString xgetXPathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString = (XmlString)this.get_store().find_element_user(TransformTypeImpl.XPATH$0, n);
            if (xmlString == null) {
                throw new IndexOutOfBoundsException();
            }
            return xmlString;
        }
    }
    
    public int sizeOfXPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(TransformTypeImpl.XPATH$0);
        }
    }
    
    public void setXPathArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, TransformTypeImpl.XPATH$0);
        }
    }
    
    public void setXPathArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(TransformTypeImpl.XPATH$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetXPathArray(final XmlString[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, TransformTypeImpl.XPATH$0);
        }
    }
    
    public void xsetXPathArray(final int n, final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XmlString xmlString2 = (XmlString)this.get_store().find_element_user(TransformTypeImpl.XPATH$0, n);
            if (xmlString2 == null) {
                throw new IndexOutOfBoundsException();
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void insertXPath(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(TransformTypeImpl.XPATH$0, n)).setStringValue(stringValue);
        }
    }
    
    public void addXPath(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(TransformTypeImpl.XPATH$0)).setStringValue(stringValue);
        }
    }
    
    public XmlString insertNewXPath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().insert_element_user(TransformTypeImpl.XPATH$0, n);
        }
    }
    
    public XmlString addNewXPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().add_element_user(TransformTypeImpl.XPATH$0);
        }
    }
    
    public void removeXPath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(TransformTypeImpl.XPATH$0, n);
        }
    }
    
    public String getAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(TransformTypeImpl.ALGORITHM$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(TransformTypeImpl.ALGORITHM$2);
        }
    }
    
    public void setAlgorithm(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(TransformTypeImpl.ALGORITHM$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(TransformTypeImpl.ALGORITHM$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlgorithm(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(TransformTypeImpl.ALGORITHM$2);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(TransformTypeImpl.ALGORITHM$2);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    static {
        XPATH$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "XPath");
        ALGORITHM$2 = new QName("", "Algorithm");
    }
}
