package HTTPClient;

class LinkedList
{
    private LinkElement head;
    private LinkElement tail;
    private LinkElement next_enum;
    
    public synchronized void addToHead(final Object elem) {
        this.head = new LinkElement(elem, this.head);
        if (this.head.next == null) {
            this.tail = this.head;
        }
    }
    
    public synchronized void addToEnd(final Object elem) {
        if (this.head == null) {
            final LinkElement linkElement = new LinkElement(elem, null);
            this.tail = linkElement;
            this.head = linkElement;
        }
        else {
            final LinkElement tail = this.tail;
            final LinkElement linkElement2 = new LinkElement(elem, null);
            tail.next = linkElement2;
            this.tail = linkElement2;
        }
    }
    
    public synchronized void remove(final Object elem) {
        if (this.head == null) {
            return;
        }
        if (this.head.element == elem) {
            this.head = this.head.next;
            return;
        }
        for (LinkElement curr = this.head; curr.next != null; curr = curr.next) {
            if (curr.next.element == elem) {
                if (curr.next == this.tail) {
                    this.tail = curr;
                }
                curr.next = curr.next.next;
                return;
            }
        }
    }
    
    public synchronized Object getFirst() {
        if (this.head == null) {
            return null;
        }
        return this.head.element;
    }
    
    public synchronized Object enumerate() {
        if (this.head == null) {
            return null;
        }
        this.next_enum = this.head.next;
        return this.head.element;
    }
    
    public synchronized Object next() {
        if (this.next_enum == null) {
            return null;
        }
        final Object elem = this.next_enum.element;
        this.next_enum = this.next_enum.next;
        return elem;
    }
    
    public static void main(final String[] args) throws Exception {
        System.err.println("\n*** Linked List Tests ...");
        LinkedList list = new LinkedList();
        list.addToHead("One");
        list.addToEnd("Last");
        if (!list.getFirst().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!list.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!list.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        if (list.next() != null) {
            throw new Exception("End of list wrong");
        }
        list.remove("One");
        if (!list.getFirst().equals("Last")) {
            throw new Exception("First element wrong");
        }
        list.remove("Last");
        if (list.getFirst() != null) {
            throw new Exception("End of list wrong");
        }
        list = new LinkedList();
        list.addToEnd("Last");
        list.addToHead("One");
        if (!list.getFirst().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!list.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!list.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        if (list.next() != null) {
            throw new Exception("End of list wrong");
        }
        if (!list.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        list.remove("One");
        if (!list.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        list.remove("Last");
        if (list.next() != null) {
            throw new Exception("End of list wrong");
        }
        list = new LinkedList();
        list.addToEnd("Last");
        list.addToHead("Two");
        list.addToHead("One");
        if (!list.getFirst().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!list.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!list.next().equals("Two")) {
            throw new Exception("Second element wrong");
        }
        if (!list.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        if (list.next() != null) {
            throw new Exception("End of list wrong");
        }
        list.remove("Last");
        list.remove("Two");
        list.remove("One");
        if (list.getFirst() != null) {
            throw new Exception("Empty list wrong");
        }
        System.err.println("\n*** Tests finished successfuly");
    }
}
