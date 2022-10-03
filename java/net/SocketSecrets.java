package java.net;

import java.io.IOException;

class SocketSecrets
{
    private static <T> void setOption(final Object o, final SocketOption<T> socketOption, final T t) throws IOException {
        SocketImpl socketImpl;
        if (o instanceof Socket) {
            socketImpl = ((Socket)o).getImpl();
        }
        else {
            if (!(o instanceof ServerSocket)) {
                throw new IllegalArgumentException();
            }
            socketImpl = ((ServerSocket)o).getImpl();
        }
        socketImpl.setOption(socketOption, t);
    }
    
    private static <T> T getOption(final Object o, final SocketOption<T> socketOption) throws IOException {
        SocketImpl socketImpl;
        if (o instanceof Socket) {
            socketImpl = ((Socket)o).getImpl();
        }
        else {
            if (!(o instanceof ServerSocket)) {
                throw new IllegalArgumentException();
            }
            socketImpl = ((ServerSocket)o).getImpl();
        }
        return socketImpl.getOption(socketOption);
    }
    
    private static <T> void setOption(final DatagramSocket datagramSocket, final SocketOption<T> socketOption, final T t) throws IOException {
        datagramSocket.getImpl().setOption(socketOption, t);
    }
    
    private static <T> T getOption(final DatagramSocket datagramSocket, final SocketOption<T> socketOption) throws IOException {
        return datagramSocket.getImpl().getOption(socketOption);
    }
}
