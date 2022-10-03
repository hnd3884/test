package jdk.jfr;

import java.util.Set;
import java.security.AccessController;
import jdk.Exported;
import jdk.jfr.internal.Control;

@MetadataDefinition
@Exported
public abstract class SettingControl extends Control
{
    protected SettingControl() {
        super(AccessController.getContext());
    }
    
    @Override
    public abstract String combine(final Set<String> p0);
    
    @Override
    public abstract void setValue(final String p0);
    
    @Override
    public abstract String getValue();
}
