package net.sf.jsqlparser.util.cnfexpression;

import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.NotExpression;
import java.util.List;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.Expression;

public class CNFConverter
{
    private Expression root;
    private Expression dummy;
    private Expression temp1;
    private Expression temp2;
    private Expression child;
    private boolean isUsed;
    private CloneHelper clone;
    
    public CNFConverter() {
        this.isUsed = false;
        this.clone = new CloneHelper();
    }
    
    public static Expression convertToCNF(final Expression expr) {
        final CNFConverter cnf = new CNFConverter();
        return cnf.convert(expr);
    }
    
    private Expression convert(final Expression express) throws IllegalStateException {
        if (this.isUsed) {
            throw new IllegalStateException("The class could only be used once!");
        }
        this.isUsed = true;
        this.reorder(express);
        this.pushNotDown();
        this.gather();
        this.pushAndUp();
        this.changeBack();
        return this.root;
    }
    
    private void reorder(final Expression express) {
        this.root = this.clone.modify(express);
        final List<Expression> list = new ArrayList<Expression>();
        list.add(this.root);
        this.dummy = new MultiAndExpression(list);
    }
    
    private void pushNotDown() {
        this.temp1 = this.root;
        this.temp2 = this.dummy;
        this.pushNot(0);
        this.root = ((MultiAndExpression)this.dummy).getChild(0);
        this.temp1 = this.root;
        this.temp2 = this.dummy;
    }
    
    private void pushNot(final int index) {
        if (this.temp1 instanceof MultiAndExpression) {
            final MultiAndExpression and = (MultiAndExpression)this.temp1;
            for (int i = 0; i < and.size(); ++i) {
                this.temp2 = and;
                this.temp1 = and.getChild(i);
                this.pushNot(i);
            }
        }
        else if (this.temp1 instanceof MultiOrExpression) {
            final MultiOrExpression or = (MultiOrExpression)this.temp1;
            for (int i = 0; i < or.size(); ++i) {
                this.temp2 = or;
                this.temp1 = or.getChild(i);
                this.pushNot(i);
            }
        }
        else if (this.temp1 instanceof NotExpression) {
            this.handleNot(index);
        }
    }
    
    private void handleNot(final int index) {
        this.child = ((NotExpression)this.temp1).getExpression();
        int nums = 1;
        while (this.child instanceof NotExpression) {
            this.child = ((NotExpression)this.child).getExpression();
            ++nums;
        }
        if (nums % 2 == 0) {
            ((MultipleExpression)this.temp2).setChild(index, this.child);
            this.temp1 = this.child;
            this.pushNot(-1);
        }
        else {
            if (!(this.child instanceof MultiAndExpression) && !(this.child instanceof MultiOrExpression)) {
                if (this.child instanceof LikeExpression) {
                    ((LikeExpression)this.child).setNot(true);
                }
                else if (this.child instanceof BinaryExpression) {
                    ((BinaryExpression)this.child).setNot();
                }
                else {
                    this.child = new NotExpression(this.child);
                }
                ((MultipleExpression)this.temp2).setChild(index, this.child);
                return;
            }
            if (this.child instanceof MultiAndExpression) {
                final MultiAndExpression and = (MultiAndExpression)this.child;
                final List<Expression> list = new ArrayList<Expression>();
                for (int i = 0; i < and.size(); ++i) {
                    final NotExpression not = new NotExpression(and.getChild(i));
                    list.add(not);
                }
                this.temp1 = new MultiOrExpression(list);
                ((MultipleExpression)this.temp2).setChild(index, this.temp1);
                this.pushNot(-1);
            }
            else if (this.child instanceof MultiOrExpression) {
                final MultiOrExpression or = (MultiOrExpression)this.child;
                final List<Expression> list = new ArrayList<Expression>();
                for (int i = 0; i < or.size(); ++i) {
                    final NotExpression not = new NotExpression(or.getChild(i));
                    list.add(not);
                }
                this.temp1 = new MultiAndExpression(list);
                ((MultipleExpression)this.temp2).setChild(index, this.temp1);
                this.pushNot(-1);
            }
        }
    }
    
