package com.cts.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;

public class ReturnCodePanel extends HtmlMacroComponent {

    @Wire private Div      rcp_wrapper;
    @Wire private Combobox rcp_combo;

    public ReturnCodePanel() {
        compose();
        Selectors.wireComponents(this, this, false);
    }

    // Show the panel (call this when Reject button is clicked)
    public void show() {
        rcp_wrapper.setVisible(true);
    }

    // Hide the panel (call this after rejection is done)
    public void hide() {
        rcp_wrapper.setVisible(false);
        rcp_combo.setValue("");
    }

    // Get selected return code — used by controller before calling reject
    public String getSelectedCode() {
        return rcp_combo.getValue();
    }

    // Check if a code was actually selected
    public boolean isCodeSelected() {
        String val = rcp_combo.getValue();
        return val != null && !val.trim().isEmpty();
    }
}