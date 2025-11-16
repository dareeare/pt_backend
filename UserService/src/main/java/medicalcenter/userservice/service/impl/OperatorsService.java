package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.OperatorMapper;
import medicalcenter.userservice.model.dto.operator.OperatorCreateEditDto;
import medicalcenter.userservice.model.dto.operator.OperatorReadDto;
import medicalcenter.userservice.model.entity.Operator;
import medicalcenter.userservice.repository.OperatorRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import medicalcenter.userservice.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class OperatorsService implements CrudService<OperatorCreateEditDto, OperatorReadDto> {
    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<OperatorReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from OperatorsService");
        return operatorMapper.toDto(operatorRepository.findAll(pageable).getContent());
    }

    @Override
    public List<OperatorReadDto> findAllByLastName(String lastName, Pageable pageable) {
        return operatorMapper.toDto(operatorRepository.findAllByLastName(lastName, pageable));
    }

    @Override
    public List<OperatorReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        return operatorMapper.toDto(operatorRepository.findAllByLastFirstName(firstName, lastName, pageable));
    }

    @Override
    public OperatorReadDto findOne(UUID id) {
        log.debug("finding operator with id: {}", id);
        Operator operator = operatorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return operatorMapper.toDto(operator);
    }

    @Override
    public OperatorReadDto findByFullName(String lastName, String firstName, String middleName) {
        Operator operator = operatorRepository.findByFullName(lastName, firstName, middleName)
                .orElseThrow(NotFoundException::new);
        return operatorMapper.toDto(operator);
    }

    @Override
    public OperatorReadDto findByPhone(String phone) {
        Operator operator = operatorRepository.findByPhone(phone).orElseThrow(NotFoundException::new);
        return operatorMapper.toDto(operator);
    }

    @Override
    @Transactional
    public OperatorReadDto save(OperatorCreateEditDto operator) {
        log.debug("saving operator: {}", operator);
        Operator entity = operatorMapper.toEntity(operator);
        return operatorMapper.toDto(operatorRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, OperatorCreateEditDto updatedOperator) {
        log.debug("updating operator with id {}", id);
        Operator operator = operatorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        int updated = operatorRepository.updateById(
                id,
                updatedOperator.lastName(),
                updatedOperator.firstName(),
                updatedOperator.middleName(),
                updatedOperator.dateOfBirth(),
                updatedOperator.phone(),
                updatedOperator.email(),
                updatedOperator.avatarPath()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Transactional
    public String updateAvatar(UUID id, MultipartFile avatarFile) throws IOException {
        log.debug("updating avatar for operator with id {}", id);
        Operator operator = operatorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        if (operator.getAvatarPath() != null) {
            fileStorageService.deleteFile(operator.getAvatarPath());
        }

        String avatarPath = fileStorageService.storeOperatorFile(avatarFile, id);

        int updated = operatorRepository.updateAvatarPath(id, avatarPath);
        if (updated == 0) {
            throw new UpdateException(id);
        }

        return avatarPath;
    }

    @Transactional
    public void deleteAvatar(UUID id) {
        log.debug("deleting avatar for operator with id {}", id);

        Operator operator = operatorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (operator.getAvatarPath() != null) {
            fileStorageService.deleteFile(operator.getAvatarPath());
            operatorRepository.updateAvatarPath(id, null);
        }
    }

    public byte[] getAvatar(UUID id) throws IOException {
        log.debug("getting avatar for operator with id {}", id);

        Operator operator = operatorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (operator.getAvatarPath() == null) {
            return null;
        }

        return fileStorageService.loadFile(operator.getAvatarPath());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting operator with id: {}", id);
        operatorRepository.deleteById(id);
    }

    public OperatorReadDto findByEmail(String email) {
        Operator operator = operatorRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        return operatorMapper.toDto(operator);
    }
}