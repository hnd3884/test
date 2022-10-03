package java.beans;

import java.beans.beancontext.BeanContext;
import java.applet.Applet;

public interface AppletInitializer
{
    void initialize(final Applet p0, final BeanContext p1);
    
    void activate(final Applet p0);
}
