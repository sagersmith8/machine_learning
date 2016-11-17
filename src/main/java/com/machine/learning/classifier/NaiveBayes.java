public class NaiveBayes implements Classifier {
    private Map<String, Counter> classCount = new Map<>();
    private Map<String, List<Map<String, Counter>>> attributeCount = new Map<>();

    private int numPoints, numAttributes;

    public void train(List<List> dataPoints, List<String> classLabel) {
	numPoints = dataPoints.size();
	if (numPoints > 0) {
	    numAttributes = dataPoints.get(0).size();
	}
	
	for (int i = 0; i < dataPoints.size(); i++) {
	    countPoint(dataPoint.get(i), classLabel.get(i));
	}
    }

    private void countPoint(List dataPoint, String classLabel) {
	ensureClass(classLabel);
	classCount.get(classLabel).increment();
	    
	for (int attrNum = 0; attrNum < numAttributes; attrNum++) {
	    String attributeValue = (String)dataPoint.get(attrNum);

	    ensureClassAttribute(classLabel, attrNum, attributeValue);
	    attributeCount.get(classLabel).get(attrNum).get(attributeValue).increment();
	}
    }

    private void ensureClass(String classLabel) {
	if (classCount.containsKey(pointClass))
	    return;
	
	classCount.put(pointClass, new Counter());
	
	List<Map<String, Counter>> classAttributeCounts = new List<>();
	for (int attrNum = 0; attrNum < numAttributes; attrNum++) {
	    classAttributeCounts.add(new Map<String, Counter>());
	}
	
	attributeCount.put(pointClass, classAttributeCounts);
    }

    private void ensureClassAttribute(String classLabel, int attributeNum, String attributeValue) {
	if (attributeCount.get(classLabel).get(attributeNum).containsKey(attributeValue))
	    return;

	attributeCount.get(classLabel).get(attributeNum).put(attributeValue, new Counter());
    }

    public String classify(List dataPoint) {
	double bestProb = 0;
	String bestClassLabel = null;
	
	for (String classLabel : classCount.keySet()) {
	    int classCount = classCount.get(classLabel).getValue();
	    double prob = (double)classCount / numPoints;
	    
	    for (int attrNum = 0; attrNum < numAttributes; attrNum++) {
		String attributeValue = dataPoint.get(attrNum);
		int attrCount = attributeCount.get(classLabel).get(attrNum).get(attributeValue).getValue();
		
		prob *= (double)attrCount / classCount;
	    }

	    if(prob > bestProb) {
		bestProb = prob;
		bestClassLabel = classLabel;
	    }
	}

	return bestClassLabel;
    }

    class Counter {
	private int value = 0;

	public void increment() {
	    value++;
	}

	public int getValue() {
	    return value;
	}
    }
}
