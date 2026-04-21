package com.bassem.bsn.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedBackRepository extends JpaRepository<FeedBack,Long> , JpaSpecificationExecutor<FeedBack> {
}
