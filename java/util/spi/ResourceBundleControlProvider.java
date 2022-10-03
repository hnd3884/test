package java.util.spi;

import java.util.ResourceBundle;

public interface ResourceBundleControlProvider
{
    ResourceBundle.Control getControl(final String p0);
}
