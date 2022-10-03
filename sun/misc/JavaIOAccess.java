package sun.misc;

import java.nio.charset.Charset;
import java.io.Console;

public interface JavaIOAccess
{
    Console console();
    
    Charset charset();
}
