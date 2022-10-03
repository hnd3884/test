package eu.medsea.mimeutil;

import java.util.Iterator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.Set;

class MimeTypeHashSet implements Set, Collection
{
    private Set hashSet;
    
    MimeTypeHashSet() {
        this.hashSet = new LinkedHashSet();
    }
    
    MimeTypeHashSet(final Collection collection) {
        this.hashSet = new LinkedHashSet();
        this.addAll(collection);
    }
    
    MimeTypeHashSet(final int initialCapacity) {
        this.hashSet = new LinkedHashSet();
        this.hashSet = new HashSet(initialCapacity);
    }
    
    MimeTypeHashSet(final int initialCapacity, final float loadFactor) {
        this.hashSet = new LinkedHashSet();
        this.hashSet = new HashSet(initialCapacity, loadFactor);
    }
    
    MimeTypeHashSet(final String arg0) {
        this.hashSet = new LinkedHashSet();
        this.add(arg0);
    }
    
    MimeTypeHashSet(final String[] arg0) {
        this.hashSet = new LinkedHashSet();
        this.add(arg0);
    }
    
    MimeTypeHashSet(final MimeType mimeType) {
        this.hashSet = new LinkedHashSet();
        this.add(mimeType);
    }
    
    public boolean add(final Object arg0) {
        if (arg0 == null) {
            return false;
        }
        if (arg0 instanceof MimeType) {
            if (this.contains(arg0)) {
                this.updateSpecificity((MimeType)arg0);
            }
            MimeUtil.addKnownMimeType((MimeType)arg0);
            return this.hashSet.add(arg0);
        }
        if (arg0 instanceof Collection) {
            return this.addAll((Collection)arg0);
        }
        if (arg0 instanceof String) {
            final String[] mimeTypes = ((String)arg0).split(",");
            boolean added = false;
            for (int i = 0; i < mimeTypes.length; ++i) {
                try {
                    if (this.add(new MimeType(mimeTypes[i]))) {
                        added = true;
                    }
                }
                catch (final Exception ex) {}
            }
            return added;
        }
        if (arg0 instanceof String[]) {
            boolean added2 = false;
            final String[] mimeTypes2 = (String[])arg0;
            for (int i = 0; i < mimeTypes2.length; ++i) {
                final String[] parts = mimeTypes2[i].split(",");
                for (int j = 0; j < parts.length; ++j) {
                    try {
                        if (this.add(new MimeType(parts[j]))) {
                            added2 = true;
                        }
                    }
                    catch (final Exception ex2) {}
                }
            }
            return added2;
        }
        return false;
    }
    
    public boolean addAll(final Collection arg0) throws NullPointerException {
        if (arg0 == null) {
            throw new NullPointerException();
        }
        boolean added = false;
        final Iterator it = arg0.iterator();
        while (it.hasNext()) {
            try {
                if (!this.add(it.next())) {
                    continue;
                }
                added = true;
            }
            catch (final Exception ex) {}
        }
        return added;
    }
    
    public void clear() {
        this.hashSet.clear();
    }
    
    public boolean contains(final Object o) {
        if (o instanceof MimeType) {
            return this.hashSet.contains(o);
        }
        if (o instanceof Collection) {
            return this.containsAll((Collection)o);
        }
        if (o instanceof String) {
            final String[] parts = ((String)o).split(",");
            for (int i = 0; i < parts.length; ++i) {
                if (!this.contains(new MimeType(parts[i]))) {
                    return false;
                }
            }
            return true;
        }
        if (o instanceof String[]) {
            final String[] mimeTypes = (String[])o;
            for (int i = 0; i < mimeTypes.length; ++i) {
                final String[] parts2 = mimeTypes[i].split(",");
                for (int j = 0; j < parts2.length; ++j) {
                    if (!this.contains(new MimeType(parts2[j]))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean containsAll(final Collection arg0) {
        if (arg0 == null) {
            throw new NullPointerException();
        }
        final Iterator it = arg0.iterator();
        while (it.hasNext()) {
            if (!this.contains(it.next())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isEmpty() {
        return this.hashSet.isEmpty();
    }
    
    public Iterator iterator() {
        return this.hashSet.iterator();
    }
    
    public boolean remove(final Object o) {
        boolean removed = false;
        if (o == null) {
            return removed;
        }
        if (o instanceof MimeType) {
            return this.hashSet.remove(o);
        }
        if (o instanceof String) {
            final String[] parts = ((String)o).split(",");
            for (int i = 0; i < parts.length; ++i) {
                if (this.remove(new MimeType(parts[i]))) {
                    removed = true;
                }
            }
        }
        else if (o instanceof String[]) {
            final String[] mimeTypes = (String[])o;
            for (int i = 0; i < mimeTypes.length; ++i) {
                final String[] parts2 = mimeTypes[i].split(",");
                for (int j = 0; j < parts2.length; ++j) {
                    if (this.remove(new MimeType(parts2[j]))) {
                        removed = true;
                    }
                }
            }
        }
        else if (o instanceof Collection) {
            return this.removeAll((Collection)o);
        }
        return removed;
    }
    
    public boolean removeAll(final Collection arg0) {
        if (arg0 == null) {
            throw new NullPointerException();
        }
        boolean removed = false;
        final Iterator it = arg0.iterator();
        while (it.hasNext()) {
            if (this.remove(it.next())) {
                removed = true;
            }
        }
        return removed;
    }
    
    public boolean retainAll(final Collection arg0) {
        if (arg0 == null) {
            throw new NullPointerException();
        }
        final Collection c = new MimeTypeHashSet(arg0);
        return this.hashSet.retainAll(c);
    }
    
    public int size() {
        return this.hashSet.size();
    }
    
    public Object[] toArray() {
        return this.hashSet.toArray();
    }
    
    public Object[] toArray(final Object[] arg0) {
        return this.hashSet.toArray(arg0);
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        final Iterator it = this.iterator();
        while (it.hasNext()) {
            buf.append(it.next().toString());
            if (it.hasNext()) {
                buf.append(",");
            }
        }
        return buf.toString();
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        final Collection c = new MimeTypeHashSet();
        c.add(o);
        return this.match(c);
    }
    
    private boolean match(final Collection c) {
        if (this.size() != c.size()) {
            return false;
        }
        final MimeType[] mt = c.toArray(new MimeType[c.size()]);
        for (int i = 0; i < mt.length; ++i) {
            if (!this.contains(mt[i])) {
                return false;
            }
        }
        return true;
    }
    
    private void updateSpecificity(final MimeType o) {
        final MimeType mimeType = this.get(o);
        final int specificity = mimeType.getSpecificity() + o.getSpecificity();
        mimeType.setSpecificity(specificity);
        o.setSpecificity(specificity);
    }
    
    private MimeType get(final MimeType mimeType) {
        final Iterator it = this.hashSet.iterator();
        while (it.hasNext()) {
            final MimeType mt = it.next();
            if (mt.equals(mimeType)) {
                return mt;
            }
        }
        return null;
    }
    
    public Collection matches(final String pattern) {
        final Collection c = new MimeTypeHashSet();
        final Iterator it = this.iterator();
        while (it.hasNext()) {
            final MimeType mimeType = it.next();
            if (mimeType.toString().matches(pattern)) {
                c.add(mimeType);
            }
        }
        return c;
    }
}
