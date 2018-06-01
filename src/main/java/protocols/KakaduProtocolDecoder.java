package protocols;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class KakaduProtocolDecoder implements ProtocolDecoder {
    public void decode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        int messageCode = ioBuffer.getInt();
        int protocolBytesSize = ioBuffer.getInt();
        byte[] messageBytes = new byte[protocolBytesSize];
        ioBuffer.get(messageBytes);

        Object decodedMessage;
        switch (messageCode){
            case 1:
                decodedMessage = Protocols.LoginToServerRequest.parseFrom(messageBytes);
                break;
            case 2:
                decodedMessage = Protocols.LoginToServerResponse.parseFrom(messageBytes);
                break;
            case 3:
                decodedMessage = Protocols.LeaveServerRequest.parseFrom(messageBytes);
                break;
            case 4:
                decodedMessage = Protocols.LeaveServerResponse.parseFrom(messageBytes);
                break;
            case 5:
                decodedMessage = Protocols.ManageRoomRequest.parseFrom(messageBytes);
                break;
            case 6:
                decodedMessage = Protocols.ManageRoomResponse.parseFrom(messageBytes);
                break;
            case 7:
                decodedMessage = Protocols.GetRoomsRequest.parseFrom(messageBytes);
                break;
            case 8:
                decodedMessage = Protocols.GetRoomsResponse.parseFrom(messageBytes);
                break;
            case 9:
                decodedMessage = Protocols.GetUsersInRoomRequest.parseFrom(messageBytes);
                break;
            case 10:
                decodedMessage = Protocols.GetUsersInRoomResponse.parseFrom(messageBytes);
                break;
            default:
                throw  new Exception("Unrecognized message: "+messageCode);
        }
        protocolDecoderOutput.write(decodedMessage);
    }

    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

    }

    public void dispose(IoSession ioSession) throws Exception {

    }
}
