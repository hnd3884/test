package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;

public class Row implements Element
{
    public static final int NULL = 0;
    public static final int CELL = 1;
    public static final int TABLE = 2;
    protected int columns;
    protected int currentColumn;
    protected boolean[] reserved;
    protected Object[] cells;
    protected int horizontalAlignment;
    
    protected Row(final int columns) {
        this.columns = columns;
        this.reserved = new boolean[columns];
        this.cells = new Object[columns];
        this.currentColumn = 0;
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 21;
    }
    
    @Override
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return false;
    }
    
    void deleteColumn(final int column) {
        if (column >= this.columns || column < 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("getcell.at.illegal.index.1", column));
        }
        --this.columns;
        final boolean[] newReserved = new boolean[this.columns];
        final Object[] newCells = new Cell[this.columns];
        for (int i = 0; i < column; ++i) {
            newReserved[i] = this.reserved[i];
            newCells[i] = this.cells[i];
            if (newCells[i] != null && i + ((Cell)newCells[i]).getColspan() > column) {
                ((Cell)newCells[i]).setColspan(((Cell)this.cells[i]).getColspan() - 1);
            }
        }
        for (int i = column; i < this.columns; ++i) {
            newReserved[i] = this.reserved[i + 1];
            newCells[i] = this.cells[i + 1];
        }
        if (this.cells[column] != null && ((Cell)this.cells[column]).getColspan() > 1) {
            newCells[column] = this.cells[column];
            ((Cell)newCells[column]).setColspan(((Cell)newCells[column]).getColspan() - 1);
        }
        this.reserved = newReserved;
        this.cells = newCells;
    }
    
    int addElement(final Object element) {
        return this.addElement(element, this.currentColumn);
    }
    
    int addElement(final Object element, final int column) {
        if (element == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("addcell.null.argument"));
        }
        if (column < 0 || column > this.columns) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("addcell.illegal.column.argument"));
        }
        if (this.getObjectID(element) != 1 && this.getObjectID(element) != 2) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("addcell.only.cells.or.tables.allowed"));
        }
        final int lColspan = (element instanceof Cell) ? ((Cell)element).getColspan() : 1;
        if (!this.reserve(column, lColspan)) {
            return -1;
        }
        this.cells[column] = element;
        this.currentColumn += lColspan - 1;
        return column;
    }
    
    void setElement(final Object aElement, final int column) {
        if (this.reserved[column]) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("setelement.position.already.taken"));
        }
        if ((this.cells[column] = aElement) != null) {
            this.reserved[column] = true;
        }
    }
    
    boolean reserve(final int column) {
        return this.reserve(column, 1);
    }
    
    boolean reserve(final int column, final int size) {
        if (column < 0 || column + size > this.columns) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("reserve.incorrect.column.size"));
        }
        for (int i = column; i < column + size; ++i) {
            if (this.reserved[i]) {
                for (int j = i; j >= column; --j) {
                    this.reserved[j] = false;
                }
                return false;
            }
            this.reserved[i] = true;
        }
        return true;
    }
    
    boolean isReserved(final int column) {
        return this.reserved[column];
    }
    
    int getElementID(final int column) {
        if (this.cells[column] == null) {
            return 0;
        }
        if (this.cells[column] instanceof Cell) {
            return 1;
        }
        if (this.cells[column] instanceof Table) {
            return 2;
        }
        return -1;
    }
    
    int getObjectID(final Object element) {
        if (element == null) {
            return 0;
        }
        if (element instanceof Cell) {
            return 1;
        }
        if (element instanceof Table) {
            return 2;
        }
        return -1;
    }
    
    public Object getCell(final int column) {
        if (column < 0 || column > this.columns) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("getcell.at.illegal.index.1.max.is.2", String.valueOf(column), String.valueOf(this.columns)));
        }
        return this.cells[column];
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < this.columns; ++i) {
            if (this.cells[i] != null) {
                return false;
            }
        }
        return true;
    }
    
    public int getColumns() {
        return this.columns;
    }
    
    public void setHorizontalAlignment(final int value) {
        this.horizontalAlignment = value;
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
}
