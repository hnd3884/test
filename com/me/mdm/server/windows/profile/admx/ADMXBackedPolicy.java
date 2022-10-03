package com.me.mdm.server.windows.profile.admx;

import org.apache.axiom.om.OMElement;
import java.util.Iterator;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAbstractFactory;
import java.util.HashMap;

public class ADMXBackedPolicy
{
    private String gpName;
    private String locURI;
    private HashMap<String, String> data;
    private boolean isEnabled;
    
    public ADMXBackedPolicy(final String gpName, final String locUri, final HashMap<String, String> data, final boolean isEnabled) {
        this.gpName = gpName;
        this.isEnabled = isEnabled;
        this.data = data;
        this.locURI = locUri;
    }
    
    public ADMXBackedPolicy(final String gpName, final boolean isEnabled) {
        this.gpName = gpName;
        this.isEnabled = isEnabled;
        this.data = new HashMap<String, String>();
    }
    
    public ADMXBackedPolicy(final String gpName, final String locURI, final boolean isEnabled) {
        this.gpName = gpName;
        this.isEnabled = isEnabled;
        this.data = new HashMap<String, String>();
        this.locURI = locURI;
    }
    
    public String getGpName() {
        return this.gpName;
    }
    
    public void setGpName(final String gpName) {
        this.gpName = gpName;
    }
    
    public HashMap<String, String> getData() {
        return this.data;
    }
    
    public void setData(final HashMap<String, String> data) {
        this.data = data;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.isEnabled = enabled;
    }
    
    public void addData(final String id, final String value) {
        this.data.put(id, value);
    }
    
    public String getLocURI() {
        return this.locURI;
    }
    
    public void setLocURI(final String locURI) {
        this.locURI = locURI;
    }
    
    @Override
    public String toString() {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final StringBuilder stringBuilder = new StringBuilder();
        if (this.isEnabled) {
            stringBuilder.append(omfac.createOMElement(new QName("enabled")).toString());
        }
        else {
            stringBuilder.append(omfac.createOMElement(new QName("disabled")).toString());
        }
        for (final String key : this.data.keySet()) {
            final OMElement dataElement = omfac.createOMElement(new QName("data"));
            dataElement.addAttribute("id", key, (OMNamespace)null);
            dataElement.addAttribute("value", (String)this.data.get(key), (OMNamespace)null);
            stringBuilder.append(dataElement.toString());
        }
        final String xmlString = stringBuilder.toString();
        return xmlString.replaceAll("><\\/.*?>", "/>");
    }
}
