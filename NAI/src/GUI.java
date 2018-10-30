import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

public class GUI {

	private JFrame frame;
	private JTable table;
	private String[] footers;
	private DefaultTableModel model = new DefaultTableModel();
	private JPanel panel = new JPanel();
	private JMenuItem mntmPredictionForm;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("NAI");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter() {

					public String getDescription() {
						return "TXT base";
					}

					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						} else {
							String filename = f.getName().toLowerCase();
							return filename.endsWith(".txt");
						}
					}
				});
				int ret = fileChooser.showDialog(null, "Open file");
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					BufferedReader br = null;
					try {
						br = new BufferedReader(new FileReader(file));
					} catch (FileNotFoundException e1) {

						e1.printStackTrace();
					}

					String st;

					panel.removeAll();
					model.setColumnCount(0);
					model.setRowCount(0);
					mntmPredictionForm.setEnabled(true);
					
					try {
						
						footers = br.readLine().split(":");
						//panel.add(new JLabel("Field name"));
						//panel.add(new JLabel("Prefered value"));
						//panel.add(new JLabel("Probability %"));
						for (int i = 0; i < footers.length; i++) {
							// System.out.println("Here");
							model.addColumn(footers[i]);
							//panel.add(new JLabel(footers[i]));
							//panel.add(new JLabel(""));
							//panel.add(new JLabel(""));
						}

						model.setColumnIdentifiers(footers);
						br.readLine();
						while ((st = br.readLine()) != null)
							// System.out.println(st);
							if(!st.contains("?"))
							model.addRow(st.split(","));

					} catch (IOException e) {

						e.printStackTrace();
					}
					try {
						br.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
					frame.setSize(frame.getPreferredSize());
					
					frame.setTitle("NAI: " + file.getName());

				}
			}
		});
		mnFile.add(mntmOpen);

		//JMenuItem mntmSave = new JMenuItem("Save as");
		//mnFile.add(mntmSave);

		JMenu mnAnalys = new JMenu("Algorithms");
		menuBar.add(mnAnalys);

		JMenu mnChooseAnalysisMethod = new JMenu("Choose analysis method");
		mnAnalys.add(mnChooseAnalysisMethod);

		JRadioButtonMenuItem rdbtnmntmKmeans = new JRadioButtonMenuItem("Bayes");
		JRadioButtonMenuItem rdbtnmntmDecisionTree = new JRadioButtonMenuItem("Decision tree");
		//JRadioButtonMenuItem rdbtnmntmBayesse = new JRadioButtonMenuItem("K-means");
		ButtonGroup btgr = new ButtonGroup();

		rdbtnmntmKmeans.setSelected(true);
		btgr.add(rdbtnmntmKmeans);
		btgr.add(rdbtnmntmDecisionTree);
		//btgr.add(rdbtnmntmBayesse);

		mnChooseAnalysisMethod.add(rdbtnmntmKmeans);

		mnChooseAnalysisMethod.add(rdbtnmntmDecisionTree);

		//mnChooseAnalysisMethod.add(rdbtnmntmBayesse);

		JMenu mnPrediction = new JMenu("Prediction");
		menuBar.add(mnPrediction);

		mntmPredictionForm = new JMenuItem("Prediction form");
		mntmPredictionForm.setEnabled(false);
		mntmPredictionForm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PredictionForm(table,getSelectedButtonText(btgr));

			}

		});
		mnPrediction.add(mntmPredictionForm);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		table = new JTable(model);

		table.setModel(model);
		frame.getContentPane().add(table, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		frame.getContentPane().add(scrollPane);

		scrollPane.setRowHeaderView(panel);
		panel.setLayout(new GridLayout(0, 3, 0, 0));

	}
	
	public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }
}
