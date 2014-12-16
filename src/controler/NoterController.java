package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;

import model.Model;

/**
 * @author sais poux
 *
 */
public class NoterController implements ActionListener {
	Model model;

	/**
	 * Crée une nouvelle instance de NoterController
	 * 
	 * @param m
	 *            model
	 */
	public NoterController(Model m) {
		model = m;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		String nom = button.getText();
		try {
			// Effectue la bonne notation en fonction du bouton selectionné
			if (nom.equals("Knn")) {
				model.noter(Model.getListTweets(), 2);
			} else if (nom.equals("Keyword")) {
				model.noter(Model.getListTweets(), 1);
			} else if (nom.equals("BayesUniPres")) {
				model.noter(Model.getListTweets(), 3);

			} else if (nom.equals("BayesUniFreq")) {
				model.noter(Model.getListTweets(), 4);

			} else if (nom.equals("BayesBigFreq")) {
				model.noter(Model.getListTweets(), 5);

			} else if (nom.equals("BayesBigPres")) {
				model.noter(Model.getListTweets(), 6);

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
