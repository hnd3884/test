package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import org.xml.sax.SAXException;
import java.util.Vector;
import com.sun.org.apache.xml.internal.utils.IntVector;
import com.sun.org.apache.xml.internal.utils.StringVector;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.IntStack;

public class SAX2RTFDTM extends SAX2DTM
{
    private static final boolean DEBUG = false;
    private int m_currentDocumentNode;
    IntStack mark_size;
    IntStack mark_data_size;
    IntStack mark_char_size;
    IntStack mark_doq_size;
    IntStack mark_nsdeclset_size;
    IntStack mark_nsdeclelem_size;
    int m_emptyNodeCount;
    int m_emptyNSDeclSetCount;
    int m_emptyNSDeclSetElemsCount;
    int m_emptyDataCount;
    int m_emptyCharsCount;
    int m_emptyDataQNCount;
    
    public SAX2RTFDTM(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing);
        this.m_currentDocumentNode = -1;
        this.mark_size = new IntStack();
        this.mark_data_size = new IntStack();
        this.mark_char_size = new IntStack();
        this.mark_doq_size = new IntStack();
        this.mark_nsdeclset_size = new IntStack();
        this.mark_nsdeclelem_size = new IntStack();
        this.m_useSourceLocationProperty = false;
        this.m_sourceSystemId = (this.m_useSourceLocationProperty ? new StringVector() : null);
        this.m_sourceLine = (this.m_useSourceLocationProperty ? new IntVector() : null);
        this.m_sourceColumn = (this.m_useSourceLocationProperty ? new IntVector() : null);
        this.m_emptyNodeCount = this.m_size;
        this.m_emptyNSDeclSetCount = ((this.m_namespaceDeclSets == null) ? 0 : this.m_namespaceDeclSets.size());
        this.m_emptyNSDeclSetElemsCount = ((this.m_namespaceDeclSetElements == null) ? 0 : this.m_namespaceDeclSetElements.size());
        this.m_emptyDataCount = this.m_data.size();
        this.m_emptyCharsCount = this.m_chars.size();
        this.m_emptyDataQNCount = this.m_dataOrQName.size();
    }
    
    @Override
    public int getDocument() {
        return this.makeNodeHandle(this.m_currentDocumentNode);
    }
    
    @Override
    public int getDocumentRoot(final int nodeHandle) {
        for (int id = this.makeNodeIdentity(nodeHandle); id != -1; id = this._parent(id)) {
            if (this._type(id) == 9) {
                return this.makeNodeHandle(id);
            }
        }
        return -1;
    }
    
    protected int _documentRoot(int nodeIdentifier) {
        if (nodeIdentifier == -1) {
            return -1;
        }
        for (int parent = this._parent(nodeIdentifier); parent != -1; parent = this._parent(nodeIdentifier)) {
            nodeIdentifier = parent;
        }
        return nodeIdentifier;
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.m_endDocumentOccured = false;
        this.m_prefixMappings = new Vector();
        this.m_contextIndexes = new IntStack();
        this.m_parents = new IntStack();
        this.m_currentDocumentNode = this.m_size;
        super.startDocument();
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.charactersFlush();
        this.m_nextsib.setElementAt(-1, this.m_currentDocumentNode);
        if (this.m_firstch.elementAt(this.m_currentDocumentNode) == -2) {
            this.m_firstch.setElementAt(-1, this.m_currentDocumentNode);
        }
        if (-1 != this.m_previous) {
            this.m_nextsib.setElementAt(-1, this.m_previous);
        }
        this.m_parents = null;
        this.m_prefixMappings = null;
        this.m_contextIndexes = null;
        this.m_currentDocumentNode = -1;
        this.m_endDocumentOccured = true;
    }
    
    public void pushRewindMark() {
        if (this.m_indexing || this.m_elemIndexes != null) {
            throw new NullPointerException("Coding error; Don't try to mark/rewind an indexed DTM");
        }
        this.mark_size.push(this.m_size);
        this.mark_nsdeclset_size.push((this.m_namespaceDeclSets == null) ? 0 : this.m_namespaceDeclSets.size());
        this.mark_nsdeclelem_size.push((this.m_namespaceDeclSetElements == null) ? 0 : this.m_namespaceDeclSetElements.size());
        this.mark_data_size.push(this.m_data.size());
        this.mark_char_size.push(this.m_chars.size());
        this.mark_doq_size.push(this.m_dataOrQName.size());
    }
    
    public boolean popRewindMark() {
        final boolean top = this.mark_size.empty();
        this.m_size = (top ? this.m_emptyNodeCount : this.mark_size.pop());
        this.m_exptype.setSize(this.m_size);
        this.m_firstch.setSize(this.m_size);
        this.m_nextsib.setSize(this.m_size);
        this.m_prevsib.setSize(this.m_size);
        this.m_parent.setSize(this.m_size);
        this.m_elemIndexes = null;
        final int ds = top ? this.m_emptyNSDeclSetCount : this.mark_nsdeclset_size.pop();
        if (this.m_namespaceDeclSets != null) {
            this.m_namespaceDeclSets.setSize(ds);
        }
        final int ds2 = top ? this.m_emptyNSDeclSetElemsCount : this.mark_nsdeclelem_size.pop();
        if (this.m_namespaceDeclSetElements != null) {
            this.m_namespaceDeclSetElements.setSize(ds2);
        }
        this.m_data.setSize(top ? this.m_emptyDataCount : this.mark_data_size.pop());
        this.m_chars.setLength(top ? this.m_emptyCharsCount : this.mark_char_size.pop());
        this.m_dataOrQName.setSize(top ? this.m_emptyDataQNCount : this.mark_doq_size.pop());
        return this.m_size == 0;
    }
    
    public boolean isTreeIncomplete() {
        return !this.m_endDocumentOccured;
    }
}
