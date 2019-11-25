package ProjectB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.HashSet;
enum Direction {
	UP, DOWN, LEFT, RIGHT
}
public class Genotype {
	static int layers = 2;
	int numOfPins;
	Integer[][][] channel;
	int yind;
	int numOfRows;
	ArrayList<Pin> S;
	ArrayList<Pin> T;
	int maxExtension;
	double F1;
	double F2;
	double fitness;
	public Genotype(ArrayList<Integer> inputs, ArrayList<Integer> outputs, int ymin) {
		this.yind = ThreadLocalRandom.current().nextInt(2*ymin, 4*ymin+1);
		this.numOfRows = yind+2;
		this.maxExtension = 10;
		this.numOfPins = inputs.size();
		this.S = new ArrayList<Pin>();
		this.T = new ArrayList<Pin>();
		this.F1 = -1;
		this.F2 = -1;
		this.fitness = -1;
		this.channel = new Integer[this.numOfRows][this.numOfPins][layers];
		for(int i = 0;i<inputs.size();i++) {
			S.add(new Pin(inputs.get(i),i,false));
		}
		for(int i = 0;i<outputs.size();i++) {
			S.add(new Pin(outputs.get(i),i,true));
		}
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
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
	public Genotype(Integer[][][] _channel,int _numOfRows,int _numOfPins) {
		this.numOfRows = _numOfRows;
		this.yind = _numOfRows - 2;
		this.maxExtension = 10;
		this.numOfPins = _numOfPins;
		this.F1 = -1;
		this.F2 = -1;
		this.fitness = -1;
		this.channel = new Integer[this.numOfRows][this.numOfPins][layers];
		for(int z = 0; z < 2; z++) {
			for(int row = 0; row < _numOfRows;row++) {
				for(int col = 0; col < _numOfPins; col++) {
					channel[row][col][z] = _channel[row][col][z];
				}
			}
		}
		calcF1();
		calcF2();
	}
	public int getNumOfPins() {
		return numOfPins;
	}
	
	private void copyChannel(Integer[][][] tempChannel) {
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
				for (int z = 0; z < layers; z++) {
					tempChannel[y][x][z] = channel[y][x][z];
				}
			}
		}
	}
	public void setFitness(double _fitness) {
		fitness = _fitness;
	}
	public double getFitness() {
		return fitness;
	}
	public Integer[][][] getChannel(){
		Integer[][][] newChannel = new Integer[this.numOfRows][this.numOfPins][layers];
		for(int z = 0; z < 2; z++) {
			for(int row = 0; row < numOfRows;row++) {
				for(int col = 0; col < numOfPins; col++) {
					newChannel[row][col][z] = channel[row][col][z];
				}
			}
		}
		
		return newChannel;
	}
	//returns num of rows without counting the first and the last row
	public int getNumOfRows() {
		return yind;
	}
	private void printTempChannel(Integer[][][] tempChannel) {
		System.out.println("layer 0:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
				System.out.print(tempChannel[y][x][0] + " ");
			}
			System.out.println(" ");
		}
		System.out.println(" ");
		System.out.println("layer 1:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
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
	
	private int indexOfObstacle(Integer[][][] tempChannel,Direction dir,int row,int column,int layer,int pinNum) {
		int index = 0;
		if(dir == Direction.DOWN) {
			index = row;
			for(int y = row+1;y<numOfRows;y++) {
				if(tempChannel[y][column][layer] == 0 || tempChannel[y][column][layer] == pinNum) {
					index = y;
				}else {
					return index;
				}
			}
		}else if(dir == Direction.UP) {
			index = row;
			for(int y = row - 1;y>0;y--) {
				if(tempChannel[y][column][layer] == 0 || tempChannel[y][column][layer] == pinNum) {
					index = y;
				}else {
					return index;
				}
			}
		}else if(dir == Direction.LEFT) {
			index = column;
			for(int x = column - 1 ;x>=0;x--) {
				if(tempChannel[row][x][layer] == 0 || tempChannel[row][x][layer] == pinNum) {
					index = x;
				}else {
					return index;
				}
			}
		}else if(dir == Direction.RIGHT) {
			index = column;
			for(int x = column+1;x<numOfPins;x++) {
				if(tempChannel[row][x][layer] == 0 || tempChannel[row][x][layer] == pinNum) {
					index = x;
				}else {
					return index;
				}
			}
		}
		return index;
	}
	private void updateTempChannel(Integer[][][] tempChannel,Direction dir,int row,int column,int layer,int index,int pinNum) {
		if(dir == Direction.DOWN) {
			for(int y = row;y<=index;y++) {
				if(tempChannel[y][column][layer] == 0 || tempChannel[y][column][layer] ==  pinNum) {
					tempChannel[y][column][layer] = pinNum;
				}else {
					return;
				}
				
			}
		}else if(dir == Direction.UP) {
			for(int y = row;y>=index;y--) {
				if(tempChannel[y][column][layer] == pinNum || tempChannel[y][column][layer] == 0) {
					tempChannel[y][column][layer] = pinNum;
				}else {
					return;
				}
				
			}
		}else if(dir == Direction.LEFT) {
			for(int x = column;x>=index;x--) {
				if(tempChannel[row][x][layer] ==  pinNum || tempChannel[row][x][layer] == 0) {
					tempChannel[row][x][layer] = pinNum;
				}else {
					return;
				}
				
			}
		}else if(dir == Direction.RIGHT) {
			for(int x = column;x<=index;x++) {
				if(tempChannel[row][x][layer] ==  pinNum || tempChannel[row][x][layer] == 0) {
					tempChannel[row][x][layer] = pinNum;
				}else {
					return;
				}
			}
		}
	}
	private int randomNumInRange(int x,int y) {
		int randomNum = 0;
		if(x>y) {
			randomNum = ThreadLocalRandom.current().nextInt(y,x+1);
		}else {
			randomNum = ThreadLocalRandom.current().nextInt(x,y+1);
		}
		return randomNum;
	}
	private int getYindexOfPin(Pin pin) {
		if(pin.isOnUpper()) {
			return 0;
		}
		return numOfRows-1;
	}
	public void calcF1() {
		this.F1 = (1/(double)yind);
	}
	public double getF1() {
		return F1;
	}
	public double getF2() {
		return F2;
	}
	// net length of net i of net segments according to the preferred direction of the layer
	//preferred layer for vertical lines is 0
	//preferred layer for horizontal lines is 1
	private int lenthOfSegmentsInPreferredLayer(int net) {
		int verticalSegments = 0;
		int horizontalSegments = 0;
		for(int row = 2; row < numOfRows - 1; row++) {
			 for(int col = 0; col < numOfPins; col++) {
				 if(channel[row-1][col][0] == net && channel[row][col][0] == net) {
					 verticalSegments++;
				 }
			 }
		}
		for(int row = 1; row < numOfRows - 1; row++) {
			 for(int col = 1; col < numOfPins; col++) {
				 if(channel[row][col-1][1] == net && channel[row][col][1] == net) {
					 horizontalSegments++;
				 }
			 }
			 
		 }
		
		return verticalSegments + horizontalSegments;
	}
	// net length of net i of net segments opposite to the preferred direction of the layer
	//preferred layer for vertical lines is 0
	//preferred layer for horizontal lines is 1
	private int lenthOfSegmentsNotInPreferredLayer(int net) {
		int verticalSegments = 0;
		int horizontalSegments = 0;
		for(int row = 2; row < numOfRows - 1; row++) {
			 for(int col = 0; col < numOfPins; col++) {
				 if(channel[row-1][col][1] == net && channel[row][col][1] == net) {
					 verticalSegments++;
				 }
			 }
		}
		for(int row = 1; row < numOfRows - 1; row++) {
			 for(int col = 1; col < numOfPins; col++) {
				 if(channel[row][col-1][0] == net && channel[row][col][0] == net) {
					 horizontalSegments++;
				 }
			 }
			 
		 }/*
		System.out.println("net: "+net);
		System.out.println("vertical: "+verticalSegments);
		System.out.println("horizontal: "+horizontalSegments);
		*/
		return verticalSegments + horizontalSegments;
	}
	private int countNumOfVias() {
		int numOfVias = 0;
		for(int row = 1; row < numOfRows-1; row++) {
			for(int col = 0; col < numOfPins; col++) {
				if(channel[row][col][0] != 0 && channel[row][col][1] == channel[row][col][0]) {
					numOfVias++;
				}
			}
		}
		return numOfVias;
	}
	public void calcF2() {
		HashSet<Integer> set = new HashSet<>();
		double a = 1.001;
		double b = 2;
		double sum = 0;
		for(int i = 0 ; i < numOfPins; i++) {
			if(!set.contains(channel[0][i][0])){
				set.add(channel[0][i][0]);
				int net = -channel[0][i][0];
				sum += lenthOfSegmentsInPreferredLayer(net) + a*lenthOfSegmentsNotInPreferredLayer(net);
						
			}
		}
		sum+= countNumOfVias()*b;
		F2 = 1/sum;
	}
	public int connectPins(Pin s,Pin t) {
		Integer[][][] tempChannel = new Integer[this.numOfRows][this.numOfPins][layers];
		copyChannel(tempChannel);
		//printTempChannel(tempChannel);
		int maxNumOfIterations = 3*(Math.abs(s.getIndex()-t.getIndex()))+yind+10;
		boolean foundSolution = false;
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
		
		while(iterNum < maxNumOfIterations ) {
			if (iterNum == 0) {
				sLayer = layerOfExtention(sDirection);
				tLayer = layerOfExtention(tDirection);
				if(channel[yIndexOfS][xIndexOfS][sLayer] != 0 && channel[yIndexOfS][xIndexOfS][sLayer] != s.getPinNum() 
					&& channel[yIndexOfS][xIndexOfS][(sLayer+1)%2] != 0 && channel[yIndexOfS][xIndexOfS][(sLayer+1)%2] != s.getPinNum()) {
					return 0;
				}
				if(channel[yIndexOfT][xIndexOfT][tLayer] != 0 && channel[yIndexOfT][xIndexOfT][tLayer] != t.getPinNum() 
						&& channel[yIndexOfT][xIndexOfT][(tLayer+1)%2] != 0 && channel[yIndexOfT][xIndexOfT][(tLayer+1)%2] != t.getPinNum()) {
					return 0;
				}
				if(channel[yIndexOfS][xIndexOfS][sLayer] != 0 && channel[yIndexOfS][xIndexOfS][sLayer] != s.getPinNum()) {
					sLayer = (sLayer+1)%2;
				}
				if(channel[yIndexOfT][xIndexOfT][tLayer] != 0 && channel[yIndexOfT][xIndexOfT][tLayer] != t.getPinNum() ) {
					tLayer = (sLayer+1)%2;
				}
				
				int sObstacleIndex = indexOfObstacle(tempChannel, sDirection, yIndexOfS, xIndexOfS, sLayer,s.getPinNum());
				int tObstacleIndex = indexOfObstacle(tempChannel, tDirection, yIndexOfT, xIndexOfT, tLayer,t.getPinNum());
			
				int newYindexForS = randomNumInRange(yIndexOfS, sObstacleIndex);
				int newYindexForT = randomNumInRange(yIndexOfT, tObstacleIndex);
				
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
				if(channel[yIndexOfS][xIndexOfS][sLayer] != 0 && channel[yIndexOfS][xIndexOfS][sLayer] != s.getPinNum()) {
					continue;
				}
				if(channel[yIndexOfT][xIndexOfT][tLayer] != 0 && channel[yIndexOfT][xIndexOfT][tLayer] != t.getPinNum() ) {
					continue;
				}
				int minXForS = indexOfObstacle(tempChannel, Direction.LEFT, yIndexOfS, xIndexOfS, sLayer,s.getPinNum());
				int maxXForS = indexOfObstacle(tempChannel, Direction.RIGHT, yIndexOfS, xIndexOfS, sLayer,s.getPinNum());
				int minXForT = indexOfObstacle(tempChannel, Direction.LEFT, yIndexOfT, xIndexOfT, tLayer,t.getPinNum());
				int maxXForT = indexOfObstacle(tempChannel, Direction.RIGHT, yIndexOfT, xIndexOfT, tLayer,t.getPinNum());
				int newXindexForS  = randomNumInRange(minXForS,maxXForS);
				int newXindexForT =  randomNumInRange(minXForT,maxXForT);
				if((yIndexOfS == getYindexOfPin(t)-1 || yIndexOfS == getYindexOfPin(t)+1) && minXForS == t.getIndex() ) {
					foundSolution = true;
					newXindexForS = minXForS;
					
				}else if((yIndexOfS == getYindexOfPin(t)-1 || yIndexOfS == getYindexOfPin(t)+1) && maxXForS == t.getIndex()) {
					foundSolution = true;
					newXindexForS = maxXForS;
					
				}else if((yIndexOfT == getYindexOfPin(s)-1 || yIndexOfT == getYindexOfPin(s)+1) && minXForT == s.getIndex() ) {
					foundSolution = true;
					newXindexForT = minXForT;
					
				}else if((yIndexOfT == getYindexOfPin(s)-1 || yIndexOfT == getYindexOfPin(s)+1) && maxXForT == s.getIndex()) {
					foundSolution = true;
					newXindexForT = maxXForT;
					
				} else {
					if (tempChannel[yIndexOfS][minXForS][sLayer] == s.getPinNum()) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, s.getPinNum());
						int endX = t.getIndex();
						int endY = getYindexOfPin(t);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, s.getPinNum(), minXForS, yIndexOfS, endX,
								endY, sLayer);
						if (foundSolution) {
							newXindexForS = minXForS;
						}
					}
					if (tempChannel[yIndexOfS][maxXForS][sLayer] == s.getPinNum() && !foundSolution) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, s.getPinNum());
						int endX = t.getIndex();
						int endY = getYindexOfPin(t);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, s.getPinNum(), maxXForS, yIndexOfS, endX,
								endY, sLayer);
						if (foundSolution) {
							newXindexForS = maxXForS;
						}
					}
					if (tempChannel[yIndexOfT][minXForT][tLayer] == t.getPinNum() && !foundSolution) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, t.getPinNum());
						int endX = s.getIndex();
						int endY = getYindexOfPin(s);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, t.getPinNum(), minXForT, yIndexOfT, endX,
								endY, tLayer);
						if (foundSolution) {
							newXindexForT = minXForT;
						}

					}
					if (tempChannel[yIndexOfT][maxXForT][tLayer] == t.getPinNum() && !foundSolution) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, t.getPinNum());
						int endX = s.getIndex();
						int endY = getYindexOfPin(s);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, t.getPinNum(), maxXForT, yIndexOfT, endX,
								endY, tLayer);
						if (foundSolution) {
							newXindexForT = maxXForT;
						}
					}
				}
				
				
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
				sLayer = layerOfExtention(sDirection);
				tLayer = layerOfExtention(tDirection);
				if(channel[yIndexOfS][xIndexOfS][sLayer] != 0 && channel[yIndexOfS][xIndexOfS][sLayer] != s.getPinNum()) {
					continue;
				}
				if(channel[yIndexOfT][xIndexOfT][tLayer] != 0 && channel[yIndexOfT][xIndexOfT][tLayer] != t.getPinNum() ) {
					continue;
				}
				int minYForS = indexOfObstacle(tempChannel, Direction.UP, yIndexOfS, xIndexOfS, sLayer,s.getPinNum());
				int maxYForS = indexOfObstacle(tempChannel, Direction.DOWN, yIndexOfS, xIndexOfS, sLayer,s.getPinNum());
				int minYForT = indexOfObstacle(tempChannel, Direction.UP, yIndexOfT, xIndexOfT, tLayer,t.getPinNum());
				int maxYForT = indexOfObstacle(tempChannel, Direction.DOWN, yIndexOfT, xIndexOfT, tLayer,t.getPinNum());
				int newYindexForS = randomNumInRange(minYForS, maxYForS);
				int newYindexForT = randomNumInRange(minYForT, maxYForT);
				if((minYForS == getYindexOfPin(t)+1) && xIndexOfS == t.getIndex() ) {
					foundSolution = true;
					newYindexForS = minYForS;
					
				}else if((maxYForS == getYindexOfPin(t)-1 ) && xIndexOfS == t.getIndex()) {
					foundSolution = true;
					newYindexForS = maxYForS;
					
				}else if((minYForT == getYindexOfPin(s)+1) && xIndexOfT == s.getIndex() ) {
					foundSolution = true;
					newYindexForT = minYForT;
					
				}else if((maxYForT == getYindexOfPin(s)-1) && xIndexOfT == s.getIndex()) {
					foundSolution = true;
					newYindexForT = maxYForT;
					
				} else {
					if (tempChannel[minYForS][xIndexOfS][sLayer] == s.getPinNum()) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, s.getPinNum());
						int endX = t.getIndex();
						int endY = getYindexOfPin(t);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, s.getPinNum(), xIndexOfS, minYForS, endX,
								endY, sLayer);
						if (foundSolution) {
							newYindexForS = minYForS;
						}
					}
					if (tempChannel[maxYForS][xIndexOfS][sLayer] == s.getPinNum()  && !foundSolution) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, s.getPinNum());
						int endX = t.getIndex();
						int endY = getYindexOfPin(t);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, s.getPinNum(), xIndexOfS, maxYForS, endX,
								endY, sLayer);
						if (foundSolution) {
							newYindexForS = maxYForS;
						}
					}
					if (tempChannel[minYForT][xIndexOfT][tLayer] == t.getPinNum() && !foundSolution) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, t.getPinNum());
						int endX = s.getIndex();
						int endY = getYindexOfPin(s);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, t.getPinNum(), xIndexOfT, minYForT, endX,
								endY, tLayer);
						if (foundSolution) {
							newYindexForT = minYForT;
						}

					}
					if (tempChannel[maxYForT][xIndexOfT][tLayer] == t.getPinNum() && !foundSolution ) {
						Integer[][][] channelForChecking = mergeChannels(tempChannel, t.getPinNum());
						int endX = s.getIndex();
						int endY = getYindexOfPin(s);
						if (endY == 0) {
							endY = 1;
						} else {
							endY--;
						}
						foundSolution = recIsTherePath(channelForChecking, t.getPinNum(), xIndexOfT, maxYForT, endX,
								endY, tLayer);
						if (foundSolution) {
							newYindexForT = maxYForT;
						}

					}
				}

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
			if(checkIfTwoPinsConnected(tempChannel,s,t) == true) {
				//System.out.println("found solution");
				findShortestPathAndConnect(tempChannel, s, t);
				//printTempChannel(tempChannel);
				//printBoard();
				return 1;
			}
			iterNum++;
		}
		//printTempChannel(tempChannel);

		return 0;
	}
	private Integer[][][] mergeChannels(Integer[][][] tempChannel,int pinNum){
		Integer[][][] channelForChecking = new Integer[this.numOfRows][this.numOfPins][layers];
		for (int z = 0; z < 2; z++) {
			for (int y = 0; y < this.numOfRows; y++) {
				for (int x = 0; x < this.numOfPins; x++) {
					if( tempChannel[y][x][z] == pinNum) {
						channelForChecking[y][x][z] = pinNum;
					}else {
						channelForChecking[y][x][z] = 0;
					}
				}
			}
		}
		return channelForChecking;
	}
	private boolean recIsTherePath(Integer[][][] tempChannel,int pin_num,int startX,int startY,int endX,int endY,int z) {
		
		if(startX >= numOfPins || startX < 0 || startY < 1 || startY >= numOfRows-1) {
			return false;
		}
		if(tempChannel[startY][startX][z] != pin_num) {
			return false;
		}
		if(startX == endX && startY == endY) {
			return true;
		}
		tempChannel[startY][startX][z]= -pin_num;
		int z2 = (z == 1)? 0 : 1;
		if(recIsTherePath(tempChannel,pin_num,startX+1,startY,endX,endY,z) || 
		   recIsTherePath(tempChannel,pin_num,startX-1,startY,endX,endY,z) ||
		   recIsTherePath(tempChannel,pin_num,startX,startY+1,endX,endY,z) ||
		   recIsTherePath(tempChannel,pin_num,startX,startY-1,endX,endY,z) ||
		   recIsTherePath(tempChannel,pin_num,startX,startY,endX,endY,z2)) {
			return true;
		}
		tempChannel[startY][startX][z]= pin_num;
		return false;
	}
	private boolean checkIfTwoPinsConnected(Integer[][][] tempChannel,Pin s,Pin e) {
		int startX = s.getIndex();
		int startY = getYindexOfPin(s);
		if(startY == 0) {
			startY=1;
		}else {
			startY--;
		}
		int endX = e.getIndex();
		int endY = getYindexOfPin(e);
		if(endY ==0) {
			endY = 1;
		}else {
			endY--;
		}
		int pin_num = s.getPinNum();
		Integer[][][] channelForChecking = new Integer[this.numOfRows][this.numOfPins][layers];
		for (int z = 0; z < 2; z++) {
			for (int y = 0; y < this.numOfRows; y++) {
				for (int x = 0; x < this.numOfPins; x++) {
					if(tempChannel[y][x][z] == pin_num) {
						channelForChecking[y][x][z] = pin_num;
					}else {
						channelForChecking[y][x][z] = 0;
					}
				}
			}
		}
		return recIsTherePath(channelForChecking, pin_num, startX, startY, endX, endY, 0);
	}
	private boolean findShortestPathAndConnect(Integer[][][] tempChannel,Pin s,Pin e) {
		int startX = s.getIndex();
		int startY = getYindexOfPin(s);
		if(startY == 0) {
			startY=1;
		}else {
			startY--;
		}
		int endX = e.getIndex();
		int endY = getYindexOfPin(e);
		if(endY ==0) {
			endY = 1;
		}else {
			endY--;
		}
		int pin_num = s.getPinNum();
		Point[][][] graph = new Point[this.numOfRows][this.numOfPins][layers];
		for (int z = 0; z < 2; z++) {
			for (int y = 0; y < this.numOfRows; y++) {
				for (int x = 0; x < this.numOfPins; x++) {
					if( tempChannel[y][x][z] == pin_num) {
						graph[y][x][z] = new Point(y,x,z);
					}else {
						graph[y][x][z] = null;
					}
				}
			}
		}
		int endZ = 0;
		boolean reachedDest = false;
		Queue<Point> queue = new LinkedList<>();
		graph[startY][startX][0].setDiscovered(true);
		queue.add(graph[startY][startX][0]);
		while(!queue.isEmpty() && !reachedDest ) {
			Point p = queue.remove();
			if(p.getX() == endX && p.getY() == endY) {
				reachedDest = true;
				endZ = p.getZ();

			}else {
				if(p.getY() < numOfRows-2 && graph[p.getY()+1][p.getX()][p.getZ()] != null &&
						!graph[p.getY()+1][p.getX()][p.getZ()].discovered) {
					graph[p.getY()+1][p.getX()][p.getZ()].setDiscovered(true);
					graph[p.getY()+1][p.getX()][p.getZ()].setParetn(p);
					queue.add(graph[p.getY()+1][p.getX()][p.getZ()]);
				}
				if(p.getY() > 1 && graph[p.getY()-1][p.getX()][p.getZ()] != null &&
						!graph[p.getY()-1][p.getX()][p.getZ()].discovered) {
					graph[p.getY()-1][p.getX()][p.getZ()].setDiscovered(true);
					graph[p.getY()-1][p.getX()][p.getZ()].setParetn(p);
					queue.add(graph[p.getY()-1][p.getX()][p.getZ()]);
				}
				if(p.getX() < numOfPins-1 && graph[p.getY()][p.getX()+1][p.getZ()] != null && 
						!graph[p.getY()][p.getX()+1][p.getZ()].discovered) {
					graph[p.getY()][p.getX()+1][p.getZ()].setDiscovered(true);
					graph[p.getY()][p.getX()+1][p.getZ()].setParetn(p);
					queue.add(graph[p.getY()][p.getX()+1][p.getZ()]);
				}
				if(p.getX() > 0 && graph[p.getY()][p.getX()-1][p.getZ()] != null && 
						!graph[p.getY()][p.getX()-1][p.getZ()].discovered) {
					graph[p.getY()][p.getX()-1][p.getZ()].setDiscovered(true);
					graph[p.getY()][p.getX()-1][p.getZ()].setParetn(p);
					queue.add(graph[p.getY()][p.getX()-1][p.getZ()]);
				}
				int z2 = (p.getZ() == 1)? 0 : 1;
				if( graph[p.getY()][p.getX()][z2] != null && !graph[p.getY()][p.getX()][z2].discovered ) {
					graph[p.getY()][p.getX()][z2].setDiscovered(true);
					graph[p.getY()][p.getX()][z2].setParetn(p);
					queue.add(graph[p.getY()][p.getX()][z2]);
				}
			}
		}
		boolean pathDidNotEnd = true;
		Point p = graph[endY][endX][endZ];
		while(pathDidNotEnd && p != null) {
			if(channel[p.getY()][p.getX()][p.getZ()]!=pin_num) {
				channel[p.getY()][p.getX()][p.getZ()] = pin_num;
			}
			if(p.getX() == startX && p.getY() == startY) {
				pathDidNotEnd = false;
			}
			p = p.parent;
		}
		return true;
	}
	private void addRowOnChannel() {
		int y = numOfRows-2;
		int yOfNewRow = randomNumInRange(1, y);
		//System.out.println("new row index = "+yOfNewRow);
		Integer[][][] newChannel = new Integer[this.numOfRows+1][this.numOfPins][layers];
		this.numOfRows++;
		this.yind = this.numOfRows-2;
		for(int i = 0; i < numOfPins;i++) {
			newChannel[0][i][0] = channel[0][i][0];
			newChannel[0][i][1] = channel[0][i][1];
			newChannel[numOfRows-1][i][0] = channel[numOfRows-2][i][0];
			newChannel[numOfRows-1][i][1] = channel[numOfRows-2][i][1];
		}
		if(yOfNewRow == 1) {
			for(int i = 0; i < numOfPins;i++) {
				if(channel[yOfNewRow][i][0] == -channel[0][i][0]) {
					newChannel[yOfNewRow][i][0] = channel[yOfNewRow][i][0];
				}else {
					newChannel[yOfNewRow][i][0] = 0;

				}
				if(channel[yOfNewRow][i][1] == -channel[0][i][1]) {
					newChannel[yOfNewRow][i][1] = channel[yOfNewRow][i][1];
				}else {
					newChannel[yOfNewRow][i][1] = 0;
				}
			}
			for(int row = yOfNewRow; row<numOfRows-2; row++) {
				for(int col = 0; col<numOfPins; col++) {
					newChannel[row+1][col][0] = channel[row][col][0];
					newChannel[row+1][col][1] = channel[row][col][1];
				}
			}
		}else if(yOfNewRow == numOfRows-3) {
			for(int row = 1; row<=numOfRows-3; row++) {
				for(int col = 0; col<numOfPins; col++) {
					newChannel[row][col][0] = channel[row][col][0];
					newChannel[row][col][1] = channel[row][col][1];
				}
			}
			for(int i = 0; i < numOfPins;i++) {
				if(channel[yOfNewRow][i][0] == -channel[numOfRows-2][i][0]) {
					newChannel[yOfNewRow+1][i][0] = channel[yOfNewRow][i][0];
				}else {
					newChannel[yOfNewRow+1][i][0] = 0;
				}
				if(channel[yOfNewRow][i][1] == -channel[numOfRows-2][i][1]) {
					newChannel[yOfNewRow+1][i][1] = channel[yOfNewRow][i][1];
				}else {
					newChannel[yOfNewRow+1][i][1] = 0;
				}
			}
		}else {
			for(int row = 1; row < yOfNewRow;row++) {
				for(int col = 0; col < numOfPins; col++) {
					newChannel[row][col][0] = channel[row][col][0];
					newChannel[row][col][1] = channel[row][col][1];
				}
			}
			for(int col = 0; col < numOfPins; col++) {
				if(channel[yOfNewRow][col][0] !=0 && channel[yOfNewRow-1][col][0] == channel[yOfNewRow][col][0]) {
					newChannel[yOfNewRow][col][0] = channel[yOfNewRow][col][0];
				}else {
					newChannel[yOfNewRow][col][0] = 0;
				}
				if(channel[yOfNewRow][col][1] !=0 && channel[yOfNewRow-1][col][1] == channel[yOfNewRow][col][1]) {
					newChannel[yOfNewRow][col][1] = channel[yOfNewRow][col][1];
				}else {
					newChannel[yOfNewRow][col][1] = 0;
				}
			}
			for(int row = yOfNewRow; row < numOfRows-2;row++) {
				for(int col = 0; col < numOfPins; col++) {
					newChannel[row+1][col][0] = channel[row][col][0];
					newChannel[row+1][col][1] = channel[row][col][1];
				}
			}
		}
		channel = newChannel;
		
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
			T.add(s);
			int numOfExtension = 0;
			boolean isPinsConnected = false;
			while(numOfExtension < maxExtension && !isPinsConnected) {
				isPinsConnected = (connectPins(s,t) == 1)? true:false;
				if(!isPinsConnected) {
					addRowOnChannel();
					numOfExtension++;
				}
			}
			if(!isPinsConnected) {
				return 0;
			}
			//System.out.print("pin s "+s.getPinNum()+" "+s.getIndex()+" pin t "+t.getPinNum()+" "+t.getIndex()+"\n");
		}
		calcF1();
		calcF2();
		return 1;
	}
	public void printBoard() {
		System.out.println("layer 0:");
		for (int y = 0; y < this.numOfRows; y++) {
			if(y != 0 && y != numOfRows-1) {
				System.out.print(" ");
			}
			
			for (int x = 0; x < this.numOfPins; x++) {
				if(y != 0 && y != numOfRows-1) {
					System.out.print(channel[y][x][0] + "  ");
				}else {
					System.out.print(channel[y][x][0] + " ");
				}
				
			}
			System.out.println();
		}
		System.out.println(" ");
		System.out.println("layer 1:");
		for (int y = 0; y < this.numOfRows; y++) {
			if (y != 0 && y != numOfRows - 1) {
				System.out.print(" ");
			}
			for (int x = 0; x < this.numOfPins; x++) {
				if (y != 0 && y != numOfRows - 1) {
					System.out.print(channel[y][x][1] + "  ");
				} else {
					System.out.print(channel[y][x][1] + " ");
				}
			}

			System.out.println();
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
		/*s.connectPins(new Pin(1,0,false),new Pin(1,2,true));
		s.addRowOnChannel();
		*/
		if(s.randomSolution() == 1) {
			System.out.println("solution found");
			s.printBoard();
			System.out.println("F1: "+s.getF1());
			System.out.println("F2: "+s.getF2());
		}else {
			System.out.println("no solution");
		}
	}
}

