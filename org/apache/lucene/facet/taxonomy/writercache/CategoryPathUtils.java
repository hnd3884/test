package org.apache.lucene.facet.taxonomy.writercache;

import org.apache.lucene.facet.taxonomy.FacetLabel;

class CategoryPathUtils
{
    public static void serialize(final FacetLabel cp, final CharBlockArray charBlockArray) {
        charBlockArray.append((char)cp.length);
        if (cp.length == 0) {
            return;
        }
        for (int i = 0; i < cp.length; ++i) {
            charBlockArray.append((char)cp.components[i].length());
            charBlockArray.append(cp.components[i]);
        }
    }
    
    public static int hashCodeOfSerialized(final CharBlockArray charBlockArray, int offset) {
        final int length = charBlockArray.charAt(offset++);
        if (length == 0) {
            return 0;
        }
        int hash = length;
        for (int i = 0; i < length; ++i) {
            final int len = charBlockArray.charAt(offset++);
            hash = hash * 31 + charBlockArray.subSequence(offset, offset + len).hashCode();
            offset += len;
        }
        return hash;
    }
    
    public static boolean equalsToSerialized(final FacetLabel cp, final CharBlockArray charBlockArray, int offset) {
        final int n = charBlockArray.charAt(offset++);
        if (cp.length != n) {
            return false;
        }
        if (cp.length == 0) {
            return true;
        }
        for (int i = 0; i < cp.length; ++i) {
            final int len = charBlockArray.charAt(offset++);
            if (len != cp.components[i].length()) {
                return false;
            }
            if (!cp.components[i].equals(charBlockArray.subSequence(offset, offset + len))) {
                return false;
            }
            offset += len;
        }
        return true;
    }
}
