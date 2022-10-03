package org.xbill.DNS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class NSECRecord extends Record
{
    private Name next;
    private int[] types;
    
    NSECRecord() {
    }
    
    Record getObject() {
        return new NSECRecord();
    }
    
    public NSECRecord(final Name name, final int dclass, final long ttl, final Name next, final int[] types) {
        super(name, 47, dclass, ttl);
        this.next = Record.checkName("next", next);
        for (int i = 0; i < types.length; ++i) {
            Type.check(types[i]);
        }
        System.arraycopy(types, 0, this.types = new int[types.length], 0, types.length);
        Arrays.sort(this.types);
    }
    
    private int[] listToArray(final List list) {
        final int size = list.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; ++i) {
            array[i] = list.get(i);
        }
        return array;
    }
    
    void rrFromWire(final DNSInput in) throws IOException {
        this.next = new Name(in);
        final int lastbase = -1;
        final List list = new ArrayList();
        while (in.remaining() > 0) {
            if (in.remaining() < 2) {
                throw new WireParseException("invalid bitmap descriptor");
            }
            final int mapbase = in.readU8();
            if (mapbase < lastbase) {
                throw new WireParseException("invalid ordering");
            }
            final int maplength = in.readU8();
            if (maplength > in.remaining()) {
                throw new WireParseException("invalid bitmap");
            }
            for (int i = 0; i < maplength; ++i) {
                final int current = in.readU8();
                if (current != 0) {
                    for (int j = 0; j < 8; ++j) {
                        if ((current & 1 << 7 - j) != 0x0) {
                            final int typecode = mapbase * 256 + i * 8 + j;
                            list.add(Mnemonic.toInteger(typecode));
                        }
                    }
                }
            }
        }
        this.types = this.listToArray(list);
    }
    
    void rdataFromString(final Tokenizer st, final Name origin) throws IOException {
        this.next = st.getName(origin);
        final List list = new ArrayList();
        while (true) {
            final Tokenizer.Token t = st.get();
            if (!t.isString()) {
                st.unget();
                Arrays.sort(this.types = this.listToArray(list));
                return;
            }
            final int typecode = Type.value(t.value);
            if (typecode < 0) {
                throw st.exception("Invalid type: " + t.value);
            }
            list.add(Mnemonic.toInteger(typecode));
        }
    }
    
    String rrToString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.next);
        for (int i = 0; i < this.types.length; ++i) {
            sb.append(" ");
            sb.append(Type.string(this.types[i]));
        }
        return sb.toString();
    }
    
    public Name getNext() {
        return this.next;
    }
    
    public int[] getTypes() {
        final int[] array = new int[this.types.length];
        System.arraycopy(this.types, 0, array, 0, this.types.length);
        return array;
    }
    
    public boolean hasType(final int type) {
        return Arrays.binarySearch(this.types, type) >= 0;
    }
    
    static void mapToWire(final DNSOutput out, final int[] array, final int mapbase, final int mapstart, final int mapend) {
        final int mapmax = array[mapend - 1] & 0xFF;
        final int maplength = mapmax / 8 + 1;
        final int[] map = new int[maplength];
        out.writeU8(mapbase);
        out.writeU8(maplength);
        for (int j = mapstart; j < mapend; ++j) {
            final int typecode = array[j];
            final int[] array2 = map;
            final int n = (typecode & 0xFF) / 8;
            array2[n] |= 1 << 7 - typecode % 8;
        }
        for (int j = 0; j < maplength; ++j) {
            out.writeU8(map[j]);
        }
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
        this.next.toWire(out, null, canonical);
        if (this.types.length == 0) {
            return;
        }
        int mapbase = -1;
        int mapstart = -1;
        for (int i = 0; i < this.types.length; ++i) {
            final int base = this.types[i] >> 8;
            if (base != mapbase) {
                if (mapstart >= 0) {
                    mapToWire(out, this.types, mapbase, mapstart, i);
                }
                mapbase = base;
                mapstart = i;
            }
        }
        mapToWire(out, this.types, mapbase, mapstart, this.types.length);
    }
}
