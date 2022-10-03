package com.sun.org.apache.xerces.internal.impl.xs.models;

import java.util.ArrayList;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.xni.QName;

public interface XSCMValidator
{
    public static final short FIRST_ERROR = -1;
    public static final short SUBSEQUENT_ERROR = -2;
    
    int[] startContentModel();
    
    Object oneTransition(final QName p0, final int[] p1, final SubstitutionGroupHandler p2);
    
    boolean endContentModel(final int[] p0);
    
    boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler p0) throws XMLSchemaException;
    
    Vector whatCanGoHere(final int[] p0);
    
    ArrayList checkMinMaxBounds();
}
