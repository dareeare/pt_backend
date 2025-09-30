package medicalcenter.userservice.service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Interface with CRUD operations
 *
 * @param <CR> DTO used for creating and updating entities
 * @param <R> read DTO
 */
public interface CrudService<CR, R> {
    List<R> findAll(Pageable pageable);

    List<R> findAllByLastName(String lastName, Pageable pageable);

    List<R> findAllByLastFirstName(String firstName, String lastName, Pageable pageable);

    R findOne(UUID id);

    R findByFullName(String lastName, String firstName, String middleName);

    R findByPhone(String phone);

    R save(CR dto);

    void update(UUID id, CR dto);

    void delete(UUID id);
}
