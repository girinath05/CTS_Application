package com.iispl.controller;

import com.cts.service.BatchService;
import com.cts.service.BatchServiceImpl;
import com.cts.service.ChequeService;
import com.cts.service.ChequeServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class DashboardController extends SelectorComposer<Component> {

    @Wire private Label lblUsername;
    @Wire private Label lblPendingCount;
    @Wire private Label lblMakerCount;
    @Wire private Label lblCheckerCount;
    @Wire private Label lblBatchCount;

    private ChequeService chequeService = new ChequeServiceImpl();
    private BatchService  batchService  = new BatchServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        String loggedUser = (String) Sessions.getCurrent().getAttribute("loggedUser");
        if (loggedUser == null) {
            Executions.sendRedirect("login.zul");
            return;
        }

        lblUsername.setValue(loggedUser);
        loadStats();
    }

    private void loadStats() {
        // FIX: each card now gets its own correct count from the right status
        int pendingSubmissions = chequeService.getPendingForMaker().size();       // SUBMITTED
        int pendingForChecker  = chequeService.getPendingForChecker().size();     // MAKER_APPROVED
        int activeBatches      = batchService.getBatchesByStatus("PENDING").size()
                               + batchService.getBatchesByStatus("SENT").size();

        lblPendingCount.setValue(String.valueOf(pendingSubmissions));  // Submission card
        lblMakerCount.setValue(String.valueOf(pendingSubmissions));    // Maker card (waiting for maker)
        lblCheckerCount.setValue(String.valueOf(pendingForChecker));   // Checker card
        lblBatchCount.setValue(String.valueOf(activeBatches));         // Batch card
    }

    @Listen("onClick = #cardSubmission")
    public void onSubmission() { Executions.sendRedirect("submission.zul"); }

    @Listen("onClick = #cardMaker")
    public void onMaker() { Executions.sendRedirect("maker.zul"); }

    @Listen("onClick = #cardChecker")
    public void onChecker() { Executions.sendRedirect("checker.zul"); }

    @Listen("onClick = #cardBatch")
    public void onBatch() { Executions.sendRedirect("batch.zul"); }

    @Listen("onClick = #cardClearing")
    public void onClearing() { Executions.sendRedirect("clearing.zul"); }

    @Listen("onClick = #btnLogout")
    public void onLogout() {
        Sessions.getCurrent().invalidate();
        Executions.sendRedirect("login.zul");
    }
}