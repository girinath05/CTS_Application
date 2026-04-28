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

import java.util.List;

public class CheckerController extends SelectorComposer<Component> {

    @Wire private Listbox  lbApprovedCheques;
    @Wire private Label    lblDrawerName;
    @Wire private Label    lblBankName;
    @Wire private Label    lblBranchName;
    @Wire private Label    lblMicrCode;
    @Wire private Label    lblChequeNumber;
    @Wire private Label    lblAccountNumber;
    @Wire private Label    lblAmountDigits;
    @Wire private Label    lblAmountWords;
    @Wire private Label    lblChequeDate;
    @Wire private Label    lblPayeeName;
    @Wire private Checkbox chkDetailsVerified;
    @Wire private Checkbox chkDateValid;
    @Wire private Checkbox chkAmountMatch;
    @Wire private Combobox cbReturnCode;
    @Wire private Button   btnApprove;
    @Wire private Button   btnBulkReject;
    @Wire private Label    lblMessage;
    @Wire private Checkbox chkSelectAll;
    @Wire private Label    lblNoData;
    @Wire private Label    lblHint;
    @Wire private Vlayout  detailArea;

    private ChequeService chequeService = new ChequeServiceImpl();
    private Cheque selectedCheque;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (Sessions.getCurrent().getAttribute("loggedUser") == null) {
            Executions.sendRedirect("login.zul");
            return;
        }
        btnApprove.setDisabled(true);
        cbReturnCode.setDisabled(true);
        lblHint.setVisible(true);
        detailArea.setVisible(false);
        loadApprovedCheques();
    }

    private void loadApprovedCheques() {
        lbApprovedCheques.getItems().clear();
        List<Cheque> list = chequeService.getPendingForChecker();

        lblNoData.setVisible(list.isEmpty());

        for (Cheque c : list) {
            Listitem item = new Listitem();
            item.setValue(c);

            Listcell checkCell = new Listcell();
            Checkbox cb = new Checkbox();
            cb.addEventListener("onCheck", e -> updateBulkRejectCount());
            checkCell.appendChild(cb);
            item.appendChild(checkCell);

            item.appendChild(new Listcell(c.getChequeNumber()));
            item.appendChild(new Listcell(c.getDrawerName()));
            lbApprovedCheques.appendChild(item);
        }

        btnBulkReject.setLabel("Bulk Reject (0)");
        btnBulkReject.setStyle(getGrayBtnStyle());
    }

    @Listen("onSelect = #lbApprovedCheques")
    public void onSelectCheque() {
        Listitem item = lbApprovedCheques.getSelectedItem();
        if (item == null) return;
        selectedCheque = (Cheque) item.getValue();
        populateReadOnly(selectedCheque);
        resetChecklist();
        lblHint.setVisible(false);
        detailArea.setVisible(true);
        lblMessage.setValue("");
    }

    @Listen("onClick = #chkSelectAll")
    public void onSelectAll() {
        boolean checked = chkSelectAll.isChecked();
        for (Listitem item : lbApprovedCheques.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            cb.setChecked(checked);
        }
        updateBulkRejectCount();
    }

    @Listen("onClick = #chkDetailsVerified; onClick = #chkDateValid; onClick = #chkAmountMatch")
    public void onChecklistChange() {
        boolean allChecked = chkDetailsVerified.isChecked()
                && chkDateValid.isChecked()
                && chkAmountMatch.isChecked();
        btnApprove.setDisabled(!allChecked);
    }

    @Listen("onClick = #btnApprove")
    public void onApprove() {
        if (selectedCheque == null) {
            showError("Please select a cheque first.");
            return;
        }
        chequeService.checkerApprove(selectedCheque.getId());
        showSuccess("Cheque approved by checker.");
        clearDetail();
        loadApprovedCheques();
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
        chequeService.checkerReject(selectedCheque.getId());
        showError("Cheque rejected by checker.");
        clearDetail();
        loadApprovedCheques();
    }

    @Listen("onClick = #btnBulkReject")
    public void onBulkReject() {
        cbReturnCode.setDisabled(false);
        if (cbReturnCode.getValue().isEmpty()) {
            showError("Please select a CTS Return Code for bulk reject.");
            return;
        }
        int count = 0;
        for (Listitem item : lbApprovedCheques.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            if (cb.isChecked()) {
                Cheque c = (Cheque) item.getValue();
                chequeService.checkerReject(c.getId());
                count++;
            }
        }
        showError(count + " cheque(s) rejected.");
        clearDetail();
        loadApprovedCheques();
    }

    @Listen("onClick = #btnDashboard")
    public void onDashboard() {
        Executions.sendRedirect("dashboard.zul");
    }

    private void updateBulkRejectCount() {
        int count = 0;
        for (Listitem item : lbApprovedCheques.getItems()) {
            Checkbox cb = (Checkbox) item.getFirstChild().getFirstChild();
            if (cb.isChecked()) count++;
        }
        btnBulkReject.setLabel("Bulk Reject (" + count + ")");
        btnBulkReject.setStyle(count > 0 ? getRedBtnStyle() : getGrayBtnStyle());
    }

    private void populateReadOnly(Cheque c) {
        lblDrawerName.setValue(c.getDrawerName());
        lblBankName.setValue(c.getBankName());
        lblBranchName.setValue(c.getBranchName());
        lblMicrCode.setValue(c.getMicrCode());
        lblChequeNumber.setValue(c.getChequeNumber());
        lblAccountNumber.setValue(c.getAccountNumber());
        lblAmountDigits.setValue(c.getAmountDigits());
        lblAmountWords.setValue(c.getAmountWords());
        lblChequeDate.setValue(c.getChequeDate() != null ? c.getChequeDate() : "");
        lblPayeeName.setValue(c.getPayeeName());
    }

    private void resetChecklist() {
        chkDetailsVerified.setChecked(false);
        chkDateValid.setChecked(false);
        chkAmountMatch.setChecked(false);
        btnApprove.setDisabled(true);
        cbReturnCode.setDisabled(true);
        cbReturnCode.setValue("");
    }

    private void clearDetail() {
        selectedCheque = null;
        lblDrawerName.setValue(""); lblBankName.setValue("");
        lblBranchName.setValue(""); lblMicrCode.setValue("");
        lblChequeNumber.setValue(""); lblAccountNumber.setValue("");
        lblAmountDigits.setValue(""); lblAmountWords.setValue("");
        lblChequeDate.setValue(""); lblPayeeName.setValue("");
        resetChecklist();
        lblHint.setVisible(true);
        detailArea.setVisible(false);
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