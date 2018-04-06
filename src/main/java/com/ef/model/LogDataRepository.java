package com.ef.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yaga created on 3/22/2018.
 */
@Repository
public interface LogDataRepository extends CrudRepository<LogData, Integer> {

    @Query(value="select count(ip),ip from logdata where log_date between ?1 and ?2 group by ip having count(ip) > ?3",nativeQuery=true)
    List<Object[]> getyDateasper(LocalDateTime d1, LocalDateTime d2, int count);
}
