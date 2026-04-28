package com.cts.controller;

import com.cts.model.Batch;
import com.cts.model.Cheque;
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
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class BatchController extends SelectorComposer<Component> {

    @Wire private Listbox  lbUnbatched;
    @Wire private Listbox  lbBatches;
    @Wire private Checkbox chkSelectAll;
    @Wire private Label    lblMessage;
    @Wire private Label    lblNoUnbatched;
    @Wire private Label    lblNoBatches;

    private ChequeService chequeService = new ChequeServiceImpl();
    private BatchService  batchService  = new BatchServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (Sessions.getCurrent().getAttribute("loggedUser") == null) {
            Executions.sendRedirect("login.zul");
            return;
        }
        loadUnbatched();
        loadBatches();
    }

    private void loadUnbatched() {
        lbUnbatched.getItems().clear();
        List<Cheque> list = chequeService.getCheckerApprovedUnbatched();

        lblNoUnbatched.setVisible(list.isEmpty());

        for (Cheque c : list) {
            Listitem item = new Listitem();
            item.setValue(c);

            Listcell checkCell = new Listcell();
            checkCell.appendChild(new Checkbox());
            item.appendChild(checkCell);

            item.appendChild(new Listcell(c.getChequeNumber()));
            item.appendChild(new Listcell(c.getDrawerName()));
            item.appendChild(new Listcell("Rs. " + c.getAmountDigits()));
            item.appendChild(new Listcell(c.getChequeDate() != null ? c.getChequeDate() : ""));
            lbUnbatched.appendChild(item);
        }
    }

    private void loadBatches() {
        lbBatches.getItems().clear();

        List<Batch> all = new ArrayList<>();
        all.addAll(batchService.getBatchesByStatus("PENDING"));
        all.addAll(batchService.getBatchesByStatus("SENT"));
        all.addAll(batchService.getBatchesByStatus("APPROVED"));
        all.addAll(batchService.getBatchesByStatus("REJECTED"));

        lblNoBatches.setVisible(all.isEmpty());

        for (Batch b : all) {
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
            if ("PENDING".equals(b.getStatus())) {
                Button btn = new Button("🚀 Move to Clearing House");
                btn.setStyle("background:#1A3C6E; color:white; border:none; "
                           + "border-radius:4px; padding:5px 12px; cursor:pointer; font-size:12px;");
                String batchId = b.getId();
                btn.addEventListener("onClick", e -> {
                    batchService.moveToClearingHouse(batchId);
                    lblMessage.setValue("Batch moved to clearing house.");
                    lblMessage.setStyle("color:#2E7D32; font-size:13px;");
                    loadBatches();
                });
                actionCell.appendChild(btn);
            } else {
                Label sentLabel = new Label("Sent to Clearing");
                sentLabel.setStyle("color:#888; font-size:12px;");
                actionCell.appendChild(sentLabel);
            }
            item.appendChild(actionCell);
            lbBatches.appendChild(item);
        }
    }

    @Listen("onClick = #chkSelectAll")
    public void onSelectAll() {
        boolean checked = chkSelectAll.isChecked();
        for (Listitem item : lbUnbatched.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            cb.setChecked(checked);
        }
    }

    @Listen("onClick = #btnCreateBatch")
    public void onCreateBatch() {
        List<Integer> selectedIds = new ArrayList<>();
        List<String>  amounts     = new ArrayList<>();

        for (Listitem item : lbUnbatched.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            if (cb.isChecked()) {
                Cheque c = (Cheque) item.getValue();
                selectedIds.add(c.getId());
                amounts.add(c.getAmountDigits());
            }
        }

        if (selectedIds.isEmpty()) {
            lblMessage.setValue("Please select at least one cheque.");
            lblMessage.setStyle("color:#D32F2F; font-size:13px;");
            return;
        }

        batchService.createBatch(selectedIds, amounts);
        lblMessage.setValue("Batch created successfully.");
        lblMessage.setStyle("color:#2E7D32; font-size:13px;");
        loadUnbatched();
        loadBatches();
    }

    @Listen("onClick = #btnDashboard")
    public void onDashboard() {
        Executions.sendRedirect("dashboard.zul");
    }

    private String getStatusStyle(String status) {
        String base = "padding:3px 10px; border-radius:4px; font-size:12px; color:white; font-weight:bold;";
        if ("PENDING".equals(status))  return base + "background:#F57C00;";
        if ("SENT".equals(status))     return base + "background:#1A3C6E;";
        if ("APPROVED".equals(status)) return base + "background:#2E7D32;";
        if ("REJECTED".equals(status)) return base + "background:#D32F2F;";
        return base + "background:#888;";
    }
}