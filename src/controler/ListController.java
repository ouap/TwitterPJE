package controler;

import java.util.List;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Model;
import twitter4j.Status;
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
		List<Status> tweets = model.getTweets();
		int index = ((JList) e.getSource()).getSelectedIndex();
		vue.userContent.setText(tweets.get(index).getUser().getName());
		vue.tweetContent.setText(tweets.get(index).getText());
		vue.comboBox.setSelectedItem(model.noteTweets[index]);
	}
}
