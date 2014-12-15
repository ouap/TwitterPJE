package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import model.TweetInfos;
import twitter4j.TwitterException;
import controler.AnalyseController;
import controler.CheckBoxController;
import controler.ComboboxController;
import controler.ListController;
import controler.NoterController;
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
	public JLabel limitLabel, userContent, iDContent;
	public JTextArea tweetContent;
	public JComboBox<String> comboBox;
	public JList<String> list;
	Model model;

	/**
	 * Création de la fenetre
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public View(Model m) {
		// Model
		model = m;
		model.addObserver(this);
		model.chargerBaseTweet();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 695, 465);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel labelRecherche = new JLabel("Entrez recherche :");
		labelRecherche.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
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
		panel.setBounds(152, 39, 525, 386);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel labelId = new JLabel("ID:");
		labelId.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelId.setBounds(56, 36, 68, 16);
		panel.add(labelId);

		iDContent = new JLabel("");
		iDContent.setBounds(141, 36, 200, 16);
		panel.add(iDContent);

		JLabel labelText = new JLabel("Text :");
		labelText.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelText.setBounds(56, 103, 67, 25);
		panel.add(labelText);

		JLabel labelUser = new JLabel("User :");
		labelUser.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelUser.setBounds(56, 66, 72, 25);
		panel.add(labelUser);

		userContent = new JLabel("");
		userContent.setBounds(140, 70, 201, 16);
		panel.add(userContent);

		tweetContent = new JTextArea();
		tweetContent.setEditable(false);
		tweetContent.setLineWrap(true);
		tweetContent.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		tweetContent.setBounds(140, 107, 358, 96);
		panel.add(tweetContent);

		JButton btnKnn = new JButton("Knn");
		btnKnn.setBounds(270, 273, 117, 29);
		panel.add(btnKnn);

		JButton btnBayesienne = new JButton("Bayesienne");
		btnBayesienne.setBounds(399, 273, 117, 29);
		panel.add(btnBayesienne);

		JButton btnMotsPosneg = new JButton("Pos/Neg");
		btnMotsPosneg.setBounds(141, 273, 117, 29);
		panel.add(btnMotsPosneg);

		JButton btnChargerBase = new JButton("Charger Base");
		btnChargerBase.setBounds(6, 7, 117, 29);
		contentPane.add(btnChargerBase);

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(
				new String[] { "0", "2", "4" }));
		comboBox.setBounds(141, 232, 72, 29);
		panel.add(comboBox);

		JLabel noteLabel = new JLabel("Note :");
		noteLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		noteLabel.setBounds(56, 237, 61, 16);
		panel.add(noteLabel);

		JSeparator separator = new JSeparator();
		separator.setBounds(56, 204, 117, 16);
		panel.add(separator);

		JLabel labelClassement = new JLabel("Classement :");
		labelClassement.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelClassement.setBounds(31, 278, 97, 16);
		panel.add(labelClassement);

		JButton btnAnalyse = new JButton("Analyse");
		btnAnalyse.setBounds(141, 333, 117, 29);
		panel.add(btnAnalyse);

		JLabel lblStats = new JLabel("Stats :");
		lblStats.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblStats.setBounds(31, 338, 61, 16);
		panel.add(lblStats);

		// Model List
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		List<TweetInfos> baseTweet = model.getBase();
		if (!baseTweet.isEmpty()) {
			int i = 1;
			for (TweetInfos tweet : baseTweet)
				listModel.addElement("Tweet nº" + i++);
		}

		list = new JList<String>(listModel);
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new LineBorder(new Color(0, 0, 0)));

		scroll.setBounds(6, 42, 140, 383);

		scroll.getViewport().setView(list);
		contentPane.add(scroll);

		JCheckBox checkProxy = new JCheckBox("Proxy");
		checkProxy.setBounds(566, 7, 112, 23);
		contentPane.add(checkProxy);

		// On ajoute les controleurs
		SearchController searchControl = new SearchController(model);
		NoterController saveControl = new NoterController(model);
		ListController listControl = new ListController(model, this);
		ComboboxController comboControl = new ComboboxController(model, this);
		CheckBoxController checkControl = new CheckBoxController(model,
				checkProxy);
		AnalyseController analyseControl = new AnalyseController();
		list.addListSelectionListener(listControl);
		searchField.addActionListener(searchControl);
		btnChargerBase.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.setListTweets(model.getBase());
				update(model, new Object());
			}
		});
		btnKnn.addActionListener(saveControl);
		btnMotsPosneg.addActionListener(saveControl);
		btnBayesienne.addActionListener(saveControl);
		comboBox.addItemListener(comboControl);
		checkProxy.addItemListener(checkControl);
		btnAnalyse.addActionListener(analyseControl);
		setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {
		List<TweetInfos> listTweet = model.getListTweets();
		DefaultListModel<String> modelTmp = (DefaultListModel<String>) list
				.getModel();
		modelTmp.clear();

		for (int i = 1; i < listTweet.size(); i++) {
			modelTmp.addElement("Tweet nº" + i);
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
