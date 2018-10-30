import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JTable;

public class Bayese {
	private JTable table;

	public Bayese(JTable table) {
		// Constructor
		this.table = table;

	}

	public String predict(String[] data) {
		// Getting prediction column index
		int predictionColumnIndex = getPredictionColumn(data);

		// This Array is used for saving unique values from column
		ArrayList<String> columnDistinct = null;

		ArrayList<Double> columnDistinctWages = new ArrayList<>();

		// I've made result as StringBuilder because operating with
		// String type takes a lot of time
		StringBuilder result = new StringBuilder("");

		// Checking if Prediction Column is not numeric
		// Currently there is no implementation for numeric predictions
		// So you can't predict numeric attributes
		// You only can predict a String columns
		if (!isColumnNumeric(table.getColumnName(predictionColumnIndex))) {

			// Getting All unique items from prediction Column
			// and filling the Array
			columnDistinct = (ArrayList<String>) getDistinctColumnItems(table.getColumnName(predictionColumnIndex));

			// =====================================Testing
			// Here is logic part starts
			// We will find allPosibility for all items from
			// columnDistinct Array
			for (int t = 0; t < columnDistinct.size(); t++) {

				// I'm using possibility to obtain data like this for ex. P(Yes|sunny) =
				// posibility
				double posibility = 0;

				// allPosibility are used as final possibility for current element
				// from columnDistinct
				// Final posibility = P(...) * P(...|...) * ...
				double allPosibility = 1;

				// Count of all elements that equals columnDistinct.get(t)
				// we need to count it to get first P(...)
				// then we divide mainCount on RowCount
				// And we will get first possibility for P(...) = mainCount/RowCount
				int mainCount = 0;

				// Counting how much elements in table
				for (int i = 0; i < table.getModel().getRowCount(); i++) {
					String wrt = (String) table.getModel().getValueAt(i, predictionColumnIndex);
					if (wrt.equals(columnDistinct.get(t))) {
						mainCount++;
					}

				}

				// Here I have NORMALIZATION for P()
				// dividend is mainCount + 1
				// I can't change mainCount because I need to use it
				// When I'll compute a P(...|...)
				double dividend = mainCount + 1;

				// divider is my divider
				// divider = RowCount + arnosc (unique elements from column)
				double divider = table.getModel().getRowCount()
						+ getDistinctColumnItems(table.getColumnName(predictionColumnIndex)).size();

				// Here I calculate a possibility for main item
				// For first P(...)
				posibility = dividend / divider;

				// count for P(...|...) elements
				// I created this count because possibility
				// for P(...|...) = count/mainCount
				int count = 0;

				// This "for" is for operating with columns
				/*
				 * -> -> -> -> -> -> -> column1 column2 | row1 data1 data2 V
				 * 
				 * | row2 data3 data4 V
				 */
				for (int i = 0; i < table.getModel().getColumnCount(); i++) {

					// I don't need a possibility for predictionColumn because I already have it
					// So I just skip if i == PredictionColumnIndex
					if (i == predictionColumnIndex) {

					}
					// If column is String data column I'm doing standard
					// bayesian computation
					else if (!isColumnNumeric(table.getModel().getColumnName(i))) {

						for (int j = 0; j < table.getModel().getRowCount(); j++) {

							// I have created this string to see if cursor is on the position
							// With currently prediction element
							String wrt = (String) table.getModel().getValueAt(j, predictionColumnIndex);

							// cursor is used for getting element on current position
							String cursor = (String) table.getModel().getValueAt(j, i);

							// our count is dividend so when cursor == data[i] and on the
							// same position with wrt, we do count++
							if (cursor.equals(data[i]) && wrt.equals(columnDistinct.get(t))) {
								count++;

							}
						}

						// process of NORMALIZATION for P(...|...)
						// divident = count+1 and divider = mainCount + arnosc(size of distinct
						// elements)
						dividend = (count + 1);
						divider = (mainCount + getDistinctColumnItems(table.getModel().getColumnName(i)).size());

						// setting count to 0 for further use
						count = 0;

						// multiplying allPosibility with new possibility
						// P()*P(...|...)*...
						allPosibility = allPosibility * (dividend / divider);

						// appending string with results to my StringBuilder
						result.append((dividend / divider) + " : posibility for " + data[i] + " " + dividend + "/"
								+ divider + " : " + columnDistinct.get(t) + "\n");

					}
					// next if for numeric columns, here i use density probability function
					// like temperature, humidity, etc.
					// more about function you can find on
					// https://en.wikipedia.org/wiki/Probability_density_function
					// I'm using this formula because I've read example with it
					else if (isColumnNumeric(table.getModel().getColumnName(i))) {

						// in this formula we need midleValue
						double midleValue = 0;

						// initializing a beta that going to be used further
						double beta = 0;

						// CHANGED!!
						// seting divider to 0
						divider = 0;

						// in this "for" I adding Value from position where row = currently element x
						// where P(int|x)
						// also I count divider because middleValue = summOfAll/countOfAll
						for (int j = 0; j < table.getModel().getRowCount(); j++) {
							String wrt = (String) table.getModel().getValueAt(j, predictionColumnIndex);
							if (wrt.equals(columnDistinct.get(t))) {
								midleValue = midleValue
										+ Double.parseDouble((String) table.getModel().getValueAt(j, i));
								divider++;
							}
						}

						// Computing middleValue
						midleValue = midleValue / divider;

						// Further code is fully density function implementation
						for (int j = 0; j < table.getModel().getRowCount(); j++) {
							String wrt = (String) table.getModel().getValueAt(j, predictionColumnIndex);
							if (wrt.equals(columnDistinct.get(t))) {
								beta = beta + Math.pow(
										(Double.parseDouble((String) table.getModel().getValueAt(j, i)) - midleValue),
										2);

							}
						}

						beta = beta / (divider - 1);

						double powOfE = Math.pow((Double.parseDouble(data[i]) - midleValue), 2);

						powOfE = powOfE / (2 * beta);
						powOfE = -powOfE;

						// Setting possibility for continuous numbers
						posibility = (1 / (Math.sqrt(beta) * (Math.sqrt(2 * Math.PI)))) * (Math.pow(Math.E, powOfE));

						// appending it to the result
						result.append(
								posibility + " : posibility for " + data[i] + " : " + columnDistinct.get(t) + "\n");

						// Multiplying allPosibility by current posibility
						// The same we've been doing for String data
						// P()*P(...|...)*...
						allPosibility = allPosibility * posibility;

					}

				}

				// appending result for StringBuilder
				result.append("ALLPOSIBILITY: " + allPosibility + " for Value " + columnDistinct.get(t) + "\n\n");

				columnDistinctWages.add(allPosibility);

			}

		} else {

		}

		// Finding the best value and adding it to the StringBuilder
		int index = 0;
		for (int i = 1; i < columnDistinctWages.size(); i++) {
			if (columnDistinctWages.get(i) > columnDistinctWages.get(index)) {
				index = i;
			}
		}
		result.append("Predicted value - \"" + columnDistinct.get(index) + "\" With probability: "
				+ columnDistinctWages.get(index));
		return result.toString() + "\n";
	}

	// Method for getting Prediction column
	// Prediction column - field which in
	// prediction form equals "Prediction"
	public int getPredictionColumn(String[] data) {

		for (int i = 0; i < data.length; i++) {
			if (data[i].equals("Predict") || data[i].isEmpty()) {
				return i;

			}

		}

		return 0;

	}

	// Method for Getting a unique items from column
	public List<String> getDistinctColumnItems(String columnName) {
		ArrayList<String> arr = new ArrayList<>();
		int columnIndex = table.getColumn(columnName).getModelIndex();

		for (int j = 0; j < table.getRowCount(); j++) {
			arr.add((String) table.getModel().getValueAt(j, columnIndex));
		}

		List<String> listDistinct = arr.stream().distinct().collect(Collectors.toList());
		return listDistinct;
	}

	// Method for checking a column for numeric value
	public boolean isColumnNumeric(String columnName) {

		String wrt = (String) table.getModel().getValueAt(0, table.getColumn(columnName).getModelIndex());
		if ((wrt.trim().matches("-?\\d+")) || (wrt.trim().matches("-?\\d+.\\d+"))) {
			return true;
		} else {

			return false;
		}

	}

}
