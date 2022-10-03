package org.apache.commons.net.nntp;

import java.util.Arrays;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;

public class Threader
{
    public Threadable thread(final List<? extends Threadable> messages) {
        return this.thread((Iterable<? extends Threadable>)messages);
    }
    
    public Threadable thread(final Iterable<? extends Threadable> messages) {
        if (messages == null) {
            return null;
        }
        HashMap<String, ThreadContainer> idTable = new HashMap<String, ThreadContainer>();
        for (final Threadable t : messages) {
            if (!t.isDummy()) {
                this.buildContainer(t, idTable);
            }
        }
        if (idTable.isEmpty()) {
            return null;
        }
        final ThreadContainer root = this.findRootSet(idTable);
        idTable.clear();
        idTable = null;
        this.pruneEmptyContainers(root);
        root.reverseChildren();
        this.gatherSubjects(root);
        if (root.next != null) {
            throw new RuntimeException("root node has a next:" + root);
        }
        for (ThreadContainer r = root.child; r != null; r = r.next) {
            if (r.threadable == null) {
                r.threadable = r.child.threadable.makeDummy();
            }
        }
        final Threadable result = (root.child == null) ? null : root.child.threadable;
        root.flush();
        return result;
    }
    
    private void buildContainer(final Threadable threadable, final HashMap<String, ThreadContainer> idTable) {
        String id = threadable.messageThreadId();
        ThreadContainer container = idTable.get(id);
        int bogusIdCount = 0;
        if (container != null) {
            if (container.threadable != null) {
                ++bogusIdCount;
                id = "<Bogus-id:" + bogusIdCount + ">";
                container = null;
            }
            else {
                container.threadable = threadable;
            }
        }
        if (container == null) {
            container = new ThreadContainer();
            container.threadable = threadable;
            idTable.put(id, container);
        }
        ThreadContainer parentRef = null;
        final String[] arr$;
        final String[] references = arr$ = threadable.messageThreadReferences();
        for (final String refString : arr$) {
            ThreadContainer ref = idTable.get(refString);
            if (ref == null) {
                ref = new ThreadContainer();
                idTable.put(refString, ref);
            }
            if (parentRef != null && ref.parent == null && parentRef != ref && !ref.findChild(parentRef)) {
                ref.parent = parentRef;
                ref.next = parentRef.child;
                parentRef.child = ref;
            }
            parentRef = ref;
        }
        if (parentRef != null && (parentRef == container || container.findChild(parentRef))) {
            parentRef = null;
        }
        if (container.parent != null) {
            ThreadContainer prev = null;
            ThreadContainer rest;
            for (rest = container.parent.child; rest != null && rest != container; rest = rest.next) {
                prev = rest;
            }
            if (rest == null) {
                throw new RuntimeException("Didnt find " + container + " in parent" + container.parent);
            }
            if (prev == null) {
                container.parent.child = container.next;
            }
            else {
                prev.next = container.next;
            }
            container.next = null;
            container.parent = null;
        }
        if (parentRef != null) {
            container.parent = parentRef;
            container.next = parentRef.child;
            parentRef.child = container;
        }
    }
    
    private ThreadContainer findRootSet(final HashMap<String, ThreadContainer> idTable) {
        final ThreadContainer root = new ThreadContainer();
        for (final Map.Entry<String, ThreadContainer> entry : idTable.entrySet()) {
            final ThreadContainer c = entry.getValue();
            if (c.parent == null) {
                if (c.next != null) {
                    throw new RuntimeException("c.next is " + c.next.toString());
                }
                c.next = root.child;
                root.child = c;
            }
        }
        return root;
    }
    
