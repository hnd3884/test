package org.apache.commons.compress.harmony.pack200;

import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map;
import java.util.Set;

public class IcBands extends BandSet
{
    private final Set innerClasses;
    private final CpBands cpBands;
    private int bit16Count;
    private final Map outerToInner;
    
    public IcBands(final SegmentHeader segmentHeader, final CpBands cpBands, final int effort) {
        super(effort, segmentHeader);
        this.innerClasses = new TreeSet();
        this.bit16Count = 0;
        this.outerToInner = new HashMap();
        this.cpBands = cpBands;
    }
    
    public void finaliseBands() {
        this.segmentHeader.setIc_count(this.innerClasses.size());
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing internal class bands...");
        final int[] ic_this_class = new int[this.innerClasses.size()];
        final int[] ic_flags = new int[this.innerClasses.size()];
        final int[] ic_outer_class = new int[this.bit16Count];
        final int[] ic_name = new int[this.bit16Count];
        int index2 = 0;
        final List innerClassesList = new ArrayList(this.innerClasses);
        for (int i = 0; i < ic_this_class.length; ++i) {
            final IcTuple icTuple = innerClassesList.get(i);
            ic_this_class[i] = icTuple.C.getIndex();
            ic_flags[i] = icTuple.F;
            if ((icTuple.F & 0x10000) != 0x0) {
                ic_outer_class[index2] = ((icTuple.C2 == null) ? 0 : (icTuple.C2.getIndex() + 1));
                ic_name[index2] = ((icTuple.N == null) ? 0 : (icTuple.N.getIndex() + 1));
                ++index2;
            }
        }
        byte[] encodedBand = this.encodeBandInt("ic_this_class", ic_this_class, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_this_class[" + ic_this_class.length + "]");
        encodedBand = this.encodeBandInt("ic_flags", ic_flags, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_flags[" + ic_flags.length + "]");
        encodedBand = this.encodeBandInt("ic_outer_class", ic_outer_class, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_outer_class[" + ic_outer_class.length + "]");
        encodedBand = this.encodeBandInt("ic_name", ic_name, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_name[" + ic_name.length + "]");
    }
    
    public void addInnerClass(final String name, final String outerName, final String innerName, int flags) {
        if (outerName != null || innerName != null) {
            if (this.namesArePredictable(name, outerName, innerName)) {
                final IcTuple innerClass = new IcTuple(this.cpBands.getCPClass(name), flags, null, null);
                this.addToMap(outerName, innerClass);
                this.innerClasses.add(innerClass);
            }
            else {
                flags |= 0x10000;
                final IcTuple icTuple = new IcTuple(this.cpBands.getCPClass(name), flags, this.cpBands.getCPClass(outerName), this.cpBands.getCPUtf8(innerName));
                final boolean added = this.innerClasses.add(icTuple);
                if (added) {
                    ++this.bit16Count;
                    this.addToMap(outerName, icTuple);
                }
            }
        }
        else {
            final IcTuple innerClass = new IcTuple(this.cpBands.getCPClass(name), flags, null, null);
            this.addToMap(this.getOuter(name), innerClass);
            this.innerClasses.add(innerClass);
        }
    }
    
    public List getInnerClassesForOuter(final String outerClassName) {
        return this.outerToInner.get(outerClassName);
    }
    
    private String getOuter(final String name) {
        return name.substring(0, name.lastIndexOf(36));
    }
    
    private void addToMap(final String outerName, final IcTuple icTuple) {
        List tuples = this.outerToInner.get(outerName);
        if (tuples == null) {
            tuples = new ArrayList();
            this.outerToInner.put(outerName, tuples);
            tuples.add(icTuple);
        }
        else {
            for (final IcTuple icT : tuples) {
                if (icTuple.equals(icT)) {
                    return;
                }
            }
            tuples.add(icTuple);
        }
    }
    
    private boolean namesArePredictable(final String name, final String outerName, final String innerName) {
        return name.equals(outerName + '$' + innerName) && innerName.indexOf(36) == -1;
    }
    
    public IcTuple getIcTuple(final CPClass inner) {
        for (final IcTuple icTuple : this.innerClasses) {
            if (icTuple.C.equals(inner)) {
                return icTuple;
            }
        }
        return null;
    }
    
    class IcTuple implements Comparable
    {
        protected CPClass C;
        protected int F;
        protected CPClass C2;
        protected CPUTF8 N;
        
        public IcTuple(final CPClass C, final int F, final CPClass C2, final CPUTF8 N) {
            this.C = C;
            this.F = F;
            this.C2 = C2;
            this.N = N;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof IcTuple) {
                final IcTuple icT = (IcTuple)o;
                if (this.C.equals(icT.C) && this.F == icT.F) {
                    if (this.C2 != null) {
                        if (!this.C2.equals(icT.C2)) {
                            return false;
                        }
                    }
                    else if (icT.C2 != null) {
                        return false;
                    }
                    if ((this.N == null) ? (icT.N == null) : this.N.equals(icT.N)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        
        @Override
        public String toString() {
            return this.C.toString();
        }
        
        @Override
        public int compareTo(final Object arg0) {
            return this.C.compareTo(((IcTuple)arg0).C);
        }
        
        public boolean isAnonymous() {
            final String className = this.C.toString();
            final String innerName = className.substring(className.lastIndexOf(36) + 1);
            return Character.isDigit(innerName.charAt(0));
        }
    }
}
