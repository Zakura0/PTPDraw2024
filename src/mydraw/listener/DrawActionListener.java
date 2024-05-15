package mydraw.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mydraw.Draw;

public class DrawActionListener implements ActionListener {
    private String command;
    Draw application;

    public DrawActionListener(String cmd, Draw app) {
        command = cmd;
        application = app;
    }

    public void actionPerformed(ActionEvent e) {
        application.getWindow().doCommand(command);
    }
}
