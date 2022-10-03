package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.util.Collection;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;

public class XMLDTDDescription extends XMLResourceIdentifierImpl implements com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription
{
    protected String fRootName;
    protected ArrayList fPossibleRoots;
    
    public XMLDTDDescription(final XMLResourceIdentifier id, final String rootName) {
        this.fRootName = null;
        this.fPossibleRoots = null;
        this.setValues(id.getPublicId(), id.getLiteralSystemId(), id.getBaseSystemId(), id.getExpandedSystemId());
        this.fRootName = rootName;
        this.fPossibleRoots = null;
    }
    
    public XMLDTDDescription(final String publicId, final String literalId, final String baseId, final String expandedId, final String rootName) {
        this.fRootName = null;
        this.fPossibleRoots = null;
        this.setValues(publicId, literalId, baseId, expandedId);
        this.fRootName = rootName;
        this.fPossibleRoots = null;
    }
    
    public XMLDTDDescription(final XMLInputSource source) {
        this.fRootName = null;
        this.fPossibleRoots = null;
        this.setValues(source.getPublicId(), null, source.getBaseSystemId(), source.getSystemId());
        this.fRootName = null;
        this.fPossibleRoots = null;
    }
    
    @Override
    public String getGrammarType() {
        return "http://www.w3.org/TR/REC-xml";
    }
    
    @Override
    public String getRootName() {
        return this.fRootName;
    }
    
    public void setRootName(final String rootName) {
        this.fRootName = rootName;
        this.fPossibleRoots = null;
    }
    
    public void setPossibleRoots(final ArrayList possibleRoots) {
        this.fPossibleRoots = possibleRoots;
    }
    
    public void setPossibleRoots(final Vector possibleRoots) {
        this.fPossibleRoots = ((possibleRoots != null) ? new ArrayList(possibleRoots) : null);
    }
    
    @Override
    public boolean equals(final Object desc) {
        if (!(desc instanceof XMLGrammarDescription)) {
            return false;
        }
        if (!this.getGrammarType().equals(((XMLGrammarDescription)desc).getGrammarType())) {
            return false;
        }
        final XMLDTDDescription dtdDesc = (XMLDTDDescription)desc;
        if (this.fRootName != null) {
            if (dtdDesc.fRootName != null && !dtdDesc.fRootName.equals(this.fRootName)) {
                return false;
            }
            if (dtdDesc.fPossibleRoots != null && !dtdDesc.fPossibleRoots.contains(this.fRootName)) {
                return false;
            }
        }
        else if (this.fPossibleRoots != null) {
            if (dtdDesc.fRootName != null) {
                if (!this.fPossibleRoots.contains(dtdDesc.fRootName)) {
                    return false;
                }
            }
            else {
                if (dtdDesc.fPossibleRoots == null) {
                    return false;
                }
                boolean found = false;
                for (int size = this.fPossibleRoots.size(), i = 0; i < size; ++i) {
                    final String root = this.fPossibleRoots.get(i);
                    found = dtdDesc.fPossibleRoots.contains(root);
                    if (found) {
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        if (this.fExpandedSystemId != null) {
            if (!this.fExpandedSystemId.equals(dtdDesc.fExpandedSystemId)) {
                return false;
            }
        }
        else if (dtdDesc.fExpandedSystemId != null) {
            return false;
        }
        if (this.fPublicId != null) {
            if (!this.fPublicId.equals(dtdDesc.fPublicId)) {
                return false;
            }
        }
        else if (dtdDesc.fPublicId != null) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.fExpandedSystemId != null) {
            return this.fExpandedSystemId.hashCode();
        }
        if (this.fPublicId != null) {
            return this.fPublicId.hashCode();
        }
        return 0;
    }
}
