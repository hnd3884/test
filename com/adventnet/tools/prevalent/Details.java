package com.adventnet.tools.prevalent;

import java.util.Properties;
import java.util.ArrayList;

public class Details
{
    private String id;
    private String product;
    private String version;
    private String productLicenseType;
    private String productCategory;
    private ArrayList componentsVector;
    private ArrayList nsCompVector;
    
    public Details() {
        this.id = null;
        this.product = null;
        this.productLicenseType = null;
        this.productCategory = null;
        this.componentsVector = null;
        this.nsCompVector = null;
        this.componentsVector = new ArrayList();
        this.nsCompVector = new ArrayList();
    }
    
    public void setID(final String id) {
        this.id = id;
    }
    
    public String getID() {
        return this.id;
    }
    
    public void setProductName(final String pName) {
        this.product = pName;
    }
    
    public String getProductName() {
        return this.product;
    }
    
    public void setProductVersion(final String pVersion) {
        this.version = pVersion;
    }
    
    public String getProductVersion() {
        return this.version;
    }
    
    public void setProductLicenseType(final String pLType) {
        this.productLicenseType = pLType;
    }
    
    public String getProductLicenseType() {
        return this.productLicenseType;
    }
    
    public void setProductCategory(final String pLCategory) {
        this.productCategory = pLCategory;
    }
    
    public String getProductCategory() {
        return this.productCategory;
    }
    
    public void addComponent(final Component comp) {
        this.componentsVector.add(comp);
    }
    
    public ArrayList getComponents() {
        return this.componentsVector;
    }
    
    public boolean isComponentPresent(final String componentName) {
        if (this.componentsVector != null) {
            for (int size = this.componentsVector.size(), i = 0; i < size; ++i) {
                final Component comp = this.componentsVector.get(i);
                final String name = comp.getName();
                if (name.equals(componentName)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public Properties getComponentProperties(final String componentName) {
        if (this.componentsVector != null) {
            for (int size = this.componentsVector.size(), i = 0; i < size; ++i) {
                final Component comp = this.componentsVector.get(i);
                final String name = comp.getName();
                if (name.equals(componentName)) {
                    final Properties prop = new Properties();
                    final ArrayList list = comp.getProperties();
                    for (int count = list.size(), j = 0; j < count; j += 2) {
                        final Object key = list.get(j);
                        final Object value = list.get(j + 1);
                        prop.put(key, value);
                    }
                    return prop;
                }
            }
            return null;
        }
        return null;
    }
    
    public void addNSComponent(final NSComponent nsComp) {
        this.nsCompVector.add(nsComp);
    }
    
    public ArrayList getNSComponents() {
        return this.nsCompVector;
    }
}
