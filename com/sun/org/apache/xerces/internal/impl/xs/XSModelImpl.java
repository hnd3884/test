package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.ListIterator;
import java.util.Iterator;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItemList;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import java.util.AbstractList;

public final class XSModelImpl extends AbstractList implements XSModel, XSNamespaceItemList
{
    private static final short MAX_COMP_IDX = 16;
    private static final boolean[] GLOBAL_COMP;
    private final int fGrammarCount;
    private final String[] fNamespaces;
    private final SchemaGrammar[] fGrammarList;
    private final SymbolHash fGrammarMap;
    private final SymbolHash fSubGroupMap;
    private final XSNamedMap[] fGlobalComponents;
    private final XSNamedMap[][] fNSComponents;
    private final StringList fNamespacesList;
    private XSObjectList fAnnotations;
    private final boolean fHasIDC;
    
    public XSModelImpl(final SchemaGrammar[] grammars) {
        this(grammars, (short)1);
    }
    
    public XSModelImpl(final SchemaGrammar[] grammars, final short s4sVersion) {
        this.fAnnotations = null;
        int len = grammars.length;
        final int initialSize = Math.max(len + 1, 5);
        String[] namespaces = new String[initialSize];
        SchemaGrammar[] grammarList = new SchemaGrammar[initialSize];
        boolean hasS4S = false;
        for (int i = 0; i < len; ++i) {
            final SchemaGrammar sg = grammars[i];
            final String tns = sg.getTargetNamespace();
            namespaces[i] = tns;
            grammarList[i] = sg;
            if (tns == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
                hasS4S = true;
            }
        }
        if (!hasS4S) {
            namespaces[len] = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            grammarList[len++] = SchemaGrammar.getS4SGrammar(s4sVersion);
        }
        for (int j = 0; j < len; ++j) {
            final SchemaGrammar sg2 = grammarList[j];
            final Vector gs = sg2.getImportedGrammars();
            for (int k = (gs == null) ? -1 : (gs.size() - 1); k >= 0; --k) {
                SchemaGrammar sg3;
                int l;
                for (sg3 = gs.elementAt(k), l = 0; l < len && sg3 != grammarList[l]; ++l) {}
                if (l == len) {
                    if (len == grammarList.length) {
                        final String[] newSA = new String[len * 2];
                        System.arraycopy(namespaces, 0, newSA, 0, len);
                        namespaces = newSA;
                        final SchemaGrammar[] newGA = new SchemaGrammar[len * 2];
                        System.arraycopy(grammarList, 0, newGA, 0, len);
                        grammarList = newGA;
                    }
                    namespaces[len] = sg3.getTargetNamespace();
                    grammarList[len] = sg3;
                    ++len;
                }
            }
        }
        this.fNamespaces = namespaces;
        this.fGrammarList = grammarList;
        boolean hasIDC = false;
        this.fGrammarMap = new SymbolHash(len * 2);
        for (int j = 0; j < len; ++j) {
            this.fGrammarMap.put(null2EmptyString(this.fNamespaces[j]), this.fGrammarList[j]);
            if (this.fGrammarList[j].hasIDConstraints()) {
                hasIDC = true;
            }
        }
        this.fHasIDC = hasIDC;
        this.fGrammarCount = len;
        this.fGlobalComponents = new XSNamedMap[17];
        this.fNSComponents = new XSNamedMap[len][17];
        this.fNamespacesList = new StringListImpl(this.fNamespaces, this.fGrammarCount);
        this.fSubGroupMap = this.buildSubGroups();
    }
    
