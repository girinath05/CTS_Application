package com.cts.service;

import com.cts.model.Cheque;
import java.util.List;

public interface ChequeService {

    void submitCheque(Cheque cheque);

    List<Cheque> getPendingForMaker();

    List<Cheque> getPendingForChecker();

    List<Cheque> getCheckerApprovedUnbatched();

    List<Cheque> getAllCheques();

    void makerApprove(int chequeId);

    void makerReject(int chequeId);

    void checkerApprove(int chequeId);

    void checkerReject(int chequeId);
}