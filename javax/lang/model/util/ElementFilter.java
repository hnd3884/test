package javax.lang.model.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ArrayList;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.Set;

public class ElementFilter
{
    private static final Set<ElementKind> CONSTRUCTOR_KIND;
    private static final Set<ElementKind> FIELD_KINDS;
    private static final Set<ElementKind> METHOD_KIND;
    private static final Set<ElementKind> PACKAGE_KIND;
    private static final Set<ElementKind> TYPE_KINDS;
    
    private ElementFilter() {
    }
    
    public static List<VariableElement> fieldsIn(final Iterable<? extends Element> iterable) {
        return listFilter(iterable, ElementFilter.FIELD_KINDS, VariableElement.class);
    }
    
    public static Set<VariableElement> fieldsIn(final Set<? extends Element> set) {
        return setFilter(set, ElementFilter.FIELD_KINDS, VariableElement.class);
    }
    
    public static List<ExecutableElement> constructorsIn(final Iterable<? extends Element> iterable) {
        return listFilter(iterable, ElementFilter.CONSTRUCTOR_KIND, ExecutableElement.class);
    }
    
    public static Set<ExecutableElement> constructorsIn(final Set<? extends Element> set) {
        return setFilter(set, ElementFilter.CONSTRUCTOR_KIND, ExecutableElement.class);
    }
    
    public static List<ExecutableElement> methodsIn(final Iterable<? extends Element> iterable) {
        return listFilter(iterable, ElementFilter.METHOD_KIND, ExecutableElement.class);
    }
    
    public static Set<ExecutableElement> methodsIn(final Set<? extends Element> set) {
        return setFilter(set, ElementFilter.METHOD_KIND, ExecutableElement.class);
    }
    
    public static List<TypeElement> typesIn(final Iterable<? extends Element> iterable) {
        return listFilter(iterable, ElementFilter.TYPE_KINDS, TypeElement.class);
    }
    
    public static Set<TypeElement> typesIn(final Set<? extends Element> set) {
        return setFilter(set, ElementFilter.TYPE_KINDS, TypeElement.class);
    }
    
    public static List<PackageElement> packagesIn(final Iterable<? extends Element> iterable) {
        return listFilter(iterable, ElementFilter.PACKAGE_KIND, PackageElement.class);
    }
    
    public static Set<PackageElement> packagesIn(final Set<? extends Element> set) {
        return setFilter(set, ElementFilter.PACKAGE_KIND, PackageElement.class);
    }
    
    private static <E extends Element> List<E> listFilter(final Iterable<? extends Element> iterable, final Set<ElementKind> set, final Class<E> clazz) {
        final ArrayList list = new ArrayList();
        for (final Element element : iterable) {
            if (set.contains(element.getKind())) {
                list.add(clazz.cast(element));
            }
        }
        return list;
    }
    
    private static <E extends Element> Set<E> setFilter(final Set<? extends Element> set, final Set<ElementKind> set2, final Class<E> clazz) {
        final LinkedHashSet set3 = new LinkedHashSet();
        for (final Element element : set) {
            if (set2.contains(element.getKind())) {
                set3.add(clazz.cast(element));
            }
        }
        return set3;
    }
    
    static {
        CONSTRUCTOR_KIND = Collections.unmodifiableSet((Set<? extends ElementKind>)EnumSet.of(ElementKind.CONSTRUCTOR));
        FIELD_KINDS = Collections.unmodifiableSet((Set<? extends ElementKind>)EnumSet.of(ElementKind.FIELD, ElementKind.ENUM_CONSTANT));
        METHOD_KIND = Collections.unmodifiableSet((Set<? extends ElementKind>)EnumSet.of(ElementKind.METHOD));
        PACKAGE_KIND = Collections.unmodifiableSet((Set<? extends ElementKind>)EnumSet.of(ElementKind.PACKAGE));
        TYPE_KINDS = Collections.unmodifiableSet((Set<? extends ElementKind>)EnumSet.of(ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE, ElementKind.ANNOTATION_TYPE));
    }
}
