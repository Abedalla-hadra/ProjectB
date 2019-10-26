package ProjectB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

enum Direction {
	UP, DOWN, LEFT, RIGHT
}
public class Genotype {
	static int layers = 2;
	int num_of_pins;
	Integer[][][] channel;
	int yind;
	int numOfRows;
	ArrayList<Pin> S;
	ArrayList<Pin> T;
	int max_extentions;
	public Genotype(ArrayList<Integer> inputs, ArrayList<Integer> outputs, int ymin) {
		this.yind = ThreadLocalRandom.current().nextInt(2*ymin, 4*ymin+1);
		this.numOfRows = yind+2;
		this.max_extentions = 10;
		this.num_of_pins = inputs.size();
		this.S = new ArrayList<Pin>();
		this.T = new ArrayList<Pin>();
		this.channel = new Integer[this.numOfRows][this.num_of_pins][layers];
		for(int i = 0;i<inputs.size();i++) {
			S.add(new Pin(inputs.get(i),i,false));
		}
		for(int i = 0;i<outputs.size();i++) {
			S.add(new Pin(outputs.get(i),i,true));
		}
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.num_of_pins; x++) {
				for (int z = 0; z < layers; z++) {
					if(y==0) {
						channel[y][x][z] = -1*outputs.get(x);
					}else if(y == numOfRows-1) {
						channel[y][x][z] = -1*inputs.get(x);
					}else {
						channel[y][x][z] = 0;
					}
				}
			}
		}
	}
	private void copyChannel(Integer[][][] tempChannel) {
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.num_of_pins; x++) {
				for (int z = 0; z < layers; z++) {
					tempChannel[y][x][z] = channel[y][x][z];
				}
			}
		}
	}
	private void printTempChannel(Integer[][][] tempChannel) {
		System.out.println("layer 0:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.num_of_pins; x++) {
				System.out.print(tempChannel[y][x][0] + " ");
			}
			System.out.println(" ");
		}
		System.out.println(" ");
		System.out.println("layer 1:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.num_of_pins; x++) {
				System.out.print(tempChannel[y][x][1] + " ");
			}
			System.out.println(" ");
		}
	}
	private int layerOfExtention(Direction dir) {
		Random randomGenerator = new Random();
		int rn = randomGenerator.nextInt(100)+1;
		
		//System.out.println(rn);
		if(dir == Direction.DOWN) {
			if(rn<=66) {
				return 0;
			}else {
				return 1;
			}
		}
		if(dir == Direction.UP) {
			if(rn<=66) {
				return 0;
			}else {
				return 1;
			}
		}
		if(dir == Direction.LEFT) {
			if(rn<=66) {
				return 1;
			}else {
				return 0;
			}
		}
		if(dir == Direction.RIGHT) {
			if(rn<=66) {
				return 1;
			}else {
				return 0;
			}
		}
		return 0;
	}
	private int indexOfObstacle(Integer[][][] tempChannel,Direction dir,int row,int column,int layer) {
		int index = 0;
		if(dir == Direction.DOWN) {
			index = row;
			for(int y = row+1;y<numOfRows;y++) {
				if(channel[y][column][layer] == 0 && tempChannel[y][column][layer] == 0) {
					index = y;
				}
			}
			index++;
		}else if(dir == Direction.UP) {
			index = row;
			for(int y = row - 1;y>0;y--) {
				if(channel[y][column][layer] == 0 && tempChannel[y][column][layer] == 0) {
					index = y;
				}
			}
			index--;
		}else if(dir == Direction.LEFT) {
			index = column;
			for(int x = column - 1 ;x>=0;x--) {
				if(channel[row][x][layer] == 0 && tempChannel[row][x][layer] == 0 ) {
					index = x;
				}
			}
		}else if(dir == Direction.RIGHT) {
			index = column;
			for(int x = column+1;x<num_of_pins;x++) {
				if(channel[row][x][layer] == 0 && tempChannel[row][x][layer] == 0) {
					index = x;
				}
			}
		}
		return index;
	}
	private void updateTempChannel(Integer[][][] tempChannel,Direction dir,int row,int column,int layer,int index,int pinNum) {
		if(dir == Direction.DOWN) {
			for(int y = row;y<=index;y++) {
				tempChannel[y][column][layer] = pinNum;
			}
		}else if(dir == Direction.UP) {
			for(int y = row;y>=index;y--) {
				tempChannel[y][column][layer] = pinNum;
			}
		}else if(dir == Direction.LEFT) {
			for(int x = column;x>=index;x--) {
				tempChannel[row][x][layer] = pinNum;
			}
		}else if(dir == Direction.RIGHT) {
			for(int x = column;x<=index;x++) {
				tempChannel[row][x][layer] = pinNum;
			}
		}
	}
	private int randomNumInRange(int x,int y) {
		int randomNum = 0;
		if(x>y) {
			randomNum = ThreadLocalRandom.current().nextInt(y,x);
		}else {
			randomNum = ThreadLocalRandom.current().nextInt(x,y);
		}
		return randomNum;
	}
	private int getYindexOfPin(Pin pin) {
		if(pin.isOnUpper()) {
			return 0;
		}
		return numOfRows-1;
	}
	public int connectPins(Pin s,Pin t) {
		Integer[][][] tempChannel = new Integer[this.numOfRows][this.num_of_pins][layers];
		copyChannel(tempChannel);
		//printTempChannel(tempChannel);
		int maxNumOfIterations = 3*(Math.abs(s.getIndex()-t.getIndex()))+yind+10;
		//System.out.println(yind);
		boolean noValidSolution = false;
		//System.out.println(maxNumOfIterations);
		int iterNum = 0;
		int tLayer ;
		int sLayer ;
		int yIndexOfS;
		int yIndexOfT;
		int xIndexOfS = s.getIndex();
		int xIndexOfT = t.getIndex();
		Direction sDirection,tDirection;
		if(s.isOnUpper()) {
			sDirection = Direction.DOWN;
			sLayer = 0;
			yIndexOfS = 1;
		}else {
			sDirection = Direction.UP;
			sLayer = 0;
			yIndexOfS = numOfRows-2;
		}
		if(t.isOnUpper()) {
			tDirection = Direction.DOWN;
			tLayer = 0;
			yIndexOfT = 1;
		}else {
			tDirection = Direction.UP;
			tLayer = 0;
			yIndexOfT = numOfRows-2;

		}
		boolean solutionFound = false;
		while(!solutionFound && iterNum < 3/*maxNumOfIterations*/ && !noValidSolution) {
			if (iterNum == 0) {
				tempChannel[yIndexOfS][xIndexOfS][sLayer] = s.getPinNum();
				tempChannel[yIndexOfT][xIndexOfT][tLayer] = t.getPinNum();
				sLayer = layerOfExtention(sDirection);
				tLayer = layerOfExtention(tDirection);
				int sObstacleIndex = indexOfObstacle(tempChannel, sDirection, yIndexOfS, xIndexOfS, sLayer);
				int tObstacleIndex = indexOfObstacle(tempChannel, tDirection, yIndexOfT, xIndexOfT, tLayer);
				/*
				System.out.println("s layer: "+sLayer);
				System.out.println("t layer: "+tLayer);
				*/
				int newYindexForS = randomNumInRange(yIndexOfS, sObstacleIndex+1);
				int newYindexForT = randomNumInRange(yIndexOfT, tObstacleIndex+1);
				/*
				System.out.println("new y for s : "+newYindexForS);
				System.out.println("new y for t : "+newYindexForT);
				*/
				updateTempChannel(tempChannel, sDirection, yIndexOfS, xIndexOfS, sLayer, newYindexForS, s.getPinNum());
				updateTempChannel(tempChannel, tDirection, yIndexOfT, xIndexOfT, tLayer, newYindexForT, t.getPinNum());
				yIndexOfS = newYindexForS;
				yIndexOfT = newYindexForT;
				if(s.getIndex()>t.getIndex()) {
					sDirection = Direction.LEFT;
					tDirection = Direction.RIGHT;
				}
			}else if(iterNum%2 == 1){
				sLayer = layerOfExtention(sDirection);
				tLayer = layerOfExtention(tDirection);
				tempChannel[yIndexOfS][xIndexOfS][sLayer] = s.getPinNum();
				tempChannel[yIndexOfT][xIndexOfT][tLayer] = t.getPinNum();
				//System.out.println("s layer: "+sLayer);
				//System.out.println("t layer: "+tLayer);
				int minXForS = indexOfObstacle(tempChannel, Direction.LEFT, yIndexOfS, xIndexOfS, sLayer);
				int maxXForS = indexOfObstacle(tempChannel, Direction.RIGHT, yIndexOfS, xIndexOfS, sLayer);
				int minXForT = indexOfObstacle(tempChannel, Direction.LEFT, yIndexOfT, xIndexOfT, tLayer);
				int maxXForT = indexOfObstacle(tempChannel, Direction.RIGHT, yIndexOfT, xIndexOfT, tLayer);
				/*
				System.out.println("min xS "+minXForS);
				System.out.println("max xS "+maxXForS);
				System.out.println("min xT "+minXForT);
				System.out.println("max xT "+maxXForT);
				*/
				int newXindexForS = randomNumInRange(minXForS,maxXForS+1);
				int newXindexForT = randomNumInRange(minXForT,maxXForT+1);
				/*
				System.out.println("new xS "+newXindexForS);
				System.out.println("new xT "+newXindexForT);
				*/
				if(newXindexForS <= xIndexOfS) {
					updateTempChannel(tempChannel, Direction.LEFT, yIndexOfS, xIndexOfS, sLayer, newXindexForS, s.getPinNum());
				}else {
					updateTempChannel(tempChannel, Direction.RIGHT, yIndexOfS, xIndexOfS, sLayer, newXindexForS, s.getPinNum());
				}
				if(newXindexForT <= xIndexOfT) {
					updateTempChannel(tempChannel, Direction.LEFT, yIndexOfT, xIndexOfT, tLayer, newXindexForT, t.getPinNum());
				}else {
					updateTempChannel(tempChannel, Direction.RIGHT, yIndexOfT, xIndexOfT, tLayer, newXindexForT, t.getPinNum());
				}
				xIndexOfS = newXindexForS;
				xIndexOfT = newXindexForT;
				if(s.isOnUpper()) {
					sDirection = Direction.DOWN;
				}else {
					sDirection = Direction.UP;

				}
				if(t.isOnUpper()) {
					tDirection = Direction.DOWN;
				}else {
					tDirection = Direction.UP;
				}
			}else if(iterNum%2 == 0) {
				tempChannel[yIndexOfS][xIndexOfS][sLayer] = s.getPinNum();
				tempChannel[yIndexOfT][xIndexOfT][tLayer] = t.getPinNum();
				sLayer = layerOfExtention(sDirection);
				tLayer = layerOfExtention(tDirection);
				int minYForS = indexOfObstacle(tempChannel, Direction.UP, yIndexOfS, xIndexOfS, sLayer);
				int maxYForS = indexOfObstacle(tempChannel, Direction.DOWN, yIndexOfS, xIndexOfS, sLayer);
				int minYForT = indexOfObstacle(tempChannel, Direction.UP, yIndexOfT, xIndexOfT, tLayer);
				int maxYForT = indexOfObstacle(tempChannel, Direction.DOWN, yIndexOfT, xIndexOfT, tLayer);
				int newYindexForS = randomNumInRange(minYForS, maxYForS+1);
				int newYindexForT = randomNumInRange(minYForT, maxYForT+1);
				if(newYindexForS <= yIndexOfS) {
					updateTempChannel(tempChannel, Direction.UP, yIndexOfS, xIndexOfS, sLayer, newYindexForS, s.getPinNum());
				}else {
					updateTempChannel(tempChannel, Direction.DOWN, yIndexOfS, xIndexOfS, sLayer, newYindexForS, s.getPinNum());
				}
				if(newYindexForT <= yIndexOfT) {
					updateTempChannel(tempChannel, Direction.UP, yIndexOfT, xIndexOfT, tLayer, newYindexForT, t.getPinNum());
				}else {
					updateTempChannel(tempChannel, Direction.DOWN, yIndexOfT, xIndexOfT, tLayer, newYindexForT, t.getPinNum());
				}
				yIndexOfS = newYindexForS;
				yIndexOfT = newYindexForT;
				if(s.getIndex()>t.getIndex()) {
					sDirection = Direction.LEFT;
					tDirection = Direction.RIGHT;
				}
			}

			iterNum++;
		}
		printTempChannel(tempChannel);

		return 1;
	}
	public int randomSolution() {
		Random randomGenerator = new Random();
		
		while (!S.isEmpty()) {
			int randomInt = randomGenerator.nextInt(S.size());
			Pin s = S.get(randomInt);
			Pin t;
			S.remove(randomInt);
			ArrayList<Integer> pinsOfTheSameNet = new ArrayList<>();
			if (T.isEmpty()) {
				for (int i = 0; i < S.size(); i++) {
					Pin temp = S.get(i);
					if (temp.getPinNum() == s.getPinNum()) {
						pinsOfTheSameNet.add(i);
					}
				}
				randomInt = randomGenerator.nextInt(pinsOfTheSameNet.size());
				t = S.get(pinsOfTheSameNet.get(randomInt));
				T.add(t);
				int x = pinsOfTheSameNet.get(randomInt);
				S.remove(x);

			} else {
				for (int i = 0; i < T.size(); i++) {
					Pin temp = T.get(i);
					if (temp.getPinNum() == s.getPinNum()) {
						pinsOfTheSameNet.add(i);
					}
				}
				if (pinsOfTheSameNet.isEmpty()) {
					for (int i = 0; i < S.size(); i++) {
						Pin temp = S.get(i);
						if (temp.getPinNum() == s.getPinNum()) {
							pinsOfTheSameNet.add(i);
						}
					}
					randomInt = randomGenerator.nextInt(pinsOfTheSameNet.size());
					t = S.get(pinsOfTheSameNet.get(randomInt));
					T.add(t);
					int x = pinsOfTheSameNet.get(randomInt);
					S.remove(x);
				} else {
					randomInt = randomGenerator.nextInt(pinsOfTheSameNet.size());
					t = T.get(pinsOfTheSameNet.get(randomInt));
					T.add(t);
				}
			}
			
			System.out.print("pin s "+s.getPinNum()+" "+s.getIndex()+" pin t "+t.getPinNum()+" "+t.getIndex()+"\n");
		}
		return 1;
	}
	public void printBoard() {
		System.out.println("layer 0:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.num_of_pins; x++) {
				System.out.print(channel[y][x][0] + " ");
			}
			System.out.println(" ");
		}
		System.out.println(" ");
		System.out.println("layer 1:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.num_of_pins; x++) {
				System.out.print(channel[y][x][1] + " ");
			}
			System.out.println(" ");
		}

	}
	public void printUnconnectedPins() {
		for(int i=0; i < S.size();i++) {
			Pin temp = S.get(i);
			System.out.print( temp.getPinNum()+" ");
			System.out.print( temp.getIndex()+" ");
			System.out.print( temp.isOnUpper());
			System.out.println();
			
		}
	}
	public static void main(String[] args) {
		ArrayList<Integer> out = new ArrayList<Integer>(Arrays.asList(2, 3,1));
		ArrayList<Integer> in = new ArrayList<Integer>(Arrays.asList(1,2,3));
		Genotype s = new Genotype(in, out, 2);
		//int y = s.getYindexOfPin(new Pin(1,0,false));
		//System.out.print(y);
		//s.printBoard();
		/*
		s.printUnconnectedPins();
		s.randomSolution();
		*/
		s.connectPins(new Pin(1,0,false),new Pin(1,2,true));
	}
}

