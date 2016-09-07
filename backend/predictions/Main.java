
import weka.core.Instances;


public class Main
{
	public static void main(String[] args) throws Exception
    {
		// Utility object
        WekaTester tester = new WekaTester();

        // Creating training instances from arff file
        Instances trainingSet = tester.createDataset("dataset.arff");

        // Creating a copy of training set
        Instances testingSet = new Instances(trainingSet);

        // The dataset will be filtered -> need to store the titles
        String[] titles = new String[testingSet.numInstances()];

        for(int instIndex = 0; instIndex < trainingSet.numInstances(); instIndex++)
        {
            titles[instIndex] = testingSet.instance(instIndex).stringValue(WekaTester.TITLE);
            
            // Rounding book's ratings to nearest 0.5 to simulate the user input on the app
            testingSet.instance(instIndex).setValue(WekaTester.AVG_RATINGS,
                Math.round(2 * trainingSet.instance(instIndex).value(WekaTester.AVG_RATINGS)) / 2.0);
        }

        // Remove the title from both datasets
        trainingSet = tester.filterData(trainingSet);
        testingSet = tester.filterData(testingSet);     

        
        Experiment experiment = new Experiment(titles, trainingSet);

        /* First experiment: outputting the percentages of correct predictions 
        	for each concept. */

        for(int i = 1; i < WekaTester.NUM_ATTRIBUTES; i++)
        {
            if(i == WekaTester.WEAPON)
                i++;
            experiment.testAttributes("" + i, WekaTester.WEAPON-1, testingSet);
        }

        for(int i = 1; i < WekaTester.NUM_ATTRIBUTES; i++)
        {
            if(i == WekaTester.MURDERER)
                i++;
            experiment.testAttributes("" + i, WekaTester.MURDERER-1, testingSet);
        }
            
        experiment.printResults(CombiValue.WEAPON, "weapon.csv");
        experiment.printResults(CombiValue.MURDERER, "murderer.csv");

        experiment.printResultsByLength(CombiValue.WEAPON, "weapon_by_length.csv");
        experiment.printResultsByLength(CombiValue.MURDERER, "murderer_by_length.csv");

        /* Second experiment: modifying the book's rating of the wrongly classified
         * instances to try to produce the right prediction. */

        experiment.printIncorrectInstances(WekaTester.WEAPON-1);
        experiment.printIncorrectInstances(WekaTester.MURDERER-1);
        
        experiment.rectifyInstances(WekaTester.WEAPON-1, "wepon_rectified.txt");
        experiment.rectifyInstances(WekaTester.MURDERER-1, "murderer_rectified.txt");
	}
}