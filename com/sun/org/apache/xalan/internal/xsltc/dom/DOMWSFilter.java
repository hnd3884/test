package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import java.util.HashMap;
import com.sun.org.apache.xml.internal.dtm.DTM;
import java.util.Map;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;

public class DOMWSFilter implements DTMWSFilter
{
    private AbstractTranslet m_translet;
    private StripFilter m_filter;
    private Map<DTM, short[]> m_mappings;
    private DTM m_currentDTM;
    private short[] m_currentMapping;
    
    public DOMWSFilter(final AbstractTranslet translet) {
        this.m_translet = translet;
        this.m_mappings = new HashMap<DTM, short[]>();
        if (translet instanceof StripFilter) {
            this.m_filter = (StripFilter)translet;
        }
    }
    
    @Override
    public short getShouldStripSpace(final int node, final DTM dtm) {
        if (this.m_filter == null || !(dtm instanceof DOM)) {
            return 1;
        }
        final DOM dom = (DOM)dtm;
        int type = 0;
        if (!(dtm instanceof DOMEnhancedForDTM)) {
            return 3;
        }
        final DOMEnhancedForDTM mappableDOM = (DOMEnhancedForDTM)dtm;
        short[] mapping;
        if (dtm == this.m_currentDTM) {
            mapping = this.m_currentMapping;
        }
        else {
            mapping = this.m_mappings.get(dtm);
            if (mapping == null) {
                mapping = mappableDOM.getMapping(this.m_translet.getNamesArray(), this.m_translet.getUrisArray(), this.m_translet.getTypesArray());
                this.m_mappings.put(dtm, mapping);
                this.m_currentDTM = dtm;
                this.m_currentMapping = mapping;
            }
        }
        final int expType = mappableDOM.getExpandedTypeID(node);
        if (expType >= 0 && expType < mapping.length) {
            type = mapping[expType];
        }
        else {
            type = -1;
        }
        if (this.m_filter.stripSpace(dom, node, type)) {
            return 2;
        }
        return 1;
    }
}
