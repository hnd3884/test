package javax.swing.text;

import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Component;
import javax.swing.UIManager;
import sun.awt.SunToolkit;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import javax.swing.Action;

public class DefaultEditorKit extends EditorKit
{
    public static final String EndOfLineStringProperty = "__EndOfLine__";
    public static final String insertContentAction = "insert-content";
    public static final String insertBreakAction = "insert-break";
    public static final String insertTabAction = "insert-tab";
    public static final String deletePrevCharAction = "delete-previous";
    public static final String deleteNextCharAction = "delete-next";
    public static final String deleteNextWordAction = "delete-next-word";
    public static final String deletePrevWordAction = "delete-previous-word";
    public static final String readOnlyAction = "set-read-only";
    public static final String writableAction = "set-writable";
    public static final String cutAction = "cut-to-clipboard";
    public static final String copyAction = "copy-to-clipboard";
    public static final String pasteAction = "paste-from-clipboard";
    public static final String beepAction = "beep";
    public static final String pageUpAction = "page-up";
    public static final String pageDownAction = "page-down";
    static final String selectionPageUpAction = "selection-page-up";
    static final String selectionPageDownAction = "selection-page-down";
    static final String selectionPageLeftAction = "selection-page-left";
    static final String selectionPageRightAction = "selection-page-right";
    public static final String forwardAction = "caret-forward";
    public static final String backwardAction = "caret-backward";
    public static final String selectionForwardAction = "selection-forward";
    public static final String selectionBackwardAction = "selection-backward";
    public static final String upAction = "caret-up";
    public static final String downAction = "caret-down";
    public static final String selectionUpAction = "selection-up";
    public static final String selectionDownAction = "selection-down";
    public static final String beginWordAction = "caret-begin-word";
    public static final String endWordAction = "caret-end-word";
    public static final String selectionBeginWordAction = "selection-begin-word";
    public static final String selectionEndWordAction = "selection-end-word";
    public static final String previousWordAction = "caret-previous-word";
    public static final String nextWordAction = "caret-next-word";
    public static final String selectionPreviousWordAction = "selection-previous-word";
    public static final String selectionNextWordAction = "selection-next-word";
    public static final String beginLineAction = "caret-begin-line";
    public static final String endLineAction = "caret-end-line";
    public static final String selectionBeginLineAction = "selection-begin-line";
    public static final String selectionEndLineAction = "selection-end-line";
    public static final String beginParagraphAction = "caret-begin-paragraph";
    public static final String endParagraphAction = "caret-end-paragraph";
    public static final String selectionBeginParagraphAction = "selection-begin-paragraph";
    public static final String selectionEndParagraphAction = "selection-end-paragraph";
    public static final String beginAction = "caret-begin";
    public static final String endAction = "caret-end";
    public static final String selectionBeginAction = "selection-begin";
    public static final String selectionEndAction = "selection-end";
    public static final String selectWordAction = "select-word";
    public static final String selectLineAction = "select-line";
    public static final String selectParagraphAction = "select-paragraph";
    public static final String selectAllAction = "select-all";
    static final String unselectAction = "unselect";
    static final String toggleComponentOrientationAction = "toggle-componentOrientation";
    public static final String defaultKeyTypedAction = "default-typed";
    private static final Action[] defaultActions;
    
    @Override
    public String getContentType() {
        return "text/plain";
    }
    
    @Override
    public ViewFactory getViewFactory() {
        return null;
    }
    
    @Override
    public Action[] getActions() {
        return DefaultEditorKit.defaultActions.clone();
    }
    
    @Override
    public Caret createCaret() {
        return null;
    }
    
    @Override
    public Document createDefaultDocument() {
        return new PlainDocument();
    }
    
    @Override
    public void read(final InputStream inputStream, final Document document, final int n) throws IOException, BadLocationException {
        this.read(new InputStreamReader(inputStream), document, n);
    }
    
