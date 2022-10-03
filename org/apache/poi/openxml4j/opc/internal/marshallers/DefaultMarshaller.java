package org.apache.poi.openxml4j.opc.internal.marshallers;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;

public final class DefaultMarshaller implements PartMarshaller
{
    @Override
    public boolean marshall(final PackagePart part, final OutputStream out) throws OpenXML4JException {
        return part.save(out);
    }
}
