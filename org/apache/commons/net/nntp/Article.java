package org.apache.commons.net.nntp;

import java.io.PrintStream;
import java.util.ArrayList;

public class Article implements Threadable
{
    private long articleNumber;
    private String subject;
    private String date;
    private String articleId;
    private String simplifiedSubject;
    private String from;
    private ArrayList<String> references;
    private boolean isReply;
    public Article kid;
    public Article next;
    
    public Article() {
        this.isReply = false;
        this.articleNumber = -1L;
    }
    
    public void addReference(final String msgId) {
        if (msgId == null || msgId.length() == 0) {
            return;
        }
        if (this.references == null) {
            this.references = new ArrayList<String>();
        }
        this.isReply = true;
        for (final String s : msgId.split(" ")) {
            this.references.add(s);
        }
    }
    
    public String[] getReferences() {
        if (this.references == null) {
            return new String[0];
        }
        return this.references.toArray(new String[this.references.size()]);
    }
    
    private void simplifySubject() {
        int start = 0;
        final String subject = this.getSubject();
        final int len = subject.length();
        boolean done = false;
        while (!done) {
            done = true;
            while (start < len && subject.charAt(start) == ' ') {
                ++start;
            }
            if (start < len - 2 && (subject.charAt(start) == 'r' || subject.charAt(start) == 'R') && (subject.charAt(start + 1) == 'e' || subject.charAt(start + 1) == 'E')) {
                if (subject.charAt(start + 2) == ':') {
                    start += 3;
                    done = false;
                }
                else if (start < len - 2 && (subject.charAt(start + 2) == '[' || subject.charAt(start + 2) == '(')) {
                    int i;
                    for (i = start + 3; i < len && subject.charAt(i) >= '0' && subject.charAt(i) <= '9'; ++i) {}
                    if (i < len - 1 && (subject.charAt(i) == ']' || subject.charAt(i) == ')') && subject.charAt(i + 1) == ':') {
                        start = i + 2;
                        done = false;
                    }
                }
            }
            if ("(no subject)".equals(this.simplifiedSubject)) {
                this.simplifiedSubject = "";
            }
            int end;
            for (end = len; end > start && subject.charAt(end - 1) < ' '; --end) {}
            if (start == 0 && end == len) {
                this.simplifiedSubject = subject;
            }
            else {
                this.simplifiedSubject = subject.substring(start, end);
            }
        }
    }
    
    public static void printThread(final Article article) {
        printThread(article, 0, System.out);
    }
    
    public static void printThread(final Article article, final PrintStream ps) {
        printThread(article, 0, ps);
    }
    
    public static void printThread(final Article article, final int depth) {
        printThread(article, depth, System.out);
    }
    
    public static void printThread(final Article article, final int depth, final PrintStream ps) {
        for (int i = 0; i < depth; ++i) {
            ps.print("==>");
        }
        ps.println(article.getSubject() + "\t" + article.getFrom() + "\t" + article.getArticleId());
        if (article.kid != null) {
            printThread(article.kid, depth + 1);
        }
        if (article.next != null) {
            printThread(article.next, depth);
        }
    }
    
    public String getArticleId() {
        return this.articleId;
    }
    
    public long getArticleNumberLong() {
        return this.articleNumber;
    }
    
    public String getDate() {
        return this.date;
    }
    
    public String getFrom() {
        return this.from;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public void setArticleId(final String string) {
        this.articleId = string;
    }
    
    public void setArticleNumber(final long l) {
        this.articleNumber = l;
    }
    
    public void setDate(final String string) {
        this.date = string;
    }
    
    public void setFrom(final String string) {
        this.from = string;
    }
    
    public void setSubject(final String string) {
        this.subject = string;
    }
    
    @Override
    public boolean isDummy() {
        return this.articleNumber == -1L;
    }
    
    @Override
    public String messageThreadId() {
        return this.articleId;
    }
    
    @Override
    public String[] messageThreadReferences() {
        return this.getReferences();
    }
    
    @Override
    public String simplifiedSubject() {
        if (this.simplifiedSubject == null) {
            this.simplifySubject();
        }
        return this.simplifiedSubject;
    }
    
    @Override
    public boolean subjectIsReply() {
        return this.isReply;
    }
    
    @Override
    public void setChild(final Threadable child) {
        this.kid = (Article)child;
        this.flushSubjectCache();
    }
    
    private void flushSubjectCache() {
        this.simplifiedSubject = null;
    }
    
    @Override
    public void setNext(final Threadable next) {
        this.next = (Article)next;
        this.flushSubjectCache();
    }
    
    @Override
    public Threadable makeDummy() {
        return new Article();
    }
    
    @Override
    public String toString() {
        return this.articleNumber + " " + this.articleId + " " + this.subject;
    }
    
    @Deprecated
    public int getArticleNumber() {
        return (int)this.articleNumber;
    }
    
    @Deprecated
    public void setArticleNumber(final int a) {
        this.articleNumber = a;
    }
    
    @Deprecated
    public void addHeaderField(final String name, final String val) {
    }
}
