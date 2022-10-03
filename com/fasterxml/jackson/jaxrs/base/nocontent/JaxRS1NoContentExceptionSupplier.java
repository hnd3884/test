package com.fasterxml.jackson.jaxrs.base.nocontent;

import java.io.IOException;
import com.fasterxml.jackson.jaxrs.base.NoContentExceptionSupplier;

public class JaxRS1NoContentExceptionSupplier implements NoContentExceptionSupplier
{
    @Override
    public IOException createNoContentException() {
        return new IOException("No content (empty input stream)");
    }
}
