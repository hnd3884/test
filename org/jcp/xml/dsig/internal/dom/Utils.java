package org.jcp.xml.dsig.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public final class Utils
{
    private Utils() {
    }
    
    public static byte[] readBytesFromStream(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final byte[] array = new byte[1024];
        int read;
        do {
            read = inputStream.read(array);
            if (read == -1) {
                break;
            }
            byteArrayOutputStream.write(array, 0, read);
        } while (read >= 1024);
        return byteArrayOutputStream.toByteArray();
    }
    
    static Set toNodeSet(final Iterator iterator) {
        final HashSet set = new HashSet();
        while (iterator.hasNext()) {
            final Node node = iterator.next();
            set.add(node);
            if (node.getNodeType() == 1) {
                final NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); ++i) {
                    set.add(attributes.item(i));
                }
            }
        }
        return set;
    }
    
    public static String parseIdFromSameDocumentURI(final String s) {
        if (s.length() == 0) {
            return null;
        }
        String s2 = s.substring(1);
        if (s2 != null && s2.startsWith("xpointer(id(")) {
            final int index = s2.indexOf(39);
            s2 = s2.substring(index + 1, s2.indexOf(39, index + 1));
        }
        return s2;
    }
    
    public static boolean sameDocumentURI(final String s) {
        return s != null && (s.length() == 0 || s.charAt(0) == '#');
    }
}
