package remembrall.model.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import remembrall.model.Invitation;
import remembrall.model.User;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByReceiverAndAcknowledgedAndDenied(User receiver, boolean ack, boolean denied);

    @EntityGraph(value = "Invitation.groceryListAndUsers")
    Optional<Invitation> findByIdAndReceiverAndAcknowledgedAndDenied(Long id, User user, boolean ack, boolean denied);
}
