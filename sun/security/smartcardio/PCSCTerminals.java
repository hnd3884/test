package sun.security.smartcardio;

import java.util.Iterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;
import javax.smartcardio.CardException;
import java.util.ArrayList;
import javax.smartcardio.CardTerminal;
import java.util.List;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.Map;
import javax.smartcardio.CardTerminals;

final class PCSCTerminals extends CardTerminals
{
    private static long contextId;
    private Map<String, ReaderState> stateMap;
    private static final Map<String, Reference<TerminalImpl>> terminals;
    
    static synchronized void initContext() throws PCSCException {
        if (PCSCTerminals.contextId == 0L) {
            PCSCTerminals.contextId = PCSC.SCardEstablishContext(0);
        }
    }
    
    private static synchronized TerminalImpl implGetTerminal(final String s) {
        final Reference reference = PCSCTerminals.terminals.get(s);
        final TerminalImpl terminalImpl = (reference != null) ? ((TerminalImpl)reference.get()) : null;
        if (terminalImpl != null) {
            return terminalImpl;
        }
        final TerminalImpl terminalImpl2 = new TerminalImpl(PCSCTerminals.contextId, s);
        PCSCTerminals.terminals.put(s, new WeakReference<TerminalImpl>(terminalImpl2));
        return terminalImpl2;
    }
    
    @Override
    public synchronized List<CardTerminal> list(State state) throws CardException {
        if (state == null) {
            throw new NullPointerException();
        }
        try {
            final String[] sCardListReaders = PCSC.SCardListReaders(PCSCTerminals.contextId);
            final ArrayList list = new ArrayList(sCardListReaders.length);
            if (this.stateMap == null) {
                if (state == State.CARD_INSERTION) {
                    state = State.CARD_PRESENT;
                }
                else if (state == State.CARD_REMOVAL) {
                    state = State.CARD_ABSENT;
                }
            }
            for (final String s : sCardListReaders) {
                final TerminalImpl implGetTerminal = implGetTerminal(s);
                switch (state) {
                    case ALL: {
                        list.add((Object)implGetTerminal);
                        break;
                    }
                    case CARD_PRESENT: {
                        if (implGetTerminal.isCardPresent()) {
                            list.add((Object)implGetTerminal);
                            break;
                        }
                        break;
                    }
                    case CARD_ABSENT: {
                        if (!implGetTerminal.isCardPresent()) {
                            list.add((Object)implGetTerminal);
                            break;
                        }
                        break;
                    }
                    case CARD_INSERTION: {
                        final ReaderState readerState = this.stateMap.get(s);
                        if (readerState != null && readerState.isInsertion()) {
                            list.add((Object)implGetTerminal);
                            break;
                        }
                        break;
                    }
                    case CARD_REMOVAL: {
                        final ReaderState readerState2 = this.stateMap.get(s);
                        if (readerState2 != null && readerState2.isRemoval()) {
                            list.add((Object)implGetTerminal);
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new CardException("Unknown state: " + state);
                    }
                }
            }
            return Collections.unmodifiableList((List<? extends CardTerminal>)list);
        }
        catch (final PCSCException ex) {
            throw new CardException("list() failed", ex);
        }
    }
    
    @Override
    public synchronized boolean waitForChange(long n) throws CardException {
        if (n < 0L) {
            throw new IllegalArgumentException("Timeout must not be negative: " + n);
        }
        if (this.stateMap == null) {
            this.stateMap = new HashMap<String, ReaderState>();
            this.waitForChange(0L);
        }
        if (n == 0L) {
            n = -1L;
        }
        try {
            final String[] sCardListReaders = PCSC.SCardListReaders(PCSCTerminals.contextId);
            final int length = sCardListReaders.length;
            if (length == 0) {
                throw new IllegalStateException("No terminals available");
            }
            final int[] array = new int[length];
            final ReaderState[] array2 = new ReaderState[length];
            for (int i = 0; i < sCardListReaders.length; ++i) {
                ReaderState readerState = this.stateMap.get(sCardListReaders[i]);
                if (readerState == null) {
                    readerState = new ReaderState();
                }
                array2[i] = readerState;
                array[i] = readerState.get();
            }
            final int[] sCardGetStatusChange = PCSC.SCardGetStatusChange(PCSCTerminals.contextId, n, array, sCardListReaders);
            this.stateMap.clear();
            for (int j = 0; j < length; ++j) {
                final ReaderState readerState2 = array2[j];
                readerState2.update(sCardGetStatusChange[j]);
                this.stateMap.put(sCardListReaders[j], readerState2);
            }
            return true;
        }
        catch (final PCSCException ex) {
            if (ex.code == -2146435062) {
                return false;
            }
            throw new CardException("waitForChange() failed", ex);
        }
    }
    
    static List<CardTerminal> waitForCards(final List<? extends CardTerminal> list, long n, final boolean b) throws CardException {
        long n2;
        if (n == 0L) {
            n = -1L;
            n2 = -1L;
        }
        else {
            n2 = 0L;
        }
        final String[] array = new String[list.size()];
        int n3 = 0;
        for (final CardTerminal cardTerminal : list) {
            if (!(cardTerminal instanceof TerminalImpl)) {
                throw new IllegalArgumentException("Invalid terminal type: " + ((TerminalImpl)cardTerminal).getClass().getName());
            }
            array[n3++] = ((TerminalImpl)cardTerminal).name;
        }
        int[] sCardGetStatusChange = new int[array.length];
        Arrays.fill(sCardGetStatusChange, 0);
        try {
            List<? extends CardTerminal> list2;
            do {
                sCardGetStatusChange = PCSC.SCardGetStatusChange(PCSCTerminals.contextId, n2, sCardGetStatusChange, array);
                n2 = n;
                list2 = null;
                for (int i = 0; i < array.length; ++i) {
                    if ((sCardGetStatusChange[i] & 0x20) != 0x0 == b) {
                        if (list2 == null) {
                            list2 = new ArrayList<CardTerminal>();
                        }
                        list2.add(implGetTerminal(array[i]));
                    }
                }
            } while (list2 == null);
            return (List<CardTerminal>)Collections.unmodifiableList((List<?>)list2);
        }
        catch (final PCSCException ex) {
            if (ex.code == -2146435062) {
                return Collections.emptyList();
            }
            throw new CardException("waitForCard() failed", ex);
        }
    }
    
    static {
        terminals = new HashMap<String, Reference<TerminalImpl>>();
    }
    
    private static class ReaderState
    {
        private int current;
        private int previous;
        
        ReaderState() {
            this.current = 0;
            this.previous = 0;
        }
        
        int get() {
            return this.current;
        }
        
        void update(final int current) {
            this.previous = this.current;
            this.current = current;
        }
        
        boolean isInsertion() {
            return !present(this.previous) && present(this.current);
        }
        
        boolean isRemoval() {
            return present(this.previous) && !present(this.current);
        }
        
        static boolean present(final int n) {
            return (n & 0x20) != 0x0;
        }
    }
}
