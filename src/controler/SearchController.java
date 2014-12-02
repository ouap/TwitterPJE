package controler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import model.Model;
import twitter4j.TwitterException;

public class SearchController implements ActionListener {
	private Model model;

	public SearchController(Model model) {
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String keyWord = ((JTextField) e.getSource()).getText();
		model.recherche = keyWord;

		if (!keyWord.equals("")) {
			model.doSearch(keyWord);
		}

		try {
			model.rateLimit = model.getLimit();
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
