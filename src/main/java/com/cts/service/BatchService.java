package com.cts.service;

import com.cts.model.Batch;
import java.util.List;

public interface BatchService {

    void createBatch(List<Integer> chequeIds, List<String> amounts);

    void moveToClearingHouse(String batchId);

    void approveBatch(String batchId);

    void rejectBatch(String batchId);

    List<Batch> getBatchesByStatus(String status);

    List<Batch> getAllBatches();
}