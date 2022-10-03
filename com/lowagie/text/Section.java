package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public class Section extends ArrayList implements TextElementArray, LargeElement
{
    public static final int NUMBERSTYLE_DOTTED = 0;
    public static final int NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT = 1;
    private static final long serialVersionUID = 3324172577544748043L;
    protected Paragraph title;
    protected String bookmarkTitle;
    protected int numberDepth;
    protected int numberStyle;
    protected float indentationLeft;
    protected float indentationRight;
    protected float indentation;
    protected boolean bookmarkOpen;
    protected boolean triggerNewPage;
    protected int subsections;
    protected ArrayList numbers;
    protected boolean complete;
    protected boolean addedCompletely;
    protected boolean notAddedYet;
    
    protected Section() {
        this.numberStyle = 0;
        this.bookmarkOpen = true;
        this.triggerNewPage = false;
        this.subsections = 0;
        this.numbers = null;
        this.complete = true;
        this.addedCompletely = false;
        this.notAddedYet = true;
        this.title = new Paragraph();
        this.numberDepth = 1;
    }
    
    protected Section(final Paragraph title, final int numberDepth) {
        this.numberStyle = 0;
        this.bookmarkOpen = true;
        this.triggerNewPage = false;
        this.subsections = 0;
        this.numbers = null;
        this.complete = true;
        this.addedCompletely = false;
        this.notAddedYet = true;
        this.numberDepth = numberDepth;
        this.title = title;
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            for (final Element element : this) {
                listener.add(element);
            }
            return true;
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 13;
    }
    
    public boolean isChapter() {
        return this.type() == 16;
    }
    
    public boolean isSection() {
        return this.type() == 13;
    }
    
    @Override
    public ArrayList getChunks() {
        final ArrayList tmp = new ArrayList();
        final Iterator i = this.iterator();
        while (i.hasNext()) {
            tmp.addAll(i.next().getChunks());
        }
        return tmp;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return false;
    }
    
    @Override
    public void add(final int index, final Object o) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        try {
            final Element element = (Element)o;
            if (!element.isNestable()) {
                throw new ClassCastException(MessageLocalization.getComposedMessage("you.can.t.add.a.1.to.a.section", element.getClass().getName()));
            }
            super.add(index, element);
        }
        catch (final ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }
    
    @Override
    public boolean add(final Object o) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        try {
            final Element element = (Element)o;
            if (element.type() == 13) {
                final Section section = (Section)o;
                section.setNumbers(++this.subsections, this.numbers);
                return super.add(section);
            }
            if (o instanceof MarkedSection && ((MarkedObject)o).element.type() == 13) {
                final MarkedSection mo = (MarkedSection)o;
                final Section section2 = (Section)mo.element;
                section2.setNumbers(++this.subsections, this.numbers);
                return super.add(mo);
            }
            if (element.isNestable()) {
                return super.add(o);
            }
            throw new ClassCastException(MessageLocalization.getComposedMessage("you.can.t.add.a.1.to.a.section", element.getClass().getName()));
        }
        catch (final ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }
    
    @Override
    public boolean addAll(final Collection collection) {
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
        return true;
    }
    
    public Section addSection(final float indentation, final Paragraph title, final int numberDepth) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        final Section section = new Section(title, numberDepth);
        section.setIndentation(indentation);
        this.add(section);
        return section;
    }
    
    public Section addSection(final float indentation, final Paragraph title) {
        return this.addSection(indentation, title, this.numberDepth + 1);
    }
    
    public Section addSection(final Paragraph title, final int numberDepth) {
        return this.addSection(0.0f, title, numberDepth);
    }
    
    public MarkedSection addMarkedSection() {
        final MarkedSection section = new MarkedSection(new Section(null, this.numberDepth + 1));
        this.add(section);
        return section;
    }
    
    public Section addSection(final Paragraph title) {
        return this.addSection(0.0f, title, this.numberDepth + 1);
    }
    
    public Section addSection(final float indentation, final String title, final int numberDepth) {
        return this.addSection(indentation, new Paragraph(title), numberDepth);
    }
    
    public Section addSection(final String title, final int numberDepth) {
        return this.addSection(new Paragraph(title), numberDepth);
    }
    
    public Section addSection(final float indentation, final String title) {
        return this.addSection(indentation, new Paragraph(title));
    }
    
    public Section addSection(final String title) {
        return this.addSection(new Paragraph(title));
    }
    
    public void setTitle(final Paragraph title) {
        this.title = title;
    }
    
    public Paragraph getTitle() {
        return constructTitle(this.title, this.numbers, this.numberDepth, this.numberStyle);
    }
    
    public static Paragraph constructTitle(final Paragraph title, final ArrayList numbers, final int numberDepth, final int numberStyle) {
        if (title == null) {
            return null;
        }
        final int depth = Math.min(numbers.size(), numberDepth);
        if (depth < 1) {
            return title;
        }
        final StringBuffer buf = new StringBuffer(" ");
        for (int i = 0; i < depth; ++i) {
            buf.insert(0, ".");
            buf.insert(0, (int)numbers.get(i));
        }
        if (numberStyle == 1) {
            buf.deleteCharAt(buf.length() - 2);
        }
        final Paragraph result = new Paragraph(title);
        result.add(0, new Chunk(buf.toString(), title.getFont()));
        return result;
    }
    
    public void setNumberDepth(final int numberDepth) {
        this.numberDepth = numberDepth;
    }
    
    public int getNumberDepth() {
        return this.numberDepth;
    }
    
    public void setNumberStyle(final int numberStyle) {
        this.numberStyle = numberStyle;
    }
    
    public int getNumberStyle() {
        return this.numberStyle;
    }
    
    public void setIndentationLeft(final float indentation) {
        this.indentationLeft = indentation;
    }
    
    public float getIndentationLeft() {
        return this.indentationLeft;
    }
    
    public void setIndentationRight(final float indentation) {
        this.indentationRight = indentation;
    }
    
    public float getIndentationRight() {
        return this.indentationRight;
    }
    
    public void setIndentation(final float indentation) {
        this.indentation = indentation;
    }
    
    public float getIndentation() {
        return this.indentation;
    }
    
    public void setBookmarkOpen(final boolean bookmarkOpen) {
        this.bookmarkOpen = bookmarkOpen;
    }
    
    public boolean isBookmarkOpen() {
        return this.bookmarkOpen;
    }
    
    public void setTriggerNewPage(final boolean triggerNewPage) {
        this.triggerNewPage = triggerNewPage;
    }
    
    public boolean isTriggerNewPage() {
        return this.triggerNewPage && this.notAddedYet;
    }
    
    public void setBookmarkTitle(final String bookmarkTitle) {
        this.bookmarkTitle = bookmarkTitle;
    }
    
    public Paragraph getBookmarkTitle() {
        if (this.bookmarkTitle == null) {
            return this.getTitle();
        }
        return new Paragraph(this.bookmarkTitle);
    }
    
    public void setChapterNumber(final int number) {
        this.numbers.set(this.numbers.size() - 1, new Integer(number));
        for (final Object s : this) {
            if (s instanceof Section) {
                ((Section)s).setChapterNumber(number);
            }
        }
    }
    
    public int getDepth() {
        return this.numbers.size();
    }
    
    private void setNumbers(final int number, final ArrayList numbers) {
        (this.numbers = new ArrayList()).add(new Integer(number));
        this.numbers.addAll(numbers);
    }
    
    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }
    
    public void setNotAddedYet(final boolean notAddedYet) {
        this.notAddedYet = notAddedYet;
    }
    
    protected boolean isAddedCompletely() {
        return this.addedCompletely;
    }
    
    protected void setAddedCompletely(final boolean addedCompletely) {
        this.addedCompletely = addedCompletely;
    }
    
    @Override
    public void flushContent() {
        this.setNotAddedYet(false);
        this.title = null;
        final Iterator i = this.iterator();
        while (i.hasNext()) {
            final Element element = i.next();
            if (element instanceof Section) {
                final Section s = (Section)element;
                if (!s.isComplete() && this.size() == 1) {
                    s.flushContent();
                    return;
                }
                s.setAddedCompletely(true);
            }
            i.remove();
        }
    }
    
    @Override
    public boolean isComplete() {
        return this.complete;
    }
    
    @Override
    public void setComplete(final boolean complete) {
        this.complete = complete;
    }
    
    public void newPage() {
        this.add(Chunk.NEXTPAGE);
    }
}
