package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.ServiceRenderedMapper;
import medicalcenter.userservice.model.dto.servicerendered.ServiceRenderedCreateEditDto;
import medicalcenter.userservice.model.dto.servicerendered.ServiceRenderedReadDto;
import medicalcenter.userservice.model.entity.ServiceRendered;
import medicalcenter.userservice.repository.ServiceRenderedRepository;
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
public class ServiceRenderedService implements CrudService<ServiceRenderedCreateEditDto, ServiceRenderedReadDto> {
    private final ServiceRenderedRepository serviceRenderedRepository;
    private final ServiceRenderedMapper serviceRenderedMapper;

    @Override
    public List<ServiceRenderedReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from ServiceRenderedService");
        return serviceRenderedMapper.toDto(serviceRenderedRepository.findAll(pageable).getContent());
    }

    @Override
    public List<ServiceRenderedReadDto> findAllByLastName(String name, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for ServiceRendered entity");
    }

    @Override
    public List<ServiceRenderedReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for ServiceRendered entity");
    }

    @Override
    public ServiceRenderedReadDto findOne(UUID id) {
        log.debug("finding service rendered with id: {}", id);
        ServiceRendered serviceRendered = serviceRenderedRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return serviceRenderedMapper.toDto(serviceRendered);
    }

    @Override
    public ServiceRenderedReadDto findByFullName(String lastName, String firstName, String middleName) {
        throw new UnsupportedOperationException("Method not supported for ServiceRendered entity");
    }

    @Override
    public ServiceRenderedReadDto findByPhone(String phone) {
        throw new UnsupportedOperationException("Method not supported for ServiceRendered entity");
    }

    @Override
    @Transactional
    public ServiceRenderedReadDto save(ServiceRenderedCreateEditDto serviceRendered) {
        log.debug("saving service rendered: {}", serviceRendered);
        ServiceRendered entity = serviceRenderedMapper.toEntity(serviceRendered);
        return serviceRenderedMapper.toDto(serviceRenderedRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, ServiceRenderedCreateEditDto updatedServiceRendered) {
        log.debug("updating service rendered with id {}", id);
        ServiceRendered serviceRendered = serviceRenderedRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        int updated = serviceRenderedRepository.updateById(
                id,
                updatedServiceRendered.visitId(),
                updatedServiceRendered.serviceId(),
                updatedServiceRendered.actualCost()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting service rendered with id: {}", id);
        serviceRenderedRepository.deleteById(id);
    }

    public List<ServiceRenderedReadDto> findByVisitId(UUID visitId, Pageable pageable) {
        return serviceRenderedMapper.toDto(serviceRenderedRepository.findByVisitId(visitId, pageable));
    }

    public List<ServiceRenderedReadDto> findByServiceId(UUID serviceId, Pageable pageable) {
        return serviceRenderedMapper.toDto(serviceRenderedRepository.findByServiceId(serviceId, pageable));
    }

    public Double calculateTotalCostByVisitId(UUID visitId) {
        return serviceRenderedRepository.calculateTotalCostByVisitId(visitId).doubleValue();
    }
}