    @Override
    public void write(final OutputStream outputStream, final Document document, final int n, final int n2) throws IOException, BadLocationException {
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        this.write(outputStreamWriter, document, n, n2);
        outputStreamWriter.flush();
    }
    
    MutableAttributeSet getInputAttributes() {
        return null;
    }
    
    @Override
    public void read(final Reader reader, final Document document, int n) throws IOException, BadLocationException {
        final char[] array = new char[4096];
        int n2 = 0;
        boolean b = false;
        boolean b2 = false;
        final boolean b3 = document.getLength() == 0;
        final MutableAttributeSet inputAttributes = this.getInputAttributes();
        int read;
        while ((read = reader.read(array, 0, array.length)) != -1) {
            int n3 = 0;
            for (int i = 0; i < read; ++i) {
                switch (array[i]) {
                    case '\r': {
                        if (n2 == 0) {
                            n2 = 1;
                            break;
                        }
                        b2 = true;
                        if (i == 0) {
                            document.insertString(n, "\n", inputAttributes);
                            ++n;
                            break;
                        }
                        array[i - 1] = '\n';
                        break;
                    }
                    case '\n': {
                        if (n2 != 0) {
                            if (i > n3 + 1) {
                                document.insertString(n, new String(array, n3, i - n3 - 1), inputAttributes);
                                n += i - n3 - 1;
                            }
                            n2 = 0;
                            n3 = i;
                            b = true;
                            break;
                        }
                        break;
                    }
                    default: {
                        if (n2 != 0) {
                            b2 = true;
                            if (i == 0) {
                                document.insertString(n, "\n", inputAttributes);
                                ++n;
                            }
                            else {
                                array[i - 1] = '\n';
                            }
                            n2 = 0;
                            break;
                        }
                        break;
                    }
                }
            }
            if (n3 < read) {
                if (n2 != 0) {
                    if (n3 >= read - 1) {
                        continue;
                    }
                    document.insertString(n, new String(array, n3, read - n3 - 1), inputAttributes);
                    n += read - n3 - 1;
                }
                else {
                    document.insertString(n, new String(array, n3, read - n3), inputAttributes);
                    n += read - n3;
                }
            }
        }
        if (n2 != 0) {
            document.insertString(n, "\n", inputAttributes);
            b2 = true;
        }
        if (b3) {
            if (b) {
                document.putProperty("__EndOfLine__", "\r\n");
            }
            else if (b2) {
                document.putProperty("__EndOfLine__", "\r");
            }
            else {
                document.putProperty("__EndOfLine__", "\n");
            }
        }
    }
    
    @Override
    public void write(final Writer writer, final Document document, final int n, final int n2) throws IOException, BadLocationException {
        if (n < 0 || n + n2 > document.getLength()) {
            throw new BadLocationException("DefaultEditorKit.write", n);
        }
        final Segment segment = new Segment();
        int i = n2;
        int n3 = n;
        Object o = document.getProperty("__EndOfLine__");
        if (o == null) {
            try {
                o = System.getProperty("line.separator");
            }
            catch (final SecurityException ex) {}
        }
        String s;
        if (o instanceof String) {
            s = (String)o;
        }
        else {
            s = null;
        }
        if (o != null && !s.equals("\n")) {
            while (i > 0) {
                final int min = Math.min(i, 4096);
                document.getText(n3, min, segment);
                int offset = segment.offset;
                final char[] array = segment.array;
                final int n4 = offset + segment.count;
                for (int j = offset; j < n4; ++j) {
                    if (array[j] == '\n') {
                        if (j > offset) {
                            writer.write(array, offset, j - offset);
                        }
                        writer.write(s);
                        offset = j + 1;
                    }
                }
                if (n4 > offset) {
                    writer.write(array, offset, n4 - offset);
                }
                n3 += min;
                i -= min;
            }
        }
        else {
            while (i > 0) {
                final int min2 = Math.min(i, 4096);
                document.getText(n3, min2, segment);
                writer.write(segment.array, segment.offset, segment.count);
                n3 += min2;
                i -= min2;
            }
        }
        writer.flush();
    }
    
