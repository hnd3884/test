package sun.management;

class DiagnosticCommandArgumentInfo
{
    private final String name;
    private final String description;
    private final String type;
    private final String defaultValue;
    private final boolean mandatory;
    private final boolean option;
    private final boolean multiple;
    private final int position;
    
    String getName() {
        return this.name;
    }
    
    String getDescription() {
        return this.description;
    }
    
    String getType() {
        return this.type;
    }
    
    String getDefault() {
        return this.defaultValue;
    }
    
    boolean isMandatory() {
        return this.mandatory;
    }
    
    boolean isOption() {
        return this.option;
    }
    
    boolean isMultiple() {
        return this.multiple;
    }
    
    int getPosition() {
        return this.position;
    }
    
    DiagnosticCommandArgumentInfo(final String name, final String description, final String type, final String defaultValue, final boolean mandatory, final boolean option, final boolean multiple, final int position) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
        this.mandatory = mandatory;
        this.option = option;
        this.multiple = multiple;
        this.position = position;
    }
}
