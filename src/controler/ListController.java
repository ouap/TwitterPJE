package controler;

import java.awt.Color;
import java.util.List;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Model;
import model.TweetInfos;
import view.View;

/**
 * @author sais poux
 *
 */
public class ListController implements ListSelectionListener {
	private Model model;
	View vue;

	/**
	 * Creé une nouvelle instance de ListController
	 * 
	 * @param m
	 *            model
	 * @param v
	 *            vue
	 */
	public ListController(Model m, View v) {
		model = m;
		vue = v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		List<TweetInfos> tweets = Model.getListTweets();
		int index = ((JList<?>) e.getSource()).getSelectedIndex();

		if (!e.getValueIsAdjusting()) {
			// Changement des information du tweet sélectionné
			vue.iDContent.setText("" + tweets.get(index).getId());
			vue.userContent.setText(tweets.get(index).getUser());
			vue.tweetContent.setText(tweets.get(index).getTweet());

			// Changement de la note dans la combobox et changement de
			// background en conséquence
			if (Model.getListTweets().get(index).getNote() == 0) {
				vue.comboBox.setSelectedIndex(0);
				vue.tweetContent.setBackground(Color.red);

			} else if (Model.getListTweets().get(index).getNote() == 2) {
				vue.comboBox.setSelectedIndex(1);
				vue.tweetContent.setBackground(Color.blue);

			} else {
				vue.tweetContent.setBackground(Color.green);
				vue.comboBox.setSelectedIndex(2);

			}
		}
	}
}
