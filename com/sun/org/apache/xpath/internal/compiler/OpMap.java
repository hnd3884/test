package com.sun.org.apache.xpath.internal.compiler;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xml.internal.utils.ObjectVector;

public class OpMap
{
    protected String m_currentPattern;
    static final int MAXTOKENQUEUESIZE = 500;
    static final int BLOCKTOKENQUEUESIZE = 500;
    ObjectVector m_tokenQueue;
    OpMapVector m_opMap;
    public static final int MAPINDEX_LENGTH = 1;
    
    public OpMap() {
        this.m_tokenQueue = new ObjectVector(500, 500);
        this.m_opMap = null;
    }
    
    @Override
    public String toString() {
        return this.m_currentPattern;
    }
    
    public String getPatternString() {
        return this.m_currentPattern;
    }
    
    public ObjectVector getTokenQueue() {
        return this.m_tokenQueue;
    }
    
    public Object getToken(final int pos) {
        return this.m_tokenQueue.elementAt(pos);
    }
    
    public int getTokenQueueSize() {
        return this.m_tokenQueue.size();
    }
    
    public OpMapVector getOpMap() {
        return this.m_opMap;
    }
    
    void shrink() {
        int n = this.m_opMap.elementAt(1);
        this.m_opMap.setToSize(n + 4);
        this.m_opMap.setElementAt(0, n);
        this.m_opMap.setElementAt(0, n + 1);
        this.m_opMap.setElementAt(0, n + 2);
        n = this.m_tokenQueue.size();
        this.m_tokenQueue.setToSize(n + 4);
        this.m_tokenQueue.setElementAt(null, n);
        this.m_tokenQueue.setElementAt(null, n + 1);
        this.m_tokenQueue.setElementAt(null, n + 2);
    }
    
    public int getOp(final int opPos) {
        return this.m_opMap.elementAt(opPos);
    }
    
    public void setOp(final int opPos, final int value) {
        this.m_opMap.setElementAt(value, opPos);
    }
    
    public int getNextOpPos(final int opPos) {
        return opPos + this.m_opMap.elementAt(opPos + 1);
    }
    
    public int getNextStepPos(final int opPos) {
        int stepType = this.getOp(opPos);
        if (stepType >= 37 && stepType <= 53) {
            return this.getNextOpPos(opPos);
        }
        if (stepType < 22 || stepType > 25) {
            throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_UNKNOWN_STEP", new Object[] { String.valueOf(stepType) }));
        }
        int newOpPos;
        for (newOpPos = this.getNextOpPos(opPos); 29 == this.getOp(newOpPos); newOpPos = this.getNextOpPos(newOpPos)) {}
        stepType = this.getOp(newOpPos);
        if (stepType < 37 || stepType > 53) {
            return -1;
        }
        return newOpPos;
    }
    
    public static int getNextOpPos(final int[] opMap, final int opPos) {
        return opPos + opMap[opPos + 1];
    }
    
    public int getFirstPredicateOpPos(final int opPos) throws TransformerException {
        final int stepType = this.m_opMap.elementAt(opPos);
        if (stepType >= 37 && stepType <= 53) {
            return opPos + this.m_opMap.elementAt(opPos + 2);
        }
        if (stepType >= 22 && stepType <= 25) {
            return opPos + this.m_opMap.elementAt(opPos + 1);
        }
        if (-2 == stepType) {
            return -2;
        }
        this.error("ER_UNKNOWN_OPCODE", new Object[] { String.valueOf(stepType) });
        return -1;
    }
    
    public void error(final String msg, final Object[] args) throws TransformerException {
        final String fmsg = XPATHMessages.createXPATHMessage(msg, args);
        throw new TransformerException(fmsg);
    }
    
    public static int getFirstChildPos(final int opPos) {
        return opPos + 2;
    }
    
    public int getArgLength(final int opPos) {
        return this.m_opMap.elementAt(opPos + 1);
    }
    
    public int getArgLengthOfStep(final int opPos) {
        return this.m_opMap.elementAt(opPos + 1 + 1) - 3;
    }
    
    public static int getFirstChildPosOfStep(final int opPos) {
        return opPos + 3;
    }
    
    public int getStepTestType(final int opPosOfStep) {
        return this.m_opMap.elementAt(opPosOfStep + 3);
    }
    
    public String getStepNS(final int opPosOfStep) {
        final int argLenOfStep = this.getArgLengthOfStep(opPosOfStep);
        if (argLenOfStep != 3) {
            return null;
        }
        final int index = this.m_opMap.elementAt(opPosOfStep + 4);
        if (index >= 0) {
            return (String)this.m_tokenQueue.elementAt(index);
        }
        if (-3 == index) {
            return "*";
        }
        return null;
    }
    
    public String getStepLocalName(final int opPosOfStep) {
        final int argLenOfStep = this.getArgLengthOfStep(opPosOfStep);
        int index = 0;
        switch (argLenOfStep) {
            case 0: {
                index = -2;
                break;
            }
            case 1: {
                index = -3;
                break;
            }
            case 2: {
                index = this.m_opMap.elementAt(opPosOfStep + 4);
                break;
            }
            case 3: {
                index = this.m_opMap.elementAt(opPosOfStep + 5);
                break;
            }
            default: {
                index = -2;
                break;
            }
        }
        if (index >= 0) {
            return this.m_tokenQueue.elementAt(index).toString();
        }
        if (-3 == index) {
            return "*";
        }
        return null;
    }
}
