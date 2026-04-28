package com.cts.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class FormField extends HtmlMacroComponent {

    @Wire private Label   ff_label;
    @Wire private Label   ff_required;
    @Wire private Textbox ff_input;

    public FormField() {
        compose();
        Selectors.wireComponents(this, this, false);
    }

    // Usage:  <formfield fieldLabel="Drawer Name" required="true" />
    public void setFieldLabel(String label) {
        ff_label.setValue(label);
    }

    // If required="true", shows a red * next to the label
    public void setRequired(String required) {
        if ("true".equals(required)) {
            ff_required.setValue("*");
        }
    }

    // Set a placeholder text inside the textbox
    public void setPlaceholder(String placeholder) {
        ff_input.setPlaceholder(placeholder);
    }

    // Get what the user typed — used by the controller
    public String getValue() {
        return ff_input.getValue();
    }

    // Clear the textbox — used after form submit
    public void clear() {
        ff_input.setValue("");
    }
}