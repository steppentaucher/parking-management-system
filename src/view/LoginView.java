package view;

import controller.PlattformManager;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
	private PlattformManager manager;
	private MainFrame mainFrame;
	private JTextField txtEmail;
	private JButton btnLogin;
	private JButton btnRegistrieren;

	public LoginView(MainFrame mf, PlattformManager pm) {
		this.mainFrame = mf;
		this.manager = pm;

	}

	private void handleLogin() {

	}
}
