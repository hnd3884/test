package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.HashMap;
import java.util.Collections;
import java.util.TreeSet;
import org.apache.commons.compress.harmony.unpack200.Segment;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

public class ClassConstantPool
{
    protected HashSet entriesContainsSet;
    protected HashSet othersContainsSet;
    private final HashSet mustStartClassPool;
    protected Map indexCache;
    private final List others;
    private final List entries;
    private boolean resolved;
    
    public ClassConstantPool() {
        this.entriesContainsSet = new HashSet();
        this.othersContainsSet = new HashSet();
        this.mustStartClassPool = new HashSet();
        this.others = new ArrayList(500);
        this.entries = new ArrayList(500);
    }
    
    public ClassFileEntry add(final ClassFileEntry entry) {
        if (entry instanceof ByteCode) {
            return null;
        }
        if (entry instanceof ConstantPoolEntry) {
            if (this.entriesContainsSet.add(entry)) {
                this.entries.add(entry);
            }
        }
        else if (this.othersContainsSet.add(entry)) {
            this.others.add(entry);
        }
        return entry;
    }
    
    public void addNestedEntries() {
        boolean added = true;
        final ArrayList parents = new ArrayList(512);
        final ArrayList children = new ArrayList(512);
        parents.addAll(this.entries);
        parents.addAll(this.others);
        while (added || parents.size() > 0) {
            children.clear();
            final int entriesOriginalSize = this.entries.size();
            final int othersOriginalSize = this.others.size();
            for (int indexParents = 0; indexParents < parents.size(); ++indexParents) {
                final ClassFileEntry entry = parents.get(indexParents);
                final ClassFileEntry[] entryChildren = entry.getNestedClassFileEntries();
                children.addAll(Arrays.asList(entryChildren));
                final boolean isAtStart = entry instanceof ByteCode && ((ByteCode)entry).nestedMustStartClassPool();
                if (isAtStart) {
                    this.mustStartClassPool.addAll(Arrays.asList(entryChildren));
                }
                this.add(entry);
            }
            added = (this.entries.size() != entriesOriginalSize || this.others.size() != othersOriginalSize);
            parents.clear();
            parents.addAll(children);
        }
    }
    
    public int indexOf(final ClassFileEntry entry) {
        if (!this.resolved) {
            throw new IllegalStateException("Constant pool is not yet resolved; this does not make any sense");
        }
        if (null == this.indexCache) {
            throw new IllegalStateException("Index cache is not initialized!");
        }
        final Integer entryIndex = this.indexCache.get(entry);
        if (entryIndex != null) {
            return entryIndex + 1;
        }
        return -1;
    }
    
    public int size() {
        return this.entries.size();
    }
    
    public ClassFileEntry get(int i) {
        if (!this.resolved) {
            throw new IllegalStateException("Constant pool is not yet resolved; this does not make any sense");
        }
        return this.entries.get(--i);
    }
    
    public void resolve(final Segment segment) {
        this.initialSort();
        this.sortClassPool();
        this.resolved = true;
        for (int it = 0; it < this.entries.size(); ++it) {
            final ClassFileEntry entry = this.entries.get(it);
            entry.resolve(this);
        }
        for (int it = 0; it < this.others.size(); ++it) {
            final ClassFileEntry entry = this.others.get(it);
            entry.resolve(this);
        }
    }
    
    private void initialSort() {
        final TreeSet inCpAll = new TreeSet((arg0, arg1) -> ((ConstantPoolEntry)arg0).getGlobalIndex() - ((ConstantPoolEntry)arg1).getGlobalIndex());
        final TreeSet cpUtf8sNotInCpAll = new TreeSet((arg0, arg1) -> ((CPUTF8)arg0).underlyingString().compareTo(((CPUTF8)arg1).underlyingString()));
        final TreeSet cpClassesNotInCpAll = new TreeSet((arg0, arg1) -> ((CPClass)arg0).getName().compareTo(((CPClass)arg1).getName()));
        for (int index = 0; index < this.entries.size(); ++index) {
            final ConstantPoolEntry entry = this.entries.get(index);
            if (entry.getGlobalIndex() == -1) {
                if (entry instanceof CPUTF8) {
                    cpUtf8sNotInCpAll.add(entry);
                }
                else {
                    if (!(entry instanceof CPClass)) {
                        throw new Error("error");
                    }
                    cpClassesNotInCpAll.add(entry);
                }
            }
            else {
                inCpAll.add(entry);
            }
        }
        this.entries.clear();
        this.entries.addAll(inCpAll);
        this.entries.addAll(cpUtf8sNotInCpAll);
        this.entries.addAll(cpClassesNotInCpAll);
    }
    
    public List entries() {
        return Collections.unmodifiableList((List<?>)this.entries);
    }
    
    protected void sortClassPool() {
        final ArrayList startOfPool = new ArrayList(this.entries.size());
        final ArrayList finalSort = new ArrayList(this.entries.size());
        for (int i = 0; i < this.entries.size(); ++i) {
            final ClassFileEntry nextEntry = this.entries.get(i);
            if (this.mustStartClassPool.contains(nextEntry)) {
                startOfPool.add(nextEntry);
            }
            else {
                finalSort.add(nextEntry);
            }
        }
        this.indexCache = new HashMap(this.entries.size());
        int index = 0;
        this.entries.clear();
        for (int itIndex = 0; itIndex < startOfPool.size(); ++itIndex) {
            final ClassFileEntry entry = startOfPool.get(itIndex);
            this.indexCache.put(entry, index);
            if (entry instanceof CPLong || entry instanceof CPDouble) {
                this.entries.add(entry);
                this.entries.add(entry);
                index += 2;
            }
            else {
                this.entries.add(entry);
                ++index;
            }
        }
        for (int itFinal = 0; itFinal < finalSort.size(); ++itFinal) {
            final ClassFileEntry entry = finalSort.get(itFinal);
            this.indexCache.put(entry, index);
            if (entry instanceof CPLong || entry instanceof CPDouble) {
                this.entries.add(entry);
                this.entries.add(entry);
                index += 2;
            }
            else {
                this.entries.add(entry);
                ++index;
            }
        }
    }
    
    public ClassFileEntry addWithNestedEntries(final ClassFileEntry entry) {
        this.add(entry);
        final ClassFileEntry[] nestedEntries = entry.getNestedClassFileEntries();
        for (int i = 0; i < nestedEntries.length; ++i) {
            this.addWithNestedEntries(nestedEntries[i]);
        }
        return entry;
    }
}
