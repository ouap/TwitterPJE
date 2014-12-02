package controler;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JList;

import model.Model;

public class ComboboxListener implements ItemListener {
	private Model model;
	JList<String> listTweets;
	JComboBox combobox;

	public ComboboxListener(Model m, JList<String> l, JComboBox combo) {
		model = m;
		listTweets = l;

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			@SuppressWarnings("unchecked")
			int item = Integer.parseInt(((String) e.getItem()));

			model.getNoteTweets().remove(listTweets.getSelectedIndex());
			model.getNoteTweets().add(listTweets.getSelectedIndex(), item);

		}
	}
}
