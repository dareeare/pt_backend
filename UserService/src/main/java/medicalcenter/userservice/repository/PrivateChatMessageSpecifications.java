package medicalcenter.userservice.repository;

import jakarta.persistence.criteria.Predicate;
import medicalcenter.userservice.model.entity.PrivateChatMessage;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrivateChatMessageSpecifications {
    
    public static Specification<PrivateChatMessage> searchPrivateMessages(
            String chatRoomId,
            String senderName,
            String recipient,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (chatRoomId != null && !chatRoomId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("chatRoomId"), chatRoomId));
            }
            
            if (senderName != null && !senderName.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("senderName"), senderName));
            }
            
            if (recipient != null && !recipient.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("recipient"), recipient));
            }
            
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), fromDate));
            }
            
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), toDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}