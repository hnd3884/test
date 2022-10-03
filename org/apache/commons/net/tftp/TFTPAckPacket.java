package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;

public final class TFTPAckPacket extends TFTPPacket
{
    int _blockNumber;
    
    public TFTPAckPacket(final InetAddress destination, final int port, final int blockNumber) {
        super(4, destination, port);
        this._blockNumber = blockNumber;
    }
    
    TFTPAckPacket(final DatagramPacket datagram) throws TFTPPacketException {
        super(4, datagram.getAddress(), datagram.getPort());
        final byte[] data = datagram.getData();
        if (this.getType() != data[1]) {
            throw new TFTPPacketException("TFTP operator code does not match type.");
        }
        this._blockNumber = ((data[2] & 0xFF) << 8 | (data[3] & 0xFF));
    }
    
    @Override
    DatagramPacket _newDatagram(final DatagramPacket datagram, final byte[] data) {
        data[0] = 0;
        data[1] = (byte)this._type;
        data[2] = (byte)((this._blockNumber & 0xFFFF) >> 8);
        data[3] = (byte)(this._blockNumber & 0xFF);
        datagram.setAddress(this._address);
        datagram.setPort(this._port);
        datagram.setData(data);
        datagram.setLength(4);
        return datagram;
    }
    
    @Override
    public DatagramPacket newDatagram() {
        final byte[] data = { 0, (byte)this._type, (byte)((this._blockNumber & 0xFFFF) >> 8), (byte)(this._blockNumber & 0xFF) };
        return new DatagramPacket(data, data.length, this._address, this._port);
    }
    
    public int getBlockNumber() {
        return this._blockNumber;
    }
    
    public void setBlockNumber(final int blockNumber) {
        this._blockNumber = blockNumber;
    }
    
    @Override
    public String toString() {
        return super.toString() + " ACK " + this._blockNumber;
    }
}
