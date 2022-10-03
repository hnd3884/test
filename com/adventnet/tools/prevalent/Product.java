package com.adventnet.tools.prevalent;

import java.util.ArrayList;
import java.io.Serializable;

public class Product implements Serializable
{
    private static final long serialVersionUID = 3487495895819222L;
    private int[] id;
    private int[] productLicenseType;
    private int[] productCategory;
    private ArrayList componentsVector;
    
    Product() {
        this.id = null;
        this.productLicenseType = null;
        this.productCategory = null;
        this.componentsVector = null;
        this.componentsVector = new ArrayList();
    }
    
    public void setID(final int[] id) {
        this.id = id;
    }
    
    public int[] getID() {
        return this.id;
    }
    
    void setProductLicenseType(final int[] pLType) {
        this.productLicenseType = pLType;
    }
    
    public int[] getProductLicenseType() {
        return this.productLicenseType;
    }
    
    void setProductCategory(final int[] pLCategory) {
        this.productCategory = pLCategory;
    }
    
    public int[] getProductCategory() {
        return this.productCategory;
    }
    
    void addComponent(final Component comp) {
        this.componentsVector.add(comp);
    }
    
    public ArrayList getComponents() {
        return this.componentsVector;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("\n ID:" + new String(Encode.revShiftBytes(this.id)));
        buf.append("\n Product License Type:" + new String(Encode.revShiftBytes(this.productLicenseType)));
        buf.append("\n Product License Category:" + new String(Encode.revShiftBytes(this.productCategory)));
        for (int size = this.componentsVector.size(), i = 0; i < size; ++i) {
            final Component comp = this.componentsVector.get(i);
            buf.append("\n" + comp);
        }
        return buf.toString();
    }
}
