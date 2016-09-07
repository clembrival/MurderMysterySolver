
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;

import weka.classifiers.Classifier;

import weka.core.Instance;
import weka.core.Instances;


/**
 * This class implements methods which were used to conduct an experiment,
 * in order to assess the accuracy of the predictions produced by the system.
 * @author 2166340B
 */
public class Experiment
{	
	/** Utility object to use Weka functionalities. */
	private WekaTester tester;

	/** The training set on which the system was trained. */
    private Instances trainingSet;

	/** Each element of the list contains for each combination of attributes:
	  * - the attributes used, 
	  * - the average of correct predictions for the cause of death,
	  * - the average of correct predictions for the murderer's gender.
	  */
	private ArrayList<CombiValue> results;

	/** Title of the novels included in the training set. */
	private String[] titles;

	/** Lists of instances which were not correctly classified, 
	  * when predicting the cause of death and the murderer's gender. */
    private IncorrectInstances incorrectWeapon, incorrectGender;


    /**
     * This class represents a dataset of wrongly classified instances.
     * @author 2166340B
     */
    private class IncorrectInstances
    {
    	/** The actual dataset containing the instances. */ 
        Instances instances;
        
        /** The indices in the training set of the instances. */ 
        ArrayList<Integer> indices;
        
        /** The number of instances in the dataset. */
        int size;

        /**
         * Constructor
         * @param classIndex the attribute to be predicted
         * @param tester the WekaTester utility object 
         * @throws Exception
         */
        public IncorrectInstances(int classIndex, WekaTester tester) throws Exception
        {
            instances = tester.filterData(tester.buildAttributes());
            instances.setClassIndex(classIndex);
            indices = new ArrayList<>();
            size = 0;
        }

        /**
         * Adding an instance to the dataset.
         * @param instance the instance to be added to the dataset
         * @param index the index of the given instance in the training set
         */
        public void add(Instance instance, int index)
        {
            instances.add(instance);
            indices.add(index);
            size++;
        }
    }

    
    /**
     * Constructor
     * @param titles the titles of the training set's novels
     * @param trainingSet the training dataset
     * @throws Exception
     */
	public Experiment(String[] titles, Instances trainingSet) throws Exception
	{
		tester = new WekaTester();
		
        this.trainingSet = trainingSet;
        
		results = new ArrayList<>();
		
		this.titles = titles;
		
        incorrectWeapon = new IncorrectInstances(WekaTester.WEAPON-1, tester);
        incorrectGender = new IncorrectInstances(WekaTester.MURDERER-1, tester);
	}


	/**
	 * Calculates the percentage of correct predictions on all the instances 
	 * of the given testing set, for the given index of the attribute to be predicted.

	 * @param classIndex the index of the attribute to be predicted
	 * @param testingSet the dataset containing the instances to be classified
	 */
	private void getCorrectness(int classIndex, Instances testingSet,
            int selectedCount) throws Exception
    {
        // Setting the index of the attribute to be predicted
        trainingSet.setClassIndex(classIndex);
        testingSet.setClassIndex(classIndex);

        // Training the classifier on the instances of the training set
        Classifier classifier = tester.train(trainingSet);           
          
        // Number of correct and incorrect predictions 
        int correct = 0;

        for(int instIndex = 0; instIndex < testingSet.numInstances(); instIndex++)
        {
        	testingSet.instance(instIndex).setMissing(classIndex);

        	// Classifying the current instance
            Instance labelled = tester.predictSingle(testingSet, 
                instIndex, classifier, classIndex);
            
            String rightAnswer = trainingSet.instance(instIndex).stringValue(classIndex);
            
            /* Comparing the prediction to the right answer 
             * and incrementing the counter if appropriate */
            if(labelled.stringValue(classIndex).equals(rightAnswer))
                correct++;
            else
            {
            	// If all the attributes were selected, add the instances to the incorrect dataset
                if(selectedCount == 7)
                {
                    if(classIndex == WekaTester.WEAPON-1)
                        incorrectWeapon.add(labelled, instIndex);
                    else
                        incorrectGender.add(labelled, instIndex);
                }
            }
        }
        
        int index = (classIndex == WekaTester.WEAPON-1) ? 
                CombiValue.WEAPON : CombiValue.MURDERER;

        // Set the percentage of correct answers of the last entry of the results list
        results.get(results.size() - 1).correctPctg[index] = (correct * 100 / testingSet.numInstances());
    }


