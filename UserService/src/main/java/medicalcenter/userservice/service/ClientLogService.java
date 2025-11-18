package medicalcenter.userservice.service;

import medicalcenter.userservice.model.dto.logging.ClientLogDto;

public interface ClientLogService {
    void persist(ClientLogDto dto, String sourceIp);
}

