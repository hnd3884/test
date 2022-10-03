package org.apache.commons.net.telnet;

public class WindowSizeOptionHandler extends TelnetOptionHandler
{
    private int m_nWidth;
    private int m_nHeight;
    protected static final int WINDOW_SIZE = 31;
    
    public WindowSizeOptionHandler(final int nWidth, final int nHeight, final boolean initlocal, final boolean initremote, final boolean acceptlocal, final boolean acceptremote) {
        super(31, initlocal, initremote, acceptlocal, acceptremote);
        this.m_nWidth = 80;
        this.m_nHeight = 24;
        this.m_nWidth = nWidth;
        this.m_nHeight = nHeight;
    }
    
    public WindowSizeOptionHandler(final int nWidth, final int nHeight) {
        super(31, false, false, false, false);
        this.m_nWidth = 80;
        this.m_nHeight = 24;
        this.m_nWidth = nWidth;
        this.m_nHeight = nHeight;
    }
    
    @Override
    public int[] startSubnegotiationLocal() {
        final int nCompoundWindowSize = this.m_nWidth * 65536 + this.m_nHeight;
        int nResponseSize = 5;
        if (this.m_nWidth % 256 == 255) {
            ++nResponseSize;
        }
        if (this.m_nWidth / 256 == 255) {
            ++nResponseSize;
        }
        if (this.m_nHeight % 256 == 255) {
            ++nResponseSize;
        }
        if (this.m_nHeight / 256 == 255) {
            ++nResponseSize;
        }
        final int[] response = new int[nResponseSize];
        response[0] = 31;
        for (int nIndex = 1, nShift = 24; nIndex < nResponseSize; ++nIndex, nShift -= 8) {
            int nTurnedOnBits = 255;
            nTurnedOnBits <<= nShift;
            response[nIndex] = (nCompoundWindowSize & nTurnedOnBits) >>> nShift;
            if (response[nIndex] == 255) {
                ++nIndex;
                response[nIndex] = 255;
            }
        }
        return response;
    }
}
