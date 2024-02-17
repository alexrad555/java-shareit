package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.entity.ItemRequestEntity;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequestEntity, Long> {

    List<ItemRequestEntity> findAllByRequestorId(Long userId, Pageable pageable);

    @Query(value = "select r from ItemRequestEntity r where r.requestor.id != ?1 ")
    List<ItemRequestEntity> findAllByUserIdNot(Long userId, Pageable pageable);

}
