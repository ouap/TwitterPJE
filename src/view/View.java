package view;

import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.Model;
import twitter4j.Status;
import twitter4j.TwitterException;
import controler.ComboboxListener;
import controler.ListController;
import controler.SaveController;
import controler.SearchController;

/**
 * Vue
 *
 */
public class View extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField searchField;
	public JLabel limitLabel, userContent;
	public JTextArea tweetContent;
	public JComboBox<String> comboBox;
	JList<String> list;
	Model model;

	/**
	 * Création de la fenetre
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public View(Model m) {
		// Model
		model = m;
		model.addObserver(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel labelRecherche = new JLabel("Entrez recherche :");
		labelRecherche.setBounds(152, 11, 112, 16);
		contentPane.add(labelRecherche);

		searchField = new JTextField();
		searchField.setBounds(288, 5, 151, 28);
		contentPane.add(searchField);
		searchField.setColumns(10);

		limitLabel = new JLabel("Limite : 180/180");
		limitLabel.setBounds(450, 12, 140, 16);
		contentPane.add(limitLabel);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		panel.setBounds(152, 39, 442, 320);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel labelText = new JLabel("Text :");
		labelText.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelText.setBounds(11, 71, 67, 25);
		panel.add(labelText);

		JLabel labelUser = new JLabel("User :");
		labelUser.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelUser.setBounds(11, 19, 72, 25);
		panel.add(labelUser);

		userContent = new JLabel("newlabel");
		userContent.setBounds(67, 23, 201, 16);
		panel.add(userContent);

		tweetContent = new JTextArea();
		tweetContent.setEditable(false);
		tweetContent.setLineWrap(true);
		tweetContent.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		tweetContent.setBounds(67, 71, 358, 96);
		panel.add(tweetContent);

		JButton btnKnn = new JButton("Knn");
		btnKnn.setBounds(151, 285, 117, 29);
		panel.add(btnKnn);

		JButton btnBayesienne = new JButton("Bayesienne");
		btnBayesienne.setBounds(280, 285, 117, 29);
		panel.add(btnBayesienne);

		JButton btnMotsPosneg = new JButton("Pos/Neg");
		btnMotsPosneg.setBounds(22, 285, 117, 29);
		panel.add(btnMotsPosneg);

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(
				new String[] { "0", "2", "4" }));
		comboBox.setBounds(67, 232, 72, 29);
		panel.add(comboBox);

		JLabel noteLabel = new JLabel("Note :");
		noteLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		noteLabel.setBounds(16, 237, 61, 16);
		panel.add(noteLabel);

		JSeparator separator = new JSeparator();
		separator.setBounds(11, 215, 117, 16);
		panel.add(separator);

		// Model List
		DefaultListModel<String> listModel = new DefaultListModel<String>();

		list = new JList<String>(listModel);
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new LineBorder(new Color(0, 0, 0)));

		scroll.setBounds(6, 42, 134, 313);

		scroll.getViewport().setView(list);
		contentPane.add(scroll);

		// On ajoute les controleurs
		SearchController searchControl = new SearchController(model);
		SaveController saveControl = new SaveController(model);
		ListController listControl = new ListController(model, this);
		ComboboxListener comboListener = new ComboboxListener(model, list,
				comboBox);
		list.addListSelectionListener(listControl);
		searchField.addActionListener(searchControl);
		btnKnn.addActionListener(saveControl);
		btnMotsPosneg.addActionListener(saveControl);
		btnBayesienne.addActionListener(saveControl);
		comboBox.addItemListener(comboListener);
		setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {

		DefaultListModel<String> modelTmp = (DefaultListModel<String>) list
				.getModel();
		modelTmp.removeAllElements();

		for (Status tweet : model.getTweets()) {
			modelTmp.addElement("" + tweet.getId());
		}

		list.setModel(modelTmp);

		try {
			limitLabel.setText(model.getLimit());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
