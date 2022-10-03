package javax.swing.text;

import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Shape;
import sun.swing.SwingUtilities2;
import java.awt.Container;
import javax.swing.JPasswordField;
import java.awt.Graphics;

public class PasswordView extends FieldView
{
    static char[] ONE;
    
    public PasswordView(final Element element) {
        super(element);
    }
    
    @Override
    protected int drawUnselectedText(final Graphics graphics, int drawEchoCharacter, final int n, final int n2, final int n3) throws BadLocationException {
        final Container container = this.getContainer();
        if (container instanceof JPasswordField) {
            final JPasswordField passwordField = (JPasswordField)container;
            if (!passwordField.echoCharIsSet()) {
                return super.drawUnselectedText(graphics, drawEchoCharacter, n, n2, n3);
            }
            if (passwordField.isEnabled()) {
                graphics.setColor(passwordField.getForeground());
            }
            else {
                graphics.setColor(passwordField.getDisabledTextColor());
            }
            final char echoChar = passwordField.getEchoChar();
            for (int n4 = n3 - n2, i = 0; i < n4; ++i) {
                drawEchoCharacter = this.drawEchoCharacter(graphics, drawEchoCharacter, n, echoChar);
            }
        }
        return drawEchoCharacter;
    }
    
    @Override
    protected int drawSelectedText(final Graphics graphics, int drawEchoCharacter, final int n, final int n2, final int n3) throws BadLocationException {
        graphics.setColor(this.selected);
        final Container container = this.getContainer();
        if (container instanceof JPasswordField) {
            final JPasswordField passwordField = (JPasswordField)container;
            if (!passwordField.echoCharIsSet()) {
                return super.drawSelectedText(graphics, drawEchoCharacter, n, n2, n3);
            }
            final char echoChar = passwordField.getEchoChar();
            for (int n4 = n3 - n2, i = 0; i < n4; ++i) {
                drawEchoCharacter = this.drawEchoCharacter(graphics, drawEchoCharacter, n, echoChar);
            }
        }
        return drawEchoCharacter;
    }
    
    protected int drawEchoCharacter(final Graphics graphics, final int n, final int n2, final char c) {
        PasswordView.ONE[0] = c;
        SwingUtilities2.drawChars(Utilities.getJComponent(this), graphics, PasswordView.ONE, 0, 1, n, n2);
        return n + graphics.getFontMetrics().charWidth(c);
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final Container container = this.getContainer();
        if (!(container instanceof JPasswordField)) {
            return null;
        }
        final JPasswordField passwordField = (JPasswordField)container;
        if (!passwordField.echoCharIsSet()) {
            return super.modelToView(n, shape, bias);
        }
        final char echoChar = passwordField.getEchoChar();
        final FontMetrics fontMetrics = passwordField.getFontMetrics(passwordField.getFont());
        final Rectangle bounds = this.adjustAllocation(shape).getBounds();
        final int n2 = (n - this.getStartOffset()) * fontMetrics.charWidth(echoChar);
        final Rectangle rectangle = bounds;
        rectangle.x += n2;
        bounds.width = 1;
        return bounds;
    }
    
    @Override
    public int viewToModel(final float n, final float n2, Shape adjustAllocation, final Position.Bias[] array) {
        array[0] = Position.Bias.Forward;
        int n3 = 0;
        final Container container = this.getContainer();
        if (container instanceof JPasswordField) {
            final JPasswordField passwordField = (JPasswordField)container;
            if (!passwordField.echoCharIsSet()) {
                return super.viewToModel(n, n2, adjustAllocation, array);
            }
            final int charWidth = passwordField.getFontMetrics(passwordField.getFont()).charWidth(passwordField.getEchoChar());
            adjustAllocation = this.adjustAllocation(adjustAllocation);
            final Rectangle rectangle = (Rectangle)((adjustAllocation instanceof Rectangle) ? adjustAllocation : adjustAllocation.getBounds());
            n3 = ((charWidth > 0) ? (((int)n - rectangle.x) / charWidth) : Integer.MAX_VALUE);
            if (n3 < 0) {
                n3 = 0;
            }
            else if (n3 > this.getStartOffset() + this.getDocument().getLength()) {
                n3 = this.getDocument().getLength() - this.getStartOffset();
            }
        }
        return this.getStartOffset() + n3;
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        switch (n) {
            case 0: {
                final Container container = this.getContainer();
                if (!(container instanceof JPasswordField)) {
                    break;
                }
                final JPasswordField passwordField = (JPasswordField)container;
                if (passwordField.echoCharIsSet()) {
                    final char echoChar = passwordField.getEchoChar();
                    final FontMetrics fontMetrics = passwordField.getFontMetrics(passwordField.getFont());
                    this.getDocument();
                    return (float)(fontMetrics.charWidth(echoChar) * this.getDocument().getLength());
                }
                break;
            }
        }
        return super.getPreferredSpan(n);
    }
    
    static {
        PasswordView.ONE = new char[1];
    }
}
