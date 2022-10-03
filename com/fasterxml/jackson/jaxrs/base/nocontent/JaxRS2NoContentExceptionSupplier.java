package com.fasterxml.jackson.jaxrs.base.nocontent;

import javax.ws.rs.core.NoContentException;
import java.io.IOException;
import com.fasterxml.jackson.jaxrs.base.NoContentExceptionSupplier;

public class JaxRS2NoContentExceptionSupplier implements NoContentExceptionSupplier
{
    @Override
    public IOException createNoContentException() {
        return (IOException)new NoContentException("No content (empty input stream)");
    }
}
