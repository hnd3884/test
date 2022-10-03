package org.apache.axiom.core;

final class ElementsIterator<T extends CoreElement> extends AbstractNodeIterator<T>
{
    private final Class<T> type;
    private final ElementMatcher<? super T> matcher;
    private final String namespaceURI;
    private final String name;
    
    public ElementsIterator(final CoreParentNode startNode, final Axis axis, final Class<T> type, final ElementMatcher<? super T> matcher, final String namespaceURI, final String name, final Semantics semantics) {
        super(startNode, axis, type, semantics);
        this.type = type;
        this.matcher = matcher;
        this.namespaceURI = namespaceURI;
        this.name = name;
    }
    
    @Override
    protected final boolean matches(final CoreNode node) throws CoreModelException {
        return this.type.isInstance(node) && this.matcher.matches(this.type.cast(node), this.namespaceURI, this.name);
    }
}
