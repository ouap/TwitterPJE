package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Model;

public class SaveController implements ActionListener {
	Model model;

	public SaveController(Model m) {
		model = m;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.save(model.listTweets);

	}

}
