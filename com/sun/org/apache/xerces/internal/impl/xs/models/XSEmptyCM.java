package com.sun.org.apache.xerces.internal.impl.xs.models;

import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.Vector;

public class XSEmptyCM implements XSCMValidator
{
    private static final short STATE_START = 0;
    private static final Vector EMPTY;
    
    @Override
    public int[] startContentModel() {
        return new int[] { 0 };
    }
    
    @Override
    public Object oneTransition(final QName elementName, final int[] currentState, final SubstitutionGroupHandler subGroupHandler) {
        if (currentState[0] < 0) {
            currentState[0] = -2;
            return null;
        }
        currentState[0] = -1;
        return null;
    }
    
    @Override
    public boolean endContentModel(final int[] currentState) {
        final boolean isFinal = false;
        final int state = currentState[0];
        return state >= 0;
    }
    
    @Override
    public boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException {
        return false;
    }
    
    @Override
    public Vector whatCanGoHere(final int[] state) {
        return XSEmptyCM.EMPTY;
    }
    
    @Override
    public ArrayList checkMinMaxBounds() {
        return null;
    }
    
    static {
        EMPTY = new Vector(0);
    }
}
