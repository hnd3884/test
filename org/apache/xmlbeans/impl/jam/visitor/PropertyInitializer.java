package org.apache.xmlbeans.impl.jam.visitor;

import org.apache.xmlbeans.impl.jam.JClass;
import java.util.Map;
import org.apache.xmlbeans.impl.jam.internal.elements.PropertyImpl;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JProperty;
import java.util.HashMap;
import org.apache.xmlbeans.impl.jam.mutable.MClass;

public class PropertyInitializer extends MVisitor
{
    @Override
    public void visit(final MClass clazz) {
        this.addProperties(clazz, true);
        this.addProperties(clazz, false);
    }
    
    private void addProperties(final MClass clazz, final boolean declared) {
        final JMethod[] methods = declared ? clazz.getDeclaredMethods() : clazz.getMethods();
        final Map name2prop = new HashMap();
        for (int i = 0; i < methods.length; ++i) {
            String name = methods[i].getSimpleName();
            if ((name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2)) {
                final JClass typ = methods[i].getReturnType();
                if (typ == null) {
                    continue;
                }
                if (methods[i].getParameters().length > 0) {
                    continue;
                }
                if (name.startsWith("get")) {
                    name = name.substring(3);
                }
                else {
                    name = name.substring(2);
                }
                JProperty prop = name2prop.get(name);
                if (prop == null) {
                    prop = (declared ? clazz.addNewDeclaredProperty(name, methods[i], null) : clazz.addNewProperty(name, methods[i], null));
                    name2prop.put(name, prop);
                }
                else if (typ.equals(prop.getType())) {
                    ((PropertyImpl)prop).setGetter(methods[i]);
                }
            }
            if (name.startsWith("set") && name.length() > 3) {
                if (methods[i].getParameters().length == 1) {
                    final JClass type = methods[i].getParameters()[0].getType();
                    name = name.substring(3);
                    JProperty prop = name2prop.get(name);
                    if (prop == null) {
                        prop = (declared ? clazz.addNewDeclaredProperty(name, null, methods[i]) : clazz.addNewProperty(name, null, methods[i]));
                        name2prop.put(name, prop);
                    }
                    else if (type.equals(prop.getType())) {
                        ((PropertyImpl)prop).setSetter(methods[i]);
                    }
                }
            }
        }
    }
}
