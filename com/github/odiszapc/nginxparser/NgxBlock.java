package com.github.odiszapc.nginxparser;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

public class NgxBlock extends NgxAbstractEntry implements Iterable<NgxEntry>
{
    private Collection<NgxEntry> entries;
    
    public NgxBlock() {
        super(new String[0]);
        this.entries = new ArrayList<NgxEntry>();
    }
    
    public NgxBlock(final String... array) {
        super(array);
        this.entries = new ArrayList<NgxEntry>();
    }
    
    public Collection<NgxEntry> getEntries() {
        return this.entries;
    }
    
    public void addEntry(final NgxEntry ngxEntry) {
        this.entries.add(ngxEntry);
    }
    
    @Override
    public String toString() {
        return super.toString() + " {";
    }
    
    @Override
    public Iterator<NgxEntry> iterator() {
        return this.getEntries().iterator();
    }
    
    public void remove(final NgxEntry ngxEntry) {
        if (null == ngxEntry) {
            throw new NullPointerException("Item can not be null");
        }
        final Iterator<NgxEntry> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            final NgxEntry ngxEntry2 = iterator.next();
            switch (NgxEntryType.fromClass(((NgxBlock)ngxEntry2).getClass())) {
                case PARAM: {
                    if (ngxEntry2.equals(ngxEntry)) {
                        iterator.remove();
                        continue;
                    }
                    continue;
                }
                case BLOCK: {
                    if (ngxEntry2.equals(ngxEntry)) {
                        iterator.remove();
                        continue;
                    }
                    ((NgxBlock)ngxEntry2).remove(ngxEntry);
                    continue;
                }
            }
        }
    }
    
    public void removeAll(final Iterable<NgxEntry> iterable) {
        if (null == iterable) {
            throw new NullPointerException("Items can not be null");
        }
        final Iterator<NgxEntry> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            this.remove(iterator.next());
        }
    }
    
    public <T extends NgxEntry> T find(final Class<T> clazz, final String... array) {
        final List<NgxEntry> all = this.findAll(clazz, new ArrayList<NgxEntry>(), array);
        if (all.isEmpty()) {
            return null;
        }
        return (T)all.get(0);
    }
    
    public NgxBlock findBlock(final String... array) {
        final NgxBlock find = this.find(NgxConfig.BLOCK, array);
        if (null == find) {
            return null;
        }
        return find;
    }
    
    public NgxParam findParam(final String... array) {
        final NgxParam find = this.find(NgxConfig.PARAM, array);
        if (null == find) {
            return null;
        }
        return find;
    }
    
    public <T extends NgxEntry> List<NgxEntry> findAll(final Class<T> clazz, final String... array) {
        return this.findAll(clazz, new ArrayList<NgxEntry>(), array);
    }
    
    public <T extends NgxEntry> List<NgxEntry> findAll(final Class<T> clazz, final List<NgxEntry> list, final String... array) {
        final ArrayList list2 = new ArrayList();
        if (0 == array.length) {
            return list2;
        }
        final String s = array[0];
        final String[] array2 = (array.length > 1) ? Arrays.copyOfRange(array, 1, array.length) : new String[0];
        for (final NgxEntry ngxEntry : this.getEntries()) {
            switch (NgxEntryType.fromClass(((NgxParam)ngxEntry).getClass())) {
                case PARAM: {
                    final NgxParam ngxParam = (NgxParam)ngxEntry;
                    if (ngxParam.getName().equals(s) && ngxParam.getClass() == clazz) {
                        list2.add(ngxParam);
                        continue;
                    }
                    continue;
                }
                case BLOCK: {
                    final NgxBlock ngxBlock = (NgxBlock)ngxEntry;
                    if (array2.length > 0) {
                        if (ngxBlock.getName().equals(s)) {
                            list2.addAll(ngxBlock.findAll((Class<NgxEntry>)clazz, list, array2));
                            continue;
                        }
                        continue;
                    }
                    else {
                        if (ngxBlock.getName().equals(s) && clazz.equals(NgxBlock.class)) {
                            list2.add(ngxBlock);
                            continue;
                        }
                        continue;
                    }
                    break;
                }
            }
        }
        return list2;
    }
}
