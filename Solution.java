package ProjectB;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Solution {

	/********************/
	static int layers = 2;
	ArrayList<Integer> inputs, outputs; // input and output pins
	int y; // y of 2D array including 2 colomns for the pins
	int x; // number of pins
	Integer[][][] solution;

	/********************/
	/*
	 * Returns an empty solution given the problem input and output pins.
	 */
	public Solution(ArrayList<Integer> inputs, ArrayList<Integer> outputs, int y) {
		if (inputs.isEmpty() || outputs.isEmpty() || y <= 0 || inputs.size() != outputs.size()) {
			// throw exception ;
		}
		this.x = inputs.size();
		this.y = y + 2;
		this.inputs = new ArrayList<Integer>(inputs);
		this.outputs = new ArrayList<Integer>(outputs);
		solution = new Integer[x][this.y][layers];
		// init all board to 0
		for (int i = 0; i < this.x; i++) {
			for (int j = 0; j < this.y; j++) {
				for (int k = 0; k < layers; k++) {
					solution[i][j][k] = 0;
				}
			}
		}

		int i = 0;
		for (int iter : this.inputs) {

			solution[i][0][0] = -1 * iter;
			i++;
		}
		i = 0;
		for (int iter : this.outputs) {
			solution[i][this.y - 1][0] = -1 * iter;
			i++;
		}
	}

	/********************/
	/*
	 * Given an empty solution of the problem changes it to a random valid of
	 * the problem.
	 */
	public void createRandomSolution() {

		// init S and T lists

		// S = pins not connected yet to any other pin
		// T = pins with connection to at least one more pin

		ArrayList<Integer> S = new ArrayList<>();

		// i think the list should be of Pair<int,Integer> where int is the
		// index and Integer is the pin
		// we should discuss it though :P
		S.addAll(this.inputs);
		S.addAll(this.outputs);
		ArrayList<Integer> T = new ArrayList<>();

		while (!S.isEmpty()) {
			// choose random pin from S
			Random rand = new Random();
			// Obtain a number between [0 - x].
			int randIndex = rand.nextInt(this.x);
			int randPin = solution[randIndex][0][0];
			S.remove(randPin);
			int i = 0;
			if (T.contains(randPin)) {
				for (i = 0; i < T.size(); i++) {
					if (T.get(i) == randPin) {
						break;
					}
				}
				connectTwoPins(solution[randIndex][0][0], T.get(i));
				T.add(randPin);
			} else {
				// choose random pin from S with the same net
			}

		}
	}

	/********************/
	/*
	 * Given two pins connect them randomly.
	 */
	public void connectTwoPins(Integer a, Integer b) {

	}

	/********************/
	/*
	 * Given an incomplete solution of the problem completes it to a random
	 * valid of the problem.
	 */
	public void completeRandomSolution() {

		// init S and T sets

		ArrayList<Integer> S = new ArrayList<>();
		ArrayList<Integer> T = new ArrayList<>();

	}

	/********************/
	public boolean isSolutionValid() {
		
		//recursive check
		//every pin is connected to every pin of the same net

		return true;
	}

	/********************/
	public void printBoard() {
		System.out.println("layer 0:");
		for (int i = 0; i < this.x; i++) {
			for (int j = 0; j < this.y; j++) {
				System.out.print(solution[i][j][0] + " ");
			}
			System.out.println(" ");
		}
		System.out.println(" ");
		System.out.println("layer 1:");
		for (int i = 0; i < this.x; i++) {
			for (int j = 0; j < this.y; j++) {
				System.out.print(solution[i][j][1] + " ");
			}
			System.out.println(" ");
		}

	}

	/********************/
	public static void main(String[] args) {

		ArrayList<Integer> in = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		ArrayList<Integer> out = new ArrayList<Integer>(Arrays.asList(2, 3, 1));
		Solution s = new Solution(in, out, 3);
		s.printBoard();
	}
	/********************/
	/********************/

}
