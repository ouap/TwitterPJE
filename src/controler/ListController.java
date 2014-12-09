package controler;

import java.util.List;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Model;
import model.TweetInfos;
import view.View;

public class ListController implements ListSelectionListener {
	private Model model;
	View vue;

	public ListController(Model m, View v) {
		model = m;
		vue = v;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		List<TweetInfos> tweets = model.getListTweets();
		int index = ((JList<?>) e.getSource()).getSelectedIndex();

		if (!e.getValueIsAdjusting()) {
			vue.userContent.setText(tweets.get(index).getUser());
			vue.tweetContent.setText(tweets.get(index).getTweet());
			// Changement de la note dans la combobox

			if (model.getListTweets().get(index).getNote() == 0) {
				vue.comboBox.setSelectedIndex(0);

			} else if (model.getListTweets().get(index).getNote() == 2) {
				vue.comboBox.setSelectedIndex(1);
			} else {
				
				vue.comboBox.setSelectedIndex(2);
			}
		}
	}
}
