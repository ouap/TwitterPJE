package controler;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JList;

import model.Model;
import view.View;

/**
 * @author sais poux
 *
 */
public class ComboboxController implements ItemListener {
	private Model model;
	JList<String> listTweets;
	JComboBox combobox;
	View vue;

	/**
	 * Creer une nouvelle instance de ComboboxController
	 * 
	 * @param m
	 *            model
	 * @param vue
	 *            vue
	 */
	public ComboboxController(Model m, View vue) {
		model = m;
		this.vue = vue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			@SuppressWarnings("unchecked")
			int item = Integer.parseInt(((String) e.getItem()));
			model.getListTweets().get(vue.list.getSelectedIndex())
					.setNote(item);
		}
	}
}
