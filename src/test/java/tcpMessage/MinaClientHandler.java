package tcpMessage;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocols.Protocols;

/**
 * @author behindjava.com
 */
public class MinaClientHandler extends IoHandlerAdapter
{
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    @Override
    public void sessionOpened(IoSession session)
    {
        System.out.println("MinaClientHandler.sessionOpened");
    }

    @Override
    public void messageReceived(IoSession session, Object message)
    {
        System.out.println("MinaClientHandler.messageReceived, class=" + message.getClass());
        if(message instanceof Protocols.GetUsersInRoomResponse){
            Protocols.GetUsersInRoomResponse newListOfUsersInRoom = (Protocols.GetUsersInRoomResponse)message;
            System.out.println("New users list!!! size="+newListOfUsersInRoom.getUsersList().size());
            for(String userNick: newListOfUsersInRoom.getUsersList()){
                System.out.println(userNick);
            }
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
    {
        System.out.println("MinaClientHandler.exceptionCaught");
        session.close();
    }

}