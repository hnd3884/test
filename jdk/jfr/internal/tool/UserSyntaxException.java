package jdk.jfr.internal.tool;

final class UserSyntaxException extends Exception
{
    private static final long serialVersionUID = 3437009454344160933L;
    
    public UserSyntaxException(final String s) {
        super(s);
    }
}
