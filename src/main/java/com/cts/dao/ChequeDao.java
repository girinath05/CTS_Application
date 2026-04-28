package com.cts.dao;

import com.cts.model.Cheque;
import java.util.List;

public interface ChequeDao {

    void insertCheque(Cheque cheque);

    List<Cheque> getChequesByStatus(String status);

    List<Cheque> getAllCheques();

    void updateStatus(int chequeId, String newStatus);

    void updateStatusAndBatch(int chequeId, String newStatus, String batchId);
}