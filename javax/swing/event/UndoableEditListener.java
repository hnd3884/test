package javax.swing.event;

import java.util.EventListener;

public interface UndoableEditListener extends EventListener
{
    void undoableEditHappened(final UndoableEditEvent p0);
}
