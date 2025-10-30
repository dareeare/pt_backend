package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.message.ChatMessageDto;
import medicalcenter.userservice.service.ChatService;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatService chatService;

    public void onMessage(ChatMessageDto message, Principal principal) {
        chatService.handleIncomingMessage(message, principal);
    }
}
