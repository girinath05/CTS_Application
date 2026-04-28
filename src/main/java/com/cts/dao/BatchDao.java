package com.cts.dao;

import com.cts.model.Batch;
import java.util.List;

public interface BatchDao {

    void insertBatch(Batch batch);

    List<Batch> getBatchesByStatus(String status);

    List<Batch> getAllBatches();

    void updateBatchStatus(String batchId, String newStatus);

    int getNextBatchNumber();
}