package ProjectB;
import java.util.ArrayList;


public class DiscreteDistribution {
	ArrayList<Double> p;
	
	public DiscreteDistribution(ArrayList<Double> _probabilities) {
		p = new ArrayList<Double>();
		p.add(_probabilities.get(0));
		for(int  i = 1 ; i < _probabilities.size(); i++) {
			p.add(_probabilities.get(i)+p.get(i-1));
		}
	}
	public int randomSample() {
		double rand = Math.random();
		//System.out.println("rand = "+rand);
		int low = 0, high = p.size()-1, mid = 0;
		int index = 0;
		while(low <= high) {
			mid = (low + high)/2;
			if(p.get(mid) >= rand) {
				high = mid-1;
				index = mid;
			}
			if(p.get(mid) < rand) {
				low = mid+1;
			}
		}
		return index;
	}
	public static void main(String[] args) {
		ArrayList<Double> probabalities = new ArrayList<Double>();
		double mut1Prob = 0.001;
		double mut2Prob = 0.002;
		double mut3Prob = 0.01;
		double mut4Prob = 0.01;
		double noMutProb = 1 - mut1Prob - mut2Prob - mut3Prob - mut4Prob;
		probabalities.add(noMutProb);
		probabalities.add(mut3Prob);
		probabalities.add(mut4Prob);
		probabalities.add(mut2Prob);
		probabalities.add(mut1Prob);
		DiscreteDistribution distribution = new DiscreteDistribution(probabalities);
		for(int i = 0 ; i < 20; i++) {
			System.out.println("index = "+distribution.randomSample());

		}
	
	}
}
