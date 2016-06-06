
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class AnalyzeMain 
{
	public static void main(String[] args) 
	{
		try
		{
			// Read data from file
			Instances data = DataSource.read("..//datasets//data.arff");
			
			// Create a copy of the data set which string attributes 
			// are converted into word vectors
			StringToWordVector filter = new StringToWordVector();
			filter.setInputFormat(data);
			Instances new_data = Filter.useFilter(data, filter);
			
			// Set index of attribute to predict
			new_data.setClassIndex(data.numAttributes() - 1);
			
			Evaluation eval = new Evaluation(new_data);
			
			// Algorithm to be used
			NaiveBayes bayes = new NaiveBayes();
			
			// Usisng cross validation to evaluate the classifier
			eval.crossValidateModel(bayes, new_data, 20, new Random(1));
			
			System.out.println(eval.toSummaryString("\nResults\n\n", false));
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
