package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.internal.unmarshallers.UnmarshallContext;

public interface PartUnmarshaller
{
    PackagePart unmarshall(final UnmarshallContext p0, final InputStream p1) throws InvalidFormatException, IOException;
}
