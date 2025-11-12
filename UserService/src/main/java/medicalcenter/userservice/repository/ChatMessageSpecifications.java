package medicalcenter.userservice.repository;

import jakarta.persistence.criteria.Predicate;
import medicalcenter.userservice.model.MessageType;
import medicalcenter.userservice.model.entity.ChatMessage;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatMessageSpecifications {
    
    public static Specification<ChatMessage> searchMessages(
            String query,
            UUID senderId,
            MessageType type,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("content")),
                    "%" + query.toLowerCase() + "%"
                ));
            }
            
            if (senderId != null) {
                predicates.add(criteriaBuilder.equal(root.get("senderId"), senderId));
            }
            
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
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

