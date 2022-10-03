package org.apache.poi.openxml4j.opc.internal;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackagePart;

public interface PartMarshaller
{
    boolean marshall(final PackagePart p0, final OutputStream p1) throws OpenXML4JException;
}
