package javax.accessibility;

import sun.awt.AWTAccessor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.IllegalComponentStateException;
import java.util.Locale;
import java.beans.PropertyChangeSupport;
import sun.awt.AppContext;

public abstract class AccessibleContext
{
    private volatile AppContext targetAppContext;
    public static final String ACCESSIBLE_NAME_PROPERTY = "AccessibleName";
    public static final String ACCESSIBLE_DESCRIPTION_PROPERTY = "AccessibleDescription";
    public static final String ACCESSIBLE_STATE_PROPERTY = "AccessibleState";
    public static final String ACCESSIBLE_VALUE_PROPERTY = "AccessibleValue";
    public static final String ACCESSIBLE_SELECTION_PROPERTY = "AccessibleSelection";
    public static final String ACCESSIBLE_CARET_PROPERTY = "AccessibleCaret";
    public static final String ACCESSIBLE_VISIBLE_DATA_PROPERTY = "AccessibleVisibleData";
    public static final String ACCESSIBLE_CHILD_PROPERTY = "AccessibleChild";
    public static final String ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY = "AccessibleActiveDescendant";
    public static final String ACCESSIBLE_TABLE_CAPTION_CHANGED = "accessibleTableCaptionChanged";
    public static final String ACCESSIBLE_TABLE_SUMMARY_CHANGED = "accessibleTableSummaryChanged";
    public static final String ACCESSIBLE_TABLE_MODEL_CHANGED = "accessibleTableModelChanged";
    public static final String ACCESSIBLE_TABLE_ROW_HEADER_CHANGED = "accessibleTableRowHeaderChanged";
    public static final String ACCESSIBLE_TABLE_ROW_DESCRIPTION_CHANGED = "accessibleTableRowDescriptionChanged";
    public static final String ACCESSIBLE_TABLE_COLUMN_HEADER_CHANGED = "accessibleTableColumnHeaderChanged";
    public static final String ACCESSIBLE_TABLE_COLUMN_DESCRIPTION_CHANGED = "accessibleTableColumnDescriptionChanged";
    public static final String ACCESSIBLE_ACTION_PROPERTY = "accessibleActionProperty";
    public static final String ACCESSIBLE_HYPERTEXT_OFFSET = "AccessibleHypertextOffset";
    public static final String ACCESSIBLE_TEXT_PROPERTY = "AccessibleText";
    public static final String ACCESSIBLE_INVALIDATE_CHILDREN = "accessibleInvalidateChildren";
    public static final String ACCESSIBLE_TEXT_ATTRIBUTES_CHANGED = "accessibleTextAttributesChanged";
    public static final String ACCESSIBLE_COMPONENT_BOUNDS_CHANGED = "accessibleComponentBoundsChanged";
    protected Accessible accessibleParent;
    protected String accessibleName;
    protected String accessibleDescription;
    private PropertyChangeSupport accessibleChangeSupport;
    private AccessibleRelationSet relationSet;
    private Object nativeAXResource;
    
    public AccessibleContext() {
        this.accessibleParent = null;
        this.accessibleName = null;
        this.accessibleDescription = null;
        this.accessibleChangeSupport = null;
        this.relationSet = new AccessibleRelationSet();
    }
    
    public String getAccessibleName() {
        return this.accessibleName;
    }
    
    public void setAccessibleName(final String accessibleName) {
        this.firePropertyChange("AccessibleName", this.accessibleName, this.accessibleName = accessibleName);
    }
    
    public String getAccessibleDescription() {
        return this.accessibleDescription;
    }
    
    public void setAccessibleDescription(final String accessibleDescription) {
        this.firePropertyChange("AccessibleDescription", this.accessibleDescription, this.accessibleDescription = accessibleDescription);
    }
    
    public abstract AccessibleRole getAccessibleRole();
    
    public abstract AccessibleStateSet getAccessibleStateSet();
    
    public Accessible getAccessibleParent() {
        return this.accessibleParent;
    }
    
    public void setAccessibleParent(final Accessible accessibleParent) {
        this.accessibleParent = accessibleParent;
    }
    
    public abstract int getAccessibleIndexInParent();
    
    public abstract int getAccessibleChildrenCount();
    
    public abstract Accessible getAccessibleChild(final int p0);
    
    public abstract Locale getLocale() throws IllegalComponentStateException;
    
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.accessibleChangeSupport == null) {
            this.accessibleChangeSupport = new PropertyChangeSupport(this);
        }
        this.accessibleChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.accessibleChangeSupport != null) {
            this.accessibleChangeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public AccessibleAction getAccessibleAction() {
        return null;
    }
    
    public AccessibleComponent getAccessibleComponent() {
        return null;
    }
    
    public AccessibleSelection getAccessibleSelection() {
        return null;
    }
    
    public AccessibleText getAccessibleText() {
        return null;
    }
    
    public AccessibleEditableText getAccessibleEditableText() {
        return null;
    }
    
    public AccessibleValue getAccessibleValue() {
        return null;
    }
    
    public AccessibleIcon[] getAccessibleIcon() {
        return null;
    }
    
    public AccessibleRelationSet getAccessibleRelationSet() {
        return this.relationSet;
    }
    
    public AccessibleTable getAccessibleTable() {
        return null;
    }
    
    public void firePropertyChange(final String s, final Object o, final Object o2) {
        if (this.accessibleChangeSupport != null) {
            if (o2 instanceof PropertyChangeEvent) {
                this.accessibleChangeSupport.firePropertyChange((PropertyChangeEvent)o2);
            }
            else {
                this.accessibleChangeSupport.firePropertyChange(s, o, o2);
            }
        }
    }
    
    static {
        AWTAccessor.setAccessibleContextAccessor(new AWTAccessor.AccessibleContextAccessor() {
            @Override
            public void setAppContext(final AccessibleContext accessibleContext, final AppContext appContext) {
                accessibleContext.targetAppContext = appContext;
            }
            
            @Override
            public AppContext getAppContext(final AccessibleContext accessibleContext) {
                return accessibleContext.targetAppContext;
            }
        });
    }
}
