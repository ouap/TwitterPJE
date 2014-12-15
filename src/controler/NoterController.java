package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;

import model.Model;

public class NoterController implements ActionListener {
	Model model;

	public NoterController(Model m) {
		model = m;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		String nom = button.getText();
		try {

			if (nom.equals("Knn")) {
				model.noter(model.getListTweets(), 2);
			} else if (nom.equals("Keyword")) {
				model.noter(model.getListTweets(), 1);
			} else if (nom.equals("BayesUniPres")) {
				model.noter(model.getListTweets(), 3);

			} else if (nom.equals("BayesUniFreq")) {
				model.noter(model.getListTweets(), 4);

			} else if (nom.equals("BayesBigFreq")) {
				model.noter(model.getListTweets(), 5);

			} else if (nom.equals("BayesBigPres")) {
				model.noter(model.getListTweets(), 6);

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
