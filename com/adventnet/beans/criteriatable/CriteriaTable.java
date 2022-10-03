package com.adventnet.beans.criteriatable;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import com.adventnet.beans.criteriatable.events.AttributeModelEvent;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import java.util.Date;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import java.awt.Component;
import java.util.Stack;
import com.adventnet.beans.criteriatable.events.CriteriaChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.FontMetrics;
import com.adventnet.beans.criteriatable.events.CriteriaChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.MouseListener;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ResourceBundle;
import java.awt.Graphics;
import java.util.Vector;
import com.adventnet.beans.criteriatable.events.AttributeModelListener;
import javax.swing.JTable;

public class CriteriaTable extends JTable implements AttributeModelListener
{
    protected Vector criteriaChangeListeners;
    protected AttributeModel attributeModel;
    public static final String GROUP_START_INDEX = "GROUP_START_INDEX";
    public static final String GROUP_END_INDEX = "GROUP_END_INDEX";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final int ROW_HEADER_COLUMN = 0;
    public static final int ATTRIBUTE_COLUMN = 1;
    public static final int COMPARATORS_COLUMN = 2;
    public static final int VALUE_COLUMN = 3;
    public static final int OPERATOR_COLUMN = 4;
    private int group_start_row;
    private int group_end_row;
    private int minCriterionLimit;
    private Graphics graph;
    private boolean advancedMode;
    private boolean popupEnabled;
    private String defaultOperator;
    private RowHeaderRenderer rowHeaderRenderer;
    private CriteriaTableCellRenderer renderer;
    private CriteriaTableCellEditor cellEditor;
    private AttributeValueCellEditor attributeValueEditor;
    private ResourceBundle bundle;
    private RowHeaderRightClickHandler rightClickHandler;
    private DateFormat dateFormat;
    
    public CriteriaTable() {
        this(0);
    }
    
    public CriteriaTable(final int minCriterionLimit) {
        super(0, 5);
        this.criteriaChangeListeners = new Vector(1);
        this.group_start_row = -1;
        this.group_end_row = -1;
        this.popupEnabled = true;
        this.rowHeaderRenderer = new RowHeaderRenderer();
        this.renderer = new CriteriaTableCellRenderer();
        this.cellEditor = new CriteriaTableCellEditor();
        this.attributeValueEditor = new AttributeValueCellEditor();
        this.rightClickHandler = new RowHeaderRightClickHandler();
        this.dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm");
        this.minCriterionLimit = minCriterionLimit;
        this.init();
    }
    
    void init() {
        ((DefaultTableModel)this.getModel()).setColumnIdentifiers(new Object[] { "", "Attribute", "Comparator", "Value", "Operator" });
        this.setShowHorizontalLines(false);
        this.setShowVerticalLines(false);
        this.getTableHeader().setReorderingAllowed(false);
        this.setRowHeight(20);
        this.setRenderers();
        this.setEditors();
        this.setAdvancedMode(false);
        this.setAttributeModel(new DefaultAttributeModel());
        this.setSelectionMode(1);
        this.addListeners();
    }
    
    public void setEditorForAttributesColumn(final AttributeCellEditor attributeCellEditor) {
        this.cellEditor.setAttributeCellEditor(attributeCellEditor);
    }
    
