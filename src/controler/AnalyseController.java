package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.performance.ValidationCroisee;

/**
 * @author sais poux
 *
 */
public class AnalyseController implements ActionListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new ValidationCroisee();
	}

}
