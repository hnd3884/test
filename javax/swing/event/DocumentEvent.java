package javax.swing.event;

import javax.swing.text.Element;
import javax.swing.text.Document;

public interface DocumentEvent
{
    int getOffset();
    
    int getLength();
    
    Document getDocument();
    
    EventType getType();
    
    ElementChange getChange(final Element p0);
    
    public static final class EventType
    {
        public static final EventType INSERT;
        public static final EventType REMOVE;
        public static final EventType CHANGE;
        private String typeString;
        
        private EventType(final String typeString) {
            this.typeString = typeString;
        }
        
        @Override
        public String toString() {
            return this.typeString;
        }
        
        static {
            INSERT = new EventType("INSERT");
            REMOVE = new EventType("REMOVE");
            CHANGE = new EventType("CHANGE");
        }
    }
    
    public interface ElementChange
    {
        Element getElement();
        
        int getIndex();
        
        Element[] getChildrenRemoved();
        
        Element[] getChildrenAdded();
    }
}