    /** For all the possible combinations of any 2 to 8 selected attributes, 
     * counts the number of correct predictions for the cause of death and the murderer's gender.
     * @param attributes a String containing the indices of the selected attributes
     * @param trainingSet the dataset storing the right answers
     * @param testingSet the dataset storing the modified instances to be classified
     */
    public void testAttributes(String attributes, int classIndex, 
            Instances testingSet) throws Exception
    {
        if(attributes.length() > 1)
        {
            System.out.println(attributes);
            
            // Create a CombiValue for the current combination and add it to the results list
            CombiValue cv = new CombiValue(attributes);
            results.add(cv);

            // Creating a testing set, setting the unselected attributes as missing
            Instances tempTestingSet = getTempTestingSet(attributes, testingSet);
    
            // Producing the predictions on the instances of the testing set
            getCorrectness(classIndex, tempTestingSet, attributes.length());
        }
        
        // Getting the index of the last selected attribute
        int lastIndex = Integer.parseInt("" + attributes.charAt(attributes.length()-1));

        // Do no recurse if we've reached the last attribute
        if(lastIndex == WekaTester.NUM_ATTRIBUTES - 1)
            return;

        for(int i = lastIndex + 1; i < WekaTester.NUM_ATTRIBUTES; i++)
        {
        	if(i == classIndex+1)
        		i++;
            testAttributes(attributes + i, classIndex, testingSet);
        }
    }


    /**
     * Creating a testing set, with the unselected attributes set as missing
     * @param attributes a String containing the indices of the selected attributes
     * @param testingSet the dataset storing the modified instances to be classified
     * @return the created testing dataset
     * @throws Exception
     */
    private Instances getTempTestingSet(String attributes, 
    		Instances testingSet) throws Exception
    {
    	// Creating an empty dataset
        Instances tempTestingSet = tester.buildAttributes();            

        /* Copying all the instances from the testingSet into the temporary testing set,
         * setting all the unselected attributes as missing */
        for(int instIndex = 0; instIndex < testingSet.numInstances(); instIndex++)
        {
        	// Setting all the values as missing
            String[] values = {"unknown","0","unknown","unknown","unknown","unknown","unknown","unknown","0"};

            // Creating an instance with those values
            tempTestingSet = tester.createInstances(tempTestingSet, values);

            // Setting the values of the selected attributes to the ones in testingSet
            for(int i = 0; i < attributes.length(); i++)
            {
                int attIndex = Integer.parseInt("" + attributes.charAt(i));

                tempTestingSet.instance(instIndex).setValue(attIndex,
                    testingSet.instance(instIndex).value(attIndex-1));
            }
        }

        // Removing the title
        return tester.filterData(tempTestingSet);
    }


    /**
     * Prints the percentages of correct predictions 
     * for all the combinations of attributes, in ascending order.
     * @param index the index of the attribute which was predicted
     * @param file the file to which the results should be output
     * @throws Exception
     */
    public void printResults(int index, String file) throws Exception
    {
    	// Sorting the results by ascending order of correct percentages
    	Collections.sort(results, CombiValue.sortByAscCorrectPctg(index));

    	PrintWriter writer = new PrintWriter(file);

    	for(CombiValue result : results)
    		// Only printing the results related to the predicted attribute 
    		if(result.correctPctg[index] != 0)
    			writer.println(result.toString());

    	writer.close();
    }


