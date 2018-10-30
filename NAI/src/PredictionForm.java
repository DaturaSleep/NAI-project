import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JTextArea;
import java.awt.Font;

public class PredictionForm {
	private JFrame frame;

	public PredictionForm(JTable table, String algorithm) {
		frame = new JFrame("Prediction Form");
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);

		textArea.setFont(new Font("Mongolian Baiti", Font.PLAIN, 18));
		JScrollPane jp = new JScrollPane(textArea);

		frame.getContentPane().add(jp, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		ArrayList<String> arr = null;
		for (int i = 0; i < table.getColumnCount(); i++) {
			arr = new ArrayList<>();
			panel.add(new JLabel(table.getColumnName(i) + ":"));

			JComboBox<String> box = new JComboBox<String>();
			box.addItem("Predict");

			for (int j = 0; j < table.getRowCount(); j++) {
				arr.add((String) table.getModel().getValueAt(j, i));
			}

			List<String> listDistinct = arr.stream().distinct().collect(Collectors.toList());
			if ((listDistinct.get(0).trim().matches("-?\\d+")) || (listDistinct.get(0).trim().matches("-?\\d+.\\d+"))) {
				panel.add(new JTextField());
			} else {

				for (int j = 0; j < listDistinct.size(); j++) {
					box.addItem(listDistinct.get(j));
				}
				panel.add(box);
			}
		}
		JButton btnPredict = new JButton("Predict");
		btnPredict.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String[] wrt = new String[panel.getComponentCount() / 2];
				int j = 0;
				for (int i = 0; i < panel.getComponentCount(); i++) {
					if (panel.getComponent(i) instanceof JTextField) {
						JTextField label = (JTextField) panel.getComponent(i);
						String labelText = label.getText();
						wrt[j] = labelText;

						j++;

					} else if (panel.getComponent(i) instanceof JComboBox) {
						JComboBox<?> box = (JComboBox<?>) panel.getComponent(i);
						String boxText = (String) box.getSelectedItem();
						wrt[j] = boxText;

						j++;

					}
				}
				switch (algorithm) {
				case "Bayes":

					Bayese bay = new Bayese(table);
					textArea.setText(bay.predict(wrt));
					break;
				case "Decision tree":
					DecissionTree tree = null;
					tree = new DecissionTree(table);
					textArea.setText(tree.predict(wrt));
				}

			}

		});

		frame.getContentPane().add(btnPredict, BorderLayout.SOUTH);

		frame.getRootPane().setDefaultButton(btnPredict);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

}
