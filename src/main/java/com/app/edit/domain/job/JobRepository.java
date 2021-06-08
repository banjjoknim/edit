package com.app.edit.domain.job;

import com.app.edit.domain.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Long>{

    Optional<Job> findById(Long jobId);

    Optional<Job> findByName(String jobName);
}
