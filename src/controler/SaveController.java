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
			model.save(model.listTweets, 1);

		} else if (nom.equals("Bayesienne")) {
			model.save(model.listTweets, 2);

		} else {
			model.save(model.listTweets, 3);
		}

	}
}
