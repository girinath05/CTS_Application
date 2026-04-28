package com.cts.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class ReadOnlyField extends HtmlMacroComponent {

    @Wire private Label rof_label;
    @Wire private Label rof_value;

    public ReadOnlyField() {
        compose();
        Selectors.wireComponents(this, this, false);
    }

    // Usage:  <readonlyfield fieldLabel="Bank Name" fieldValue="SBI" />
    public void setFieldLabel(String label) {
        rof_label.setValue(label);
    }

    public void setFieldValue(String value) {
        // If value is null or empty, show a dash instead of blank
        rof_value.setValue((value != null && !value.isEmpty()) ? value : "—");
    }
}