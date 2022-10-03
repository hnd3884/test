package javax.swing.plaf.basic;

import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.plaf.ActionMapUIResource;

class LazyActionMap extends ActionMapUIResource
{
    private transient Object _loader;
    
    static void installLazyActionMap(final JComponent component, final Class clazz, final String s) {
        ActionMap actionMap = (ActionMap)UIManager.get(s);
        if (actionMap == null) {
            actionMap = new LazyActionMap(clazz);
            UIManager.getLookAndFeelDefaults().put(s, actionMap);
        }
        SwingUtilities.replaceUIActionMap(component, actionMap);
    }
    
    static ActionMap getActionMap(final Class clazz, final String s) {
        ActionMap actionMap = (ActionMap)UIManager.get(s);
        if (actionMap == null) {
            actionMap = new LazyActionMap(clazz);
            UIManager.getLookAndFeelDefaults().put(s, actionMap);
        }
        return actionMap;
    }
    
    private LazyActionMap(final Class loader) {
        this._loader = loader;
    }
    
    public void put(final Action action) {
        this.put(action.getValue("Name"), action);
    }
    
    @Override
    public void put(final Object o, final Action action) {
        this.loadIfNecessary();
        super.put(o, action);
    }
    
    @Override
    public Action get(final Object o) {
        this.loadIfNecessary();
        return super.get(o);
    }
    
    @Override
    public void remove(final Object o) {
        this.loadIfNecessary();
        super.remove(o);
    }
    
    @Override
    public void clear() {
        this.loadIfNecessary();
        super.clear();
    }
    
    @Override
    public Object[] keys() {
        this.loadIfNecessary();
        return super.keys();
    }
    
    @Override
    public int size() {
        this.loadIfNecessary();
        return super.size();
    }
    
    @Override
    public Object[] allKeys() {
        this.loadIfNecessary();
        return super.allKeys();
    }
    
    @Override
    public void setParent(final ActionMap parent) {
        this.loadIfNecessary();
        super.setParent(parent);
    }
    
    private void loadIfNecessary() {
        if (this._loader != null) {
            final Object loader = this._loader;
            this._loader = null;
            final Class clazz = (Class)loader;
            try {
                clazz.getDeclaredMethod("loadActionMap", LazyActionMap.class).invoke(clazz, this);
            }
            catch (final NoSuchMethodException ex) {
                assert false : "LazyActionMap unable to load actions " + clazz;
            }
            catch (final IllegalAccessException ex2) {
                assert false : "LazyActionMap unable to load actions " + ex2;
            }
            catch (final InvocationTargetException ex3) {
                assert false : "LazyActionMap unable to load actions " + ex3;
            }
            catch (final IllegalArgumentException ex4) {
                assert false : "LazyActionMap unable to load actions " + ex4;
            }
        }
    }
}
