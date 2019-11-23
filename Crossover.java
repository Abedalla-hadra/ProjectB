package ProjectB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.HashSet;
public class Crossover {
	Integer[][][] parent1;
	Integer[][][] parent2;
	int numOfPins;
	int numOfRowsP1;
	int numOfRowsP2;
	public Crossover(Genotype p1,Genotype p2){
		numOfPins = p1.getNumOfPins();
		numOfRowsP1 = p1.getNumOfRows()+2;
		numOfRowsP2 = p2.getNumOfRows()+2;
		parent1 = p1.getChannel();
		parent2 = p2.getChannel();
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
	private int transferRoutingWithinRange(Integer[][][] p,Integer[][][] subP,int startY,int startX,int pinNum
			,int minX,int maxX,int maxY,int z) {
		if(startX > maxX || startX < minX || startY < 0 || startY > maxY) {
			return 0;		
		}
		if(p[startY][startX][z] == -pinNum && (startY == 0 || startY == maxY)) {
			return 1;
		}
		if(p[startY][startX][z] != pinNum) {
			return 0;
		}
		p[startY][startX][z] = -pinNum;
		subP[startY][startX][z] = pinNum;
		int res1 = transferRoutingWithinRange(p,subP,startY+1,startX,pinNum,minX,maxX,maxY,z);
		int res2 = transferRoutingWithinRange(p,subP,startY-1,startX,pinNum,minX,maxX,maxY,z);
		int res3 = transferRoutingWithinRange(p,subP,startY,startX+1,pinNum,minX,maxX,maxY,z);
		int res4 = transferRoutingWithinRange(p,subP,startY,startX-1,pinNum,minX,maxX,maxY,z);
		int res5 = transferRoutingWithinRange(p,subP,startY,startX,pinNum,minX,maxX,maxY,(z+1)%2);
		if(res1 == 1 || res2 == 1 || res3 == 1 || res4 == 1 || res5 == 1) {
			p[startY][startX][z] = pinNum;
			return 1;
		}
		p[startY][startX][z] = pinNum;
		subP[startY][startX][z] = 0;

		return 0;
	}
	//return type should be Genotype, but for now it's void for testing
	public void crossoverOp() {
		int xc = randomNumInRange(0,numOfPins-2);
		
		Integer[][][] subP1 = new Integer[numOfRowsP1][numOfPins][2];
		Integer[][][] subP2 = new Integer[numOfRowsP2][numOfPins][2];
		initializeSubs(subP1,subP2);
		updateSubP1(xc,subP1);
		updateSubP2(xc,subP2);
		//printChannel(parent1,numOfRowsP1);
		System.out.println(xc);
		printChannel(subP1,numOfRowsP1);
		subP1 = deleteUnoccupiedRows(subP1,numOfRowsP1);
		subP2 = deleteUnoccupiedRows(subP2,numOfRowsP2);
		int subP1NumRows = subP1.length;
		int subP2NumRows = subP2.length;
		printChannel(subP2,subP2.length);
		//printChannel(subP2,numOfRowsP2);

	}
	private Integer[][][] deleteUnoccupiedRows(Integer[][][] sub,int numOfRows) {
		
		ArrayList<Integer> rowsToKeep = new ArrayList<Integer>();
		for(int row = 1; row < numOfRows-1; row++) {
			int firstLayerLastX = sub[row][0][0];
			int secondLayerLastX = sub[row][0][1];
			boolean rowAdded = false;
			if(row == 1 || row == numOfRows-2 ) {
				rowsToKeep.add(row);
			}else {
				for(int col = 1; col <numOfPins && !rowAdded; col++ ) {
					if(firstLayerLastX != 0 && sub[row][col][0] == firstLayerLastX) {
						rowsToKeep.add(row);
						rowAdded = true;
					}else if(secondLayerLastX != 0 && sub[row][col][1] == secondLayerLastX) {
						rowsToKeep.add(row);
						rowAdded = true;
					}else if(sub[row][col][0] != 0 && sub[row][col][0] == sub[row][col][1]) {
						rowsToKeep.add(row);
						rowAdded = true;
					}
					firstLayerLastX = sub[row][col][0];
					secondLayerLastX = sub[row][col][1];
				}
			}
		}
		int newRowsNum = rowsToKeep.size()+2;
		Integer[][][] newSub = new Integer[newRowsNum][numOfPins][2];
		for(int col = 0 ; col < numOfPins; col++) {
			newSub[0][col][0] = sub[0][col][0];
			newSub[0][col][1] = sub[0][col][1];
			newSub[newRowsNum-1][col][0] = sub[numOfRows-1][col][0];
			newSub[newRowsNum-1][col][1] = sub[numOfRows-1][col][1];
		}
		for (int row = 1; row < newRowsNum - 1; row++) {
			int rowToCopy = rowsToKeep.get(row - 1);
			for (int col = 0; col < numOfPins; col++) {
				newSub[row][col][0] = sub[rowToCopy][col][0];
				newSub[row][col][1] = sub[rowToCopy][col][1];
			}
		}
		return newSub;
	}
	private void updateSubP1(int xc,Integer[][][] subP1) {
		for(int i = 0; i <= xc; i++) {
			if( parent1[1][i][0] !=0 && parent1[1][i][0] == -parent1[0][i][0]) {
				transferRoutingWithinRange(parent1,subP1,1,i,parent1[1][i][0],0,xc,numOfRowsP1-1,0);
			}else {
				transferRoutingWithinRange(parent1,subP1,1,i,parent1[1][i][1],0,xc,numOfRowsP1-1,1);
			}
			
			if( parent1[numOfRowsP1-2][i][0] !=0 && parent1[numOfRowsP1-2][i][0] == -parent1[numOfRowsP1-1][i][0]) {
				transferRoutingWithinRange(parent1,subP1,numOfRowsP1-2,i,parent1[numOfRowsP1-2][i][0],0,xc,numOfRowsP1-1,0);
			}else {
				transferRoutingWithinRange(parent1,subP1,numOfRowsP1-2,i,parent1[numOfRowsP1-2][i][1],0,xc,numOfRowsP1-1,1);
			}
			
		}
	}
	private void updateSubP2(int xc,Integer[][][] subP2) {
		for(int i = xc+1; i < numOfPins; i++) {
			if( parent2[1][i][0] !=0 && parent2[1][i][0] == -parent2[0][i][0]) {
				transferRoutingWithinRange(parent2,subP2,1,i,parent2[1][i][0],xc+1,numOfPins-1,numOfRowsP2-1,0);
			}else {
				transferRoutingWithinRange(parent2,subP2,1,i,parent2[1][i][1],xc+1,numOfPins-1,numOfRowsP2-1,1);
			}
			
			if( parent2[numOfRowsP2-2][i][0] !=0 && parent2[numOfRowsP2-2][i][0] == -parent2[numOfRowsP2-1][i][0]) {
				transferRoutingWithinRange(parent2,subP2,numOfRowsP2-2,i,parent2[numOfRowsP2-2][i][0],xc+1,numOfPins-1,numOfRowsP2-1,0);
			}else {
				transferRoutingWithinRange(parent2,subP2,numOfRowsP2-2,i,parent2[numOfRowsP2-2][i][1],xc+1,numOfPins-1,numOfRowsP2-1,1);
			}
			
		}
	}
	private void initializeSubs(Integer[][][] subP1,Integer[][][] subP2) {
		for(int z = 0 ; z < 2 ; z++) {
			for(int row = 0; row < numOfRowsP1; row++) {
				for(int col = 0; col < numOfPins; col++) {
					if(row == 0 || row == numOfRowsP1-1) {
						subP1[row][col][z] = parent1[row][col][z];
					}else {
						subP1[row][col][z] = 0;
					}
				}
			}
		}
		for(int z = 0 ; z < 2 ; z++) {
			for(int row = 0; row < numOfRowsP2; row++) {
				for(int col = 0; col < numOfPins; col++) {
					if(row == 0 || row == numOfRowsP2-1) {
						subP2[row][col][z] = parent2[row][col][z];
					}else {
						subP2[row][col][z] = 0;
					}
				}
			}
		}
		
	}
	
	private void printChannel(Integer[][][] tempChannel,int numOfRows) {
		System.out.println("layer 0:");
		for (int y = 0; y < numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
				System.out.print(tempChannel[y][x][0] + " ");
			}
			System.out.println(" ");
		}
		System.out.println(" ");
		System.out.println("layer 1:");
		for (int y = 0; y <numOfRows; y++) {
			for (int x = 0; x < this.numOfPins; x++) {
				System.out.print(tempChannel[y][x][1] + " ");
			}
			System.out.println(" ");
		}
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> out = new ArrayList<Integer>(Arrays.asList(2, 3,1));
		ArrayList<Integer> in = new ArrayList<Integer>(Arrays.asList(1,2,3));
        int count = 0;
        ArrayList<Genotype> channels = new ArrayList<>();
		
		while(count <2) {
			Genotype s = new Genotype(in, out, 2);
			if(s.randomSolution() == 1) {
				channels.add(s);
				count++;
			}

		}
		Crossover cross = new Crossover(channels.get(0),channels.get(1));
		cross.crossoverOp();
	
	}

}