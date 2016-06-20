
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.j48.NBTreeClassifierTree;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;


public class AnalyzeMain 
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
			
			Evaluation eval = new Evaluation(new_data);
			
			ZeroR classifier = new ZeroR();
			eval.crossValidateModel(classifier, new_data, 20, new Random(1));
			
			System.out.println(eval.toSummaryString("\nResults\n\n", false));
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
