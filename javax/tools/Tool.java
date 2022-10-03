package javax.tools;

import javax.lang.model.SourceVersion;
import java.util.Set;
import java.io.OutputStream;
import java.io.InputStream;

public interface Tool
{
    int run(final InputStream p0, final OutputStream p1, final OutputStream p2, final String... p3);
    
    Set<SourceVersion> getSourceVersions();
}
