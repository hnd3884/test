package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.xs.XSElementDecl;
import java.util.Vector;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.xni.QName;

public interface XSCMValidator
{
    public static final short FIRST_ERROR = -1;
    public static final short SUBSEQUENT_ERROR = -2;
    
    int[] startContentModel();
    
    Object oneTransition(final QName p0, final int[] p1, final SubstitutionGroupHandler p2, final XSElementDeclHelper p3);
    
    boolean endContentModel(final int[] p0);
    
    boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler p0, final XSConstraints p1) throws XMLSchemaException;
    
    Vector whatCanGoHere(final int[] p0);
    
    int[] occurenceInfo(final int[] p0);
    
    String getTermName(final int p0);
    
    boolean isCompactedForUPA();
    
    XSElementDecl findMatchingElemDecl(final QName p0, final SubstitutionGroupHandler p1);
}
