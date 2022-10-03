package com.sun.xml.internal.fastinfoset.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class PrefixArray extends ValueArray
{
    public static final int PREFIX_MAP_SIZE = 64;
    private int _initialCapacity;
    public String[] _array;
    private PrefixArray _readOnlyArray;
    private PrefixEntry[] _prefixMap;
    private PrefixEntry _prefixPool;
    private NamespaceEntry _namespacePool;
    private NamespaceEntry[] _inScopeNamespaces;
    public int[] _currentInScope;
    public int _declarationId;
    
    public PrefixArray(final int initialCapacity, final int maximumCapacity) {
        this._prefixMap = new PrefixEntry[64];
        this._initialCapacity = initialCapacity;
        this._maximumCapacity = maximumCapacity;
        this._array = new String[initialCapacity];
        this._inScopeNamespaces = new NamespaceEntry[initialCapacity + 2];
        this._currentInScope = new int[initialCapacity + 2];
        this.increaseNamespacePool(initialCapacity);
        this.increasePrefixPool(initialCapacity);
        this.initializeEntries();
    }
    
    public PrefixArray() {
        this(10, Integer.MAX_VALUE);
    }
    
    private final void initializeEntries() {
        this._inScopeNamespaces[0] = this._namespacePool;
        this._namespacePool = this._namespacePool.next;
        this._inScopeNamespaces[0].next = null;
        this._inScopeNamespaces[0].prefix = "";
        this._inScopeNamespaces[0].namespaceName = "";
        this._inScopeNamespaces[0].namespaceIndex = (this._currentInScope[0] = 0);
        int index = KeyIntMap.indexFor(KeyIntMap.hashHash(this._inScopeNamespaces[0].prefix.hashCode()), this._prefixMap.length);
        this._prefixMap[index] = this._prefixPool;
        this._prefixPool = this._prefixPool.next;
        this._prefixMap[index].next = null;
        this._prefixMap[index].prefixId = 0;
        this._inScopeNamespaces[1] = this._namespacePool;
        this._namespacePool = this._namespacePool.next;
        this._inScopeNamespaces[1].next = null;
        this._inScopeNamespaces[1].prefix = "xml";
        this._inScopeNamespaces[1].namespaceName = "http://www.w3.org/XML/1998/namespace";
        this._inScopeNamespaces[1].namespaceIndex = (this._currentInScope[1] = 1);
        index = KeyIntMap.indexFor(KeyIntMap.hashHash(this._inScopeNamespaces[1].prefix.hashCode()), this._prefixMap.length);
        if (this._prefixMap[index] == null) {
            this._prefixMap[index] = this._prefixPool;
            this._prefixPool = this._prefixPool.next;
            this._prefixMap[index].next = null;
        }
        else {
            final PrefixEntry e = this._prefixMap[index];
            this._prefixMap[index] = this._prefixPool;
            this._prefixPool = this._prefixPool.next;
            this._prefixMap[index].next = e;
        }
        this._prefixMap[index].prefixId = 1;
    }
    
    private final void increaseNamespacePool(final int capacity) {
        if (this._namespacePool == null) {
            this._namespacePool = new NamespaceEntry();
        }
        for (int i = 0; i < capacity; ++i) {
            final NamespaceEntry ne = new NamespaceEntry();
            ne.next = this._namespacePool;
            this._namespacePool = ne;
        }
    }
    
    private final void increasePrefixPool(final int capacity) {
        if (this._prefixPool == null) {
            this._prefixPool = new PrefixEntry();
        }
        for (int i = 0; i < capacity; ++i) {
            final PrefixEntry pe = new PrefixEntry();
            pe.next = this._prefixPool;
            this._prefixPool = pe;
        }
    }
    
    public int countNamespacePool() {
        int i = 0;
        for (NamespaceEntry e = this._namespacePool; e != null; e = e.next) {
            ++i;
        }
        return i;
    }
    
    public int countPrefixPool() {
        int i = 0;
        for (PrefixEntry e = this._prefixPool; e != null; e = e.next) {
            ++i;
        }
        return i;
    }
    
    @Override
    public final void clear() {
        for (int i = this._readOnlyArraySize; i < this._size; ++i) {
            this._array[i] = null;
        }
        this._size = this._readOnlyArraySize;
    }
    
    public final void clearCompletely() {
        this._prefixPool = null;
        this._namespacePool = null;
        for (int i = 0; i < this._size + 2; ++i) {
            this._currentInScope[i] = 0;
            this._inScopeNamespaces[i] = null;
        }
        for (int i = 0; i < this._prefixMap.length; ++i) {
            this._prefixMap[i] = null;
        }
        this.increaseNamespacePool(this._initialCapacity);
        this.increasePrefixPool(this._initialCapacity);
        this.initializeEntries();
        this._declarationId = 0;
        this.clear();
    }
    
    public final String[] getArray() {
        if (this._array == null) {
            return null;
        }
        final String[] clonedArray = new String[this._array.length];
        System.arraycopy(this._array, 0, clonedArray, 0, this._array.length);
        return clonedArray;
    }
    
    @Override
    public final void setReadOnlyArray(final ValueArray readOnlyArray, final boolean clear) {
        if (!(readOnlyArray instanceof PrefixArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { readOnlyArray }));
        }
        this.setReadOnlyArray((PrefixArray)readOnlyArray, clear);
    }
    
    public final void setReadOnlyArray(final PrefixArray readOnlyArray, final boolean clear) {
        if (readOnlyArray != null) {
            this._readOnlyArray = readOnlyArray;
            this._readOnlyArraySize = readOnlyArray.getSize();
            this.clearCompletely();
            this._inScopeNamespaces = new NamespaceEntry[this._readOnlyArraySize + this._inScopeNamespaces.length];
            this._currentInScope = new int[this._readOnlyArraySize + this._currentInScope.length];
            this.initializeEntries();
            if (clear) {
                this.clear();
            }
            this._array = this.getCompleteArray();
            this._size = this._readOnlyArraySize;
        }
    }
    
    public final String[] getCompleteArray() {
        if (this._readOnlyArray == null) {
            return this.getArray();
        }
        final String[] ra = this._readOnlyArray.getCompleteArray();
        final String[] a = new String[this._readOnlyArraySize + this._array.length];
        System.arraycopy(ra, 0, a, 0, this._readOnlyArraySize);
        return a;
    }
    
    public final String get(final int i) {
        return this._array[i];
    }
    
    public final int add(final String s) {
        if (this._size == this._array.length) {
            this.resize();
        }
        this._array[this._size++] = s;
        return this._size;
    }
    
    protected final void resize() {
        if (this._size == this._maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
        }
        int newSize = this._size * 3 / 2 + 1;
        if (newSize > this._maximumCapacity) {
            newSize = this._maximumCapacity;
        }
        final String[] newArray = new String[newSize];
        System.arraycopy(this._array, 0, newArray, 0, this._size);
        this._array = newArray;
        newSize += 2;
        final NamespaceEntry[] newInScopeNamespaces = new NamespaceEntry[newSize];
        System.arraycopy(this._inScopeNamespaces, 0, newInScopeNamespaces, 0, this._inScopeNamespaces.length);
        this._inScopeNamespaces = newInScopeNamespaces;
        final int[] newCurrentInScope = new int[newSize];
        System.arraycopy(this._currentInScope, 0, newCurrentInScope, 0, this._currentInScope.length);
        this._currentInScope = newCurrentInScope;
    }
    
    public final void clearDeclarationIds() {
        for (int i = 0; i < this._size; ++i) {
            final NamespaceEntry e = this._inScopeNamespaces[i];
            if (e != null) {
                e.declarationId = 0;
            }
        }
        this._declarationId = 1;
    }
    
    public final void pushScope(int prefixIndex, int namespaceIndex) throws FastInfosetException {
        if (this._namespacePool == null) {
            this.increaseNamespacePool(16);
        }
        final NamespaceEntry e = this._namespacePool;
        this._namespacePool = e.next;
        final NamespaceEntry current = this._inScopeNamespaces[++prefixIndex];
        if (current == null) {
            e.declarationId = this._declarationId;
            e.namespaceIndex = (this._currentInScope[prefixIndex] = ++namespaceIndex);
            e.next = null;
            this._inScopeNamespaces[prefixIndex] = e;
        }
        else {
            if (current.declarationId >= this._declarationId) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateNamespaceAttribute"));
            }
            e.declarationId = this._declarationId;
            e.namespaceIndex = (this._currentInScope[prefixIndex] = ++namespaceIndex);
            e.next = current;
            current.declarationId = 0;
            this._inScopeNamespaces[prefixIndex] = e;
        }
    }
    
    public final void pushScopeWithPrefixEntry(final String prefix, final String namespaceName, int prefixIndex, int namespaceIndex) throws FastInfosetException {
        if (this._namespacePool == null) {
            this.increaseNamespacePool(16);
        }
        if (this._prefixPool == null) {
            this.increasePrefixPool(16);
        }
        final NamespaceEntry e = this._namespacePool;
        this._namespacePool = e.next;
        final NamespaceEntry current = this._inScopeNamespaces[++prefixIndex];
        if (current == null) {
            e.declarationId = this._declarationId;
            e.namespaceIndex = (this._currentInScope[prefixIndex] = ++namespaceIndex);
            e.next = null;
            this._inScopeNamespaces[prefixIndex] = e;
        }
        else {
            if (current.declarationId >= this._declarationId) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateNamespaceAttribute"));
            }
            e.declarationId = this._declarationId;
            e.namespaceIndex = (this._currentInScope[prefixIndex] = ++namespaceIndex);
            e.next = current;
            current.declarationId = 0;
            this._inScopeNamespaces[prefixIndex] = e;
        }
        final PrefixEntry p = this._prefixPool;
        this._prefixPool = this._prefixPool.next;
        p.prefixId = prefixIndex;
        e.prefix = prefix;
        e.namespaceName = namespaceName;
        e.prefixEntryIndex = KeyIntMap.indexFor(KeyIntMap.hashHash(prefix.hashCode()), this._prefixMap.length);
        final PrefixEntry pCurrent = this._prefixMap[e.prefixEntryIndex];
        p.next = pCurrent;
        this._prefixMap[e.prefixEntryIndex] = p;
    }
    
    public final void popScope(int prefixIndex) {
        final NamespaceEntry e = this._inScopeNamespaces[++prefixIndex];
        this._inScopeNamespaces[prefixIndex] = e.next;
        this._currentInScope[prefixIndex] = ((e.next != null) ? e.next.namespaceIndex : 0);
        e.next = this._namespacePool;
        this._namespacePool = e;
    }
    
    public final void popScopeWithPrefixEntry(int prefixIndex) {
        final NamespaceEntry e = this._inScopeNamespaces[++prefixIndex];
        this._inScopeNamespaces[prefixIndex] = e.next;
        this._currentInScope[prefixIndex] = ((e.next != null) ? e.next.namespaceIndex : 0);
        e.prefix = (e.namespaceName = null);
        e.next = this._namespacePool;
        this._namespacePool = e;
        PrefixEntry current = this._prefixMap[e.prefixEntryIndex];
        if (current.prefixId == prefixIndex) {
            this._prefixMap[e.prefixEntryIndex] = current.next;
            current.next = this._prefixPool;
            this._prefixPool = current;
        }
        else {
            PrefixEntry prev = current;
            for (current = current.next; current != null; current = current.next) {
                if (current.prefixId == prefixIndex) {
                    prev.next = current.next;
                    current.next = this._prefixPool;
                    this._prefixPool = current;
                    break;
                }
                prev = current;
            }
        }
    }
    
    public final String getNamespaceFromPrefix(final String prefix) {
        final int index = KeyIntMap.indexFor(KeyIntMap.hashHash(prefix.hashCode()), this._prefixMap.length);
        for (PrefixEntry pe = this._prefixMap[index]; pe != null; pe = pe.next) {
            final NamespaceEntry ne = this._inScopeNamespaces[pe.prefixId];
            if (prefix == ne.prefix || prefix.equals(ne.prefix)) {
                return ne.namespaceName;
            }
        }
        return null;
    }
    
    public final String getPrefixFromNamespace(final String namespaceName) {
        int position = 0;
        while (++position < this._size + 2) {
            final NamespaceEntry ne = this._inScopeNamespaces[position];
            if (ne != null && namespaceName.equals(ne.namespaceName)) {
                return ne.prefix;
            }
        }
        return null;
    }
    
    public final Iterator getPrefixes() {
        return new Iterator() {
            int _position = 1;
            NamespaceEntry _ne = PrefixArray.this._inScopeNamespaces[this._position];
            
            @Override
            public boolean hasNext() {
                return this._ne != null;
            }
            
            @Override
            public Object next() {
                if (this._position == PrefixArray.this._size + 2) {
                    throw new NoSuchElementException();
                }
                final String prefix = this._ne.prefix;
                this.moveToNext();
                return prefix;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private final void moveToNext() {
                while (++this._position < PrefixArray.this._size + 2) {
                    this._ne = PrefixArray.this._inScopeNamespaces[this._position];
                    if (this._ne != null) {
                        return;
                    }
                }
                this._ne = null;
            }
        };
    }
    
    public final Iterator getPrefixesFromNamespace(final String namespaceName) {
        return new Iterator() {
            String _namespaceName = namespaceName;
            int _position = 0;
            NamespaceEntry _ne;
            
            {
                this.moveToNext();
            }
            
            @Override
            public boolean hasNext() {
                return this._ne != null;
            }
            
            @Override
            public Object next() {
                if (this._position == PrefixArray.this._size + 2) {
                    throw new NoSuchElementException();
                }
                final String prefix = this._ne.prefix;
                this.moveToNext();
                return prefix;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private final void moveToNext() {
                while (++this._position < PrefixArray.this._size + 2) {
                    this._ne = PrefixArray.this._inScopeNamespaces[this._position];
                    if (this._ne != null && this._namespaceName.equals(this._ne.namespaceName)) {
                        return;
                    }
                }
                this._ne = null;
            }
        };
    }
    
    private static class PrefixEntry
    {
        private PrefixEntry next;
        private int prefixId;
    }
    
    private static class NamespaceEntry
    {
        private NamespaceEntry next;
        private int declarationId;
        private int namespaceIndex;
        private String prefix;
        private String namespaceName;
        private int prefixEntryIndex;
    }
}
