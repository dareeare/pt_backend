package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.ServiceMapper;
import medicalcenter.userservice.model.dto.service.ServiceCreateEditDto;
import medicalcenter.userservice.model.dto.service.ServiceReadDto;
import medicalcenter.userservice.repository.ServiceRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ServiceService implements CrudService<ServiceCreateEditDto, ServiceReadDto> {
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Override
    public List<ServiceReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from ServiceService");
        return serviceMapper.toDto(serviceRepository.findAll(pageable).getContent());
    }

    @Override
    public List<ServiceReadDto> findAllByLastName(String name, Pageable pageable) {
        return serviceMapper.toDto(serviceRepository.findByNameOfServiceContainingIgnoreCase(name, pageable));
    }

    @Override
    public List<ServiceReadDto> findAllByLastFirstName(String name, String unused, Pageable pageable) {
        return findAllByLastName(name, pageable);
    }

    @Override
    public ServiceReadDto findOne(UUID id) {
        log.debug("finding service with id: {}", id);
        medicalcenter.userservice.model.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return serviceMapper.toDto(service);
    }

    @Override
    public ServiceReadDto findByFullName(String lastName, String firstName, String middleName) {
        throw new UnsupportedOperationException("Method not supported for Service entity");
    }

    @Override
    public ServiceReadDto findByPhone(String phone) {
        throw new UnsupportedOperationException("Method not supported for Service entity");
    }

    @Override
    @Transactional
    public ServiceReadDto save(ServiceCreateEditDto serviceDto) {
        log.debug("saving service: {}", serviceDto);
        medicalcenter.userservice.model.entity.Service entity = serviceMapper.toEntity(serviceDto);
        return serviceMapper.toDto(serviceRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, ServiceCreateEditDto updatedService) {
        log.debug("updating service with id {}", id);
        medicalcenter.userservice.model.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        int updated = serviceRepository.updateById(
                id,
                updatedService.nameOfService(),
                updatedService.cost(),
                updatedService.durationMinutes(),
                updatedService.information(),
                updatedService.doctorId()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting service with id: {}", id);
        serviceRepository.deleteById(id);
    }

    public List<ServiceReadDto> findByDoctorId(UUID doctorId, Pageable pageable) {
        return serviceMapper.toDto(serviceRepository.findByDoctorId(doctorId, pageable));
    }

    public List<ServiceReadDto> findByCostBetween(Double minCost, Double maxCost, Pageable pageable) {
        return serviceMapper.toDto(serviceRepository.findByCostBetween(minCost, maxCost, pageable));
    }
}