package com.iispl.controller;

import com.cts.model.Batch;
import com.cts.service.BatchService;
import com.cts.service.BatchServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class ClearingHouseController extends SelectorComposer<Component> {

    @Wire private Listbox lbBatches;
    @Wire private Label   lblMessage;
    @Wire private Label   lblCurrentView;
    @Wire private Label   lblEmptyBatches;
    @Wire private Button  btnPending;
    @Wire private Button  btnApproved;
    @Wire private Button  btnRejected;

    private BatchService batchService = new BatchServiceImpl();
    private String currentView = "SENT";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (Sessions.getCurrent().getAttribute("loggedUser") == null) {
            Executions.sendRedirect("login.zul");
            return;
        }
        loadBatches("SENT");
    }

    private void loadBatches(String status) {
        currentView = status;
        lbBatches.getItems().clear();

        List<Batch> list;
        String viewLabel;

        if ("APPROVED".equals(status)) {
            list = batchService.getBatchesByStatus("APPROVED");
            viewLabel = "Approved Batches";
        } else if ("REJECTED".equals(status)) {
            list = batchService.getBatchesByStatus("REJECTED");
            viewLabel = "Rejected Batches";
        } else {
            list = batchService.getBatchesByStatus("SENT");
            viewLabel = "Pending Batches";
        }

        lblCurrentView.setValue("Viewing: " + viewLabel);
        updateTabStyles(status);
        lblEmptyBatches.setVisible(list.isEmpty());

        boolean showActions = "SENT".equals(status);

        for (Batch b : list) {
            Listitem item = new Listitem();
            item.setValue(b.getId());

            item.appendChild(new Listcell(b.getId()));
            item.appendChild(new Listcell(String.valueOf(b.getChequeCount())));

            Listcell statusCell = new Listcell();
            Label statusLabel = new Label(b.getStatus());
            statusLabel.setStyle(getStatusStyle(b.getStatus()));
            statusCell.appendChild(statusLabel);
            item.appendChild(statusCell);

            Listcell actionCell = new Listcell();
            if (showActions) {
                Button btnApproveRow = new Button("✅ Approve");
                btnApproveRow.setStyle(
                    "background:#2E7D32; color:white; border:none; "
                  + "border-radius:4px; padding:5px 12px; cursor:pointer; font-size:12px; margin-right:6px;");

                Button btnRejectRow = new Button("❌ Reject");
                btnRejectRow.setStyle(
                    "background:#D32F2F; color:white; border:none; "
                  + "border-radius:4px; padding:5px 12px; cursor:pointer; font-size:12px;");

                String batchId = b.getId();
                btnApproveRow.addEventListener("onClick", e -> {
                    batchService.approveBatch(batchId);
                    lblMessage.setValue("Batch approved.");
                    lblMessage.setStyle("color:#2E7D32; font-size:13px;");
                    loadBatches("SENT");
                });
                btnRejectRow.addEventListener("onClick", e -> {
                    batchService.rejectBatch(batchId);
                    lblMessage.setValue("Batch rejected.");
                    lblMessage.setStyle("color:#D32F2F; font-size:13px;");
                    loadBatches("SENT");
                });

                actionCell.appendChild(btnApproveRow);
                actionCell.appendChild(btnRejectRow);
            } else {
                Label noAction = new Label("No further action");
                noAction.setStyle("color:#888; font-size:12px;");
                actionCell.appendChild(noAction);
            }
            item.appendChild(actionCell);
            lbBatches.appendChild(item);
        }
    }

    private void updateTabStyles(String activeStatus) {
        if ("SENT".equals(activeStatus)) {
            btnPending.setStyle("background:#1A3C6E; color:white; border:2px solid #1A3C6E; border-radius:4px; padding:8px 18px; cursor:pointer; font-size:13px;");
        } else {
            btnPending.setStyle("border:2px solid #1A3C6E; color:#1A3C6E; background:white; border-radius:4px; padding:8px 18px; cursor:pointer; font-size:13px;");
        }
        if ("APPROVED".equals(activeStatus)) {
            btnApproved.setStyle("background:#2E7D32; color:white; border:2px solid #2E7D32; border-radius:4px; padding:8px 18px; cursor:pointer; font-size:13px;");
        } else {
            btnApproved.setStyle("border:2px solid #2E7D32; color:#2E7D32; background:white; border-radius:4px; padding:8px 18px; cursor:pointer; font-size:13px;");
        }
        if ("REJECTED".equals(activeStatus)) {
            btnRejected.setStyle("background:#D32F2F; color:white; border:2px solid #D32F2F; border-radius:4px; padding:8px 18px; cursor:pointer; font-size:13px;");
        } else {
            btnRejected.setStyle("border:2px solid #D32F2F; color:#D32F2F; background:white; border-radius:4px; padding:8px 18px; cursor:pointer; font-size:13px;");
        }
    }

    @Listen("onClick = #btnPending")
    public void onPending() {
        lblMessage.setValue("");
        loadBatches("SENT");
    }

    @Listen("onClick = #btnApproved")
    public void onApproved() {
        lblMessage.setValue("");
        loadBatches("APPROVED");
    }

    @Listen("onClick = #btnRejected")
    public void onRejected() {
        lblMessage.setValue("");
        loadBatches("REJECTED");
    }

    @Listen("onClick = #btnDashboard")
    public void onDashboard() {
        Executions.sendRedirect("dashboard.zul");
    }

    private String getStatusStyle(String status) {
        String base = "padding:3px 10px; border-radius:4px; font-size:12px; color:white; font-weight:bold;";
        if ("SENT".equals(status))     return base + "background:#1A3C6E;";
        if ("APPROVED".equals(status)) return base + "background:#2E7D32;";
        if ("REJECTED".equals(status)) return base + "background:#D32F2F;";
        if ("PENDING".equals(status))  return base + "background:#F57C00;";
        return base + "background:#888;";
    }
}