    static {
        defaultActions = new Action[] { new InsertContentAction(), new DeletePrevCharAction(), new DeleteNextCharAction(), new ReadOnlyAction(), new DeleteWordAction("delete-previous-word"), new DeleteWordAction("delete-next-word"), new WritableAction(), new CutAction(), new CopyAction(), new PasteAction(), new VerticalPageAction("page-up", -1, false), new VerticalPageAction("page-down", 1, false), new VerticalPageAction("selection-page-up", -1, true), new VerticalPageAction("selection-page-down", 1, true), new PageAction("selection-page-left", true, true), new PageAction("selection-page-right", false, true), new InsertBreakAction(), new BeepAction(), new NextVisualPositionAction("caret-forward", false, 3), new NextVisualPositionAction("caret-backward", false, 7), new NextVisualPositionAction("selection-forward", true, 3), new NextVisualPositionAction("selection-backward", true, 7), new NextVisualPositionAction("caret-up", false, 1), new NextVisualPositionAction("caret-down", false, 5), new NextVisualPositionAction("selection-up", true, 1), new NextVisualPositionAction("selection-down", true, 5), new BeginWordAction("caret-begin-word", false), new EndWordAction("caret-end-word", false), new BeginWordAction("selection-begin-word", true), new EndWordAction("selection-end-word", true), new PreviousWordAction("caret-previous-word", false), new NextWordAction("caret-next-word", false), new PreviousWordAction("selection-previous-word", true), new NextWordAction("selection-next-word", true), new BeginLineAction("caret-begin-line", false), new EndLineAction("caret-end-line", false), new BeginLineAction("selection-begin-line", true), new EndLineAction("selection-end-line", true), new BeginParagraphAction("caret-begin-paragraph", false), new EndParagraphAction("caret-end-paragraph", false), new BeginParagraphAction("selection-begin-paragraph", true), new EndParagraphAction("selection-end-paragraph", true), new BeginAction("caret-begin", false), new EndAction("caret-end", false), new BeginAction("selection-begin", true), new EndAction("selection-end", true), new DefaultKeyTypedAction(), new InsertTabAction(), new SelectWordAction(), new SelectLineAction(), new SelectParagraphAction(), new SelectAllAction(), new UnselectAction(), new ToggleComponentOrientationAction(), new DumpModelAction() };
    }
    
