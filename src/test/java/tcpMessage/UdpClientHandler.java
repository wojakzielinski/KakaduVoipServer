package tcpMessage;


import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protocols.Protocols;

/**
 * Created by Szymon on 22.07.2018.
 */
public class UdpClientHandler extends IoHandlerAdapter {
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if(message instanceof Protocols.UdpPacket){
            System.out.println("UdpClient packet arrived time = "+System.currentTimeMillis());
        }
    }
}
