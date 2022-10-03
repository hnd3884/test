package com.me.tools.zcutil;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.Properties;

public class ProductConf
{
    private boolean load;
    private boolean license;
    private boolean actionLog;
    private boolean loadQueryFormProduct;
    private boolean licenseQueryFromProduct;
    private String[] excludeArr;
    private Properties licenseQuery;
    private Properties loadQuery;
    private Element loadElement;
    private Element formsElement;
    private Element rootElement;
    private Properties formKeyProp;
    
    public ProductConf() {
        this.load = true;
        this.license = true;
        this.actionLog = true;
        this.loadQueryFormProduct = false;
        this.licenseQueryFromProduct = false;
        this.excludeArr = null;
        this.licenseQuery = null;
        this.loadQuery = null;
        this.loadElement = null;
        this.formsElement = null;
        this.rootElement = null;
        this.formKeyProp = null;
    }
    
    public void setRootElement(final Element rootElement) {
        this.rootElement = rootElement;
    }
    
    public void setLoadFormAccess(final boolean val) {
        this.load = val;
    }
    
    public void setLicenseFormAccess(final boolean val) {
        this.license = val;
    }
    
    public void setActionLogFormAccess(final boolean val) {
        this.actionLog = val;
    }
    
    public void setBaseFormExcludeFields(final String[] arr) {
        this.excludeArr = arr;
    }
    
    public void setLicenseQuery(final Properties prop) {
        this.licenseQuery = prop;
    }
    
    public void setLoadQuery(final Properties prop) {
        this.loadQuery = prop;
    }
    
    public void setLoadQueryFromProduct(final boolean val) {
        this.loadQueryFormProduct = val;
    }
    
    public void setLicenseQueryFromProduct(final boolean val) {
        this.licenseQueryFromProduct = val;
    }
    
    public boolean isLoadFormEnabled() {
        return this.load;
    }
    
    public boolean isLicenseFormEnabled() {
        return this.license;
    }
    
    public boolean isActionLogFormEnabled() {
        return this.actionLog;
    }
    
    public String[] getBaseFormExcludeFileds() {
        return this.excludeArr;
    }
    
    public Properties getLicenseQuery() {
        return this.licenseQuery;
    }
    
    public Properties getLoadQuery() {
        return this.loadQuery;
    }
    
    public boolean isLoadQueryFromProduct() {
        return this.loadQueryFormProduct;
    }
    
    public boolean isLicenseQueryFromProduct() {
        return this.licenseQueryFromProduct;
    }
    
    public void setLoadElement(final Element loadElement) {
        this.loadElement = loadElement;
    }
    
    public Element getLoadElement() {
        return this.loadElement;
    }
    
    public void setLicenseElement(final Element loadElement) {
        this.loadElement = loadElement;
    }
    
    public Element getLicenseElement() {
        return this.loadElement;
    }
    
    public Element getRootElement() {
        return this.rootElement;
    }
    
    public void setFormsKeys() {
        try {
            final NodeList nl = this.rootElement.getElementsByTagName("forms");
            if (nl != null && nl.getLength() > 0) {
                final Element privateKeyEle = (Element)nl.item(0);
                this.formKeyProp = ConfFileReader.getPropertyValues(privateKeyEle, "form", "name", "value");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getFormKey(final String formName) {
        if (this.formKeyProp != null) {
            return this.formKeyProp.getProperty(formName, null);
        }
        return null;
    }
}