    public static class DefaultKeyTypedAction extends TextAction
    {
        public DefaultKeyTypedAction() {
            super("default-typed");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null && actionEvent != null) {
                if (!textComponent.isEditable() || !textComponent.isEnabled()) {
                    return;
                }
                final String actionCommand = actionEvent.getActionCommand();
                final int modifiers = actionEvent.getModifiers();
                if (actionCommand != null && actionCommand.length() > 0) {
                    boolean printableCharacterModifiersMask = true;
                    final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                    if (defaultToolkit instanceof SunToolkit) {
                        printableCharacterModifiersMask = ((SunToolkit)defaultToolkit).isPrintableCharacterModifiersMask(modifiers);
                    }
                    final char char1 = actionCommand.charAt(0);
                    if ((printableCharacterModifiersMask && char1 >= ' ' && char1 != '\u007f') || (!printableCharacterModifiersMask && char1 >= '\u200c' && char1 <= '\u200d')) {
                        textComponent.replaceSelection(actionCommand);
                    }
                }
            }
        }
    }
    
    public static class InsertContentAction extends TextAction
    {
        public InsertContentAction() {
            super("insert-content");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null && actionEvent != null) {
                if (!textComponent.isEditable() || !textComponent.isEnabled()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                    return;
                }
                final String actionCommand = actionEvent.getActionCommand();
                if (actionCommand != null) {
                    textComponent.replaceSelection(actionCommand);
                }
                else {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    public static class InsertBreakAction extends TextAction
    {
        public InsertBreakAction() {
            super("insert-break");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                if (!textComponent.isEditable() || !textComponent.isEnabled()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                    return;
                }
                textComponent.replaceSelection("\n");
            }
        }
    }
    
    public static class InsertTabAction extends TextAction
    {
        public InsertTabAction() {
            super("insert-tab");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                if (!textComponent.isEditable() || !textComponent.isEnabled()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                    return;
                }
                textComponent.replaceSelection("\t");
            }
        }
    }
    
    static class DeletePrevCharAction extends TextAction
    {
        DeletePrevCharAction() {
            super("delete-previous");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            boolean b = true;
            if (textComponent != null && textComponent.isEditable()) {
                try {
                    final Document document = textComponent.getDocument();
                    final Caret caret = textComponent.getCaret();
                    final int dot = caret.getDot();
                    final int mark = caret.getMark();
                    if (dot != mark) {
                        document.remove(Math.min(dot, mark), Math.abs(dot - mark));
                        b = false;
                    }
                    else if (dot > 0) {
                        int n = 1;
                        if (dot > 1) {
                            final String text = document.getText(dot - 2, 2);
                            final char char1 = text.charAt(0);
                            final char char2 = text.charAt(1);
                            if (char1 >= '\ud800' && char1 <= '\udbff' && char2 >= '\udc00' && char2 <= '\udfff') {
                                n = 2;
                            }
                        }
                        document.remove(dot - n, n);
                        b = false;
                    }
                }
                catch (final BadLocationException ex) {}
            }
            if (b) {
                UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
            }
        }
    }
    
    static class DeleteNextCharAction extends TextAction
    {
        DeleteNextCharAction() {
            super("delete-next");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            boolean b = true;
            if (textComponent != null && textComponent.isEditable()) {
                try {
                    final Document document = textComponent.getDocument();
                    final Caret caret = textComponent.getCaret();
                    final int dot = caret.getDot();
                    final int mark = caret.getMark();
                    if (dot != mark) {
                        document.remove(Math.min(dot, mark), Math.abs(dot - mark));
                        b = false;
                    }
                    else if (dot < document.getLength()) {
                        int n = 1;
                        if (dot < document.getLength() - 1) {
                            final String text = document.getText(dot, 2);
                            final char char1 = text.charAt(0);
                            final char char2 = text.charAt(1);
                            if (char1 >= '\ud800' && char1 <= '\udbff' && char2 >= '\udc00' && char2 <= '\udfff') {
                                n = 2;
                            }
                        }
                        document.remove(dot, n);
                        b = false;
                    }
                }
                catch (final BadLocationException ex) {}
            }
            if (b) {
                UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
            }
        }
    }
    
    static class DeleteWordAction extends TextAction
    {
        DeleteWordAction(final String s) {
            super(s);
            assert s == "delete-next-word";
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null && actionEvent != null) {
                if (!textComponent.isEditable() || !textComponent.isEnabled()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                    return;
                }
                boolean b = true;
                try {
                    final int selectionStart = textComponent.getSelectionStart();
                    final Element paragraphElement = Utilities.getParagraphElement(textComponent, selectionStart);
                    int n;
                    if ("delete-next-word" == this.getValue("Name")) {
                        n = Utilities.getNextWordInParagraph(textComponent, paragraphElement, selectionStart, false);
                        if (n == -1) {
                            final int endOffset = paragraphElement.getEndOffset();
                            if (selectionStart == endOffset - 1) {
                                n = endOffset;
                            }
                            else {
                                n = endOffset - 1;
                            }
                        }
                    }
                    else {
                        n = Utilities.getPrevWordInParagraph(textComponent, paragraphElement, selectionStart);
                        if (n == -1) {
                            final int startOffset = paragraphElement.getStartOffset();
                            if (selectionStart == startOffset) {
                                n = startOffset - 1;
                            }
                            else {
                                n = startOffset;
                            }
                        }
                    }
                    final int min = Math.min(selectionStart, n);
                    final int abs = Math.abs(n - selectionStart);
                    if (min >= 0) {
                        textComponent.getDocument().remove(min, abs);
                        b = false;
                    }
                }
                catch (final BadLocationException ex) {}
                if (b) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    static class ReadOnlyAction extends TextAction
    {
        ReadOnlyAction() {
            super("set-read-only");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                textComponent.setEditable(false);
            }
        }
    }
    
    static class WritableAction extends TextAction
    {
        WritableAction() {
            super("set-writable");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                textComponent.setEditable(true);
            }
        }
    }
    
    public static class CutAction extends TextAction
    {
        public CutAction() {
            super("cut-to-clipboard");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                textComponent.cut();
            }
        }
    }
    
    public static class CopyAction extends TextAction
    {
        public CopyAction() {
            super("copy-to-clipboard");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                textComponent.copy();
            }
        }
    }
    
    public static class PasteAction extends TextAction
    {
        public PasteAction() {
            super("paste-from-clipboard");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                textComponent.paste();
            }
        }
    }
    
    public static class BeepAction extends TextAction
    {
        public BeepAction() {
            super("beep");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            UIManager.getLookAndFeel().provideErrorFeedback(this.getTextComponent(actionEvent));
        }
    }
    
    static class VerticalPageAction extends TextAction
    {
        private boolean select;
        private int direction;
        
        public VerticalPageAction(final String s, final int direction, final boolean select) {
            super(s);
            this.select = select;
            this.direction = direction;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final Rectangle visibleRect = textComponent.getVisibleRect();
                final Rectangle rectangle = new Rectangle(visibleRect);
                final int caretPosition = textComponent.getCaretPosition();
                int n = this.direction * textComponent.getScrollableBlockIncrement(visibleRect, 1, this.direction);
                final int y = visibleRect.y;
                final Caret caret = textComponent.getCaret();
                final Point magicCaretPosition = caret.getMagicCaretPosition();
                if (caretPosition != -1) {
                    try {
                        final Rectangle modelToView = textComponent.modelToView(caretPosition);
                        final int n2 = (magicCaretPosition != null) ? magicCaretPosition.x : modelToView.x;
                        final int height = modelToView.height;
                        if (height > 0) {
                            n = n / height * height;
                        }
                        rectangle.y = this.constrainY(textComponent, y + n, visibleRect.height);
                        int n3;
                        if (visibleRect.contains(modelToView.x, modelToView.y)) {
                            n3 = textComponent.viewToModel(new Point(n2, this.constrainY(textComponent, modelToView.y + n, 0)));
                        }
                        else if (this.direction == -1) {
                            n3 = textComponent.viewToModel(new Point(n2, rectangle.y));
                        }
                        else {
                            n3 = textComponent.viewToModel(new Point(n2, rectangle.y + visibleRect.height));
                        }
                        final int constrainOffset = this.constrainOffset(textComponent, n3);
                        if (constrainOffset != caretPosition) {
                            final int adjustedY = this.getAdjustedY(textComponent, rectangle, constrainOffset);
                            if ((this.direction == -1 && adjustedY <= y) || (this.direction == 1 && adjustedY >= y)) {
                                rectangle.y = adjustedY;
                                if (this.select) {
                                    textComponent.moveCaretPosition(constrainOffset);
                                }
                                else {
                                    textComponent.setCaretPosition(constrainOffset);
                                }
                            }
                        }
                    }
                    catch (final BadLocationException ex) {}
                }
                else {
                    rectangle.y = this.constrainY(textComponent, y + n, visibleRect.height);
                }
                if (magicCaretPosition != null) {
                    caret.setMagicCaretPosition(magicCaretPosition);
                }
                textComponent.scrollRectToVisible(rectangle);
            }
        }
        
        private int constrainY(final JTextComponent textComponent, int max, final int n) {
            if (max < 0) {
                max = 0;
            }
            else if (max + n > textComponent.getHeight()) {
                max = Math.max(0, textComponent.getHeight() - n);
            }
            return max;
        }
        
        private int constrainOffset(final JTextComponent textComponent, int length) {
            final Document document = textComponent.getDocument();
            if (length != 0 && length > document.getLength()) {
                length = document.getLength();
            }
            if (length < 0) {
                length = 0;
            }
            return length;
        }
        
        private int getAdjustedY(final JTextComponent textComponent, final Rectangle rectangle, final int n) {
            int n2 = rectangle.y;
            try {
                final Rectangle modelToView = textComponent.modelToView(n);
                if (modelToView.y < rectangle.y) {
                    n2 = modelToView.y;
                }
                else if (modelToView.y > rectangle.y + rectangle.height || modelToView.y + modelToView.height > rectangle.y + rectangle.height) {
                    n2 = modelToView.y + modelToView.height - rectangle.height;
                }
            }
            catch (final BadLocationException ex) {}
            return n2;
        }
    }
    
    static class PageAction extends TextAction
    {
        private boolean select;
        private boolean left;
        
        public PageAction(final String s, final boolean left, final boolean select) {
            super(s);
            this.select = select;
            this.left = left;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final Rectangle rectangle = new Rectangle();
                textComponent.computeVisibleRect(rectangle);
                if (this.left) {
                    rectangle.x = Math.max(0, rectangle.x - rectangle.width);
                }
                else {
                    final Rectangle rectangle2 = rectangle;
                    rectangle2.x += rectangle.width;
                }
                if (textComponent.getCaretPosition() != -1) {
                    int caretPosition;
                    if (this.left) {
                        caretPosition = textComponent.viewToModel(new Point(rectangle.x, rectangle.y));
                    }
                    else {
                        caretPosition = textComponent.viewToModel(new Point(rectangle.x + rectangle.width - 1, rectangle.y + rectangle.height - 1));
                    }
                    final Document document = textComponent.getDocument();
                    if (caretPosition != 0 && caretPosition > document.getLength() - 1) {
                        caretPosition = document.getLength() - 1;
                    }
                    else if (caretPosition < 0) {
                        caretPosition = 0;
                    }
                    if (this.select) {
                        textComponent.moveCaretPosition(caretPosition);
                    }
                    else {
                        textComponent.setCaretPosition(caretPosition);
                    }
                }
            }
        }
    }
    
    static class DumpModelAction extends TextAction
    {
        DumpModelAction() {
            super("dump-model");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final Document document = textComponent.getDocument();
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).dump(System.err);
                }
            }
        }
    }
    
    static class NextVisualPositionAction extends TextAction
    {
        private boolean select;
        private int direction;
        
        NextVisualPositionAction(final String s, final boolean select, final int direction) {
            super(s);
            this.select = select;
            this.direction = direction;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final Caret caret = textComponent.getCaret();
                final DefaultCaret defaultCaret = (caret instanceof DefaultCaret) ? ((DefaultCaret)caret) : null;
                final int dot = caret.getDot();
                final Position.Bias[] array = { null };
                Point magicCaretPosition = caret.getMagicCaretPosition();
                try {
                    if (magicCaretPosition == null && (this.direction == 1 || this.direction == 5)) {
                        final Rectangle rectangle = (defaultCaret != null) ? textComponent.getUI().modelToView(textComponent, dot, defaultCaret.getDotBias()) : textComponent.modelToView(dot);
                        magicCaretPosition = new Point(rectangle.x, rectangle.y);
                    }
                    final NavigationFilter navigationFilter = textComponent.getNavigationFilter();
                    int dot2;
                    if (navigationFilter != null) {
                        dot2 = navigationFilter.getNextVisualPositionFrom(textComponent, dot, (defaultCaret != null) ? defaultCaret.getDotBias() : Position.Bias.Forward, this.direction, array);
                    }
                    else {
                        dot2 = textComponent.getUI().getNextVisualPositionFrom(textComponent, dot, (defaultCaret != null) ? defaultCaret.getDotBias() : Position.Bias.Forward, this.direction, array);
                    }
                    if (array[0] == null) {
                        array[0] = Position.Bias.Forward;
                    }
                    if (defaultCaret != null) {
                        if (this.select) {
                            defaultCaret.moveDot(dot2, array[0]);
                        }
                        else {
                            defaultCaret.setDot(dot2, array[0]);
                        }
                    }
                    else if (this.select) {
                        caret.moveDot(dot2);
                    }
                    else {
                        caret.setDot(dot2);
                    }
                    if (magicCaretPosition != null && (this.direction == 1 || this.direction == 5)) {
                        textComponent.getCaret().setMagicCaretPosition(magicCaretPosition);
                    }
                }
                catch (final BadLocationException ex) {}
            }
        }
    }
    
    static class BeginWordAction extends TextAction
    {
        private boolean select;
        
        BeginWordAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                try {
                    final int wordStart = Utilities.getWordStart(textComponent, textComponent.getCaretPosition());
                    if (this.select) {
                        textComponent.moveCaretPosition(wordStart);
                    }
                    else {
                        textComponent.setCaretPosition(wordStart);
                    }
                }
                catch (final BadLocationException ex) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    static class EndWordAction extends TextAction
    {
        private boolean select;
        
        EndWordAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                try {
                    final int wordEnd = Utilities.getWordEnd(textComponent, textComponent.getCaretPosition());
                    if (this.select) {
                        textComponent.moveCaretPosition(wordEnd);
                    }
                    else {
                        textComponent.setCaretPosition(wordEnd);
                    }
                }
                catch (final BadLocationException ex) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    static class PreviousWordAction extends TextAction
    {
        private boolean select;
        
        PreviousWordAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                int caretPosition = textComponent.getCaretPosition();
                boolean b = false;
                try {
                    final Element paragraphElement = Utilities.getParagraphElement(textComponent, caretPosition);
                    caretPosition = Utilities.getPreviousWord(textComponent, caretPosition);
                    if (caretPosition < paragraphElement.getStartOffset()) {
                        caretPosition = Utilities.getParagraphElement(textComponent, caretPosition).getEndOffset() - 1;
                    }
                }
                catch (final BadLocationException ex) {
                    if (caretPosition != 0) {
                        caretPosition = 0;
                    }
                    else {
                        b = true;
                    }
                }
                if (!b) {
                    if (this.select) {
                        textComponent.moveCaretPosition(caretPosition);
                    }
                    else {
                        textComponent.setCaretPosition(caretPosition);
                    }
                }
                else {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    static class NextWordAction extends TextAction
    {
        private boolean select;
        
        NextWordAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                int caretPosition = textComponent.getCaretPosition();
                boolean b = false;
                final int n = caretPosition;
                final Element paragraphElement = Utilities.getParagraphElement(textComponent, caretPosition);
                try {
                    caretPosition = Utilities.getNextWord(textComponent, caretPosition);
                    if (caretPosition >= paragraphElement.getEndOffset() && n != paragraphElement.getEndOffset() - 1) {
                        caretPosition = paragraphElement.getEndOffset() - 1;
                    }
                }
                catch (final BadLocationException ex) {
                    final int length = textComponent.getDocument().getLength();
                    if (caretPosition != length) {
                        if (n != paragraphElement.getEndOffset() - 1) {
                            caretPosition = paragraphElement.getEndOffset() - 1;
                        }
                        else {
                            caretPosition = length;
                        }
                    }
                    else {
                        b = true;
                    }
                }
                if (!b) {
                    if (this.select) {
                        textComponent.moveCaretPosition(caretPosition);
                    }
                    else {
                        textComponent.setCaretPosition(caretPosition);
                    }
                }
                else {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    static class BeginLineAction extends TextAction
    {
        private boolean select;
        
        BeginLineAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                try {
                    final int rowStart = Utilities.getRowStart(textComponent, textComponent.getCaretPosition());
                    if (this.select) {
                        textComponent.moveCaretPosition(rowStart);
                    }
                    else {
                        textComponent.setCaretPosition(rowStart);
                    }
                }
                catch (final BadLocationException ex) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    static class EndLineAction extends TextAction
    {
        private boolean select;
        
        EndLineAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                try {
                    final int rowEnd = Utilities.getRowEnd(textComponent, textComponent.getCaretPosition());
                    if (this.select) {
                        textComponent.moveCaretPosition(rowEnd);
                    }
                    else {
                        textComponent.setCaretPosition(rowEnd);
                    }
                }
                catch (final BadLocationException ex) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                }
            }
        }
    }
    
    static class BeginParagraphAction extends TextAction
    {
        private boolean select;
        
        BeginParagraphAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final int startOffset = Utilities.getParagraphElement(textComponent, textComponent.getCaretPosition()).getStartOffset();
                if (this.select) {
                    textComponent.moveCaretPosition(startOffset);
                }
                else {
                    textComponent.setCaretPosition(startOffset);
                }
            }
        }
    }
    
    static class EndParagraphAction extends TextAction
    {
        private boolean select;
        
        EndParagraphAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final int min = Math.min(textComponent.getDocument().getLength(), Utilities.getParagraphElement(textComponent, textComponent.getCaretPosition()).getEndOffset());
                if (this.select) {
                    textComponent.moveCaretPosition(min);
                }
                else {
                    textComponent.setCaretPosition(min);
                }
            }
        }
    }
    
    static class BeginAction extends TextAction
    {
        private boolean select;
        
        BeginAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                if (this.select) {
                    textComponent.moveCaretPosition(0);
                }
                else {
                    textComponent.setCaretPosition(0);
                }
            }
        }
    }
    
    static class EndAction extends TextAction
    {
        private boolean select;
        
        EndAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final int length = textComponent.getDocument().getLength();
                if (this.select) {
                    textComponent.moveCaretPosition(length);
                }
                else {
                    textComponent.setCaretPosition(length);
                }
            }
        }
    }
    
    static class SelectWordAction extends TextAction
    {
        private Action start;
        private Action end;
        
        SelectWordAction() {
            super("select-word");
            this.start = new BeginWordAction("pigdog", false);
            this.end = new EndWordAction("pigdog", true);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.start.actionPerformed(actionEvent);
            this.end.actionPerformed(actionEvent);
        }
    }
    
    static class SelectLineAction extends TextAction
    {
        private Action start;
        private Action end;
        
        SelectLineAction() {
            super("select-line");
            this.start = new BeginLineAction("pigdog", false);
            this.end = new EndLineAction("pigdog", true);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.start.actionPerformed(actionEvent);
            this.end.actionPerformed(actionEvent);
        }
    }
    
    static class SelectParagraphAction extends TextAction
    {
        private Action start;
        private Action end;
        
        SelectParagraphAction() {
            super("select-paragraph");
            this.start = new BeginParagraphAction("pigdog", false);
            this.end = new EndParagraphAction("pigdog", true);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.start.actionPerformed(actionEvent);
            this.end.actionPerformed(actionEvent);
        }
    }
    
    static class SelectAllAction extends TextAction
    {
        SelectAllAction() {
            super("select-all");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                final Document document = textComponent.getDocument();
                textComponent.setCaretPosition(0);
                textComponent.moveCaretPosition(document.getLength());
            }
        }
    }
    
    static class UnselectAction extends TextAction
    {
        UnselectAction() {
            super("unselect");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                textComponent.setCaretPosition(textComponent.getCaretPosition());
            }
        }
    }
    
    static class ToggleComponentOrientationAction extends TextAction
    {
        ToggleComponentOrientationAction() {
            super("toggle-componentOrientation");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent != null) {
                ComponentOrientation componentOrientation;
                if (textComponent.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) {
                    componentOrientation = ComponentOrientation.LEFT_TO_RIGHT;
                }
                else {
                    componentOrientation = ComponentOrientation.RIGHT_TO_LEFT;
                }
                textComponent.setComponentOrientation(componentOrientation);
                textComponent.repaint();
            }
        }
    }
}
