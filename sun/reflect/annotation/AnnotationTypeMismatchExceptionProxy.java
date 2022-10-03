package sun.reflect.annotation;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Method;

class AnnotationTypeMismatchExceptionProxy extends ExceptionProxy
{
    private static final long serialVersionUID = 7844069490309503934L;
    private Method member;
    private String foundType;
    
    AnnotationTypeMismatchExceptionProxy(final String foundType) {
        this.foundType = foundType;
    }
    
    AnnotationTypeMismatchExceptionProxy setMember(final Method member) {
        this.member = member;
        return this;
    }
    
    @Override
    protected RuntimeException generateException() {
        return new AnnotationTypeMismatchException(this.member, this.foundType);
    }
}
