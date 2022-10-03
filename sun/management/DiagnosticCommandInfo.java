package sun.management;

import java.util.List;

class DiagnosticCommandInfo
{
    private final String name;
    private final String description;
    private final String impact;
    private final String permissionClass;
    private final String permissionName;
    private final String permissionAction;
    private final boolean enabled;
    private final List<DiagnosticCommandArgumentInfo> arguments;
    
    String getName() {
        return this.name;
    }
    
    String getDescription() {
        return this.description;
    }
    
    String getImpact() {
        return this.impact;
    }
    
    String getPermissionClass() {
        return this.permissionClass;
    }
    
    String getPermissionName() {
        return this.permissionName;
    }
    
    String getPermissionAction() {
        return this.permissionAction;
    }
    
    boolean isEnabled() {
        return this.enabled;
    }
    
    List<DiagnosticCommandArgumentInfo> getArgumentsInfo() {
        return this.arguments;
    }
    
    DiagnosticCommandInfo(final String name, final String description, final String impact, final String permissionClass, final String permissionName, final String permissionAction, final boolean enabled, final List<DiagnosticCommandArgumentInfo> arguments) {
        this.name = name;
        this.description = description;
        this.impact = impact;
        this.permissionClass = permissionClass;
        this.permissionName = permissionName;
        this.permissionAction = permissionAction;
        this.enabled = enabled;
        this.arguments = arguments;
    }
}
