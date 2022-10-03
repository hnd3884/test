package jdk.jfr.internal.tool;

final class UserDataException extends Exception
{
    private static final long serialVersionUID = 6656457380115167810L;
    
    public UserDataException(final String s) {
        super(s);
    }
}
