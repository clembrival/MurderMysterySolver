
import java.util.Comparator;


/**
  * This class represents a triplet containing the combination of attributes,
  * with the percentages of correct predictions for this combination. 
  * @author 2166340B
  */
public class CombiValue
{
	/** Number of attributes which can be predicted. */
	static final int NUM_CONCEPTS = 2;
	
	/** Indices for each concept. */
	static final int WEAPON = 0, MURDERER = 1;

	/** Selected attributes. */
	String combination;
	/** Percentages of correct predictions produced for each concept. */
	int[] correctPctg;


	/**
	 * Constructor
	 * @param combination a String containing the selected attributes
	 */
	public CombiValue(String combination)
	{
		this.combination = combination;
		correctPctg = new int[NUM_CONCEPTS];
	}


	/**
	 * @param classIndex the index of the attribute to be predicted
	 * @return a Comparator to sort a collection of CombiValues in ascending order
	 * 			of percentages of correct predictions
	 */
	public static Comparator<CombiValue> sortByAscCorrectPctg(final int classIndex)
	{
		Comparator<CombiValue> comparator = new Comparator<CombiValue>()
		{
			@Override
			public int compare(CombiValue cv1, CombiValue cv2)
			{
				int cv1Pctg = cv1.correctPctg[classIndex];
				int cv2Pctg = cv2.correctPctg[classIndex];

				if(cv1Pctg < cv2Pctg)
					return -1;
				else if(cv1Pctg > cv2Pctg)
					return 1;
				else
					return cv1.combination.compareTo(cv2.combination);
			}
		};
		return comparator;
	}


	/**
	 * @returns a String with the following format:
	 * 			combination, concept1_percentage, concept2_percentage
	 */
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(combination);
		builder.append(",");
		
		for(int index = 0; index < NUM_CONCEPTS; index++)
		{
			builder.append(correctPctg[index]);
			if(index < NUM_CONCEPTS-1)
				builder.append(",");
		}
		
		return builder.toString();
	}
}