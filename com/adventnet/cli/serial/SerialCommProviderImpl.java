package com.adventnet.cli.serial;

import java.io.IOException;
import com.adventnet.cli.CLIMessage;
import com.adventnet.cli.util.CLILogMgr;
import com.adventnet.cli.transport.CLIProtocolOptions;
import com.adventnet.cli.transport.CLITransportProvider;

public class SerialCommProviderImpl implements CLITransportProvider
{
    SerialCommSession session;
    
    public SerialCommProviderImpl() {
        this.session = null;
    }
    
    public void open(final CLIProtocolOptions cliProtocolOptions) throws Exception {
        final SerialCommOptionsImpl serialCommOptionsImpl = (SerialCommOptionsImpl)cliProtocolOptions;
        (this.session = new SerialCommSession()).open(serialCommOptionsImpl.getPortId());
        this.session.setSerialCommParameters(serialCommOptionsImpl.getBaudRate(), serialCommOptionsImpl.getDataBits(), serialCommOptionsImpl.getStopBits(), serialCommOptionsImpl.getParity());
        this.session.setFlowControlMode(serialCommOptionsImpl.getFlowControlMode());
        CLILogMgr.setDebugMessage("CLIUSER", "SerialCommOptionsImpl: session opened successfully", 4, null);
        this.session.setReadTimeout(100);
    }
    
    public void close() throws Exception {
        this.session.close();
        CLILogMgr.setDebugMessage("CLIUSER", "SerialCommProviderImpl: closing session", 4, null);
    }
    
    public void write(final CLIMessage cliMessage) throws IOException {
        CLILogMgr.setDebugMessage("CLIUSER", "SerialCommProviderImpl: sending message " + cliMessage.getData().length(), 4, null);
        this.session.write(cliMessage.getData().getBytes());
    }
    
    public CLIMessage read() throws IOException {
        CLIMessage cliMessage = null;
        final byte[] read = this.session.read();
        if (read != null) {
            cliMessage = new CLIMessage(new String(read));
            CLILogMgr.setDebugMessage("CLIUSER", "SerialCommProviderImpl: received message " + cliMessage.getData().length(), 4, null);
            CLILogMgr.setDebugMessage("CLIUSER", "SerialCommProviderImpl: received message " + cliMessage.getData(), 4, null);
        }
        return cliMessage;
    }
}
