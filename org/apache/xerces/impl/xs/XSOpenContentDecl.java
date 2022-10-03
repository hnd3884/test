package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.XSWildcard;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSOpenContent;

public class XSOpenContentDecl implements XSOpenContent
{
    public short fMode;
    public boolean fAppliesToEmpty;
    public XSWildcardDecl fWildcard;
    private String fDescription;
    
    public XSOpenContentDecl() {
        this.fMode = 0;
        this.fAppliesToEmpty = false;
        this.fWildcard = null;
        this.fDescription = null;
    }
    
    public String toString() {
        if (this.fDescription == null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("OC[mode=");
            if (this.fMode == 0) {
                sb.append("none,");
            }
            else if (this.fMode == 1) {
                sb.append("interleave,");
            }
            else {
                sb.append("suffix,");
            }
            sb.append(this.fWildcard.toString());
            sb.append("]");
            this.fDescription = sb.toString();
        }
        return this.fDescription;
    }
    
    public short getType() {
        return 18;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    public short getModeType() {
        return this.fMode;
    }
    
    public XSWildcard getWildcard() {
        return this.fWildcard;
    }
    
    public boolean appliesToEmpty() {
        return this.fAppliesToEmpty;
    }
}
