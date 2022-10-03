package javax.swing.text;

import java.io.IOException;

public class ChangedCharSetException extends IOException
{
    String charSetSpec;
    boolean charSetKey;
    
    public ChangedCharSetException(final String charSetSpec, final boolean charSetKey) {
        this.charSetSpec = charSetSpec;
        this.charSetKey = charSetKey;
    }
    
    public String getCharSetSpec() {
        return this.charSetSpec;
    }
    
    public boolean keyEqualsCharSet() {
        return this.charSetKey;
    }
}
