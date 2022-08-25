package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequestor(int id);
    List<Request> findAllByRequestor(int id, Pageable pageable);

    @Query("select r from Request r where not r.requestor = ?1 order by r.created desc")
    List<Request> search(int requestorId, Pageable pageable);
}
