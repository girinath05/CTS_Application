package com.cts.service;

import java.util.List;

import com.cts.dao.ChequeDao;
import com.cts.dao.ChequeDaoImpl;
import com.cts.model.Cheque;

public class ChequeServiceImpl implements ChequeService {

    // Use the DAO to talk to the database
    private ChequeDao chequeDao = new ChequeDaoImpl();

    @Override
    public void submitCheque(Cheque cheque) {
        chequeDao.insertCheque(cheque);
    }

    @Override
    public List<Cheque> getPendingForMaker() {
        return chequeDao.getChequesByStatus("SUBMITTED");
    }

    @Override
    public List<Cheque> getPendingForChecker() {
        return chequeDao.getChequesByStatus("MAKER_APPROVED");
    }

    @Override
    public List<Cheque> getCheckerApprovedUnbatched() {
        return chequeDao.getChequesByStatus("CHECKER_APPROVED");
    }

    @Override
    public List<Cheque> getAllCheques() {
        return chequeDao.getAllCheques();
    }

    @Override
    public void makerApprove(int chequeId) {
        chequeDao.updateStatus(chequeId, "MAKER_APPROVED");
    }

    @Override
    public void makerReject(int chequeId) {
        chequeDao.updateStatus(chequeId, "REJECTED");
    }

    @Override
    public void checkerApprove(int chequeId) {
        chequeDao.updateStatus(chequeId, "CHECKER_APPROVED");
    }

    @Override
    public void checkerReject(int chequeId) {
        chequeDao.updateStatus(chequeId, "REJECTED");
    }
}