package com.cts.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;

public class ApproveRejectButtons extends HtmlMacroComponent {

    @Wire private Button arb_btnApprove;
    @Wire private Button arb_btnReject;

    public ApproveRejectButtons() {
        compose();
        Selectors.wireComponents(this, this, false);
    }

    // The controller passes its own listener here for Approve
    // Usage:  approveRejectButtons.setOnApprove(event -> onApprove())
    public void setOnApprove(EventListener<Event> listener) {
        arb_btnApprove.addEventListener(Events.ON_CLICK, listener);
    }

    // The controller passes its own listener here for Reject
    public void setOnReject(EventListener<Event> listener) {
        arb_btnReject.addEventListener(Events.ON_CLICK, listener);
    }

    // Disable approve button (e.g. when checklist not completed)
    public void setApproveDisabled(boolean disabled) {
        arb_btnApprove.setDisabled(disabled);
    }
}