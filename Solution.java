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
	int maxNumDescndants;
	int maxNumOfGenerations;
	double fitnessSum;
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
		this.sizeOfPopulation = 20;
		this.maxNumDescndants = 10;
		this.fitnessSum = 0;
		this.maxNumOfGenerations = 10;

	}
	public Genotype getSolution() {
		ArrayList<Genotype> population = new ArrayList<>();
		int count = 0;
		
		while(count <sizeOfPopulation) {
			Genotype s = new Genotype(inputs, outputs, 2);
			if(s.randomSolution() == 1) {
				population.add(s);
				count++;
			}
		}
		calcFitnessOfPopulation(population);
		Genotype bestIndividual = population.get(population.size()-1);
		
		for(int i = 0; i < maxNumOfGenerations; i++) {
			ArrayList<Genotype> descendants = new ArrayList<Genotype>();
			//System.out.println(population.size());

			for(int j = 0; j < maxNumDescndants; j++) {
				Genotype pAlpha = selection(population);
				Genotype pBeta = selection(population);
				Crossover crossover = new Crossover(pAlpha,pBeta);
				Genotype descndant = crossover.crossoverOp();
				while(descndant == null) {
					descndant = crossover.crossoverOp();
				}
				descendants.add(descndant);
			}
			calcFitnessOfPopulation(descendants);
			population = reduction(population, descendants);
			if(bestIndividual.getFitness() < population.get(population.size()-1).getFitness()) {
				bestIndividual = population.get(population.size()-1);
			}
			mutationOnPopulation(population);
			calcFitnessOfPopulation(population);
		}
		return bestIndividual;
	}
	private void mutationOnPopulation(ArrayList<Genotype> population) {
		for(int i = 0; i < population.size(); i++) {
			population.get(i).mutationOperator();
		}
	}
	private void preScale(double umax, double uavg, double umin,ArrayList<Double> ab) {
		double fmultiple = 2.0, delta;
		if(umin > (fmultiple*uavg - umax)/(fmultiple - 1.0)) {
			delta = umax - uavg;
			ab.add(((fmultiple - 1.0)*uavg)/delta); //a
			ab.add((uavg*(umax -fmultiple*uavg))/delta);
		}else {
			delta = uavg - umin;
			ab.add((uavg/delta));
			ab.add(((-umin*uavg)/delta));
		}
		//System.out.println("a= "+ab.get(0)+" b= "+ab.get(1));
	}
	private double scalePop(ArrayList<Genotype> channels, double max, double avg, double min) {
		
		double _fitnessSum = 0;
		ArrayList<Double> ab = new ArrayList<>();
		preScale(max, avg, min, ab);
		Double a = new Double(ab.get(0));
		Double b = new Double(ab.get(1));
		for(int i = 0; i < channels.size(); i++) {
			double fitness = a*channels.get(i).getFitness() + b;
			channels.get(i).setFitness(fitness);
		    _fitnessSum += fitness;
		}
		return _fitnessSum;
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
	    for(Genotype geno : channels) {
	    	geno.calcF1();
	    	geno.calcF2();
	    }
		
		Collections.sort(channels,compareById);
		int startIndex = 0;
		int endIndex = 0;
		this.fitnessSum = 0;
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
					double f1OfEndp1 = 1/((double)(channels.get(endIndex).getNumOfRows())-1);
					double f1OfEnd = channels.get(endIndex).getF1();//F1 of Pend
					double fitness = f1OfEndp1 - (f1OfEndp1-f1OfEnd)/(endIndex-startIndex+1);
					channels.get(endIndex).setFitness(fitness);
				}
				for(int x = startIndex+1; x < endIndex; x++) {
					double fitnessPstart = channels.get(startIndex).getFitness();
					double fitnessPend = channels.get(endIndex).getFitness();
					double deltaF = fitnessPend - fitnessPstart;
					double deltaF2 = channels.get(endIndex).getF2() - channels.get(startIndex).getF2();
					if(deltaF2 == 0) {
						deltaF2 = endIndex - startIndex+1;
					}
					double fitnessPx = fitnessPend - (deltaF*(channels.get(endIndex).getF2()-channels.get(x).getF2()))/deltaF2;
					channels.get(x).setFitness(fitnessPx);
				}
				startIndex = i;
				endIndex = i;
			}
		}
		double max = channels.get(channels.size() -1).getFitness() , min = channels.get(0).getFitness() , avg;
		for(int i = 0; i < channels.size(); i++) {
			fitnessSum += channels.get(i).getFitness();
		}/*
		for(Genotype geno : channels) {
			System.out.println(geno.fitness);
		}*/
		avg = fitnessSum/((double)channels.size());
		this.fitnessSum = scalePop(channels,max, avg, min);
	

		//System.out.println("sum Fitness: "+fitnessSum);
	}
	public Genotype selection(ArrayList<Genotype> channels) {
		ArrayList<Double> probs = new ArrayList<Double>();
		for(int i = channels.size()-1; i >= 0; i--) {
			if(channels.get(i).getFitness() > 0.0) {
				probs.add(channels.get(i).getFitness()/fitnessSum);
			}
		}
		DiscreteDistribution distribution = new DiscreteDistribution(probs);
		int randChannel = distribution.randomSample();
		return channels.get(channels.size()-1-randChannel);
	}
	public ArrayList<Genotype> reduction(ArrayList<Genotype> population, ArrayList<Genotype> descendants){
		ArrayList<Genotype> bestPopulation = new ArrayList<Genotype>();
		int i = population.size()-1;
		int j = descendants.size()-1;
		while(bestPopulation.size() < sizeOfPopulation && i >= 0 && j >= 0) {
			if(population.get(i).getFitness() >= descendants.get(j).getFitness()) {
				bestPopulation.add(population.get(i));
				i--;
			}else {
				bestPopulation.add(descendants.get(j));
				j--;
			}
		}
		while(bestPopulation.size() < sizeOfPopulation && i >= 0) {
			bestPopulation.add(population.get(i));
			i--;
		}
		while(bestPopulation.size() < sizeOfPopulation && j >= 0) {
			bestPopulation.add(descendants.get(j));
			j--;
		}

		return bestPopulation;
	}
	/********************/
	public static void main(String[] args) {

		ArrayList<Integer> out = new ArrayList<Integer>(Arrays.asList(2, 3,1));
		ArrayList<Integer> in = new ArrayList<Integer>(Arrays.asList(1,2,3));
		Solution sol = new Solution(out, in);
		Genotype best = sol.getSolution();
		best.printBoard();
		
	}
	
	/********************/
	/********************/

}