    /**
     * Prints the percentages of correct predictions for all the combinations of attributes,
     * sorted by descending combination length and ascending percentages
     * @param index the index of the attribute which was predicted
     * @param file the file to which the results should be output
     * @throws Exception
     */
    public void printResultsByLength(int index, String file) throws Exception
    {
    	// Sorting the results by ascending order of correct percentages
    	Collections.sort(results, CombiValue.sortByAscCorrectPctg(index));

    	// Each StringBuilder stores the results for a specific combination length  
    	StringBuilder[] builders = new StringBuilder[6];

    	for(CombiValue result : results)
    	{
    		// Only printing the results related to the predicted attribute
    		if(result.correctPctg[index] != 0)
    		{
    			int length = result.combination.length();

	    		if(builders[length-2] == null)
	    			builders[length-2] = new StringBuilder();
	    		
	    		builders[length-2].append(result.toString());
	    		builders[length-2].append("\n");
    		}
    	}

    	PrintWriter writer = new PrintWriter(file);

    	for(int i = builders.length - 1; i >= 0; i--)
    		writer.println(builders[i].toString());

    	writer.close();
    }


    /**
     * Prints the instances which were wrongly classified to the console.
     * @param classIndex the index of the attribute which was predicted
     */
    public void printIncorrectInstances(int classIndex)
    {
        IncorrectInstances incorrectInst = (classIndex == WekaTester.WEAPON-1) ?
            incorrectWeapon : incorrectGender;

        for(int index = 0; index < incorrectInst.size; index++)
        {
            System.out.print(titles[incorrectInst.indices.get(index)] + ",");
            System.out.println(incorrectInst.instances.instance(index).toString());
        }
        System.out.println();
    }


    /**
     * Sets the book rating of each wrongly classified instance to a different value
     * and checks if this change produces the right prediction 
     * @param classIndex the attribute of the attribute which was predicted
     * @param file the file to which the results should be output
     * @throws Exception
     */
    public void rectifyInstances(int classIndex, String file) throws Exception
    {
        // Setting the index of the attribute to be predicted
        trainingSet.setClassIndex(classIndex);

        IncorrectInstances incorrectInst = (classIndex == WekaTester.WEAPON-1) ?
                incorrectWeapon : incorrectGender;

        Instances instances = incorrectInst.instances;
        ArrayList<Integer> indices = incorrectInst.indices;

        PrintWriter writer = new PrintWriter(file);

        for(int index = 0; index < instances.numInstances(); index++)
        {
            writer.print(titles[indices.get(index)]);
            writer.print(" : ");
            writer.println(getCorrectRatings(instances, index, 
                indices.get(index), classIndex));
        }
        writer.close();
    }


    /**
     * Find the ratings which produced the correct prediction.
     * @param instances the wrongly classified instances
     * @param instIndex the index of the instance to be rectified  
     * @param trainingIndex the index of the instance in the training set
     * @param classIndex the index of the attribute to be predicted
     * @return a String containing the ratings which produced the right prediction, 
     * 			or "none" if there were none
     * @throws Exception
     */
    private String getCorrectRatings(Instances instances, int instIndex, 
            int trainingIndex, int classIndex) throws Exception
    {
        StringBuilder correctRatings = new StringBuilder();

        double rating = 0.5;
        boolean found = false;

        while(rating <= 5.0)
        {
            instances.instance(instIndex).setValue(WekaTester.AVG_RATINGS-1, rating);

            // Training the classifier on the instances of the training set
            Classifier classifier = tester.train(trainingSet);           

            Instance labelled = tester.predictSingle(instances, 
                instIndex, classifier, classIndex);

            String rightAnswer = trainingSet.instance(trainingIndex).stringValue(classIndex);
        
            // Comparing the prediction to the right answer
            if(labelled.stringValue(classIndex).equals(rightAnswer))
            {
                found = true;
                correctRatings.append(rating);
                correctRatings.append(" ");
            }
                
            rating += 0.5;
        }

        return found ? correctRatings.toString() : "none";
    }
}