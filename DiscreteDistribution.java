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
		probabalities.add((double)3/6);
		probabalities.add((double)2/6);
		probabalities.add((double)1/6);
		DiscreteDistribution distribution = new DiscreteDistribution(probabalities);
		for(int i = 0 ; i < 7; i++) {
			System.out.println("prob = "+distribution.randomSample());

		}
	
	}
}
