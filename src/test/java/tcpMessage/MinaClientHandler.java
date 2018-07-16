package tcpMessage;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocols.Protocols;
import tcpMessage.controllers.ClientPanelController;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.List;

import static tcpMessage.controllers.ClientPanelController.reloadTemp;

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
    public void messageReceived(IoSession session, Object message) throws InterruptedException
    {

        System.out.println("MinaClientHandler.messageReceived, class=" + message.getClass());
        if(message instanceof Protocols.GetUsersInRoomResponse){
            Protocols.GetUsersInRoomResponse newListOfUsersInRoom = (Protocols.GetUsersInRoomResponse)message;
            System.out.println("New users list!!! size="+newListOfUsersInRoom.getUsersList().size());

            ArrayList<String> sendUsers = new ArrayList<>();

            for(String userNick: newListOfUsersInRoom.getUsersList()){
                System.out.println(userNick);
                sendUsers.add(userNick);
            }

            ClientPanelController.reloadTemp(sendUsers);

        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
    {
        System.out.println("MinaClientHandler.exceptionCaught");
        session.close();
    }

}