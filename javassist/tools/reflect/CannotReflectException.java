package javassist.tools.reflect;

import javassist.CannotCompileException;

public class CannotReflectException extends CannotCompileException
{
    private static final long serialVersionUID = 1L;
    
    public CannotReflectException(final String msg) {
        super(msg);
    }
}
