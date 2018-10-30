import java.util.ArrayList;
import javax.swing.JTable;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class DecissionTree {

	private JTable table;
	// Creating Bayese class to use methods in it
	// I'm using here method isColumnNumeric();
	// We can use Bayese methods because tables the same
	// We just sending this table to Bayese class
	private Bayese bay = null;
	private Instances isTrainingSet = null;
	private FastVector fvWekaAttributes = null;

	public DecissionTree(JTable table) {

		bay = new Bayese(table);

		// creating fastVectors of all Attributes
		fvWekaAttributes = new FastVector(table.getColumnCount());

		// filling fvWekaAttributes
		for (int i = 0; i < table.getColumnCount(); i++) {

			// If column is numeric I add blank attribute with column name
			if (bay.isColumnNumeric(table.getModel().getColumnName(i))) {
				fvWekaAttributes.addElement(new Attribute(table.getModel().getColumnName(i)));

			} else {

				// If column is not numeric I create new FastVector and
				// fill it with all possible distinct values from column
				ArrayList<String> distinctList = (ArrayList<String>) bay
						.getDistinctColumnItems(table.getModel().getColumnName(i));
				FastVector wrt = new FastVector(distinctList.size());
				for (int j = 0; j < distinctList.size(); j++) {
					wrt.addElement(distinctList.get(j));
				}

				// Then I add new Attribute with Vector data in it to fvWekaAttributes
				fvWekaAttributes.addElement(new Attribute(table.getModel().getColumnName(i), wrt));
			}

		}

		// Then I create a TrainingSet with name Rel, fvWekaAttributes Attributes and
		// capacity of table RowCount
		isTrainingSet = new Instances("Rel", fvWekaAttributes, table.getRowCount());

		// Adding our columns from table to the training Set
		for (int i = 0; i < table.getRowCount(); i++) {

			Instance iExample = new DenseInstance(table.getColumnCount());

			for (int j = 0; j < table.getColumnCount(); j++) {

				// If Column is numeric we need to set it value to the double
				// If not, we need to set it as String with +""
				if (bay.isColumnNumeric(table.getModel().getColumnName(j))) {
					iExample.setValue((Attribute) fvWekaAttributes.elementAt(j),
							Double.parseDouble(table.getModel().getValueAt(i, j) + ""));
				} else {
					iExample.setValue((Attribute) fvWekaAttributes.elementAt(j),
							table.getModel().getValueAt(i, j) + "");
				}
			}

			// add the instance
			isTrainingSet.add(iExample);

		}

	}

	public String predict(String[] wrt) {

		// Setting the Prediction field index
		isTrainingSet.setClassIndex(bay.getPredictionColumn(wrt));

		// Creating J48 tree
		J48 tree = new J48();

		// Creating option with -U that will create
		// an unpruned tree (full tree without Wekka reduction)
		String[] options = new String[1];
		options[0] = "-U";
		try {
			// Setting options
			tree.setOptions(options);

			// Building an classifier
			tree.buildClassifier(isTrainingSet);

		} catch (Exception e) {

			e.printStackTrace();
		}

		Instance iUse = new DenseInstance(wrt.length);
		
		
		for(int i = 0 ; i < wrt.length;i++) {
			if(isNumeric(wrt[i]))
				iUse.setValue((Attribute)fvWekaAttributes.elementAt(i), Double.parseDouble(wrt[i]));
			else {
				if(wrt[i].equals("Predict")||wrt[i].isEmpty()) {
					//iUse.setValue((Attribute)fvWekaAttributes.elementAt(i), null);
				}else
				iUse.setValue((Attribute)fvWekaAttributes.elementAt(i), wrt[i]);
			}
		}

		// Get the likelihood of each classes
		// fDistribution[0] is the probability of being “positive”
		// fDistribution[1] is the probability of being “negative”
		iUse.setDataset(isTrainingSet);
		String resultate = null;
		try {
			double[] fDistribution = tree.distributionForInstance(iUse);
			int index = 0;
			double max = fDistribution[0];
			
			for(int i = 0 ; i < fDistribution.length;i++) {
				if(max < fDistribution[i]) {
					max = fDistribution[i];
					index = i;
				}
				resultate = isTrainingSet.classAttribute().value(index);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tree.toString() + "\nYour result is: " + resultate;
	}
	
	public boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
