package org.owasp.esapi;

import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.errors.ExecutorException;
import java.util.List;
import java.io.File;

public interface Executor
{
    ExecuteResult executeSystemCommand(final File p0, final List p1) throws ExecutorException;
    
    ExecuteResult executeSystemCommand(final File p0, final List p1, final File p2, final Codec p3, final boolean p4, final boolean p5) throws ExecutorException;
}
