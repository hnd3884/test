package com.github.odiszapc.nginxparser;

public class NgxParam extends NgxAbstractEntry
{
    public NgxParam() {
        super(new String[0]);
    }
    
    public NgxParam(final String... array) {
        super(array);
    }
    
    @Override
    public String toString() {
        final String string = super.toString();
        if (string.isEmpty()) {
            return string;
        }
        return string + ";";
    }
}
