
import java.util.Random;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

public class Training 
{
	public static void main(String[] args) 
	{
		try
		{
			// Loading the data
			Instances data = DataSource.read("data.arff");
			
			/* Many algorithms can't handle string attributes (book title)
			 * so we use a filter to transform the string attribute
			 * into a nominal one. */
			StringToNominal filter = new StringToNominal();
			filter.setAttributeRange("1");
			filter.setInputFormat(data);
			
			// Applying the filter to the data
			Instances new_data = Filter.useFilter(data, filter);
			
			// Setting the index of the class to be predicted
			new_data.setClassIndex(data.numAttributes() - 1);
						
			// Algorithm to be used
			J48 j48 = new J48();

			// Training the classifier
			j48.buildClassifier(new_data);

			// Outputting the classifier to a file
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("j48.model"));
			oos.writeObject(j48);
			oos.flush();
			oos.close();
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
