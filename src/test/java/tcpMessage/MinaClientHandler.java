package tcpMessage;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocols.Protocols;
import tcpMessage.controllers.ClientPanelController;

import java.util.ArrayList;

/**
 * @author behindjava.com
 */
public class MinaClientHandler extends IoHandlerAdapter {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private ClientPanelController clientPanelController;

    public void setClientPanelController(ClientPanelController clientPanelController) {
        this.clientPanelController = clientPanelController;
    }

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("MinaClientHandler.sessionOpened");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws InterruptedException {

        System.out.println("MinaClientHandler.messageReceived, class=" + message.getClass());
        if (message instanceof Protocols.GetUsersInRoomResponse) {
            this.clientPanelController.handleGetUsersInRoomResponse((Protocols.GetUsersInRoomResponse) message);
        } else if (message instanceof Protocols.GetRoomsResponse) {
            Protocols.GetRoomsResponse response = (Protocols.GetRoomsResponse) message;
            this.clientPanelController.setNewRoomList(response.getRoomsList());
        } else if (message instanceof Protocols.LeaveServerResponse) {
            this.clientPanelController.handle_leave_server_response((Protocols.LeaveServerResponse) message);
        } else if (message instanceof Protocols.LoginToServerResponse) {
            this.clientPanelController.handle_login_to_server_response((Protocols.LoginToServerResponse) message);
        } else if (message instanceof Protocols.ManageRoomResponse) {
            this.clientPanelController.handle_manage_room_response((Protocols.ManageRoomResponse) message);
        } else {
            System.out.println("Unknown instance of response from server, message: " + message.toString());
        }

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        System.out.println("MinaClientHandler.exceptionCaught");
        cause.printStackTrace();
        session.close();
    }

}