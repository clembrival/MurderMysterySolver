import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.Evaluation;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import weka.core.converters.ConverterUtils.DataSource;


public class WekaTester
{
    /* Attributes of the dataset. */
    private static final String[] LOCATIONS = {"unknown", "UK", "International"};
    private static final String[] POVS = {"unknown", "First", "Third"};
	private static final String[] DETECTIVES = {"unknown","Hercule Poirot", "Tommy and Tuppence", "Colonel Race",
            "Superintendent Battle", "Miss Marple", "Mystery novel"};
    private static final String[] WEAPONS = {"unknown","Poison", "Stabbing", "Accident", "Shooting", "Strangling",
            "Concussion", "ThroatSlit", "None", "Drowning"};

    /* Indices of the attributes of the instances. */
    private static final int TITLE = 0, YEAR = 1, LOCATION = 2, POV = 3,
            DETECTIVE = 4, WEAPON = 5, AVG_RATINGS = 6, NUM_RATINGS = 7;


    /* Creates a dataset from console input. */
    public Instances createDataset(String stringAttributes) throws Exception
    {  	
    	Instances data = buildAttributes();
    	double[] values = new double[data.numAttributes()];

    	Scanner scanner = new Scanner(System.in);

    	System.out.println("Create new instance? (y/n)");
    	String answer = scanner.nextLine();

    	while ("y".equals(answer))
    	{
    		System.out.println("Enter the title: ");
	    	values[TITLE] = data.attribute(TITLE).addStringValue(scanner.nextLine());
	    	System.out.println("Enter the year of publication: ");
	        values[YEAR] = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter the setting (UK or International): ");
	        values[LOCATION] = data.attribute(2).indexOfValue(scanner.nextLine());
            System.out.println("Enter the point of view (First or Third): ");
	        values[POV] = data.attribute(3).indexOfValue(scanner.nextLine());
            System.out.println("Enter the detective: ");
	        values[DETECTIVE] = data.attribute(4).indexOfValue(scanner.nextLine());
            System.out.println("Enter the ratings average: ");
	        values[AVG_RATINGS] = Double.parseDouble(scanner.nextLine());
            System.out.println("Enter the number of ratings: ");
	        values[NUM_RATINGS] = Integer.parseInt(scanner.nextLine());;

	        data.add(new DenseInstance(1.0, values));
	        System.out.println("Row added\n");

	        System.out.println("Create new instance? (y/n)");
    		answer = scanner.nextLine();
    	}

    	if(data.numInstances() == 0)
    		return null;

        System.out.println("Data filtered\n");

        return filterData(data, stringAttributes);	
    }


    /* Loads a dataset from a .arff file. */
    public Instances loadDataFile(String dataFile, String stringAttributes) throws Exception
    {
    	// Loading the data
		Instances data = DataSource.read(dataFile);

		data = filterData(data, stringAttributes);

		System.out.println("Loaded and filtered data from " + dataFile + "\n");

		return data;
    }


    public Instances filterData(Instances data, String stringAttributes) throws Exception
    {
        /* Many algorithms can't handle string attributes (book title for example)
         * so we use a filter to transform the string attribute into a nominal one. */
        StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices(stringAttributes);
        filter.setInputFormat(data);
            
        // Applying the filter to the data
        Instances new_data = Filter.useFilter(data, filter);

        return new_data;
    }

