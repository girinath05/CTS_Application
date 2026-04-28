package com.cts.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class ModuleCard extends HtmlMacroComponent {

    @Wire private Div   mc_card;
    @Wire private Label mc_icon;
    @Wire private Label mc_title;
    @Wire private Label mc_desc;

    public ModuleCard() {
        compose();
        Selectors.wireComponents(this, this, false);
        // No click listener here.
        // The parent div in dashboard.zul has the id that DashboardController listens to.
        // Clicking the card bubbles up to that parent div automatically.
    }

    public void setModuleIcon(String icon) {
        mc_icon.setValue(icon);
    }

    public void setModuleTitle(String title) {
        mc_title.setValue(title);
    }

    public void setModuleDesc(String desc) {
        mc_desc.setValue(desc);
    }

    public void setIconBg(String bg) {
        mc_icon.setStyle(
            "background:" + bg + ";" +
            "width:60px; height:60px; border-radius:14px;" +
            "display:flex; align-items:center; justify-content:center;" +
            "font-size:28px; color:white; flex-shrink:0;"
        );
    }
}