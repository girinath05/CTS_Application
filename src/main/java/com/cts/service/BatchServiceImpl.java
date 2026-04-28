package com.cts.service;

import java.util.List;

import com.cts.dao.BatchDao;
import com.cts.dao.BatchDaoImpl;
import com.cts.dao.ChequeDao;
import com.cts.dao.ChequeDaoImpl;
import com.cts.model.Batch;

public class BatchServiceImpl implements BatchService {

    private BatchDao  batchDao  = new BatchDaoImpl();
    private ChequeDao chequeDao = new ChequeDaoImpl();

    @Override
    public void createBatch(List<Integer> chequeIds, List<String> amounts) {

        // Generate a new batch ID like BATCH001, BATCH002 ...
        int    nextNumber = batchDao.getNextBatchNumber();
        String batchId    = String.format("BATCH%03d", nextNumber);

        // Calculate total amount of all selected cheques
        double total = 0;
        for (String amt : amounts) {
            try {
                // Remove commas before parsing  e.g. "1,000" -> 1000
                total += Double.parseDouble(amt.replace(",", ""));
            } catch (NumberFormatException e) {
                // Skip if amount is not a valid number
            }
        }

        // Save the batch record
        Batch batch = new Batch();
        batch.setId(batchId);
        batch.setChequeCount(chequeIds.size());
        batch.setTotalAmount(String.valueOf((long) total));
        batchDao.insertBatch(batch);

        // Update each cheque's status and link it to this batch
        for (Integer chequeId : chequeIds) {
            chequeDao.updateStatusAndBatch(chequeId, "BATCHED", batchId);
        }
    }

    @Override
    public void moveToClearingHouse(String batchId) {
        batchDao.updateBatchStatus(batchId, "SENT");
    }

    @Override
    public void approveBatch(String batchId) {
        batchDao.updateBatchStatus(batchId, "APPROVED");
    }

    @Override
    public void rejectBatch(String batchId) {
        batchDao.updateBatchStatus(batchId, "REJECTED");
    }

    @Override
    public List<Batch> getBatchesByStatus(String status) {
        return batchDao.getBatchesByStatus(status);
    }

    @Override
    public List<Batch> getAllBatches() {
        return batchDao.getAllBatches();
    }
}