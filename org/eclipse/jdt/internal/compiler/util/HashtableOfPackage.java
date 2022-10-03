package org.eclipse.jdt.internal.compiler.util;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;

public final class HashtableOfPackage
{
    public char[][] keyTable;
    public PackageBinding[] valueTable;
    public int elementSize;
    int threshold;
    
    public HashtableOfPackage() {
        this(3);
    }
    
    public HashtableOfPackage(final int size) {
        this.elementSize = 0;
        this.threshold = size;
        int extraRoom = (int)(size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new char[extraRoom][];
        this.valueTable = new PackageBinding[extraRoom];
    }
    
    public boolean containsKey(final char[] key) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return true;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return false;
    }
    
    public PackageBinding get(final char[] key) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return this.valueTable[index];
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return null;
    }
    
    public PackageBinding put(final char[] key, final PackageBinding value) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return this.valueTable[index] = value;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return value;
    }
    
    private void rehash() {
        final HashtableOfPackage newHashtable = new HashtableOfPackage(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            final char[] currentKey;
            if ((currentKey = this.keyTable[i]) != null) {
                newHashtable.put(currentKey, this.valueTable[i]);
            }
        }
        this.keyTable = newHashtable.keyTable;
        this.valueTable = newHashtable.valueTable;
        this.threshold = newHashtable.threshold;
    }
    
    public int size() {
        return this.elementSize;
    }
    
    @Override
    public String toString() {
        String s = "";
        for (int i = 0, length = this.valueTable.length; i < length; ++i) {
            final PackageBinding pkg;
            if ((pkg = this.valueTable[i]) != null) {
                s = String.valueOf(s) + pkg.toString() + "\n";
            }
        }
        return s;
    }
}
