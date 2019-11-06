package ProjectB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Queue;
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
	public Genotype(ArrayList<Integer> inputs, ArrayList<Integer> outputs, int ymin) {
		this.yind = ThreadLocalRandom.current().nextInt(2*ymin, 4*ymin+1);
		this.numOfRows = yind+2;
		this.maxExtension = 10;
		this.numOfPins = inputs.size();
		this.S = new ArrayList<Pin>();
		this.T = new ArrayList<Pin>();
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
	private void copyChannel(Integer[][][] tempChannel) {
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
				for (int z = 0; z < layers; z++) {
					tempChannel[y][x][z] = channel[y][x][z];
				}
			}
		}
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
	private int indexOfObstacle(Integer[][][] tempChannel,Direction dir,int row,int column,int layer) {
		int index = 0;
		if(dir == Direction.DOWN) {
			index = row;
			for(int y = row+1;y<numOfRows;y++) {
				if(channel[y][column][layer] == 0 && tempChannel[y][column][layer] == 0) {
					index = y;
				}
			}
		}else if(dir == Direction.UP) {
			index = row;
			for(int y = row - 1;y>0;y--) {
				if(channel[y][column][layer] == 0 && tempChannel[y][column][layer] == 0) {
					index = y;
				}
			}
		}else if(dir == Direction.LEFT) {
			index = column;
			for(int x = column - 1 ;x>=0;x--) {
				if(channel[row][x][layer] == 0 && tempChannel[row][x][layer] == 0 ) {
					index = x;
				}
			}
		}else if(dir == Direction.RIGHT) {
			index = column;
			for(int x = column+1;x<numOfPins;x++) {
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
	public int connectPins(Pin s,Pin t) {
		Integer[][][] tempChannel = new Integer[this.numOfRows][this.numOfPins][layers];
		copyChannel(tempChannel);
		//printTempChannel(tempChannel);
		int maxNumOfIterations = 3*(Math.abs(s.getIndex()-t.getIndex()))+yind+10;
		//System.out.println(yind);
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
		
		while(iterNum < maxNumOfIterations ) {
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
				int newYindexForS = randomNumInRange(yIndexOfS, sObstacleIndex);
				int newYindexForT = randomNumInRange(yIndexOfT, tObstacleIndex);
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
				int newXindexForS = randomNumInRange(minXForS,maxXForS);
				int newXindexForT = randomNumInRange(minXForT,maxXForT);
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
				int newYindexForS = randomNumInRange(minYForS, maxYForS);
				int newYindexForT = randomNumInRange(minYForT, maxYForT);
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
	private boolean recIsTherePath(Integer[][][] tempChannel,int pin_num,int startX,int startY,int endX,int endY,int z) {
		if(startX == endX && startY == endY) {
			return true;
		}
		if(startX >= numOfPins || startX < 0 || startY < 0 || startY >= numOfRows) {
			return false;
		}
		if(tempChannel[startY][startX][z] != pin_num) {
			return false;
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
					if(channel[y][x][z] == pin_num || tempChannel[y][x][z] == pin_num) {
						channelForChecking[y][x][z] = pin_num;
					}else {
						channelForChecking[y][x][z] = channel[y][x][z];
					}
				}
			}
		}
		return recIsTherePath(channelForChecking, pin_num, startX, startY, endX, endY, 0);
	}
	private void findShortestPathAndConnect(Integer[][][] tempChannel,Pin s,Pin e) {
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
					if(channel[y][x][z] == pin_num || tempChannel[y][x][z] == pin_num) {
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
				System.out.println("solution found");

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
	}
	private void addRowOnChannel() {
		int y = numOfRows-2;
		int yOfNewRow = randomNumInRange(1, y);
		System.out.println("new row index = "+yOfNewRow);
		Integer[][][] newChannel = new Integer[this.numOfRows+1][this.numOfPins][layers];
		this.numOfRows++;
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
		return 1;
	}
	public void printBoard() {
		System.out.println("layer 0:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
				System.out.print(channel[y][x][0] + " ");
			}
			System.out.println(" ");
		}
		System.out.println(" ");
		System.out.println("layer 1:");
		for (int y = 0; y < this.numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
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
		/*s.connectPins(new Pin(1,0,false),new Pin(1,2,true));
		s.addRowOnChannel();
		*/
		s.randomSolution();
		s.printBoard();
	}
}

