package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class TableHeaderRendererSortedState extends State
{
    TableHeaderRendererSortedState() {
        super("Sorted");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        final String s = (String)component.getClientProperty("Table.sortOrder");
        return s != null && ("ASCENDING".equals(s) || "DESCENDING".equals(s));
    }
}
