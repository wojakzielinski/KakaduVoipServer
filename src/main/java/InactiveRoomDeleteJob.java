import handler.ServerData;
import model.ServerRoom;
import model.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import protocols.Protocols;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by Szymon on 21.08.2018.
 */
public class InactiveRoomDeleteJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Start deleting");
        Calendar twoDaysAgo = new GregorianCalendar();
        twoDaysAgo.setTime(new Date());
        twoDaysAgo.add(Calendar.DAY_OF_MONTH, -2);
        long twoDaysAgoTime = twoDaysAgo.getTime().getTime();
        //if no users inside and two days without update, delete room
        boolean anythingRemoved = ServerData.INSTANCE.serverRoomMap.entrySet().removeIf(entry -> entry.getValue().getUsersInRoom().size() == 0 && entry.getValue().getLastUpdateTime().getTime() < twoDaysAgoTime);
        if (anythingRemoved) {
            System.out.println("REMOVED SOMETHINF");
            sendNewRoomListToLoggerUsers();
        }

        for(Map.Entry<String, ServerRoom> serverRoomEntry : ServerData.INSTANCE.serverRoomMap.entrySet()){
            serverRoomEntry.getValue().setLastUpdateTime(twoDaysAgo.getTime());
        }
    }

    private void sendNewRoomListToLoggerUsers() {
        Protocols.GetRoomsResponse.Builder responseBuilder = Protocols.GetRoomsResponse.newBuilder();
        for (Map.Entry<String, ServerRoom> serverRoomEntry : ServerData.INSTANCE.serverRoomMap.entrySet()) {
            Protocols.Room.Builder room = Protocols.Room.newBuilder();
            room.setRoomName(serverRoomEntry.getValue().getName());
            room.setOwner(serverRoomEntry.getValue().getOwner().getNick());
            responseBuilder.addRooms(room);
        }
        responseBuilder.setStatus(Protocols.StatusCode.OK);
        Protocols.GetRoomsResponse response = responseBuilder.build();
        for (User user : ServerData.INSTANCE.loggedUsers) {
            user.getTcpSession().write(response);
        }
    }
}
