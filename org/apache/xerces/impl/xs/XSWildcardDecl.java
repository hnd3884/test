package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSWildcard;

public class XSWildcardDecl implements XSWildcard
{
    public static final String ABSENT;
    public short fType;
    public short fProcessContents;
    public String[] fNamespaceList;
    public XSObjectList fAnnotations;
    public QName[] fDisallowedNamesList;
    public boolean fDisallowedDefined;
    public boolean fDisallowedSibling;
    private String fDescription;
    
    public XSWildcardDecl() {
        this.fType = 1;
        this.fProcessContents = 1;
        this.fAnnotations = null;
        this.fDisallowedNamesList = null;
        this.fDisallowedDefined = false;
        this.fDisallowedSibling = false;
        this.fDescription = null;
    }
    
    public boolean allowNamespace(final String s) {
        if (this.fType == 1) {
            return true;
        }
        if (this.fType == 2) {
            int n = 0;
            for (int length = this.fNamespaceList.length, n2 = 0; n2 < length && n == 0; ++n2) {
                if (s == this.fNamespaceList[n2]) {
                    n = 1;
                }
            }
            if (n == 0) {
                return true;
            }
        }
        if (this.fType == 3) {
            for (int length2 = this.fNamespaceList.length, i = 0; i < length2; ++i) {
                if (s == this.fNamespaceList[i]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public final boolean allowQName(final QName qName) {
        return this.allowName(qName.uri, qName.localpart);
    }
    
    public final boolean allowName(final String s, final String s2) {
        return this.allowNamespace(s) && (this.fDisallowedNamesList == null || this.fDisallowedNamesList.length == 0 || this.isNameAllowed(s, s2));
    }
    
    private boolean isNameAllowed(final String s, final String s2) {
        for (int i = 0; i < this.fDisallowedNamesList.length; ++i) {
            if (this.fDisallowedNamesList[i].uri == s && this.fDisallowedNamesList[i].localpart == s2) {
                return false;
            }
        }
        return true;
    }
    
    public boolean weakerProcessContents(final XSWildcardDecl xsWildcardDecl) {
        return (this.fProcessContents == 3 && xsWildcardDecl.fProcessContents == 1) || (this.fProcessContents == 2 && xsWildcardDecl.fProcessContents != 2);
    }
    
    public String toString() {
        if (this.fDescription == null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("WC[");
            switch (this.fType) {
                case 1: {
                    sb.append("##any");
                    break;
                }
                case 2: {
                    sb.append("##other");
                    sb.append(':');
                }
                case 3: {
                    if (this.fNamespaceList.length == 0) {
                        break;
                    }
                    sb.append("\"");
                    if (this.fNamespaceList[0] != null) {
                        sb.append(this.fNamespaceList[0]);
                    }
                    sb.append("\"");
                    for (int i = 1; i < this.fNamespaceList.length; ++i) {
                        sb.append(",\"");
                        if (this.fNamespaceList[i] != null) {
                            sb.append(this.fNamespaceList[i]);
                        }
                        sb.append("\"");
                    }
                    break;
                }
            }
            if (this.fDisallowedNamesList != null) {
                sb.append(", notQName(");
                if (this.fDisallowedNamesList.length > 0) {
                    sb.append(this.fDisallowedNamesList[0]);
                    for (int j = 1; j < this.fDisallowedNamesList.length; ++j) {
                        sb.append(", ");
                        sb.append(this.fDisallowedNamesList[j]);
                    }
                }
                if (this.fDisallowedDefined) {
                    sb.append(", ");
                    sb.append("##defined");
                }
                if (this.fDisallowedSibling) {
                    sb.append(", ");
                    sb.append("##definedSibling");
                }
                sb.append(')');
            }
            sb.append(']');
            this.fDescription = sb.toString();
        }
        return this.fDescription;
    }
    
    public short getType() {
        return 9;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public short getConstraintType() {
        return this.fType;
    }
    
    public StringList getNsConstraintList() {
        return new StringListImpl(this.fNamespaceList, (this.fNamespaceList == null) ? 0 : this.fNamespaceList.length);
    }
    
    public short getProcessContents() {
        return this.fProcessContents;
    }
    
    public String getProcessContentsAsString() {
        switch (this.fProcessContents) {
            case 2: {
                return "skip";
            }
            case 3: {
                return "lax";
            }
            case 1: {
                return "strict";
            }
            default: {
                return "invalid value";
            }
        }
    }
    
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    static {
        ABSENT = null;
    }
}
