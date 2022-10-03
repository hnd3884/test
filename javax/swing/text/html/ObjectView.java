package javax.swing.text.html;

import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import sun.reflect.misc.MethodUtil;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import sun.reflect.misc.ReflectUtil;
import java.awt.Component;
import javax.swing.text.Element;
import javax.swing.text.ComponentView;

public class ObjectView extends ComponentView
{
    public ObjectView(final Element element) {
        super(element);
    }
    
    @Override
    protected Component createComponent() {
        final AttributeSet attributes = this.getElement().getAttributes();
        final String s = (String)attributes.getAttribute(HTML.Attribute.CLASSID);
        try {
            ReflectUtil.checkPackageAccess(s);
            final Object instance = Class.forName(s, true, Thread.currentThread().getContextClassLoader()).newInstance();
            if (instance instanceof Component) {
                final Component component = (Component)instance;
                this.setParameters(component, attributes);
                return component;
            }
        }
        catch (final Throwable t) {}
        return this.getUnloadableRepresentation();
    }
    
    Component getUnloadableRepresentation() {
        final JLabel label = new JLabel("??");
        label.setForeground(Color.red);
        return label;
    }
    
    private void setParameters(final Component component, final AttributeSet set) {
        final Class<? extends Component> class1 = component.getClass();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(class1);
        }
        catch (final IntrospectionException ex) {
            System.err.println("introspector failed, ex: " + ex);
            return;
        }
        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; ++i) {
            final Object attribute = set.getAttribute(propertyDescriptors[i].getName());
            if (attribute instanceof String) {
                final String s = (String)attribute;
                final Method writeMethod = propertyDescriptors[i].getWriteMethod();
                if (writeMethod == null) {
                    return;
                }
                if (writeMethod.getParameterTypes().length != 1) {
                    return;
                }
                final Object[] array = { s };
                try {
                    MethodUtil.invoke(writeMethod, component, array);
                }
                catch (final Exception ex2) {
                    System.err.println("Invocation failed");
                }
            }
        }
    }
}
