package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.privatechat.PrivateSendMessageRequest;
import medicalcenter.userservice.model.dto.privatechat.PrivateTypingEvent;
import medicalcenter.userservice.service.PrivateChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PrivateChatWebSocketController {

    private final PrivateChatService privateChatService;

    @MessageMapping("/private/chat.send")
    @PreAuthorize("isAuthenticated()")
    @SendToUser("/queue/private/messages")
    public Object sendPrivateMessage(@Payload PrivateSendMessageRequest request, 
                                   Principal principal) {
        log.debug("Received private message from {} to {}", principal.getName(), request.recipient());
        try {
            return privateChatService.sendPrivateMessage(request, principal);
        } catch (Exception e) {
            log.error("Error sending private message", e);
            throw e;
        }
    }

    @MessageMapping("/private/chat.typing")
    @PreAuthorize("isAuthenticated()")
    public void handlePrivateTyping(@Payload PrivateTypingEvent event, Principal principal) {
        log.debug("Private typing event from {} in room {}: {}", principal.getName(), 
                 event.chatRoomId(), event.isTyping());
        privateChatService.handlePrivateTypingEvent(event);
    }

    @MessageMapping("/private/chat.join")
    @PreAuthorize("isAuthenticated()")
    public void joinPrivateChat(@Payload String chatRoomId, Principal principal) {
        log.debug("User {} joining private chat room: {}", principal.getName(), chatRoomId);
        privateChatService.joinPrivateChatRoom(principal.getName(), chatRoomId);
    }

    @MessageMapping("/private/chat.leave")
    @PreAuthorize("isAuthenticated()")
    public void leavePrivateChat(@Payload String chatRoomId, Principal principal) {
        log.debug("User {} leaving private chat room: {}", principal.getName(), chatRoomId);
        privateChatService.leavePrivateChatRoom(principal.getName(), chatRoomId);
    }

    @MessageMapping("/private/user.online")
    @PreAuthorize("isAuthenticated()")
    public void userOnline(Principal principal) {
        log.debug("User {} is online for private chat", principal.getName());
        privateChatService.registerOnline(principal.getName());
    }

    @MessageMapping("/private/user.offline")
    @PreAuthorize("isAuthenticated()")
    public void userOffline(Principal principal) {
        log.debug("User {} is offline from private chat", principal.getName());
        privateChatService.registerOffline(principal.getName());
    }
}