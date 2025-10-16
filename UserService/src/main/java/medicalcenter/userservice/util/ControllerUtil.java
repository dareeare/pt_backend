package medicalcenter.userservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

import java.util.List;

@UtilityClass
public class ControllerUtil {
    public <T> ResponseEntity<List<T>> getListResponseEntity(List<T> all) {
        if (all.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(all);
    }
}
