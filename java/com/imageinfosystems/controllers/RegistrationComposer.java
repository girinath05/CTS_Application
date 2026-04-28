package com.imageinfosystems.controllers;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import com.imageinfosystems.dao.UserDaoImpl;
import com.imageinfosystems.service.RegisterRequest;
import com.imageinfosystems.service.RegistractionResult;
import com.imageinfosystems.service.UserService;
import com.imageinfosystems.service.UserServiceImpl;


public class RegistrationComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Textbox tbName;

    @Wire
    private Textbox tbEmail;

    @Wire
    private Textbox tbPassword;

    @Wire
    private Textbox tbConfirm;

    @Wire
    private Radiogroup rgGender;

    @Wire
    private Combobox cbCountry;

    @Wire
    private Label lblMsg;

    private final UserService userService =
        new UserServiceImpl(new UserDaoImpl());

    @Listen("onClick = #btnRegister")
    public void register() {

        String gender = "";

        Radio r = rgGender.getSelectedItem();
        if (r != null) {
            gender = r.getLabel();
        }

        String country = "";

        Comboitem c = cbCountry.getSelectedItem();
        if (c != null) {
            country = c.getLabel();
        }

        RegisterRequest req = new RegisterRequest(
            tbName.getValue(),
            tbEmail.getValue(),
            tbPassword.getValue(),
            tbConfirm.getValue(),
            gender,
            country
        );

        RegistractionResult result = userService.register(req);

        lblMsg.setValue(result.getMessage());
        
        if(result.isOk()) {
        	Messagebox.show("Registration Successfull!");
        	clear();
        }
    }

    @Listen("onClick = #btnClear")
    public void clear() {

        tbName.setValue("");
        tbEmail.setValue("");
        tbPassword.setValue("");
        tbConfirm.setValue("");
        rgGender.setSelectedItem(null);
        cbCountry.setSelectedItem(null);
        lblMsg.setValue("");
    }
}