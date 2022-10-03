package com.sun.jndi.toolkit.dir;

import javax.naming.Name;
import java.util.NoSuchElementException;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;

public class ContextEnumerator implements NamingEnumeration<Binding>
{
    private static boolean debug;
    private NamingEnumeration<Binding> children;
    private Binding currentChild;
    private boolean currentReturned;
    private Context root;
    private ContextEnumerator currentChildEnum;
    private boolean currentChildExpanded;
    private boolean rootProcessed;
    private int scope;
    private String contextName;
    
    public ContextEnumerator(final Context context) throws NamingException {
        this(context, 2);
    }
    
    public ContextEnumerator(final Context context, final int n) throws NamingException {
        this(context, n, "", n != 1);
    }
    
    protected ContextEnumerator(final Context root, final int scope, final String contextName, final boolean b) throws NamingException {
        this.children = null;
        this.currentChild = null;
        this.currentReturned = false;
        this.currentChildEnum = null;
        this.currentChildExpanded = false;
        this.rootProcessed = false;
        this.scope = 2;
        this.contextName = "";
        if (root == null) {
            throw new IllegalArgumentException("null context passed");
        }
        this.root = root;
        if (scope != 0) {
            this.children = this.getImmediateChildren(root);
        }
        this.scope = scope;
        this.contextName = contextName;
        this.rootProcessed = !b;
        this.prepNextChild();
    }
    
    protected NamingEnumeration<Binding> getImmediateChildren(final Context context) throws NamingException {
        return context.listBindings("");
    }
    
    protected ContextEnumerator newEnumerator(final Context context, final int n, final String s, final boolean b) throws NamingException {
        return new ContextEnumerator(context, n, s, b);
    }
    
    @Override
    public boolean hasMore() throws NamingException {
        return !this.rootProcessed || (this.scope != 0 && this.hasMoreDescendants());
    }
    
    @Override
    public boolean hasMoreElements() {
        try {
            return this.hasMore();
        }
        catch (final NamingException ex) {
            return false;
        }
    }
    
    @Override
    public Binding nextElement() {
        try {
            return this.next();
        }
        catch (final NamingException ex) {
            throw new NoSuchElementException(ex.toString());
        }
    }
    
    @Override
    public Binding next() throws NamingException {
        if (!this.rootProcessed) {
            this.rootProcessed = true;
            return new Binding("", this.root.getClass().getName(), this.root, true);
        }
        if (this.scope != 0 && this.hasMoreDescendants()) {
            return this.getNextDescendant();
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public void close() throws NamingException {
        this.root = null;
    }
    
    private boolean hasMoreChildren() throws NamingException {
        return this.children != null && this.children.hasMore();
    }
    
    private Binding getNextChild() throws NamingException {
        final Binding binding = this.children.next();
        Binding binding2;
        if (binding.isRelative() && !this.contextName.equals("")) {
            final Name parse = this.root.getNameParser("").parse(this.contextName);
            parse.add(binding.getName());
            if (ContextEnumerator.debug) {
                System.out.println("ContextEnumerator: adding " + parse);
            }
            binding2 = new Binding(parse.toString(), binding.getClassName(), binding.getObject(), binding.isRelative());
        }
        else {
            if (ContextEnumerator.debug) {
                System.out.println("ContextEnumerator: using old binding");
            }
            binding2 = binding;
        }
        return binding2;
    }
    
    private boolean hasMoreDescendants() throws NamingException {
        if (!this.currentReturned) {
            if (ContextEnumerator.debug) {
                System.out.println("hasMoreDescendants returning " + (this.currentChild != null));
            }
            return this.currentChild != null;
        }
        if (this.currentChildExpanded && this.currentChildEnum.hasMore()) {
            if (ContextEnumerator.debug) {
                System.out.println("hasMoreDescendants returning true");
            }
            return true;
        }
        if (ContextEnumerator.debug) {
            System.out.println("hasMoreDescendants returning hasMoreChildren");
        }
        return this.hasMoreChildren();
    }
    
    private Binding getNextDescendant() throws NamingException {
        if (!this.currentReturned) {
            if (ContextEnumerator.debug) {
                System.out.println("getNextDescedant: simple case");
            }
            this.currentReturned = true;
            return this.currentChild;
        }
        if (this.currentChildExpanded && this.currentChildEnum.hasMore()) {
            if (ContextEnumerator.debug) {
                System.out.println("getNextDescedant: expanded case");
            }
            return this.currentChildEnum.next();
        }
        if (ContextEnumerator.debug) {
            System.out.println("getNextDescedant: next case");
        }
        this.prepNextChild();
        return this.getNextDescendant();
    }
    
    private void prepNextChild() throws NamingException {
        if (this.hasMoreChildren()) {
            try {
                this.currentChild = this.getNextChild();
                this.currentReturned = false;
            }
            catch (final NamingException ex) {
                if (ContextEnumerator.debug) {
                    System.out.println(ex);
                }
                if (ContextEnumerator.debug) {
                    ex.printStackTrace();
                }
            }
            if (this.scope == 2 && this.currentChild.getObject() instanceof Context) {
                this.currentChildEnum = this.newEnumerator((Context)this.currentChild.getObject(), this.scope, this.currentChild.getName(), false);
                this.currentChildExpanded = true;
                if (ContextEnumerator.debug) {
                    System.out.println("prepNextChild: expanded");
                }
            }
            else {
                this.currentChildExpanded = false;
                this.currentChildEnum = null;
                if (ContextEnumerator.debug) {
                    System.out.println("prepNextChild: normal");
                }
            }
            return;
        }
        this.currentChild = null;
    }
    
    static {
        ContextEnumerator.debug = false;
    }
}
