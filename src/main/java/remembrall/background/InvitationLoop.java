package remembrall.background;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import remembrall.model.repository.InvitationRepository;
import remembrall.service.PushService;

import java.util.concurrent.TimeUnit;

@Component
public class InvitationLoop {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private PushService pushService;

    @Scheduled(fixedDelayString = "PT1H")
    public void remindPendingInvitations() {
        Long oneDayAgo = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24);
        // TODO: filter on deny/ack or delete invitations on deny/ack
        invitationRepository.findByCreatedTimestampSmallerThanAndPushedReminder(oneDayAgo, false).forEach(invitation -> {
            pushService.remindInvitation(invitation);
            invitation.setPushedReminder(true);
            invitationRepository.save(invitation);
        });
    }
}