    /* Loads a model from a file and returns the corresponding classifier. */
    public Classifier loadModel(String inputName) throws Exception
    {
        // Loading the model
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputName));
        
        // Creating the classifier from the model
        Classifier classifier = (Classifier) ois.readObject();
        ois.close();

        System.out.println("Produced classifier from " + inputName + "\n");

        return classifier;
    }


    /* Creates a .model file from a classifier. */
    public void outputModel(Classifier classifier, String outputName) throws Exception
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputName));
        oos.writeObject(classifier);
        oos.flush();
        oos.close();

        System.out.println("Produced " + outputName + "\n");
    }


    /* Creates and trains a classifier on a dataset and outputs the model to a file. */
    public Classifier train(Instances data, int classIndex) throws Exception
    {
    	// Setting the index of the class to be predicted
		data.setClassIndex(classIndex);
					
		// Algorithm to be used
		HoeffdingTree classifier = new HoeffdingTree();

		// Training the classifier
		classifier.buildClassifier(data);

		// Outputting the model to a file.
		outputModel(classifier, "classifier.model");

        System.out.println("Classifier trained\n");

		return classifier;
    }


    /* Classifies one instance from console input and loading the model from a file. */
    public Instance predictSingle(Instances data, int instanceIndex, 
            Classifier classifier, int classIndex) throws Exception
    {
        // Loading the data to be classified
        data.setClassIndex(classIndex);

        // Classifying the given instance
        double label = classifier.classifyInstance(data.instance(instanceIndex));
        data.instance(instanceIndex).setClassValue(label);

        System.out.println("Predicted murder weapon: " 
        	+ data.instance(instanceIndex).classAttribute().value(0) + "\n");

        return data.instance(instanceIndex);
    }


    public Instances predictAll(Instances data, Classifier classifier,
            int classIndex) throws Exception
    {
        // Loading the data to be classified
        data.setClassIndex(classIndex);

        for(int i = 0; i < data.numInstances(); i++)
        {
            double label = classifier.classifyInstance(data.instance(i));
            data.instance(i).setClassValue(label);
            System.out.println("Predicted murder weapon for instance " + i + 1
                + ": " + data.instance(i).classAttribute().value(0) + "\n");
        }
        return data;
    }


    /* Sets up the attributes of the dataset. */
    private Instances buildAttributes()
    {
    	// Setting the string attribute
        Attribute titleAtt = new Attribute("title", (ArrayList<String>) null);

        // Setting the numeric attributes
        Attribute yearAtt = new Attribute("year");
        Attribute avgRatingsAtt = new Attribute("average_ratings");
        Attribute numRatingsAtt = new Attribute("number_of_ratings");

        // Setting the nominal attributes
        List<String> locationValues = new ArrayList<String>(2);
        Collections.addAll(locationValues, LOCATIONS);
        Attribute locationAtt = new Attribute("location", locationValues);

        List<String> povValues = new ArrayList<String>(2);
        Collections.addAll(povValues, POVS);
        Attribute povAtt = new Attribute("point_of_view", povValues);

        List<String> detectiveValues = new ArrayList<String>(6);
        Collections.addAll(detectiveValues, DETECTIVES);
        Attribute detectiveAtt = new Attribute("detective", detectiveValues);

        List<String> weaponValues = new ArrayList<String>(9);
        Collections.addAll(weaponValues, WEAPONS);
        Attribute weaponAtt = new Attribute("murder_weapon", weaponValues);


        ArrayList<Attribute> attributes = new ArrayList<Attribute>(8);
        attributes.add(titleAtt);
        attributes.add(yearAtt);
        attributes.add(locationAtt);
        attributes.add(povAtt);
        attributes.add(detectiveAtt);
        attributes.add(weaponAtt);
        attributes.add(avgRatingsAtt);
        attributes.add(numRatingsAtt);

        return new Instances("data", attributes, 0);
    }


    /* Evaluates a given classifier and outputs the results to the console. */
    public void evaluate(Instances data, Classifier classifier) throws Exception
    {
    	Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, 20, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n\n", false) + "\n");
    }


    /* Retrains a classifier with a single instance. */
    public void retrain(Instance newInstance, Classifier classifier) throws Exception
    {
    	UpdateableClassifier newClassifier = (UpdateableClassifier) classifier;
        newClassifier.updateClassifier(newInstance);
        System.out.println("Classifier retrained\n");
    }


    public static void main(String[] args) throws Exception
    {
        WekaTester tester = new WekaTester();

        Instances data = tester.loadDataFile("data.arff", "1");

        Classifier classifier = tester.train(data, tester.WEAPON - 1);

        classifier = null;
        classifier = tester.loadModel("classifier.model");

        tester.evaluate(data, classifier);

        Instances dataFromConsole = tester.createDataset("1");

        if(dataFromConsole == null)
        	System.exit(0);

        data.add(dataFromConsole.instance(0));

        tester.predictSingle(data, data.numInstances() - 1, classifier, tester.WEAPON - 1);

        tester.predictAll(dataFromConsole, classifier, tester.WEAPON - 1);        
    }

}