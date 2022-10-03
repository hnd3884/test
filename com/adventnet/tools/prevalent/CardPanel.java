package com.adventnet.tools.prevalent;

import java.lang.reflect.Constructor;
import java.awt.Window;
import java.awt.Component;
import java.util.Enumeration;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.applet.Applet;
import java.util.Hashtable;
import javax.swing.JPanel;

public class CardPanel extends JPanel
{
    private Hashtable instances;
    private String[] classNames;
    private String[] cardNames;
    private Applet applet;
    private String curCardName;
    
    public CardPanel() {
        this.instances = new Hashtable(17);
        this.classNames = new String[0];
        this.cardNames = new String[0];
        this.curCardName = null;
        this.setLayout(new BorderLayout());
    }
    
    public CardPanel(final Applet appletArg) {
        this();
        this.applet = appletArg;
    }
    
    public String[] getClassNames() {
        return this.classNames;
    }
    
    public String[] getCardNames() {
        return this.cardNames;
    }
    
    private void clear() {
        this.showCard(null);
        final Enumeration enumer = this.instances.elements();
        while (enumer.hasMoreElements()) {
            final Object obj = enumer.nextElement();
            if (obj instanceof CardInterface) {
                ((CardInterface)obj).destroy();
            }
        }
        this.instances.clear();
    }
    
    public void setCardAndClassNames(final String[] cardAndClassNamesArg) {
        this.clear();
        if (cardAndClassNamesArg == null) {
            this.classNames = new String[0];
            this.cardNames = new String[0];
            return;
        }
        this.cardNames = new String[cardAndClassNamesArg.length];
        this.classNames = new String[cardAndClassNamesArg.length];
        for (int i = 0; i < cardAndClassNamesArg.length; ++i) {
            if (cardAndClassNamesArg[i] != null && !cardAndClassNamesArg[i].equals("")) {
                final int seperator = cardAndClassNamesArg[i].indexOf(61);
                if (seperator == -1) {
                    throw new IllegalArgumentException(" the Class name is missing for card Name:" + cardAndClassNamesArg[i]);
                }
                final String cardName = cardAndClassNamesArg[i].substring(0, seperator);
                this.cardNames[i] = cardName.trim();
                final String className = cardAndClassNamesArg[i].substring(seperator + 1);
                this.classNames[i] = className.trim();
            }
        }
    }
    
    public String[] getCardAndClassNames() {
        final String[] cardAndClasNames = new String[this.cardNames.length];
        for (int i = 0; i < this.cardNames.length; ++i) {
            cardAndClasNames[i] = this.cardNames[i] + "=" + this.classNames[i];
        }
        return cardAndClasNames;
    }
    
    public String getSelectedCardName() {
        return this.curCardName;
    }
    
    public Component getSelectedCard() {
        if (this.curCardName == null) {
            return null;
        }
        return this.instances.get(this.curCardName);
    }
    
    public Component getCard(final String cardNameArg) {
        return this.getCard(cardNameArg, null);
    }
    
    public Component getCard(final String cardNameArg, final Object[] initParamsArg) {
        return this.getComponent(cardNameArg, initParamsArg);
    }
    
    public Component getCardIfAlreadyCreated(final String cardNameArg) {
        return this.instances.get(cardNameArg);
    }
    
    public void showCard(final String cardNameArg) {
        this.showCard(cardNameArg, null, null);
    }
    
    public void showCard(final String cardNameArg, final Object[] showParamsArg) {
        this.showCard(cardNameArg, showParamsArg, null);
    }
    
    public void showCard(final String cardNameArg, final Object[] showParamsArg, final Object[] initParamsArg) {
        if (cardNameArg != this.curCardName && (cardNameArg == null || !cardNameArg.equals(this.curCardName))) {
            if (this.curCardName != null) {
                final Component comp = this.getComponent(0);
                if (comp instanceof CardInterface) {
                    ((CardInterface)comp).disappear();
                }
                this.remove(comp);
                this.curCardName = null;
            }
            if (cardNameArg != null) {
                final Component comp = this.getComponent(cardNameArg, initParamsArg);
                this.add(comp, "Center");
                if (comp instanceof CardInterface) {
                    ((CardInterface)comp).show(showParamsArg);
                }
            }
            this.curCardName = cardNameArg;
            this.revalidate();
            this.repaint();
        }
    }
    
    public String getFirstCardName() {
        if (this.cardNames.length == 0) {
            return null;
        }
        return this.cardNames[0];
    }
    
    public String getNextCardName() {
        String nextCardName = null;
        if (this.curCardName == null) {
            if (this.cardNames.length > 0) {
                nextCardName = this.cardNames[0];
            }
        }
        else {
            final int index = this.getIndexFor(this.curCardName);
            if (index != this.cardNames.length - 1) {
                nextCardName = this.cardNames[index + 1];
            }
        }
        return nextCardName;
    }
    
    public String getPreviousCardName() {
        String previousCardName = null;
        if (this.curCardName != null) {
            final int index = this.getIndexFor(this.curCardName);
            if (index != 0) {
                previousCardName = this.cardNames[index - 1];
            }
        }
        return previousCardName;
    }
    
    private int getIndexFor(final String cardNameArg) {
        int index = -1;
        for (int i = 0; i < this.cardNames.length; ++i) {
            if (cardNameArg.equals(this.cardNames[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private Component getComponent(final String cardNameArg, final Object[] initParamsArg) {
        Component comp = this.instances.get(cardNameArg);
        if (comp != null) {
            return comp;
        }
        final int index = this.getIndexFor(cardNameArg);
        if (index == -1) {
            throw new IllegalArgumentException(" CardName \"" + cardNameArg + "\" has not been specified in the CardAndClassNames property.");
        }
        comp = this.getCardComponent(this.classNames[index], cardNameArg);
        if (initParamsArg != null && comp instanceof CardInterface) {
            ((CardInterface)comp).initialize(initParamsArg);
        }
        this.instances.put(cardNameArg, comp);
        return comp;
    }
    
    private Component getCardComponent(final String classNameArg, final String cardNameArg) {
        Class cls = null;
        try {
            cls = Class.forName(classNameArg);
        }
        catch (final Throwable th) {
            throw new NoClassDefFoundError(" Unable to create class \"" + classNameArg + "\" corresponding to card \"" + cardNameArg + "\". Encountered exception " + th.getClass().getName() + ". Exception Message : " + th.getMessage());
        }
        if (!Component.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException(" Cannot use class \"" + classNameArg + "\" corresponding to card \"" + cardNameArg + "\". The class is not a subclass of " + Component.class.getName());
        }
        if (Window.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException(" Cannot use class \"" + classNameArg + "\" corresponding to card \"" + cardNameArg + "\". The class is a subclass of " + Window.class.getName() + ".Cannot add a window to a container");
        }
        Object obj = null;
        try {
            if (this.applet != null) {
                try {
                    final Constructor cons = cls.getConstructor(Applet.class);
                    obj = cons.newInstance(this.applet);
                }
                catch (final NoSuchMethodException ex) {}
            }
            if (obj == null) {
                obj = cls.newInstance();
            }
        }
        catch (final Throwable th2) {
            throw new InstantiationError(" Unable to create a new Instance of class \"" + classNameArg + "\" corresponding to card \"" + cardNameArg + "\". Encountered exception " + th2.getClass().getName() + ". Exception Message : " + th2.getMessage());
        }
        return (Component)obj;
    }
}
