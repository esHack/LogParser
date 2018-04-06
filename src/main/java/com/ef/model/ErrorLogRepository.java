package com.ef.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepository extends CrudRepository<ErrorLog, Integer> {
}
