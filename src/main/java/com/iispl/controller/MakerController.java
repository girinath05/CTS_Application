package com.iispl.controller;

import com.cts.model.Cheque;
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

public class MakerController extends SelectorComposer<Component> {

    @Wire private Listbox  lbPendingCheques;
    @Wire private Textbox  tbDrawerName;
    @Wire private Textbox  tbBankName;
    @Wire private Textbox  tbBranchName;
    @Wire private Textbox  tbMicrCode;
    @Wire private Textbox  tbChequeNumber;
    @Wire private Textbox  tbAccountNumber;
    @Wire private Textbox  tbAmountDigits;
    @Wire private Textbox  tbAmountWords;
    @Wire private Textbox  tbChequeDate;
    @Wire private Textbox  tbPayeeName;
    @Wire private Combobox cbReturnCode;
    @Wire private Label    lblMessage;
    @Wire private Checkbox chkSelectAll;
    @Wire private Label    lblNoData;
    @Wire private Label    lblHint;
    @Wire private Vlayout  formArea;
    @Wire private Button   btnBulkReject;

    private ChequeService chequeService = new ChequeServiceImpl();
    private List<Cheque> pendingList = new ArrayList<>();
    private Cheque selectedCheque;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (Sessions.getCurrent().getAttribute("loggedUser") == null) {
            Executions.sendRedirect("login.zul");
            return;
        }
        lblHint.setVisible(true);
        formArea.setVisible(false);
        loadPendingCheques();
    }

    private void loadPendingCheques() {
        lbPendingCheques.getItems().clear();
        pendingList = chequeService.getPendingForMaker();

        lblNoData.setVisible(pendingList.isEmpty());

        for (Cheque c : pendingList) {
            Listitem item = new Listitem();
            item.setValue(c);

            Listcell checkCell = new Listcell();
            Checkbox cb = new Checkbox();
            cb.addEventListener("onCheck", e -> updateBulkRejectCount());
            checkCell.appendChild(cb);
            item.appendChild(checkCell);

            item.appendChild(new Listcell(c.getChequeNumber()));
            item.appendChild(new Listcell(c.getDrawerName()));
            lbPendingCheques.appendChild(item);
        }

        btnBulkReject.setLabel("Bulk Reject (0)");
        btnBulkReject.setStyle(getGrayBtnStyle());
    }

    @Listen("onSelect = #lbPendingCheques")
    public void onSelectCheque() {
        Listitem item = lbPendingCheques.getSelectedItem();
        if (item == null) return;
        selectedCheque = (Cheque) item.getValue();
        populateForm(selectedCheque);
        lblHint.setVisible(false);
        formArea.setVisible(true);
        cbReturnCode.setDisabled(true);
        cbReturnCode.setValue("");
        lblMessage.setValue("");
    }

    @Listen("onClick = #chkSelectAll")
    public void onSelectAll() {
        boolean checked = chkSelectAll.isChecked();
        for (Listitem item : lbPendingCheques.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            cb.setChecked(checked);
        }
        updateBulkRejectCount();
    }

    @Listen("onClick = #btnApprove")
    public void onApprove() {
        if (selectedCheque == null) {
            showError("Please select a cheque first.");
            return;
        }
        chequeService.makerApprove(selectedCheque.getId());
        showSuccess("Cheque approved successfully.");
        clearForm();
        loadPendingCheques();
    }

    @Listen("onClick = #btnReject")
    public void onReject() {
        if (selectedCheque == null) {
            showError("Please select a cheque first.");
            return;
        }
        cbReturnCode.setDisabled(false);
        if (cbReturnCode.getValue().isEmpty()) {
            showError("Please select a CTS Return Code to reject.");
            return;
        }
        chequeService.makerReject(selectedCheque.getId());
        showError("Cheque rejected.");
        clearForm();
        loadPendingCheques();
    }

    @Listen("onClick = #btnBulkReject")
    public void onBulkReject() {
        cbReturnCode.setDisabled(false);
        if (cbReturnCode.getValue().isEmpty()) {
            showError("Please select a CTS Return Code for bulk reject.");
            return;
        }
        int count = 0;
        for (Listitem item : lbPendingCheques.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            if (cb.isChecked()) {
                Cheque c = (Cheque) item.getValue();
                chequeService.makerReject(c.getId());
                count++;
            }
        }
        showError(count + " cheque(s) rejected.");
        clearForm();
        loadPendingCheques();
    }

    @Listen("onClick = #btnDashboard")
    public void onDashboard() {
        Executions.sendRedirect("dashboard.zul");
    }

    private void updateBulkRejectCount() {
        int count = 0;
        for (Listitem item : lbPendingCheques.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            if (cb.isChecked()) count++;
        }
        btnBulkReject.setLabel("Bulk Reject (" + count + ")");
        btnBulkReject.setStyle(count > 0 ? getRedBtnStyle() : getGrayBtnStyle());
    }

    private void populateForm(Cheque c) {
        tbDrawerName.setValue(c.getDrawerName());
        tbBankName.setValue(c.getBankName());
        tbBranchName.setValue(c.getBranchName());
        tbMicrCode.setValue(c.getMicrCode());
        tbChequeNumber.setValue(c.getChequeNumber());
        tbAccountNumber.setValue(c.getAccountNumber());
        tbAmountDigits.setValue(c.getAmountDigits());
        tbAmountWords.setValue(c.getAmountWords());
        tbChequeDate.setValue(c.getChequeDate() != null ? c.getChequeDate() : "");
        tbPayeeName.setValue(c.getPayeeName());
    }

    private void clearForm() {
        selectedCheque = null;
        tbDrawerName.setValue(""); tbBankName.setValue("");
        tbBranchName.setValue(""); tbMicrCode.setValue("");
        tbChequeNumber.setValue(""); tbAccountNumber.setValue("");
        tbAmountDigits.setValue(""); tbAmountWords.setValue("");
        tbChequeDate.setValue(""); tbPayeeName.setValue("");
        cbReturnCode.setValue(""); cbReturnCode.setDisabled(true);
        lblHint.setVisible(true);
        formArea.setVisible(false);
    }

    private void showSuccess(String msg) {
        lblMessage.setValue(msg);
        lblMessage.setStyle("color:#2E7D32; font-size:13px; margin-bottom:12px;");
    }

    private void showError(String msg) {
        lblMessage.setValue(msg);
        lblMessage.setStyle("color:#DC2626; font-size:13px; margin-bottom:12px;");
    }

    private String getGrayBtnStyle() {
        return "background:#9CA3AF; color:white; border:none; "
             + "border-radius:6px; padding:9px 8px; cursor:pointer; font-size:13px; width:100%;";
    }

    private String getRedBtnStyle() {
        return "background:#DC2626; color:white; border:none; "
             + "border-radius:6px; padding:9px 8px; cursor:pointer; font-size:13px; width:100%;";
    }
}