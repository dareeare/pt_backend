package medicalcenter.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.logging.ClientLogDto;
import medicalcenter.userservice.service.ClientLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Client Logs", description = "Сбор логов клиентского приложения")
public class ClientLogController {

    private final ClientLogService clientLogService;

    @Operation(summary = "Получить лог с клиента")
    @PostMapping
    public ResponseEntity<Void> ingest(@Valid @RequestBody ClientLogDto clientLogDto,
                                       HttpServletRequest request) {
        clientLogService.persist(clientLogDto, request.getRemoteAddr());
        return ResponseEntity.accepted().build();
    }
}


