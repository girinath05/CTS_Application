package com.cts.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class StatusBadge extends HtmlMacroComponent {

    @Wire private Label sb_label;

    public StatusBadge() {
        compose();
        Selectors.wireComponents(this, this, false);
    }

    // Usage in .zul:  <statusbadge statusValue="SUBMITTED" />
    // Automatically picks the right color based on status text
    public void setStatusValue(String status) {
        sb_label.setValue(status);
        sb_label.setStyle(
            "padding:4px 12px; border-radius:20px; font-size:12px;" +
            "font-weight:700; color:white; display:inline-block;" +
            "background:" + getColor(status) + ";"
        );
    }

    private String getColor(String status) {
        if (status == null) return "#9CA3AF";
        switch (status) {
            case "SUBMITTED":        return "#F57C00";
            case "MAKER_APPROVED":   return "#1976D2";
            case "CHECKER_APPROVED": return "#2E7D32";
            case "BATCHED":          return "#7B1FA2";
            case "REJECTED":         return "#D32F2F";
            case "PENDING":          return "#F57C00";
            case "SENT":             return "#1A3C6E";
            case "APPROVED":         return "#2E7D32";
            default:                 return "#9CA3AF";
        }
    }
}