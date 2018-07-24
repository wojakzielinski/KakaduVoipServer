package protocols.udp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import protocols.Protocols;

/**
 * Created by Szymon on 23.07.2018.
 */
public class KakaduVoiceEncoder implements ProtocolEncoder {
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        byte[] messageBytes;
        int messageCode;

        if (o instanceof Protocols.UdpPacket) {
            messageCode = 1;
            messageBytes = ((Protocols.UdpPacket) o).toByteArray();
        } else if (o instanceof Protocols.RegisterUdpSession) {
            messageCode = 2;
            messageBytes = ((Protocols.RegisterUdpSession) o).toByteArray();
        } else throw new Exception("Unrecognized protocol");

        IoBuffer ioBuffer = IoBuffer.allocate(8 + messageBytes.length);
        ioBuffer.putInt(messageCode);
        ioBuffer.putInt(messageBytes.length);
        ioBuffer.put(messageBytes);
        ioBuffer.flip();
        protocolEncoderOutput.write(ioBuffer);
    }

    public void dispose(IoSession ioSession) throws Exception {

    }
}
