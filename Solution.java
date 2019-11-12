package ProjectB;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Solution {

	/********************/
	static int layers = 2;
	ArrayList<Integer> inputs, outputs; // input and output pins
	int sizeOfPopulation;

	/********************/
	/*
	 * Returns an empty solution given the problem input and output pins.
	 */
	public Solution(ArrayList<Integer> inputs, ArrayList<Integer> outputs) {
		if (inputs.isEmpty() || outputs.isEmpty()  || inputs.size() != outputs.size()) {
			// throw exception ;
		}
		this.inputs = new ArrayList<Integer>(inputs);
		this.outputs = new ArrayList<Integer>(outputs);
		this.sizeOfPopulation = 6;
		
	}
	private void calcFitnessOfPopulation(ArrayList<Genotype> channels) {
		//lambda for sorting channels by their fitness
		Comparator<Genotype> compareById = (Genotype o1, Genotype o2) -> {if(o1.getF1() == o2.getF1()) {
	    	if(o1.getF2() > o2.getF2()) {
	    		return 1;
	    	}else {
	    		return -1;
	    	}
	    }else{
	    	if(o1.getF1() > o2.getF1()) {
	    		return 1;
	    	}else {
	    		return -1;
	    	}
	    }};
		Collections.sort(channels,compareById);
		int startIndex = 0;
		int endIndex = 0;
		for(int i = 1; i < channels.size()+1; i++) {
			if(i == channels.size() && startIndex == i-1) {
				channels.get(startIndex).setFitness(channels.get(startIndex).getF1());
			}
			else if( i < channels.size() && channels.get(startIndex).getF1() == channels.get(i).getF1()) {
				endIndex = i;
			}else {
				channels.get(startIndex).setFitness(channels.get(startIndex).getF1());
				if(endIndex < channels.size()-1) {
					double f1OfEndp1 = channels.get(endIndex+1).getF1();//F1 of Pend+1
					double f1OfEnd = channels.get(endIndex).getF1();//F1 of Pend
					double fitness = f1OfEndp1 - (f1OfEndp1-f1OfEnd)/(endIndex-startIndex+1);
					channels.get(endIndex).setFitness(fitness);
				}else {
					channels.get(endIndex).setFitness(channels.get(endIndex).getF1());
				}
				for(int x = startIndex+1; x < endIndex; x++) {
					double fitnessPstart = channels.get(startIndex).getFitness();
					double fitnessPend = channels.get(endIndex).getFitness();
					double deltaF = fitnessPend - fitnessPstart;
					double deltaF2 = channels.get(endIndex).getF2() - channels.get(startIndex).getF2();
					double fitnessPx = fitnessPend - (deltaF*(channels.get(endIndex).getF2()-channels.get(x).getF2()))/deltaF2;
					channels.get(x).setFitness(fitnessPx);
				}
				startIndex = i;
				endIndex = i;
			}
		}
		for(int i = 0; i < 6; i++) {
			System.out.println("Fitness: "+channels.get(i).getFitness());
		}
	}

	/********************/
	public static void main(String[] args) {

		ArrayList<Integer> out = new ArrayList<Integer>(Arrays.asList(2, 3,1));
		ArrayList<Integer> in = new ArrayList<Integer>(Arrays.asList(1,2,3));
		Solution sol = new Solution(out, in);
		ArrayList<Genotype> channels = new ArrayList<>();
		int count = 0;
		
		while(count <6) {
			Genotype s = new Genotype(in, out, 2);
			if(s.randomSolution() == 1) {
				channels.add(s);
				count++;
			}

		}
		Comparator<Genotype> compareById = (Genotype o1, Genotype o2) -> {if(o1.getF1() == o2.getF1()) {
	    	if(o1.getF2() > o2.getF2()) {
	    		return 1;
	    	}else {
	    		return -1;
	    	}
	    }else{
	    	if(o1.getF1() > o2.getF1()) {
	    		return 1;
	    	}else {
	    		return -1;
	    	}
	    }};
		Collections.sort(channels,compareById);
		for(int i = 0; i < 6; i++) {
			System.out.println("F1: "+channels.get(i).getF1()+" F2: "+channels.get(i).getF2());
		}
		System.out.println("calculating Fitness");
		sol.calcFitnessOfPopulation(channels);
	}
	
	/********************/
	/********************/

}
