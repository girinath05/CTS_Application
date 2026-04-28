package com.cts.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class LoginController extends SelectorComposer<Component> {

    @Wire private Textbox tbUsername;
    @Wire private Textbox tbPassword;
    @Wire private Label   lblError;

    @Listen("onClick = #btnLogin")
    public void onLogin() {
        String username = tbUsername.getValue().trim();
        String password = tbPassword.getValue().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setValue("Please enter username and password.");
            return;
        }

        if ("admin".equals(username) && "admin".equals(password)) {
            Sessions.getCurrent().setAttribute("loggedUser", username);
            Executions.sendRedirect("dashboard.zul");
        } else {
            lblError.setValue("Invalid username or password.");
        }
    }
}