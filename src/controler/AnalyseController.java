package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.performance.ValidationCroisee;

public class AnalyseController implements ActionListener {

	public AnalyseController() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new ValidationCroisee();
	}

}