    private SymbolHash buildSubGroups_Org() {
        final SubstitutionGroupHandler sgHandler = new SubstitutionGroupHandler(null);
        for (int i = 0; i < this.fGrammarCount; ++i) {
            sgHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
        }
        final XSNamedMap elements = this.getComponents((short)2);
        final int len = elements.getLength();
        final SymbolHash subGroupMap = new SymbolHash(len * 2);
        for (int j = 0; j < len; ++j) {
            final XSElementDecl head = (XSElementDecl)elements.item(j);
            final XSElementDeclaration[] subGroup = sgHandler.getSubstitutionGroup(head);
            subGroupMap.put(head, (subGroup.length > 0) ? new XSObjectListImpl(subGroup, subGroup.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return subGroupMap;
    }
    
    private SymbolHash buildSubGroups() {
        final SubstitutionGroupHandler sgHandler = new SubstitutionGroupHandler(null);
        for (int i = 0; i < this.fGrammarCount; ++i) {
            sgHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
        }
        final XSObjectListImpl elements = this.getGlobalElements();
        final int len = elements.getLength();
        final SymbolHash subGroupMap = new SymbolHash(len * 2);
        for (int j = 0; j < len; ++j) {
            final XSElementDecl head = (XSElementDecl)elements.item(j);
            final XSElementDeclaration[] subGroup = sgHandler.getSubstitutionGroup(head);
            subGroupMap.put(head, (subGroup.length > 0) ? new XSObjectListImpl(subGroup, subGroup.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return subGroupMap;
    }
    
    private XSObjectListImpl getGlobalElements() {
        final SymbolHash[] tables = new SymbolHash[this.fGrammarCount];
        int length = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            tables[i] = this.fGrammarList[i].fAllGlobalElemDecls;
            length += tables[i].getLength();
        }
        if (length == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        final XSObject[] components = new XSObject[length];
        int start = 0;
        for (int j = 0; j < this.fGrammarCount; ++j) {
            tables[j].getValues(components, start);
            start += tables[j].getLength();
        }
        return new XSObjectListImpl(components, length);
    }
    
    @Override
    public StringList getNamespaces() {
        return this.fNamespacesList;
    }
    
    @Override
    public XSNamespaceItemList getNamespaceItems() {
        return this;
    }
    
    @Override
    public synchronized XSNamedMap getComponents(final short objectType) {
        if (objectType <= 0 || objectType > 16 || !XSModelImpl.GLOBAL_COMP[objectType]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        final SymbolHash[] tables = new SymbolHash[this.fGrammarCount];
        if (this.fGlobalComponents[objectType] == null) {
            for (int i = 0; i < this.fGrammarCount; ++i) {
                switch (objectType) {
                    case 3:
                    case 15:
                    case 16: {
                        tables[i] = this.fGrammarList[i].fGlobalTypeDecls;
                        break;
                    }
                    case 1: {
                        tables[i] = this.fGrammarList[i].fGlobalAttrDecls;
                        break;
                    }
                    case 2: {
                        tables[i] = this.fGrammarList[i].fGlobalElemDecls;
                        break;
                    }
                    case 5: {
                        tables[i] = this.fGrammarList[i].fGlobalAttrGrpDecls;
                        break;
                    }
                    case 6: {
                        tables[i] = this.fGrammarList[i].fGlobalGroupDecls;
                        break;
                    }
                    case 11: {
                        tables[i] = this.fGrammarList[i].fGlobalNotationDecls;
                        break;
                    }
                }
            }
            if (objectType == 15 || objectType == 16) {
                this.fGlobalComponents[objectType] = new XSNamedMap4Types(this.fNamespaces, tables, this.fGrammarCount, objectType);
            }
            else {
                this.fGlobalComponents[objectType] = new XSNamedMapImpl(this.fNamespaces, tables, this.fGrammarCount);
            }
        }
        return this.fGlobalComponents[objectType];
    }
    
    @Override
    public synchronized XSNamedMap getComponentsByNamespace(final short objectType, final String namespace) {
        if (objectType <= 0 || objectType > 16 || !XSModelImpl.GLOBAL_COMP[objectType]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        int i = 0;
        if (namespace != null) {
            while (i < this.fGrammarCount) {
                if (namespace.equals(this.fNamespaces[i])) {
                    break;
                }
                ++i;
            }
        }
        else {
            while (i < this.fGrammarCount) {
                if (this.fNamespaces[i] == null) {
                    break;
                }
                ++i;
            }
        }
        if (i == this.fGrammarCount) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        if (this.fNSComponents[i][objectType] == null) {
            SymbolHash table = null;
            switch (objectType) {
                case 3:
                case 15:
                case 16: {
                    table = this.fGrammarList[i].fGlobalTypeDecls;
                    break;
                }
                case 1: {
                    table = this.fGrammarList[i].fGlobalAttrDecls;
                    break;
                }
                case 2: {
                    table = this.fGrammarList[i].fGlobalElemDecls;
                    break;
                }
                case 5: {
                    table = this.fGrammarList[i].fGlobalAttrGrpDecls;
                    break;
                }
                case 6: {
                    table = this.fGrammarList[i].fGlobalGroupDecls;
                    break;
                }
                case 11: {
                    table = this.fGrammarList[i].fGlobalNotationDecls;
                    break;
                }
            }
            if (objectType == 15 || objectType == 16) {
                this.fNSComponents[i][objectType] = new XSNamedMap4Types(namespace, table, objectType);
            }
            else {
                this.fNSComponents[i][objectType] = new XSNamedMapImpl(namespace, table);
            }
        }
        return this.fNSComponents[i][objectType];
    }
    
    @Override
    public XSTypeDefinition getTypeDefinition(final String name, final String namespace) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSTypeDefinition)sg.fGlobalTypeDecls.get(name);
    }
    
    public XSTypeDefinition getTypeDefinition(final String name, final String namespace, final String loc) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalTypeDecl(name, loc);
    }
    
    @Override
    public XSAttributeDeclaration getAttributeDeclaration(final String name, final String namespace) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSAttributeDeclaration)sg.fGlobalAttrDecls.get(name);
    }
    
    public XSAttributeDeclaration getAttributeDeclaration(final String name, final String namespace, final String loc) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalAttributeDecl(name, loc);
    }
    
    @Override
    public XSElementDeclaration getElementDeclaration(final String name, final String namespace) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSElementDeclaration)sg.fGlobalElemDecls.get(name);
    }
    