    private void addListeners() {
        this.addMouseListener(this.rightClickHandler);
        this.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(final TableModelEvent tableModelEvent) {
                if (tableModelEvent.getType() == 0) {
                    CriteriaTable.this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 8));
                    CriteriaTable.this.repaint();
                }
            }
        });
    }
    
    private void addMinimumNumberOfRows() {
        for (int i = 0; i < this.getMinimumCriterionLimit(); ++i) {
            this.addNewCriterion();
        }
    }
    
    public void setFocusable(final boolean focusable) {
        super.setFocusable(focusable);
        if (!focusable) {
            this.removeMouseListener(this.rightClickHandler);
        }
        else {
            this.addMouseListener(this.rightClickHandler);
        }
    }
    
    public AttributeValueEditorComponentsTable getDefaultAttributeValueEditorComponents() {
        return this.attributeValueEditor.getAttributeValueEditorComponentsTable();
    }
    
    public void setDefaultAttributeValueEditorComponent(final Class clazz, final AttributeValueEditorComponent attributeValueEditorComponent) {
        this.attributeValueEditor.getAttributeValueEditorComponentsTable().setDefaultEditorForType(clazz, attributeValueEditorComponent);
    }
    
    public ComparatorsTable getDefaultComparators() {
        return this.cellEditor.getComparatorsTable();
    }
    
    public void setDefaultComparators(final Class clazz, final String[] array) {
        this.cellEditor.getComparatorsTable().setDefaultComparatorsForType(clazz, array);
    }
    
    private String[] getComparators(final Attribute attribute) {
        return this.cellEditor.getComparators(attribute);
    }
    
    private void setRowHeadersWidth(final int preferredWidth) {
        this.getColumnModel().getColumn(0).setMinWidth(preferredWidth);
        this.getColumnModel().getColumn(0).setMaxWidth(preferredWidth);
        this.getColumnModel().getColumn(0).setPreferredWidth(preferredWidth);
    }
    
    private void setOperatorColumnWidth() {
        if (this.graph != null) {
            final FontMetrics fontMetrics = this.graph.getFontMetrics(this.getFont());
            int stringWidth = -1;
            for (int i = 0; i < this.attributeModel.getOperatorsCount(); ++i) {
                final String operator = this.attributeModel.getOperator(i);
                if (fontMetrics.stringWidth(operator) > stringWidth) {
                    stringWidth = fontMetrics.stringWidth(operator);
                }
            }
            final int preferredWidth = stringWidth + 30;
            final TableColumn column = this.getColumnModel().getColumn(4);
            column.setMinWidth(preferredWidth);
            column.setMaxWidth(preferredWidth);
            column.setPreferredWidth(preferredWidth);
        }
    }
    
    public void paint(final Graphics graph) {
        if (this.graph == null) {
            this.graph = graph;
            if (this.isAdvancedMode()) {
                this.setOperatorColumnWidth();
            }
        }
        super.paint(graph);
    }
    
    private void setEditors() {
        for (int i = 1; i < this.getColumnCount(); ++i) {
            if (i != 3) {
                this.getColumnModel().getColumn(i).setCellEditor(this.cellEditor);
            }
            else {
                this.getColumnModel().getColumn(i).setCellEditor(this.attributeValueEditor);
            }
        }
    }
    
    private void setRenderers() {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (i == 0) {
                this.getColumnModel().getColumn(i).setCellRenderer(this.rowHeaderRenderer);
            }
            else if (i != 3) {
                this.getColumnModel().getColumn(i).setCellRenderer(this.renderer);
            }
        }
    }
    
    public AttributeModel getAttributeModel() {
        return this.attributeModel;
    }
    
    public void setAttributeModel(final AttributeModel attributeModel) {
        if (attributeModel == null) {
            throw new IllegalArgumentException("Cannot set a null AttributeModel");
        }
        final AttributeModel attributeModel2 = this.getAttributeModel();
        if (attributeModel != null && attributeModel2 != attributeModel) {
            (this.attributeModel = attributeModel).addAttributeModelListener(this);
            this.addMinimumNumberOfRows();
            this.firePropertyChange("attributeModel", attributeModel2, attributeModel);
        }
    }
    
    public void setAdvancedMode(final boolean advancedMode) {
        this.advancedMode = advancedMode;
        this.setMode();
    }
    
    public boolean isAdvancedMode() {
        return this.advancedMode;
    }
    
    private void setMode() {
        if (this.isAdvancedMode()) {
            this.showColumn(0);
            this.setRowHeadersWidth(30);
            this.showColumn(4);
            this.setOperatorColumnWidth();
            this.setGropingRowsHeight(20);
        }
        else {
            this.hideColumn(0);
            this.hideColumn(4);
            this.setGropingRowsHeight(1);
        }
    }
    
    private void setGropingRowsHeight(final int n) {
        for (int i = 0; i < this.getRowCount(); ++i) {
            if (this.getValueAt(i, 0) != null) {
                this.setRowHeight(i, n);
            }
        }
    }
    
    private void hideColumn(final int n) {
        final TableColumn column = this.getColumnModel().getColumn(n);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
    }
    
    private void showColumn(final int n) {
        final TableColumn column = this.getColumnModel().getColumn(n);
        column.setMinWidth(15);
        column.setMaxWidth(Integer.MAX_VALUE);
        column.setPreferredWidth(75);
    }
    
    public void moveCriterionUp(final int n, final int n2) {
        final int n3 = n - 1;
        this.getTableModel().moveRow(n, n2, n3);
        if (n3 >= 0) {
            this.setRowSelectionInterval(n3, n3 + (n2 - n));
        }
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 4));
    }
    
    public void moveCriterionDown(final int n, final int n2) {
        final int n3 = n + 1;
        this.getTableModel().moveRow(n, n2, n3);
        if (n3 >= 0) {
            this.setRowSelectionInterval(n3, n3 + (n2 - n));
        }
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 5));
    }
    
    public void addNewCriterion() {
        final Attribute attribute = this.attributeModel.getAttribute(0);
        this.getTableModel().addRow(new Object[] { null, attribute, this.getComparators(attribute)[0], null, this.attributeModel.getOperator(0) });
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 0));
    }
    
    void insertNewCriterion(final int n, final boolean b) {
        final Attribute attribute = this.attributeModel.getAttribute(0);
        if (b) {
            this.getTableModel().insertRow(n, new Object[] { null, attribute, this.getComparators(attribute)[0], null, this.attributeModel.getOperator(0) });
        }
        else {
            this.getTableModel().insertRow(n + 1, new Object[] { null, attribute, this.getComparators(attribute)[0], null, this.attributeModel.getOperator(0) });
        }
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 1));
    }
    
    void removeCriterionAt(int n) {
        if (this.getCriterionCount() <= this.getMinimumCriterionLimit()) {
            return;
        }
        for (Object o = this.getValueAt(n, 0); o != null; o = this.getValueAt(--n, 0)) {}
        final CriteriaChangeEvent criteriaChangeEvent = new CriteriaChangeEvent(this, 2);
        if (n + 1 >= this.getRowCount() || n - 1 < 0) {
            this.getTableModel().removeRow(n);
            this.fireCriteriaChangeEvent(criteriaChangeEvent);
            return;
        }
        if (this.getValueAt(n + 1, 0) != null && this.getValueAt(n - 1, 0) != null && this.getValueAt(n + 1, 0).equals("GROUP_END_INDEX") && this.getValueAt(n - 1, 0).equals("GROUP_START_INDEX")) {
            this.getTableModel().removeRow(n + 1);
            this.getTableModel().removeRow(n);
            this.getTableModel().removeRow(n - 1);
            this.fireCriteriaChangeEvent(criteriaChangeEvent);
            return;
        }
        this.getTableModel().removeRow(n);
        this.fireCriteriaChangeEvent(criteriaChangeEvent);
    }
    
    public void cancelEditing() {
        if (this.isEditing()) {
            this.getCellEditor(this.getEditingRow(), this.getEditingColumn()).cancelCellEditing();
        }
    }
    
    public void removeLastCriterion() {
        this.cancelEditing();
        this.removeCriterionAt(this.getRowCount() - 1);
    }
    
    public void clearAllCriterions() {
        this.cancelEditing();
        for (int i = this.getTableModel().getRowCount() - 1; i >= 0; --i) {
            this.getTableModel().removeRow(i);
        }
        this.addMinimumNumberOfRows();
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 3));
    }
    
    public void groupCriterions(final int n, final int n2) {
        this.getTableModel().insertRow(n, new Object[] { "GROUP_START_INDEX", "(" });
        this.getTableModel().insertRow(n2 + 2, new Object[] { "GROUP_END_INDEX", ")", null, null, this.attributeModel.getOperator(0) });
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 6));
    }
    
    public void ungroupCriterions(final int n, final int n2) {
        this.getTableModel().removeRow(n2);
        this.getTableModel().removeRow(n);
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 7));
    }
    
    public int getCriterionCount() {
        int rowCount = this.getRowCount();
        for (int i = 0; i < this.getRowCount(); ++i) {
            if (this.getValueAt(i, 0) != null) {
                --rowCount;
            }
        }
        return rowCount;
    }
    
    private DefaultTableModel getTableModel() {
        return (DefaultTableModel)this.getModel();
    }
    
    public boolean isCellEditable(final int n, final int n2) {
        if (n2 == 0) {
            return false;
        }
        if (n2 == 1 && this.getValueAt(n, 0) != null && this.attributeModel.getGroupingElementsCount() <= 0) {
            return false;
        }
        if (n2 == 4) {
            if (n == this.getRowCount() - 1) {
                return false;
            }
            if (this.getValueAt(n + 1, 0) != null && this.getValueAt(n + 1, 0).equals("GROUP_END_INDEX")) {
                return false;
            }
        }
        return n2 == 1 || this.getValueAt(n, 0) == null || (!this.getValueAt(n, 0).equals("GROUP_START_INDEX") && n2 == 4 && !this.getValueAt(n, 0).equals("GROUP_START_INDEX"));
    }
    
    public void editingStopped(final ChangeEvent changeEvent) {
        final int editingRow = this.getEditingRow();
        final int editingColumn = this.getEditingColumn();
        super.editingStopped(changeEvent);
        if (editingColumn == 1 && this.getValueAt(editingRow, 0) == null) {
            final Object value = this.getValueAt(editingRow, editingColumn);
            if (value instanceof Attribute) {
                this.setValueAt(this.getComparators((Attribute)value)[0], editingRow, 2);
                this.setValueAt(null, editingRow, 3);
            }
        }
        this.repaint();
    }
    
    public void addCriteriaChangeListener(final CriteriaChangeListener criteriaChangeListener) {
        this.criteriaChangeListeners.add(criteriaChangeListener);
    }
    
    public void removeCriteriaChangeListener(final CriteriaChangeListener criteriaChangeListener) {
        this.criteriaChangeListeners.remove(criteriaChangeListener);
    }
    
    private void fireCriteriaChangeEvent(final CriteriaChangeEvent criteriaChangeEvent) {
        for (int i = 0; i < this.criteriaChangeListeners.size(); ++i) {
            ((CriteriaChangeListener)this.criteriaChangeListeners.elementAt(i)).criteriaChanged(criteriaChangeEvent);
        }
    }
    
    private int findMatchingGroupEndIndex(final int n) {
        final Stack stack = new Stack();
        for (int i = n; i < this.getRowCount(); ++i) {
            final Object value = this.getValueAt(i, 0);
            if (value != null) {
                if (value.equals("GROUP_START_INDEX")) {
                    stack.push(new Integer(i));
                }
                else if (value.equals("GROUP_END_INDEX") && stack.pop() == n) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private int findMatchingGroupStartIndex(final int n) {
        final Stack stack = new Stack();
        for (int i = n; i >= 0; --i) {
            final Object value = this.getValueAt(i, 0);
            if (value != null) {
                if (value.equals("GROUP_END_INDEX")) {
                    stack.push(new Integer(i));
                }
                else if (value.equals("GROUP_START_INDEX") && stack.pop() == n) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public Component prepareRenderer(final TableCellRenderer tableCellRenderer, final int n, final int n2) {
        Font font = null;
        final Component prepareRenderer = super.prepareRenderer(tableCellRenderer, n, n2);
        if (n2 == 0 && prepareRenderer instanceof JComponent) {
            ((JComponent)prepareRenderer).setToolTipText(this.getString("Right click on the row header for more options"));
        }
        if (n2 != 0) {
            font = prepareRenderer.getFont();
            prepareRenderer.setFont(new Font(font.getName(), 0, font.getSize()));
        }
        prepareRenderer.setForeground(this.getForeground());
        if (n == this.group_end_row) {
            if (n2 == 1) {
                prepareRenderer.setForeground(Color.BLUE);
                prepareRenderer.setFont(new Font(font.getName(), 1, font.getSize()));
                this.group_end_row = -1;
            }
        }
        else if (n == this.group_start_row) {
            if (n2 == 1) {
                prepareRenderer.setForeground(Color.BLUE);
                prepareRenderer.setFont(new Font(font.getName(), 1, font.getSize()));
                this.group_start_row = -1;
            }
        }
        else if (n2 == 3) {
            if (this.getValueAt(n, 0) == null) {
                if (prepareRenderer instanceof JComponent) {
                    ((JComponent)prepareRenderer).setBorder(new LineBorder(this.getForeground()));
                    final Object value = this.getValueAt(n, n2);
                    if (value instanceof Date) {
                        final String format = this.dateFormat.format((Date)value);
                        if (prepareRenderer instanceof JLabel) {
                            ((JLabel)prepareRenderer).setText(format);
                        }
                    }
                }
            }
            else if (prepareRenderer instanceof JComponent) {
                ((JComponent)prepareRenderer).setBorder(null);
            }
        }
        return prepareRenderer;
    }
    
    public boolean isValidToUngroup(final int n, final int n2) {
        final Object value = this.getValueAt(n, 0);
        final Object value2 = this.getValueAt(n2, 0);
        return value != null && value2 != null && value.equals("GROUP_START_INDEX") && value2.equals("GROUP_END_INDEX") && this.isValidToGroup(n, n2);
    }
    
    public boolean isValidToGroup(final int n, final int n2) {
        final Stack stack = new Stack();
        for (int i = n; i <= n2; ++i) {
            final Object value = this.getValueAt(i, 0);
            if (value != null) {
                if (value.equals("GROUP_START_INDEX")) {
                    stack.push(value);
                }
                else if (value.equals("GROUP_END_INDEX")) {
                    if (stack.isEmpty()) {
                        return false;
                    }
                    stack.pop();
                }
            }
        }
        return stack.isEmpty();
    }
    
    private Attribute getAttributeByValueString(final String s) {
        for (int i = 0; i < this.attributeModel.getAttributeCount(); ++i) {
            final Attribute attribute = this.attributeModel.getAttribute(i);
            if (attribute.getValueObject().toString().equals(s)) {
                return attribute;
            }
        }
        return null;
    }
    
    public void addOperandToDisplay(final String s) {
        final DefaultTableModel defaultTableModel = (DefaultTableModel)this.getModel();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '(') {
                defaultTableModel.addRow(new Object[] { "GROUP_START_INDEX", "(" });
            }
            else if (s.charAt(i) == ')') {
                String s2 = null;
                if (i + 1 < s.length() && s.charAt(i + 1) != ')') {
                    s2 = new StringTokenizer(s.substring(i + 1), "[(").nextToken();
                    if (s2 != null) {
                        s2 = s2.trim();
                    }
                }
                defaultTableModel.addRow(new Object[] { "GROUP_END_INDEX", ")", null, null, s2 });
            }
            else if (s.charAt(i) == '[') {
                String s3 = null;
                final int index = s.indexOf("]", i);
                final String substring = s.substring(i + 1, index);
                if (substring == null || !substring.trim().equalsIgnoreCase("NULL")) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(substring, "/");
                    final Attribute attributeByValueString = this.getAttributeByValueString(stringTokenizer.nextToken().trim());
                    final String nextToken = stringTokenizer.nextToken();
                    final String nextToken2 = stringTokenizer.nextToken();
                    Object trim;
                    if (nextToken2 != null && nextToken2.trim().equalsIgnoreCase("null")) {
                        trim = null;
                    }
                    else {
                        trim = nextToken2.trim();
                    }
                    if (index + 1 < s.length() && s.charAt(index + 1) != ')') {
                        s3 = new StringTokenizer(s.substring(index + 1), "[(").nextToken();
                        if (s3 != null) {
                            s3 = s3.trim();
                        }
                    }
                    defaultTableModel.addRow(new Object[] { null, attributeByValueString, nextToken.trim(), trim, s3 });
                }
            }
        }
    }
    
    public Criteria getCriteria() {
        if (this.getCriterionCount() == 0 || this.getRowCount() == 0) {
            return null;
        }
        return this.createCriteriaObject(0, this.getRowCount() - 1);
    }
    
    private Criteria createCriteriaObject(int n, final int n2) {
        Operand operand = null;
        Operand operand2 = null;
        final String s = null;
        if (n >= this.getRowCount()) {
            return new Criteria(operand, operand2, s);
        }
        Object o;
        for (o = this.getValueAt(n, 0); !this.isAdvancedMode() && o != null; o = this.getValueAt(n, 0)) {
            ++n;
        }
        if (o == null) {
            operand = this.createCriterion(n);
        }
        else if (o.equals("GROUP_START_INDEX")) {
            final int n3 = this.findMatchingGroupEndIndex(n) - 1;
            final int n4 = n + 1;
            operand = this.createCriteriaObject(n4, n3);
            if (n4 - 2 >= 0) {
                final Object value = this.getValueAt(n4 - 2, 0);
                if (value != null && value.equals("GROUP_START_INDEX")) {
                    operand.setGroupStartsBeforeThis(true);
                }
            }
            if (n3 + 2 < this.getRowCount()) {
                final Object value2 = this.getValueAt(n3 + 2, 0);
                if (value2 != null && value2.equals("GROUP_END_INDEX")) {
                    operand.setGroupEndsAfterThis(true);
                }
            }
            n = n3 + 1;
        }
        String defaultOperator;
        if (this.isAdvancedMode()) {
            defaultOperator = (String)this.getValueAt(n, 4);
        }
        else {
            defaultOperator = this.defaultOperator;
        }
        if (++n >= this.getRowCount()) {
            return new Criteria(operand, operand2, defaultOperator);
        }
        Object o2;
        for (o2 = this.getValueAt(n, 0); !this.isAdvancedMode() && o2 != null; o2 = this.getValueAt(n, 0)) {
            ++n;
        }
        if (o2 == null) {
            if (n == n2) {
                operand2 = this.createCriterion(n);
            }
            else {
                operand2 = this.createCriteriaObject(n, n2);
            }
        }
        else if (o2.equals("GROUP_START_INDEX")) {
            operand2 = this.createCriteriaObject(n, n2);
        }
        return new Criteria(operand, operand2, defaultOperator);
    }
    
    private Criterion createCriterion(final int n) {
        final Attribute attribute = (Attribute)this.getValueAt(n, 1);
        final Object valueObject = attribute.getValueObject();
        final String s = (String)this.getValueAt(n, 2);
        Object value = this.getValueAt(n, 3);
        if (value != null && !value.equals("")) {
            if (attribute.getAttributeClass() == Attribute.INTEGER_TYPE) {
                value = new Integer((String)value);
            }
            else if (attribute.getAttributeClass() == Attribute.LONG_TYPE) {
                value = new Long((String)value);
            }
            else if (attribute.getAttributeClass() == Attribute.FLOAT_TYPE) {
                value = new Float((String)value);
            }
            else if (attribute.getAttributeClass() == Attribute.DOUBLE_TYPE) {
                value = new Double((String)value);
            }
        }
        if (!this.isAdvancedMode()) {
            return new Criterion(valueObject, s, value);
        }
        boolean b = false;
        boolean b2 = false;
        if (n > 0) {
            final Object value2 = this.getValueAt(n - 1, 0);
            if (value2 != null && value2.equals("GROUP_START_INDEX")) {
                b = true;
            }
        }
        if (n < this.getRowCount() - 1) {
            final Object value3 = this.getValueAt(n + 1, 0);
            if (value3 != null && value3.equals("GROUP_END_INDEX")) {
                b2 = true;
            }
        }
        return new Criterion(valueObject, s, value, b, b2);
    }
    
    public void useOperatorForAllCriterions(final String defaultOperator) {
        this.defaultOperator = defaultOperator;
    }
    
    public void setPopupEnabled(final boolean popupEnabled) {
        this.popupEnabled = popupEnabled;
    }
    
    public boolean isPopupEnabled() {
        return this.popupEnabled;
    }
    
    public void setDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }
    
    public int getMinimumCriterionLimit() {
        return this.minCriterionLimit;
    }
    
    public void setResourceBundle(final ResourceBundle bundle) {
        this.bundle = bundle;
        this.rightClickHandler.setUpTexts();
    }
    
    public ResourceBundle getResourceBundle() {
        return this.bundle;
    }
    
    public String getString(final String s) {
        String string = null;
        if (this.bundle != null) {
            try {
                string = this.bundle.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (string == null || string.equals("")) {
            return s.trim();
        }
        return string;
    }
    
    public void attributeModelChanged(final AttributeModelEvent attributeModelEvent) {
        if (attributeModelEvent.getType() == 1) {
            final Attribute attribute = attributeModelEvent.getAttribute();
            this.cancelEditing();
            for (int i = this.getRowCount() - 1; i >= 0; --i) {
                if (i >= this.getRowCount()) {
                    i = this.getRowCount() - 1;
                }
                if (i >= 0 && this.getValueAt(i, 0) == null && this.getValueAt(i, 1) == attribute) {
                    if (this.getCriterionCount() > this.getMinimumCriterionLimit()) {
                        this.removeCriterionAt(i);
                    }
                    else if (this.getAttributeModel().getAttributeCount() > 0) {
                        final Attribute attribute2 = this.getAttributeModel().getAttribute(0);
                        this.setValueAt(attribute2, i, 1);
                        this.setValueAt(this.getComparators(attribute2)[0], i, 2);
                    }
                    else {
                        this.removeCriterionAt(i);
                    }
                }
            }
        }
    }
    
    public void addCriterionToDisplay(final Criterion criterion) {
        if (criterion.isGroupStartsBeforeThis()) {
            ((DefaultTableModel)this.getModel()).addRow(new Object[] { "GROUP_START_INDEX", "(" });
        }
        ((DefaultTableModel)this.getModel()).addRow(new Object[] { null, this.attributeModel.getAttributeByValue(criterion.getAttributeObject()), criterion.getComparator(), criterion.getValue(), this.defaultOperator });
        if (criterion.isGroupEndsAfterThis()) {
            ((DefaultTableModel)this.getModel()).addRow(new Object[] { "GROUP_END_INDEX", ")", null, null, null });
        }
        this.fireCriteriaChangeEvent(new CriteriaChangeEvent(this, 0));
    }
    
    public void addCriteriaToDisplay(final Criteria criteria) {
        final Operand leftOperand = criteria.getLeftOperand();
        final Operand rightOperand = criteria.getRightOperand();
        if (criteria.isGroupStartsBeforeThis()) {
            ((DefaultTableModel)this.getModel()).addRow(new Object[] { "GROUP_START_INDEX", "(" });
        }
        if (leftOperand instanceof Criterion) {
            this.addCriterionToDisplay((Criterion)leftOperand);
        }
        else if (leftOperand instanceof Criteria) {
            this.addCriteriaToDisplay((Criteria)leftOperand);
        }
        ((DefaultTableModel)this.getModel()).setValueAt(criteria.getOperator(), ((DefaultTableModel)this.getModel()).getRowCount() - 1, 4);
        if (rightOperand instanceof Criterion) {
            this.addCriterionToDisplay((Criterion)rightOperand);
        }
        else if (rightOperand instanceof Criteria) {
            this.addCriteriaToDisplay((Criteria)rightOperand);
        }
        if (criteria.isGroupEndsAfterThis()) {
            ((DefaultTableModel)this.getModel()).addRow(new Object[] { "GROUP_END_INDEX", ")", null, null, null });
        }
    }
    
    class RowHeaderRightClickHandler extends MouseAdapter
    {
        JPopupMenu popup;
        JMenuItem insertAbove;
        JMenuItem insertBelow;
        JMenuItem delete;
        JMenuItem group;
        JMenuItem ungroup;
        JMenuItem moveUp;
        JMenuItem moveDown;
        private int row;
        private int column;
        
        public RowHeaderRightClickHandler() {
            this.popup = new JPopupMenu();
            this.insertAbove = new JMenuItem();
            this.popup.add(this.insertAbove);
            final Font font = this.insertAbove.getFont();
            final Font font2 = new Font(font.getName(), 0, font.getSize());
            this.insertAbove.setFont(font2);
            this.insertAbove.addActionListener(new ActionListener() {
                private final /* synthetic */ RowHeaderRightClickHandler this$1 = this$1;
                
                public void actionPerformed(final ActionEvent actionEvent) {
                    this.this$1.insertAboveActionPerformed(actionEvent);
                }
            });
            this.insertBelow = new JMenuItem();
            this.popup.add(this.insertBelow);
            this.insertBelow.setFont(font2);
            this.insertBelow.addActionListener(new ActionListener() {
                private final /* synthetic */ RowHeaderRightClickHandler this$1 = this$1;
                
                public void actionPerformed(final ActionEvent actionEvent) {
                    this.this$1.insertBelowActionPerformed(actionEvent);
                }
            });
            this.popup.add(new JSeparator());
            this.delete = new JMenuItem();
            this.popup.add(this.delete);
            this.delete.setFont(font2);
            this.delete.addActionListener(new ActionListener() {
                private final /* synthetic */ RowHeaderRightClickHandler this$1 = this$1;
                
                public void actionPerformed(final ActionEvent actionEvent) {
                    this.this$1.deleteActionPerformed(actionEvent);
                }
            });
            this.popup.add(new JSeparator());
            this.group = new JMenuItem();
            this.popup.add(this.group);
            this.group.setFont(font2);
            this.group.addActionListener(new ActionListener() {
                private final /* synthetic */ RowHeaderRightClickHandler this$1 = this$1;
                
                public void actionPerformed(final ActionEvent actionEvent) {
                    this.this$1.groupActionPerformed(actionEvent);
                }
            });
            this.ungroup = new JMenuItem();
            this.popup.add(this.ungroup);
            this.ungroup.setFont(font2);
            this.ungroup.addActionListener(new ActionListener() {
                private final /* synthetic */ RowHeaderRightClickHandler this$1 = this$1;
                
                public void actionPerformed(final ActionEvent actionEvent) {
                    this.this$1.ungroupActionPerformed(actionEvent);
                }
            });
            this.popup.add(new JSeparator());
            this.moveUp = new JMenuItem();
            this.popup.add(this.moveUp);
            this.moveUp.setFont(font2);
            this.moveUp.addActionListener(new ActionListener() {
                private final /* synthetic */ RowHeaderRightClickHandler this$1 = this$1;
                
                public void actionPerformed(final ActionEvent actionEvent) {
                    this.this$1.moveUpActionPerformed(actionEvent);
                }
            });
            this.moveDown = new JMenuItem();
            this.popup.add(this.moveDown);
            this.moveDown.setFont(font2);
            this.moveDown.addActionListener(new ActionListener() {
                private final /* synthetic */ RowHeaderRightClickHandler this$1 = this$1;
                
                public void actionPerformed(final ActionEvent actionEvent) {
                    this.this$1.moveDownActionPerformed(actionEvent);
                }
            });
            this.setUpTexts();
        }
        
        void setUpTexts() {
            this.insertAbove.setText(CriteriaTable.this.getString("Insert Above"));
            this.insertBelow.setText(CriteriaTable.this.getString("Insert Below"));
            this.delete.setText(CriteriaTable.this.getString("Delete"));
            this.group.setText(CriteriaTable.this.getString("Group"));
            this.ungroup.setText(CriteriaTable.this.getString("Ungroup"));
            this.moveUp.setText(CriteriaTable.this.getString("Move Up"));
            this.moveDown.setText(CriteriaTable.this.getString("Move Down"));
        }
        
        private void moveUpActionPerformed(final ActionEvent actionEvent) {
            if (CriteriaTable.this.getSelectedRowCount() > 0) {
                CriteriaTable.this.moveCriterionUp(CriteriaTable.this.getSelectedRows()[0], CriteriaTable.this.getSelectedRows()[CriteriaTable.this.getSelectedRowCount() - 1]);
            }
        }
        
        private void moveDownActionPerformed(final ActionEvent actionEvent) {
            if (CriteriaTable.this.getSelectedRowCount() > 0) {
                CriteriaTable.this.moveCriterionDown(CriteriaTable.this.getSelectedRows()[0], CriteriaTable.this.getSelectedRows()[CriteriaTable.this.getSelectedRowCount() - 1]);
            }
        }
        
        private void insertAboveActionPerformed(final ActionEvent actionEvent) {
            CriteriaTable.this.insertNewCriterion(this.row, true);
        }
        
        private void insertBelowActionPerformed(final ActionEvent actionEvent) {
            CriteriaTable.this.insertNewCriterion(this.row, false);
        }
        
        private void deleteActionPerformed(final ActionEvent actionEvent) {
            CriteriaTable.this.removeCriterionAt(this.row);
        }
        
        private void ungroupActionPerformed(final ActionEvent actionEvent) {
            if (CriteriaTable.this.getSelectedRowCount() > 0) {
                CriteriaTable.this.ungroupCriterions(CriteriaTable.this.getSelectedRows()[0], CriteriaTable.this.getSelectedRows()[CriteriaTable.this.getSelectedRowCount() - 1]);
            }
        }
        
        private void groupActionPerformed(final ActionEvent actionEvent) {
            if (CriteriaTable.this.getSelectedRowCount() > 0) {
                CriteriaTable.this.groupCriterions(CriteriaTable.this.getSelectedRows()[0], CriteriaTable.this.getSelectedRows()[CriteriaTable.this.getSelectedRowCount() - 1]);
            }
        }
        
        private void enableDisableOptions() {
            final int n = CriteriaTable.this.getSelectedRows()[0];
            final int n2 = CriteriaTable.this.getSelectedRows()[CriteriaTable.this.getSelectedRowCount() - 1];
            if (CriteriaTable.this.getSelectedRowCount() < 1) {
                this.group.setEnabled(false);
                this.ungroup.setEnabled(false);
            }
            else {
                this.group.setEnabled(CriteriaTable.this.isValidToGroup(n, n2));
                this.ungroup.setEnabled(CriteriaTable.this.isValidToUngroup(n, n2));
            }
            if (CriteriaTable.this.getValueAt(this.row, 0) != null || CriteriaTable.this.getSelectedRowCount() > 1) {
                this.delete.setEnabled(false);
            }
            else if (CriteriaTable.this.getCriterionCount() <= 1) {
                this.delete.setEnabled(false);
            }
            else {
                this.delete.setEnabled(true);
            }
            if (CriteriaTable.this.getSelectedRowCount() > 1) {
                this.insertAbove.setEnabled(false);
                this.insertBelow.setEnabled(false);
            }
            else {
                this.insertAbove.setEnabled(true);
                this.insertBelow.setEnabled(true);
            }
            if (CriteriaTable.this.isAdvancedMode() && CriteriaTable.this.getSelectedRowCount() > 0) {
                if (CriteriaTable.this.isValidToGroup(n, n2)) {
                    if (n - 1 >= 0) {
                        this.moveUp.setEnabled(true);
                    }
                    else {
                        this.moveUp.setEnabled(false);
                    }
                    if (n2 + 1 < CriteriaTable.this.getRowCount()) {
                        this.moveDown.setEnabled(true);
                    }
                    else {
                        this.moveDown.setEnabled(false);
                    }
                }
                else {
                    this.moveUp.setEnabled(false);
                    this.moveDown.setEnabled(false);
                }
            }
        }
        
        public void mouseClicked(final MouseEvent mouseEvent) {
            this.column = CriteriaTable.this.columnAtPoint(new Point(mouseEvent.getX(), mouseEvent.getY()));
            this.row = CriteriaTable.this.rowAtPoint(new Point(mouseEvent.getX(), mouseEvent.getY()));
            final Object value = CriteriaTable.this.getValueAt(this.row, 0);
            if (value != null) {
                if (value.equals("GROUP_START_INDEX")) {
                    CriteriaTable.this.group_start_row = this.row;
                    CriteriaTable.this.group_end_row = CriteriaTable.this.findMatchingGroupEndIndex(this.row);
                }
                else if (value.equals("GROUP_END_INDEX")) {
                    CriteriaTable.this.group_end_row = this.row;
                    CriteriaTable.this.group_start_row = CriteriaTable.this.findMatchingGroupStartIndex(this.row);
                }
            }
            CriteriaTable.this.repaint();
        }
        
        public void mouseReleased(final MouseEvent mouseEvent) {
            this.column = CriteriaTable.this.columnAtPoint(new Point(mouseEvent.getX(), mouseEvent.getY()));
            this.row = CriteriaTable.this.rowAtPoint(new Point(mouseEvent.getX(), mouseEvent.getY()));
            if (CriteriaTable.this.isPopupEnabled()) {
                boolean b = false;
                for (int i = 0; i < CriteriaTable.this.getSelectedRowCount(); ++i) {
                    if (this.row == CriteriaTable.this.getSelectedRows()[i]) {
                        b = true;
                        break;
                    }
                }
                if (!b && this.row >= 0) {
                    CriteriaTable.this.setRowSelectionInterval(this.row, this.row);
                }
                if (mouseEvent.getButton() == 3 && CriteriaTable.this.getSelectedRowCount() > 0 && this.column == 0) {
                    this.enableDisableOptions();
                    this.popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        }
    }
}