    private void pruneEmptyContainers(final ThreadContainer parent) {
        ThreadContainer prev = null;
        for (ThreadContainer container = parent.child, next = container.next; container != null; container = next, next = ((container == null) ? null : container.next)) {
            if (container.threadable == null && container.child == null) {
                if (prev == null) {
                    parent.child = container.next;
                }
                else {
                    prev.next = container.next;
                }
                container = prev;
            }
            else if (container.threadable == null && container.child != null && (container.parent != null || container.child.next == null)) {
                final ThreadContainer kids = container.child;
                if (prev == null) {
                    parent.child = kids;
                }
                else {
                    prev.next = kids;
                }
                ThreadContainer tail;
                for (tail = kids; tail.next != null; tail = tail.next) {
                    tail.parent = container.parent;
                }
                tail.parent = container.parent;
                tail.next = container.next;
                next = kids;
                container = prev;
            }
            else if (container.child != null) {
                this.pruneEmptyContainers(container);
            }
            prev = container;
        }
    }
    
    private void gatherSubjects(final ThreadContainer root) {
        int count = 0;
        for (ThreadContainer c = root.child; c != null; c = c.next) {
            ++count;
        }
        HashMap<String, ThreadContainer> subjectTable = new HashMap<String, ThreadContainer>((int)(count * 1.2), 0.9f);
        count = 0;
        for (ThreadContainer c2 = root.child; c2 != null; c2 = c2.next) {
            Threadable threadable = c2.threadable;
            if (threadable == null) {
                threadable = c2.child.threadable;
            }
            final String subj = threadable.simplifiedSubject();
            if (subj != null) {
                if (subj.length() != 0) {
                    final ThreadContainer old = subjectTable.get(subj);
                    if (old == null || (c2.threadable == null && old.threadable != null) || (old.threadable != null && old.threadable.subjectIsReply() && c2.threadable != null && !c2.threadable.subjectIsReply())) {
                        subjectTable.put(subj, c2);
                        ++count;
                    }
                }
            }
        }
        if (count == 0) {
            return;
        }
        ThreadContainer prev = null;
        for (ThreadContainer c3 = root.child, rest = c3.next; c3 != null; c3 = rest, rest = ((rest == null) ? null : rest.next)) {
            Threadable threadable2 = c3.threadable;
            if (threadable2 == null) {
                threadable2 = c3.child.threadable;
            }
            final String subj2 = threadable2.simplifiedSubject();
            if (subj2 != null) {
                if (subj2.length() != 0) {
                    final ThreadContainer old2 = subjectTable.get(subj2);
                    if (old2 != c3) {
                        if (prev == null) {
                            root.child = c3.next;
                        }
                        else {
                            prev.next = c3.next;
                        }
                        c3.next = null;
                        if (old2.threadable == null && c3.threadable == null) {
                            ThreadContainer tail;
                            for (tail = old2.child; tail != null && tail.next != null; tail = tail.next) {}
                            if (tail != null) {
                                tail.next = c3.child;
                            }
                            for (tail = c3.child; tail != null; tail = tail.next) {
                                tail.parent = old2;
                            }
                            c3.child = null;
                        }
                        else if (old2.threadable == null || (c3.threadable != null && c3.threadable.subjectIsReply() && !old2.threadable.subjectIsReply())) {
                            c3.parent = old2;
                            c3.next = old2.child;
                            old2.child = c3;
                        }
                        else {
                            final ThreadContainer newc = new ThreadContainer();
                            newc.threadable = old2.threadable;
                            newc.child = old2.child;
                            for (ThreadContainer tail2 = newc.child; tail2 != null; tail2 = tail2.next) {
                                tail2.parent = newc;
                            }
                            old2.threadable = null;
                            old2.child = null;
                            c3.parent = old2;
                            newc.parent = old2;
                            old2.child = c3;
                            c3.next = newc;
                        }
                        c3 = prev;
                    }
                }
            }
            prev = c3;
        }
        subjectTable.clear();
        subjectTable = null;
    }
    
    @Deprecated
    public Threadable thread(final Threadable[] messages) {
        if (messages == null) {
            return null;
        }
        return this.thread(Arrays.asList(messages));
    }
}
