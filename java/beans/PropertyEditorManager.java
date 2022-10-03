package java.beans;

public class PropertyEditorManager
{
    public static void registerEditor(final Class<?> clazz, final Class<?> clazz2) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPropertiesAccess();
        }
        ThreadGroupContext.getContext().getPropertyEditorFinder().register(clazz, clazz2);
    }
    
    public static PropertyEditor findEditor(final Class<?> clazz) {
        return ThreadGroupContext.getContext().getPropertyEditorFinder().find(clazz);
    }
    
    public static String[] getEditorSearchPath() {
        return ThreadGroupContext.getContext().getPropertyEditorFinder().getPackages();
    }
    
    public static void setEditorSearchPath(final String[] packages) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPropertiesAccess();
        }
        ThreadGroupContext.getContext().getPropertyEditorFinder().setPackages(packages);
    }
}
