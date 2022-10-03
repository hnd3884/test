package org.apache.xerces.impl.xs;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.ListIterator;
import java.util.Iterator;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSIDCDefinition;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.util.XSNamedMap4Types;
import org.apache.xerces.impl.xs.util.XSNamedMapImpl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import java.util.Vector;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSModel;
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
    
    public XSModelImpl(final SchemaGrammar[] array) {
        this(array, (short)1);
    }
    
    public XSModelImpl(final SchemaGrammar[] array, final short n) {
        this.fAnnotations = null;
        int length = array.length;
        final int max = Math.max(length + 1, 5);
        String[] fNamespaces = new String[max];
        SchemaGrammar[] fGrammarList = new SchemaGrammar[max];
        boolean b = false;
        for (int i = 0; i < length; ++i) {
            final SchemaGrammar schemaGrammar = array[i];
            final String targetNamespace = schemaGrammar.getTargetNamespace();
            fNamespaces[i] = targetNamespace;
            fGrammarList[i] = schemaGrammar;
            if (targetNamespace == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
                b = true;
            }
        }
        if (!b) {
            fNamespaces[length] = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            fGrammarList[length++] = SchemaGrammar.getS4SGrammar(n);
        }
        for (int j = 0; j < length; ++j) {
            final Vector importedGrammars = fGrammarList[j].getImportedGrammars();
            for (int k = (importedGrammars == null) ? -1 : (importedGrammars.size() - 1); k >= 0; --k) {
                SchemaGrammar schemaGrammar2;
                int n2;
                for (schemaGrammar2 = importedGrammars.elementAt(k), n2 = 0; n2 < length && schemaGrammar2 != fGrammarList[n2]; ++n2) {}
                if (n2 == length) {
                    if (length == fGrammarList.length) {
                        final String[] array2 = new String[length * 2];
                        System.arraycopy(fNamespaces, 0, array2, 0, length);
                        fNamespaces = array2;
                        final SchemaGrammar[] array3 = new SchemaGrammar[length * 2];
                        System.arraycopy(fGrammarList, 0, array3, 0, length);
                        fGrammarList = array3;
                    }
                    fNamespaces[length] = schemaGrammar2.getTargetNamespace();
                    fGrammarList[length] = schemaGrammar2;
                    ++length;
                }
            }
        }
        this.fNamespaces = fNamespaces;
        this.fGrammarList = fGrammarList;
        boolean fHasIDC = false;
        this.fGrammarMap = new SymbolHash(length * 2);
        for (int l = 0; l < length; ++l) {
            this.fGrammarMap.put(null2EmptyString(this.fNamespaces[l]), this.fGrammarList[l]);
            if (this.fGrammarList[l].hasIDConstraints()) {
                fHasIDC = true;
            }
        }
        this.fHasIDC = fHasIDC;
        this.fGrammarCount = length;
        this.fGlobalComponents = new XSNamedMap[17];
        this.fNSComponents = new XSNamedMap[length][17];
        this.fNamespacesList = new StringListImpl(this.fNamespaces, this.fGrammarCount);
        this.fSubGroupMap = this.buildSubGroups(n);
    }
    
    private SymbolHash buildSubGroups_Org(final short n) {
        final SubstitutionGroupHandler substitutionGroupHandler = new SubstitutionGroupHandler(null);
        for (int i = 0; i < this.fGrammarCount; ++i) {
            substitutionGroupHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
        }
        final XSNamedMap components = this.getComponents((short)2);
        final int length = components.getLength();
        final SymbolHash symbolHash = new SymbolHash(length * 2);
        for (int j = 0; j < length; ++j) {
            final XSElementDecl xsElementDecl = (XSElementDecl)components.item(j);
            final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, n);
            symbolHash.put(xsElementDecl, (substitutionGroup.length > 0) ? new XSObjectListImpl(substitutionGroup, substitutionGroup.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return symbolHash;
    }
    
    private SymbolHash buildSubGroups(final short n) {
        final SubstitutionGroupHandler substitutionGroupHandler = new SubstitutionGroupHandler(null);
        for (int i = 0; i < this.fGrammarCount; ++i) {
            substitutionGroupHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
        }
        final XSObjectListImpl globalElements = this.getGlobalElements();
        final int length = globalElements.getLength();
        final SymbolHash symbolHash = new SymbolHash(length * 2);
        for (int j = 0; j < length; ++j) {
            final XSElementDecl xsElementDecl = (XSElementDecl)globalElements.item(j);
            final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, n);
            symbolHash.put(xsElementDecl, (substitutionGroup.length > 0) ? new XSObjectListImpl(substitutionGroup, substitutionGroup.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return symbolHash;
    }
    
    private XSObjectListImpl getGlobalElements() {
        final SymbolHash[] array = new SymbolHash[this.fGrammarCount];
        int n = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            array[i] = this.fGrammarList[i].fAllGlobalElemDecls;
            n += array[i].getLength();
        }
        if (n == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        final XSObject[] array2 = new XSObject[n];
        int n2 = 0;
        for (int j = 0; j < this.fGrammarCount; ++j) {
            array[j].getValues(array2, n2);
            n2 += array[j].getLength();
        }
        return new XSObjectListImpl(array2, n);
    }
    
    public StringList getNamespaces() {
        return this.fNamespacesList;
    }
    
    public XSNamespaceItemList getNamespaceItems() {
        return this;
    }
    
    public synchronized XSNamedMap getComponents(final short n) {
        if (n <= 0 || n > 16 || !XSModelImpl.GLOBAL_COMP[n]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        final SymbolHash[] array = new SymbolHash[this.fGrammarCount];
        if (this.fGlobalComponents[n] == null) {
            for (int i = 0; i < this.fGrammarCount; ++i) {
                switch (n) {
                    case 3:
                    case 15:
                    case 16: {
                        array[i] = this.fGrammarList[i].fGlobalTypeDecls;
                        break;
                    }
                    case 1: {
                        array[i] = this.fGrammarList[i].fGlobalAttrDecls;
                        break;
                    }
                    case 2: {
                        array[i] = this.fGrammarList[i].fGlobalElemDecls;
                        break;
                    }
                    case 5: {
                        array[i] = this.fGrammarList[i].fGlobalAttrGrpDecls;
                        break;
                    }
                    case 6: {
                        array[i] = this.fGrammarList[i].fGlobalGroupDecls;
                        break;
                    }
                    case 11: {
                        array[i] = this.fGrammarList[i].fGlobalNotationDecls;
                        break;
                    }
                    case 10: {
                        array[i] = this.fGrammarList[i].fGlobalIDConstraintDecls;
                        break;
                    }
                }
            }
            if (n == 15 || n == 16) {
                this.fGlobalComponents[n] = new XSNamedMap4Types(this.fNamespaces, array, this.fGrammarCount, n);
            }
            else {
                this.fGlobalComponents[n] = new XSNamedMapImpl(this.fNamespaces, array, this.fGrammarCount);
            }
        }
        return this.fGlobalComponents[n];
    }
    
    public synchronized XSNamedMap getComponentsByNamespace(final short n, final String s) {
        if (n <= 0 || n > 16 || !XSModelImpl.GLOBAL_COMP[n]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        int i = 0;
        if (s != null) {
            while (i < this.fGrammarCount) {
                if (s.equals(this.fNamespaces[i])) {
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
        if (this.fNSComponents[i][n] == null) {
            SymbolHash symbolHash = null;
            switch (n) {
                case 3:
                case 15:
                case 16: {
                    symbolHash = this.fGrammarList[i].fGlobalTypeDecls;
                    break;
                }
                case 1: {
                    symbolHash = this.fGrammarList[i].fGlobalAttrDecls;
                    break;
                }
                case 2: {
                    symbolHash = this.fGrammarList[i].fGlobalElemDecls;
                    break;
                }
                case 5: {
                    symbolHash = this.fGrammarList[i].fGlobalAttrGrpDecls;
                    break;
                }
                case 6: {
                    symbolHash = this.fGrammarList[i].fGlobalGroupDecls;
                    break;
                }
                case 11: {
                    symbolHash = this.fGrammarList[i].fGlobalNotationDecls;
                    break;
                }
                case 10: {
                    symbolHash = this.fGrammarList[i].fGlobalIDConstraintDecls;
                    break;
                }
            }
            if (n == 15 || n == 16) {
                this.fNSComponents[i][n] = new XSNamedMap4Types(s, symbolHash, n);
            }
            else {
                this.fNSComponents[i][n] = new XSNamedMapImpl(s, symbolHash);
            }
        }
        return this.fNSComponents[i][n];
    }
    
    public XSTypeDefinition getTypeDefinition(final String s, final String s2) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSTypeDefinition)schemaGrammar.fGlobalTypeDecls.get(s);
    }
    
    public XSTypeDefinition getTypeDefinition(final String s, final String s2, final String s3) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalTypeDecl(s, s3);
    }
    
    public XSAttributeDeclaration getAttributeDeclaration(final String s, final String s2) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSAttributeDeclaration)schemaGrammar.fGlobalAttrDecls.get(s);
    }
    
    public XSAttributeDeclaration getAttributeDeclaration(final String s, final String s2, final String s3) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalAttributeDecl(s, s3);
    }
    
    public XSElementDeclaration getElementDeclaration(final String s, final String s2) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSElementDeclaration)schemaGrammar.fGlobalElemDecls.get(s);
    }
    
    public XSElementDeclaration getElementDeclaration(final String s, final String s2, final String s3) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalElementDecl(s, s3);
    }
    
    public XSAttributeGroupDefinition getAttributeGroup(final String s, final String s2) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSAttributeGroupDefinition)schemaGrammar.fGlobalAttrGrpDecls.get(s);
    }
    
    public XSAttributeGroupDefinition getAttributeGroup(final String s, final String s2, final String s3) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalAttributeGroupDecl(s, s3);
    }
    
    public XSModelGroupDefinition getModelGroupDefinition(final String s, final String s2) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSModelGroupDefinition)schemaGrammar.fGlobalGroupDecls.get(s);
    }
    
    public XSModelGroupDefinition getModelGroupDefinition(final String s, final String s2, final String s3) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalGroupDecl(s, s3);
    }
    
    public XSIDCDefinition getIDCDefinition(final String s, final String s2) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSIDCDefinition)schemaGrammar.fGlobalIDConstraintDecls.get(s);
    }
    
    public XSIDCDefinition getIDCDefinition(final String s, final String s2, final String s3) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getIDConstraintDecl(s, s3);
    }
    
    public XSNotationDeclaration getNotationDeclaration(final String s, final String s2) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSNotationDeclaration)schemaGrammar.fGlobalNotationDecls.get(s);
    }
    
    public XSNotationDeclaration getNotationDeclaration(final String s, final String s2, final String s3) {
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(null2EmptyString(s2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalNotationDecl(s, s3);
    }
    
    public synchronized XSObjectList getAnnotations() {
        if (this.fAnnotations != null) {
            return this.fAnnotations;
        }
        int n = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            n += this.fGrammarList[i].fNumAnnotations;
        }
        if (n == 0) {
            return this.fAnnotations = XSObjectListImpl.EMPTY_LIST;
        }
        final XSAnnotationImpl[] array = new XSAnnotationImpl[n];
        int n2 = 0;
        for (int j = 0; j < this.fGrammarCount; ++j) {
            final SchemaGrammar schemaGrammar = this.fGrammarList[j];
            if (schemaGrammar.fNumAnnotations > 0) {
                System.arraycopy(schemaGrammar.fAnnotations, 0, array, n2, schemaGrammar.fNumAnnotations);
                n2 += schemaGrammar.fNumAnnotations;
            }
        }
        return this.fAnnotations = new XSObjectListImpl(array, array.length);
    }
    
    private static final String null2EmptyString(final String s) {
        return (s == null) ? XMLSymbols.EMPTY_STRING : s;
    }
    
    public boolean hasIDConstraints() {
        return this.fHasIDC;
    }
    
    public XSObjectList getSubstitutionGroup(final XSElementDeclaration xsElementDeclaration) {
        return (XSObjectList)this.fSubGroupMap.get(xsElementDeclaration);
    }
    
    public int getLength() {
        return this.fGrammarCount;
    }
    
    public XSNamespaceItem item(final int n) {
        if (n < 0 || n >= this.fGrammarCount) {
            return null;
        }
        return this.fGrammarList[n];
    }
    
    public Object get(final int n) {
        if (n >= 0 && n < this.fGrammarCount) {
            return this.fGrammarList[n];
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    public int size() {
        return this.getLength();
    }
    
    public Iterator iterator() {
        return this.listIterator0(0);
    }
    
    public ListIterator listIterator() {
        return this.listIterator0(0);
    }
    
    public ListIterator listIterator(final int n) {
        if (n >= 0 && n < this.fGrammarCount) {
            return this.listIterator0(n);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    private ListIterator listIterator0(final int n) {
        return new XSNamespaceItemListIterator(n);
    }
    
    public Object[] toArray() {
        final Object[] array = new Object[this.fGrammarCount];
        this.toArray0(array);
        return array;
    }
    
    public Object[] toArray(Object[] array) {
        if (array.length < this.fGrammarCount) {
            array = (Object[])Array.newInstance(array.getClass().getComponentType(), this.fGrammarCount);
        }
        this.toArray0(array);
        if (array.length > this.fGrammarCount) {
            array[this.fGrammarCount] = null;
        }
        return array;
    }
    
    private void toArray0(final Object[] array) {
        if (this.fGrammarCount > 0) {
            System.arraycopy(this.fGrammarList, 0, array, 0, this.fGrammarCount);
        }
    }
    
    static {
        GLOBAL_COMP = new boolean[] { false, true, true, true, false, true, true, false, false, false, true, true, false, false, false, true, true };
    }
    
    private final class XSNamespaceItemListIterator implements ListIterator
    {
        private int index;
        
        public XSNamespaceItemListIterator(final int index) {
            this.index = index;
        }
        
        public boolean hasNext() {
            return this.index < XSModelImpl.this.fGrammarCount;
        }
        
        public Object next() {
            if (this.index < XSModelImpl.this.fGrammarCount) {
                return XSModelImpl.this.fGrammarList[this.index++];
            }
            throw new NoSuchElementException();
        }
        
        public boolean hasPrevious() {
            return this.index > 0;
        }
        
        public Object previous() {
            if (this.index > 0) {
                final SchemaGrammar[] access$100 = XSModelImpl.this.fGrammarList;
                final int index = this.index - 1;
                this.index = index;
                return access$100[index];
            }
            throw new NoSuchElementException();
        }
        
        public int nextIndex() {
            return this.index;
        }
        
        public int previousIndex() {
            return this.index - 1;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        public void set(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        public void add(final Object o) {
            throw new UnsupportedOperationException();
        }
    }
}
