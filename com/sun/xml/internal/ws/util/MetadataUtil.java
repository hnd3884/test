package com.sun.xml.internal.ws.util;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.server.SDDocument;
import java.util.Map;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.istack.internal.NotNull;

public class MetadataUtil
{
    public static Map<String, SDDocument> getMetadataClosure(@NotNull final String systemId, @NotNull final SDDocumentResolver resolver, final boolean onlyTopLevelSchemas) {
        final Map<String, SDDocument> closureDocs = new HashMap<String, SDDocument>();
        final Set<String> remaining = new HashSet<String>();
        remaining.add(systemId);
        while (!remaining.isEmpty()) {
            final Iterator<String> it = remaining.iterator();
            final String current = it.next();
            remaining.remove(current);
            final SDDocument currentDoc = resolver.resolve(current);
            final SDDocument old = closureDocs.put(currentDoc.getURL().toExternalForm(), currentDoc);
            assert old == null;
            final Set<String> imports = currentDoc.getImports();
            if (currentDoc.isSchema() && onlyTopLevelSchemas) {
                continue;
            }
            for (final String importedDoc : imports) {
                if (closureDocs.get(importedDoc) == null) {
                    remaining.add(importedDoc);
                }
            }
        }
        return closureDocs;
    }
}
