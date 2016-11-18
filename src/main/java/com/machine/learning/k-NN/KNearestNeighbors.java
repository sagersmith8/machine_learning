import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class KNearestNeighbors extends Classifier {

    int k;
    ArrayList<DataWithDistance> data;
    ArrayList<String> classLabel;
    HashSet<String> possibleClasses;
    Map<String, AtomicInteger> voteResults;
    
    class DataWithDistance implements Comparable {
	ArrayList data;
	double distance;

	public int compareTo(DataWithDistance other) {
	    return distance - other.distance;
	}
    }
    
    public KNearestNeighbors(int k) {
	this.k = (int)Math.max(k,1);
    }
    
    @Override
    public void train(List<List> dataPoints, List<String> classLabel) {
	data = new ArrayList (dataPoints.length);
	possibleClasses = new HashSet(classLabel);
	for (int i = 0; i < dataPoints.length; i++) {
	    data = dataPoints;
	}
	this.classLabel = classLabel;
    }

    @Override
    public String classify(List dataPoint) {
	for (int i = 0; i < data.size(); i++) {
	    data.get(i).distance = calculateDistance(data.get(i).data, dataPoint);
	}
	return vote(k);
    }

    public String vote(int k) {
	data = Collections.sort(data);
	//voteReseults = new Map<>();
	for (int i = 0; i < k; i++) {
	    String classLabel = data.get(i).data.classifier;
	    if (!voteResults.containsKey(classLabel)) {
		voteResults.put(classLabel, new AtomicInteger());
	    }
	    voteResults.get(classLabel).incrementAndGet();
	}
	int max = 0;
	ArrayList<String> results = new ArrayList<String>();
	for(String classLabel : voteResults.keySet()){
	    int cur = voteResults.get(classLabel).get();
	    if (cur > max) {
		max = cur;
		results.clear();
		results.add(classLabel);
	    } else if (cur == max) {
		results.add(classLabel);
	    }
	}
	Collections.shuffle(results);
	return results.get(0);
    }
}
