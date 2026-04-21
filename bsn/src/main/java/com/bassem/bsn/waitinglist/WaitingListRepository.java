package com.bassem.bsn.waitinglist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WaitingListRepository extends JpaRepository<WaitingList, Long> , JpaSpecificationExecutor<WaitingList>
{
}
