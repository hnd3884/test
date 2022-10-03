package javax.management.relation;

public class RoleStatus
{
    public static final int NO_ROLE_WITH_NAME = 1;
    public static final int ROLE_NOT_READABLE = 2;
    public static final int ROLE_NOT_WRITABLE = 3;
    public static final int LESS_THAN_MIN_ROLE_DEGREE = 4;
    public static final int MORE_THAN_MAX_ROLE_DEGREE = 5;
    public static final int REF_MBEAN_OF_INCORRECT_CLASS = 6;
    public static final int REF_MBEAN_NOT_REGISTERED = 7;
    
    public static boolean isRoleStatus(final int n) {
        return n == 1 || n == 2 || n == 3 || n == 4 || n == 5 || n == 6 || n == 7;
    }
}
