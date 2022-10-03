package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.poi.util.Units;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.Date;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;
import org.apache.poi.util.LocaleUtil;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.apache.poi.sl.usermodel.Comment;

public class XSLFComment implements Comment
{
    final CTComment comment;
    final XSLFCommentAuthors authors;
    
    XSLFComment(final CTComment comment, final XSLFCommentAuthors authors) {
        this.comment = comment;
        this.authors = authors;
    }
    
    public String getAuthor() {
        return this.authors.getAuthorById(this.comment.getAuthorId()).getName();
    }
    
    public void setAuthor(final String author) {
        if (author == null) {
            throw new IllegalArgumentException("author must not be null");
        }
        final CTCommentAuthorList list = this.authors.getCTCommentAuthorsList();
        long maxId = -1L;
        for (final CTCommentAuthor aut : list.getCmAuthorArray()) {
            maxId = Math.max(aut.getId(), maxId);
            if (author.equals(aut.getName())) {
                this.comment.setAuthorId(aut.getId());
                return;
            }
        }
        final CTCommentAuthor newAuthor = list.addNewCmAuthor();
        newAuthor.setName(author);
        newAuthor.setId(maxId + 1L);
        newAuthor.setInitials(author.replaceAll("\\s*(\\w)\\S*", "$1").toUpperCase(LocaleUtil.getUserLocale()));
        this.comment.setAuthorId(maxId + 1L);
    }
    
    public String getAuthorInitials() {
        final CTCommentAuthor aut = this.authors.getAuthorById(this.comment.getAuthorId());
        return (aut == null) ? null : aut.getInitials();
    }
    
    public void setAuthorInitials(final String initials) {
        final CTCommentAuthor aut = this.authors.getAuthorById(this.comment.getAuthorId());
        if (aut != null) {
            aut.setInitials(initials);
        }
    }
    
    public String getText() {
        return this.comment.getText();
    }
    
    public void setText(final String text) {
        this.comment.setText(text);
    }
    
    public Date getDate() {
        final Calendar cal = this.comment.getDt();
        return (cal == null) ? null : cal.getTime();
    }
    
    public void setDate(final Date date) {
        final Calendar cal = LocaleUtil.getLocaleCalendar();
        cal.setTime(date);
        this.comment.setDt(cal);
    }
    
    public Point2D getOffset() {
        final CTPoint2D pos = this.comment.getPos();
        return new Point2D.Double(Units.toPoints(pos.getX()), Units.toPoints(pos.getY()));
    }
    
    public void setOffset(final Point2D offset) {
        CTPoint2D pos = this.comment.getPos();
        if (pos == null) {
            pos = this.comment.addNewPos();
        }
        pos.setX((long)Units.toEMU(offset.getX()));
        pos.setY((long)Units.toEMU(offset.getY()));
    }
}
