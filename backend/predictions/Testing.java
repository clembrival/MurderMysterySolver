
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.Classifier;
import weka.core.Instances;


public class Testing
{
	public static void main(String[] args) throws Exception
	{
		// Loading the model
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("j48.model"));

		// Creating the classifier from the model
		Classifier classifier = (Classifier) ois.readObject();
		ois.close();

		// Loading the data to be classified
		Instances unlabelled = new Instances(new BufferedReader(new FileReader("test.arff")));
		unlabelled.setClassIndex(unlabelled.numAttributes() - 1);

		Instances labelled = new Instances(unlabelled);

		// Classifying each instance
		for(int i = 0; i < unlabelled.numInstances(); i++)
		{
			double label = classifier.classifyInstance(unlabelled.instance(i));
			labelled.instance(i).setClassValue(label);
		}

		// Outputting the classified instances to a file
		BufferedWriter writer = new BufferedWriter(new FileWriter("labelled.arff"));
		writer.write(labelled.toString());
		writer.newLine();
		writer.flush();
		writer.close();


	}
}