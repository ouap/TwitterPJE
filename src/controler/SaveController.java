package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import model.Model;

public class SaveController implements ActionListener {
	Model model;

	public SaveController(Model m) {
		model = m;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		String nom = button.getText();

		if (nom.equals("Knn")) {
			model.save(model.getListTweetsSearch(), 1);

		} else if (nom.equals("Bayesienne")) {
			model.save(model.getListTweetsSearch(), 2);

		} else {
			model.save(model.getListTweetsSearch(), 3);
		}

	}
}
