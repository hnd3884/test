package HTTPClient;

class LinkElement
{
    Object element;
    LinkElement next;
    
    LinkElement(final Object elem, final LinkElement next) {
        this.element = elem;
        this.next = next;
    }
}
