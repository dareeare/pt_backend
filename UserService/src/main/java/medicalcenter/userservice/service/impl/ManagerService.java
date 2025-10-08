package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.exception.AlreadyExistsException;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.ManagerMapper;
import medicalcenter.userservice.model.dto.manager.*;
import medicalcenter.userservice.model.entity.Manager;
import medicalcenter.userservice.repository.ManagerRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class ManagerService implements CrudService<ManagerCreateEditDto, ManagerReadDto> {
    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    @Override
    public List<ManagerReadDto> findAll(Pageable pageable) {
        return managerMapper.toDto(managerRepository.findAll());
    }

    @Override
    public List<ManagerReadDto> findAllByLastName(String lastName, Pageable pageable) {
        return managerMapper.toDto(managerRepository.findAllByLastName(lastName, pageable));
    }

    @Override
    public List<ManagerReadDto> findAllByLastFirstName(String firstName, String lastName, Pageable pageable) {
        return managerMapper.toDto(managerRepository.findAllByLastFirstName(firstName, lastName, pageable));
    }

    @Override
    public ManagerReadDto findOne(UUID id) {
        Manager manager = managerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return managerMapper.toDto(manager);
    }

    @Override
    public ManagerReadDto findByFullName(String lastName, String firstName, String middleName) {
        Manager manager = managerRepository.findByFullName(lastName, firstName, middleName)
                .orElseThrow(() -> new NotFoundException(
                        "Manager '%s %s %s' not found".formatted(firstName, middleName, lastName)
                ));
        return managerMapper.toDto(manager);
    }

    @Override
    public ManagerReadDto findByPhone(String phone) {
        Manager manager = managerRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("Manager with phone '%s' not found".formatted(phone)));
        return managerMapper.toDto(manager);
    }

    @Override
    public ManagerReadDto save(ManagerCreateEditDto dto) {
        if (managerRepository.findByPhone(dto.phone()).isPresent()) {
            throw new AlreadyExistsException(
                    "Manager '%s %s %s' already exists".formatted(dto.firstName(), dto.middleName(), dto.lastName()));
        }
        Manager save = managerRepository.save(managerMapper.toEntity(dto));
        return managerMapper.toDto(save);
    }

    @Override
    public void update(UUID id, ManagerCreateEditDto dto) {
        if (!managerRepository.existsById(id)) {
            throw new NotFoundException(id);
        }

        int updated = managerRepository.updateById(
                id,
                dto.lastName(),
                dto.firstName(),
                dto.middleName(),
                dto.dateOfBirth(),
                dto.phone(),
                dto.email());

        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    public void delete(UUID id) {
        managerRepository.deleteById(id);
    }
}
