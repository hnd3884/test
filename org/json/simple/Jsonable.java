package org.json.simple;

import java.io.IOException;
import java.io.Writer;

public interface Jsonable
{
    String toJson();
    
    void toJson(final Writer p0) throws IOException;
}