    private void gather() {
        final Queue<Expression> queue = new LinkedList<Expression>();
        queue.offer(this.temp1);
        while (!queue.isEmpty()) {
            final Expression express = queue.poll();
            if (express instanceof MultiAndExpression) {
                final MultiAndExpression and = (MultiAndExpression)express;
                while (true) {
                    int index = 0;
                    Expression get = null;
                    while (index < and.size()) {
                        get = and.getChild(index);
                        if (get instanceof MultiAndExpression) {
                            break;
                        }
                        ++index;
                    }
                    if (index == and.size()) {
                        break;
                    }
                    and.removeChild(index);
                    final MultipleExpression order = (MultipleExpression)get;
                    for (int i = 0; i < order.size(); ++i) {
                        and.addChild(index, order.getChild(i));
                        ++index;
                    }
                }
                for (int j = 0; j < and.size(); ++j) {
                    queue.offer(and.getChild(j));
                }
            }
            else {
                if (!(express instanceof MultiOrExpression)) {
                    continue;
                }
                final MultiOrExpression or = (MultiOrExpression)express;
                while (true) {
                    int index = 0;
                    Expression get = null;
                    while (index < or.size()) {
                        get = or.getChild(index);
                        if (get instanceof MultiOrExpression) {
                            break;
                        }
                        ++index;
                    }
                    if (index == or.size()) {
                        break;
                    }
                    or.removeChild(index);
                    final MultipleExpression order = (MultipleExpression)get;
                    for (int i = 0; i < order.size(); ++i) {
                        or.addChild(index, order.getChild(i));
                        ++index;
                    }
                }
                for (int j = 0; j < or.size(); ++j) {
                    queue.offer(or.getChild(j));
                }
            }
        }
    }
    
    private void pushAndUp() {
        final Queue<Mule> queue = new LinkedList<Mule>();
        final Stack<Mule> stack = new Stack<Mule>();
        final Mule root = new Mule(this.temp2, this.temp1, 0);
        queue.offer(root);
        int level = 1;
        while (!queue.isEmpty()) {
            for (int size = queue.size(), i = 0; i < size; ++i) {
                final Mule mule = queue.poll();
                final Expression parent = mule.parent;
                final Expression child = mule.child;
                if (parent instanceof MultiAndExpression && child instanceof MultiOrExpression) {
                    stack.push(mule);
                }
                if (child instanceof MultipleExpression) {
                    final MultipleExpression multi = (MultipleExpression)child;
                    for (int j = 0; j < multi.size(); ++j) {
                        final Expression get = multi.getChild(j);
                        if (get instanceof MultipleExpression) {
                            final Mule added = new Mule(child, get, level);
                            queue.offer(added);
                        }
                    }
                }
            }
            ++level;
        }
        this.pushAnd(stack);
        this.root = ((MultiAndExpression)this.dummy).getChild(0);
        this.temp1 = this.root;
        this.temp2 = this.dummy;
        this.gather();
    }
    
    private void pushAnd(final Stack<Mule> stack) {
        int level = 0;
        if (!stack.isEmpty()) {
            level = stack.peek().level;
        }
        while (!stack.isEmpty()) {
            final Mule mule = stack.pop();
            if (level != mule.level) {
                this.gather();
                level = mule.level;
            }
            final Queue<Mule> queue = new LinkedList<Mule>();
            final Mule combined = new Mule(mule.parent, mule.child, 0);
            queue.offer(combined);
            while (!queue.isEmpty()) {
                final Mule get = queue.poll();
                final Expression parent = get.parent;
                final Expression child = get.child;
                final MultipleExpression children = (MultipleExpression)child;
                int index = 0;
                MultiAndExpression and = null;
                while (index < children.size()) {
                    if (children.getChild(index) instanceof MultiAndExpression) {
                        and = (MultiAndExpression)children.getChild(index);
                        break;
                    }
                    ++index;
                }
                if (index == children.size()) {
                    continue;
                }
                children.removeChild(index);
                final MultipleExpression parents = (MultipleExpression)parent;
                final List<Expression> list = new ArrayList<Expression>();
                final MultiAndExpression newand = new MultiAndExpression(list);
                parents.setChild(parents.getIndex(children), newand);
                for (int i = 0; i < and.size(); ++i) {
                    final Expression temp = this.clone.shallowCopy(children);
                    final MultipleExpression mtemp = (MultipleExpression)temp;
                    mtemp.addChild(mtemp.size(), and.getChild(i));
                    newand.addChild(i, mtemp);
                    queue.offer(new Mule((Expression)newand, (Expression)mtemp, 0));
                }
            }
        }
    }
    
    private void changeBack() {
        if (!(this.root instanceof MultiAndExpression)) {
            return;
        }
        final MultipleExpression temp = (MultipleExpression)this.root;
        for (int i = 0; i < temp.size(); ++i) {
            temp.setChild(i, this.clone.changeBack(true, temp.getChild(i)));
        }
        this.root = this.clone.changeBack(false, temp);
    }
    
    private class Mule
    {
        private Expression parent;
        private Expression child;
        private int level;
        
        private Mule(final Expression parent, final Expression child, final int level) {
            this.parent = parent;
            this.child = child;
            this.level = level;
        }
    }
}