    public XSElementDeclaration getElementDeclaration(final String name, final String namespace, final String loc) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalElementDecl(name, loc);
    }
    
    @Override
    public XSAttributeGroupDefinition getAttributeGroup(final String name, final String namespace) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSAttributeGroupDefinition)sg.fGlobalAttrGrpDecls.get(name);
    }
    
    public XSAttributeGroupDefinition getAttributeGroup(final String name, final String namespace, final String loc) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalAttributeGroupDecl(name, loc);
    }
    
    @Override
    public XSModelGroupDefinition getModelGroupDefinition(final String name, final String namespace) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSModelGroupDefinition)sg.fGlobalGroupDecls.get(name);
    }
    
    public XSModelGroupDefinition getModelGroupDefinition(final String name, final String namespace, final String loc) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalGroupDecl(name, loc);
    }
    
    @Override
    public XSNotationDeclaration getNotationDeclaration(final String name, final String namespace) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSNotationDeclaration)sg.fGlobalNotationDecls.get(name);
    }
    
    public XSNotationDeclaration getNotationDeclaration(final String name, final String namespace, final String loc) {
        final SchemaGrammar sg = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalNotationDecl(name, loc);
    }
    
    @Override
    public synchronized XSObjectList getAnnotations() {
        if (this.fAnnotations != null) {
            return this.fAnnotations;
        }
        int totalAnnotations = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            totalAnnotations += this.fGrammarList[i].fNumAnnotations;
        }
        if (totalAnnotations == 0) {
            return this.fAnnotations = XSObjectListImpl.EMPTY_LIST;
        }
        final XSAnnotationImpl[] annotations = new XSAnnotationImpl[totalAnnotations];
        int currPos = 0;
        for (int j = 0; j < this.fGrammarCount; ++j) {
            final SchemaGrammar currGrammar = this.fGrammarList[j];
            if (currGrammar.fNumAnnotations > 0) {
                System.arraycopy(currGrammar.fAnnotations, 0, annotations, currPos, currGrammar.fNumAnnotations);
                currPos += currGrammar.fNumAnnotations;
            }
        }
        return this.fAnnotations = new XSObjectListImpl(annotations, annotations.length);
    }
    
    private static final String null2EmptyString(final String str) {
        return (str == null) ? XMLSymbols.EMPTY_STRING : str;
    }
    
    public boolean hasIDConstraints() {
        return this.fHasIDC;
    }
    
    @Override
    public XSObjectList getSubstitutionGroup(final XSElementDeclaration head) {
        return (XSObjectList)this.fSubGroupMap.get(head);
    }
    
    @Override
    public int getLength() {
        return this.fGrammarCount;
    }
    
    @Override
    public XSNamespaceItem item(final int index) {
        if (index < 0 || index >= this.fGrammarCount) {
            return null;
        }
        return this.fGrammarList[index];
    }
    
    @Override
    public Object get(final int index) {
        if (index >= 0 && index < this.fGrammarCount) {
            return this.fGrammarList[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
    
    @Override
    public int size() {
        return this.getLength();
    }
    
    @Override
    public Iterator iterator() {
        return this.listIterator0(0);
    }
    
    @Override
    public ListIterator listIterator() {
        return this.listIterator0(0);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        if (index >= 0 && index < this.fGrammarCount) {
            return this.listIterator0(index);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
    
    private ListIterator listIterator0(final int index) {
        return new XSNamespaceItemListIterator(index);
    }
    
    @Override
    public Object[] toArray() {
        final Object[] a = new Object[this.fGrammarCount];
        this.toArray0(a);
        return a;
    }
    
    @Override
    public Object[] toArray(Object[] a) {
        if (a.length < this.fGrammarCount) {
            final Class arrayClass = a.getClass();
            final Class componentType = arrayClass.getComponentType();
            a = (Object[])Array.newInstance(componentType, this.fGrammarCount);
        }
        this.toArray0(a);
        if (a.length > this.fGrammarCount) {
            a[this.fGrammarCount] = null;
        }
        return a;
    }
    
    private void toArray0(final Object[] a) {
        if (this.fGrammarCount > 0) {
            System.arraycopy(this.fGrammarList, 0, a, 0, this.fGrammarCount);
        }
    }
    
    static {
        GLOBAL_COMP = new boolean[] { false, true, true, true, false, true, true, false, false, false, false, true, false, false, false, true, true };
    }
    
    private final class XSNamespaceItemListIterator implements ListIterator
    {
        private int index;
        
        public XSNamespaceItemListIterator(final int index) {
            this.index = index;
        }
        
        @Override
        public boolean hasNext() {
            return this.index < XSModelImpl.this.fGrammarCount;
        }
        
        @Override
        public Object next() {
            if (this.index < XSModelImpl.this.fGrammarCount) {
                return XSModelImpl.this.fGrammarList[this.index++];
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public boolean hasPrevious() {
            return this.index > 0;
        }
        
        @Override
        public Object previous() {
            if (this.index > 0) {
                final SchemaGrammar[] access$100 = XSModelImpl.this.fGrammarList;
                final int index = this.index - 1;
                this.index = index;
                return access$100[index];
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public int nextIndex() {
            return this.index;
        }
        
        @Override
        public int previousIndex() {
            return this.index - 1;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void set(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Object o) {
            throw new UnsupportedOperationException();
        }
    }
}
