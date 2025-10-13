package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewCreateEditDto;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewReadDto;
import medicalcenter.userservice.service.impl.DoctorReviewsService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-reviews")
@RequiredArgsConstructor
public class DoctorReviewsController {
    private final DoctorReviewsService doctorReviewsService;

    @GetMapping
    public ResponseEntity<List<DoctorReviewReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(doctorReviewsService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorReviewReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorReviewsService.findOne(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorReviewReadDto>> getByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(doctorReviewsService.findByDoctorId(doctorId, pageable));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DoctorReviewReadDto>> getByPatientId(@PathVariable UUID patientId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(doctorReviewsService.findByPatientId(patientId, pageable));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<DoctorReviewReadDto> getByVisitId(@PathVariable UUID visitId) {
        return ResponseEntity.ok(doctorReviewsService.findByVisitId(visitId));
    }

    @GetMapping("/doctor/{doctorId}/approved")
    public ResponseEntity<List<DoctorReviewReadDto>> getApprovedByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(doctorReviewsService.findApprovedByDoctorId(doctorId, pageable));
    }

    @GetMapping("/doctor/{doctorId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByDoctorId(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorReviewsService.getAverageRatingByDoctorId(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/approved-count")
    public ResponseEntity<Long> getApprovedReviewsCountByDoctorId(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorReviewsService.getApprovedReviewsCountByDoctorId(doctorId));
    }

    @PostMapping
    public ResponseEntity<DoctorReviewReadDto> createReview(@RequestBody @Valid DoctorReviewCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorReviewsService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReview(@PathVariable UUID id, @RequestBody @Valid DoctorReviewCreateEditDto dto) {
        doctorReviewsService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveReview(@PathVariable UUID id) {
        doctorReviewsService.approveReview(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        doctorReviewsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}