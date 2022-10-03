package org.apache.lucene.index;

import java.io.IOException;

public final class TwoPhaseCommitTool
{
    private TwoPhaseCommitTool() {
    }
    
    private static void rollback(final TwoPhaseCommit... objects) {
        for (final TwoPhaseCommit tpc : objects) {
            if (tpc != null) {
                try {
                    tpc.rollback();
                }
                catch (final Throwable t) {}
            }
        }
    }
    
    public static void execute(final TwoPhaseCommit... objects) throws PrepareCommitFailException, CommitFailException {
        TwoPhaseCommit tpc = null;
        try {
            for (int i = 0; i < objects.length; ++i) {
                tpc = objects[i];
                if (tpc != null) {
                    tpc.prepareCommit();
                }
            }
        }
        catch (final Throwable t) {
            rollback(objects);
            throw new PrepareCommitFailException(t, tpc);
        }
        try {
            for (int i = 0; i < objects.length; ++i) {
                tpc = objects[i];
                if (tpc != null) {
                    tpc.commit();
                }
            }
        }
        catch (final Throwable t) {
            rollback(objects);
            throw new CommitFailException(t, tpc);
        }
    }
    
    public static class PrepareCommitFailException extends IOException
    {
        public PrepareCommitFailException(final Throwable cause, final TwoPhaseCommit obj) {
            super("prepareCommit() failed on " + obj, cause);
        }
    }
    
    public static class CommitFailException extends IOException
    {
        public CommitFailException(final Throwable cause, final TwoPhaseCommit obj) {
            super("commit() failed on " + obj, cause);
        }
    }
}
