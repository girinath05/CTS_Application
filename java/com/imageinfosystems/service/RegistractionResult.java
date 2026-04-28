package com.imageinfosystems.service;

public class RegistractionResult {

    private final boolean ok;
    private final String message;

    public RegistractionResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public static RegistractionResult success(String msg) {
        return new RegistractionResult(true, msg);
    }

    public static RegistractionResult fail(String msg) {
        return new RegistractionResult(false, msg);
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }
}