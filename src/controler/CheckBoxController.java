package controler;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JCheckBox;

import model.Model;

public class CheckBoxController implements ItemListener {
	Model model;
	JCheckBox check;

	public CheckBoxController(Model m, JCheckBox checkProxy) {
		model = m;
		check = checkProxy;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		try {
			if (check.isSelected()) {
				model.proxyHandler(1);
			} else {
				model.proxyHandler(0);
			}

		} catch (IOException e1) {
			System.out.println("Probleme de lecture ecriture");
		}

	}
}
