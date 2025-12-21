package com.myorg.views.generics.dialog;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class ConfirmWindow extends ConfirmDialog {

    public ConfirmWindow(String message, Runnable callbackYes) {
        this(
                "Are you sure you want to continue?",
                message,
                callbackYes);
    }

    public ConfirmWindow(Runnable callbackYes) {
        this(
                "Are you sure you want to continue?",
                "",
                callbackYes);
    }

    public ConfirmWindow(String title, String message, Runnable callbackYes) {
        setCloseOnEsc(true);
        setCancelable(true);

        setHeader(title);
        setText(message);

        setConfirmText("Yes");
        addConfirmListener(event -> {
            callbackYes.run();
            close();
        });

        setCancelText("No");
        addCancelListener(cancelEvent -> close());
    }
}
