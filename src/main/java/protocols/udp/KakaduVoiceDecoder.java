package protocols.udp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import protocols.Protocols;

/**
 * Created by Szymon on 23.07.2018.
 */
public class KakaduVoiceDecoder implements ProtocolDecoder {
    public void decode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        int messageCode = ioBuffer.getInt();
        int protocolBytesSize = ioBuffer.getInt();
        byte[] messageBytes = new byte[protocolBytesSize];
        ioBuffer.get(messageBytes);

        Object decodedMessage;
        switch (messageCode) {
            case 1:
                decodedMessage = Protocols.UdpPacket.parseFrom(messageBytes);
                break;
            case 2:
                decodedMessage = Protocols.RegisterUdpSession.parseFrom(messageBytes);
                break;
            default:
                throw new Exception("Unrecognized message: " + messageCode);
        }
        protocolDecoderOutput.write(decodedMessage);
    }

    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

    }

    public void dispose(IoSession ioSession) throws Exception {

    }
}
