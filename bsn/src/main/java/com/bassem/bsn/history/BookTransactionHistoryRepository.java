package com.bassem.bsn.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory,Integer>, JpaSpecificationExecutor<BookTransactionHistory> {
}
