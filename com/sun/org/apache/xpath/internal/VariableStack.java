package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.QName;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.objects.XObject;

public class VariableStack implements Cloneable
{
    public static final int CLEARLIMITATION = 1024;
    XObject[] _stackFrames;
    int _frameTop;
    private int _currentFrameBottom;
    int[] _links;
    int _linksTop;
    private static XObject[] m_nulls;
    
    public VariableStack() {
        this._stackFrames = new XObject[8192];
        this._links = new int[4096];
        this.reset();
    }
    
    public synchronized Object clone() throws CloneNotSupportedException {
        final VariableStack vs = (VariableStack)super.clone();
        vs._stackFrames = this._stackFrames.clone();
        vs._links = this._links.clone();
        return vs;
    }
    
    public XObject elementAt(final int i) {
        return this._stackFrames[i];
    }
    
    public int size() {
        return this._frameTop;
    }
    
    public void reset() {
        this._frameTop = 0;
        this._linksTop = 0;
        this._links[this._linksTop++] = 0;
        this._stackFrames = new XObject[this._stackFrames.length];
    }
    
    public void setStackFrame(final int sf) {
        this._currentFrameBottom = sf;
    }
    
    public int getStackFrame() {
        return this._currentFrameBottom;
    }
    
    public int link(final int size) {
        this._currentFrameBottom = this._frameTop;
        this._frameTop += size;
        if (this._frameTop >= this._stackFrames.length) {
            final XObject[] newsf = new XObject[this._stackFrames.length + 4096 + size];
            System.arraycopy(this._stackFrames, 0, newsf, 0, this._stackFrames.length);
            this._stackFrames = newsf;
        }
        if (this._linksTop + 1 >= this._links.length) {
            final int[] newlinks = new int[this._links.length + 2048];
            System.arraycopy(this._links, 0, newlinks, 0, this._links.length);
            this._links = newlinks;
        }
        this._links[this._linksTop++] = this._currentFrameBottom;
        return this._currentFrameBottom;
    }
    
    public void unlink() {
        final int[] links = this._links;
        final int linksTop = this._linksTop - 1;
        this._linksTop = linksTop;
        this._frameTop = links[linksTop];
        this._currentFrameBottom = this._links[this._linksTop - 1];
    }
    
    public void unlink(final int currentFrame) {
        final int[] links = this._links;
        final int linksTop = this._linksTop - 1;
        this._linksTop = linksTop;
        this._frameTop = links[linksTop];
        this._currentFrameBottom = currentFrame;
    }
    
    public void setLocalVariable(final int index, final XObject val) {
        this._stackFrames[index + this._currentFrameBottom] = val;
    }
    
    public void setLocalVariable(final int index, final XObject val, final int stackFrame) {
        this._stackFrames[index + stackFrame] = val;
    }
    
    public XObject getLocalVariable(final XPathContext xctxt, int index) throws TransformerException {
        index += this._currentFrameBottom;
        final XObject val = this._stackFrames[index];
        if (null == val) {
            throw new TransformerException(XPATHMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", null), xctxt.getSAXLocator());
        }
        if (val.getType() == 600) {
            return this._stackFrames[index] = val.execute(xctxt);
        }
        return val;
    }
    
    public XObject getLocalVariable(int index, final int frame) throws TransformerException {
        index += frame;
        final XObject val = this._stackFrames[index];
        return val;
    }
    
    public XObject getLocalVariable(final XPathContext xctxt, int index, final boolean destructiveOK) throws TransformerException {
        index += this._currentFrameBottom;
        final XObject val = this._stackFrames[index];
        if (null == val) {
            throw new TransformerException(XPATHMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", null), xctxt.getSAXLocator());
        }
        if (val.getType() == 600) {
            return this._stackFrames[index] = val.execute(xctxt);
        }
        return destructiveOK ? val : val.getFresh();
    }
    
    public boolean isLocalSet(final int index) throws TransformerException {
        return this._stackFrames[index + this._currentFrameBottom] != null;
    }
    
    public void clearLocalSlots(int start, final int len) {
        start += this._currentFrameBottom;
        System.arraycopy(VariableStack.m_nulls, 0, this._stackFrames, start, len);
    }
    
    public void setGlobalVariable(final int index, final XObject val) {
        this._stackFrames[index] = val;
    }
    
    public XObject getGlobalVariable(final XPathContext xctxt, final int index) throws TransformerException {
        final XObject val = this._stackFrames[index];
        if (val.getType() == 600) {
            return this._stackFrames[index] = val.execute(xctxt);
        }
        return val;
    }
    
    public XObject getGlobalVariable(final XPathContext xctxt, final int index, final boolean destructiveOK) throws TransformerException {
        final XObject val = this._stackFrames[index];
        if (val.getType() == 600) {
            return this._stackFrames[index] = val.execute(xctxt);
        }
        return destructiveOK ? val : val.getFresh();
    }
    
    public XObject getVariableOrParam(final XPathContext xctxt, final QName qname) throws TransformerException {
        throw new TransformerException(XPATHMessages.createXPATHMessage("ER_VAR_NOT_RESOLVABLE", new Object[] { qname.toString() }));
    }
    
    static {
        VariableStack.m_nulls = new XObject[1024];
    }
}
