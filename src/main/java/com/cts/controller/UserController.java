package com.cts.controller;

import com.cts.model.Cheque;
import com.cts.service.ChequeService;
import com.cts.service.ChequeServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import java.text.SimpleDateFormat;
import java.util.List;

public class UserController extends SelectorComposer<Component> {

    @Wire private Textbox  tbDrawerName;
    @Wire private Textbox  tbBankName;
    @Wire private Textbox  tbBranchName;
    @Wire private Textbox  tbMicrCode;
    @Wire private Textbox  tbChequeNumber;
    @Wire private Textbox  tbAccountNumber;
    @Wire private Combobox tbAmountDigits;    // Combobox so user can pick OR type amount
    @Wire private Textbox  tbAmountWords;     // Read-only, auto-filled by onAmountChange()
    @Wire private Datebox  dbChequeDate;
    @Wire private Textbox  tbPayeeName;
    @Wire private Label    lblMessage;
    @Wire private Listbox  lbMyCheques;
    @Wire private Label    lblEmptyCheques;

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private ChequeService chequeService = new ChequeServiceImpl();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (Sessions.getCurrent().getAttribute("loggedUser") == null) {
            Executions.sendRedirect("login.zul");
            return;
        }
        loadAllCheques();
    }

    // Fires when user selects from dropdown OR finishes typing in Amount in Digits
    // Automatically fills Amount in Words using Indian number system (Lakh, Crore)
    @Listen("onChange = #tbAmountDigits")
    public void onAmountChange() {
        String amountText = tbAmountDigits.getValue().trim();

        if (amountText.isEmpty()) {
            tbAmountWords.setValue("");
            return;
        }

        try {
            long amount = Long.parseLong(amountText);
            tbAmountWords.setValue(convertToWords(amount));
        } catch (NumberFormatException e) {
            // User typed something that is not a valid number
            tbAmountWords.setValue("");
        }
    }

    @Listen("onClick = #btnSubmit")
    public void onSubmit() {
        if (!validate()) return;

        Cheque c = new Cheque();
        c.setDrawerName(tbDrawerName.getValue().trim());
        c.setBankName(tbBankName.getValue().trim());
        c.setBranchName(tbBranchName.getValue().trim());
        c.setMicrCode(tbMicrCode.getValue().trim());
        c.setChequeNumber(tbChequeNumber.getValue().trim());
        c.setAccountNumber(tbAccountNumber.getValue().trim());
        c.setAmountDigits(tbAmountDigits.getValue().trim());
        c.setAmountWords(tbAmountWords.getValue().trim());

        if (dbChequeDate.getValue() != null) {
            // Format as yyyy-MM-dd (10 chars) — safely fits VARCHAR(20)
            c.setChequeDate(DATE_FMT.format(dbChequeDate.getValue()));
        }

        c.setPayeeName(tbPayeeName.getValue().trim());

        try {
            chequeService.submitCheque(c);
            showSuccess("Cheque submitted successfully.");
            clearForm();
            loadAllCheques();
        } catch (Exception e) {
            showError("Submission failed: " + e.getMessage());
        }
    }

    @Listen("onClick = #btnClear")
    public void onClear() {
        clearForm();
        lblMessage.setValue("");
    }

    @Listen("onClick = #btnDashboard")
    public void onDashboard() {
        Executions.sendRedirect("dashboard.zul");
    }

    private boolean validate() {
        if (tbDrawerName.getValue().trim().isEmpty()
                || tbChequeNumber.getValue().trim().isEmpty()
                || tbAmountDigits.getValue().trim().isEmpty()
                || dbChequeDate.getValue() == null) {
            showError("Please fill all required fields (marked with *).");
            return false;
        }
        return true;
    }

    private void clearForm() {
        tbDrawerName.setValue("");
        tbBankName.setValue("");
        tbBranchName.setValue("");
        tbMicrCode.setValue("");
        tbChequeNumber.setValue("");
        tbAccountNumber.setValue("");
        tbAmountDigits.setValue("");
        tbAmountWords.setValue("");
        dbChequeDate.setValue(null);
        tbPayeeName.setValue("");
    }

    private void loadAllCheques() {
        lbMyCheques.getItems().clear();
        List<Cheque> list = chequeService.getAllCheques();

        lblEmptyCheques.setVisible(list.isEmpty());

        for (Cheque c : list) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(c.getChequeNumber()));
            item.appendChild(new Listcell("Rs. " + c.getAmountDigits()));
            item.appendChild(new Listcell(c.getChequeDate() != null ? c.getChequeDate() : ""));

            Listcell statusCell = new Listcell();
            Label statusLabel = new Label(c.getStatus());
            statusLabel.setStyle(getStatusStyle(c.getStatus()));
            statusCell.appendChild(statusLabel);
            item.appendChild(statusCell);

            lbMyCheques.appendChild(item);
        }
    }

    private void showSuccess(String msg) {
        lblMessage.setValue(msg);
        lblMessage.setStyle("color:#2E7D32; font-size:13px; margin-top:8px;");
    }

    private void showError(String msg) {
        lblMessage.setValue(msg);
        lblMessage.setStyle("color:#D32F2F; font-size:13px; margin-top:8px;");
    }

    private String getStatusStyle(String status) {
        String base = "padding:3px 10px; border-radius:4px; font-size:12px; "
                    + "color:white; font-weight:bold;";
        if ("SUBMITTED".equals(status))        return base + "background:#F57C00;";
        if ("MAKER_APPROVED".equals(status))   return base + "background:#1976D2;";
        if ("CHECKER_APPROVED".equals(status)) return base + "background:#2E7D32;";
        if ("BATCHED".equals(status))          return base + "background:#7B1FA2;";
        if ("REJECTED".equals(status))         return base + "background:#D32F2F;";
        return base + "background:#888;";
    }

    // Converts number to Indian words — e.g. 150000 → "One Lakh Fifty Thousand"
    private String convertToWords(long number) {
        if (number == 0) return "Zero";

        String[] ones = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen",
            "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        };
        String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
        };

        if (number < 20) {
            return ones[(int) number];
        } else if (number < 100) {
            return tens[(int) (number / 10)]
                   + (number % 10 != 0 ? " " + ones[(int) (number % 10)] : "");
        } else if (number < 1000) {
            return ones[(int) (number / 100)] + " Hundred"
                   + (number % 100 != 0 ? " " + convertToWords(number % 100) : "");
        } else if (number < 100000) {
            return convertToWords(number / 1000) + " Thousand"
                   + (number % 1000 != 0 ? " " + convertToWords(number % 1000) : "");
        } else if (number < 10000000) {
            return convertToWords(number / 100000) + " Lakh"
                   + (number % 100000 != 0 ? " " + convertToWords(number % 100000) : "");
        } else {
            return convertToWords(number / 10000000) + " Crore"
                   + (number % 10000000 != 0 ? " " + convertToWords(number % 10000000) : "");
        }
    }
}