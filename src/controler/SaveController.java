package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Model;

public class SaveController implements ActionListener {
	private Model model;

	/**
	 * Crée une nouvelle instance de SaveController
	 * 
	 * @param m
	 *            model
	 */
	public SaveController(Model m) {
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
		model.save();
	}

}
