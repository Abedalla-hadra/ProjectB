package ProjectB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.HashSet;
public class Crossover {
	static int layers = 2;
	Integer[][][] parent1;
	Integer[][][] parent2;
	Integer[][][] descendant;
	int descendantRowsNum;
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
	private int transferRoutingWithinRange(Integer[][][] p,Integer[][][] subP,int startY,int startX,int y,int x,int pinNum
			,int minX,int maxX,int maxY,int z) {
		if(x > maxX || x < minX || y < 1 || y > maxY-1) {
			return 0;		
		}
		if((startY != y || x != startX) && p[y][x][z] == pinNum && y == 1 && p[0][x][z] == -pinNum ) {
			return 1;
		}
		if((startY != y || x != startX) && p[y][x][z] == pinNum && y == maxY-1 && p[maxY][x][z] == -pinNum ) {
			return 1;
		}
		if(p[y][x][z] != pinNum) {
			return 0;
		}
		p[y][x][z] = -pinNum;
		subP[y][x][z] = pinNum;
		int res1 = transferRoutingWithinRange(p,subP,startY,startX,y+1,x,pinNum,minX,maxX,maxY,z);
		int res2 = transferRoutingWithinRange(p,subP,startY,startX,y-1,x,pinNum,minX,maxX,maxY,z);
		int res3 = transferRoutingWithinRange(p,subP,startY,startX,y,x+1,pinNum,minX,maxX,maxY,z);
		int res4 = transferRoutingWithinRange(p,subP,startY,startX,y,x-1,pinNum,minX,maxX,maxY,z);
		int res5 = transferRoutingWithinRange(p,subP,startY,startX,y,x,pinNum,minX,maxX,maxY,(z+1)%2);
		if(res1 == 1 || res2 == 1 || res3 == 1 || res4 == 1 || res5 == 1) {
			p[y][x][z] = pinNum;
			return 1;
		}
		p[y][x][z] = pinNum;
		subP[y][x][z] = 0;

		return 0;
	}
	//return type should be Genotype, but for now it's void for testing
	public Genotype crossoverOp() {
		int xc = randomNumInRange(0,numOfPins-2);
		Integer[][][] subP1 = new Integer[numOfRowsP1][numOfPins][2];
		Integer[][][] subP2 = new Integer[numOfRowsP2][numOfPins][2];
		initializeSubs(subP1,subP2);
		updateSubP1(xc,subP1);
		updateSubP2(xc,subP2);
		subP1 = deleteUnoccupiedRows(subP1,numOfRowsP1);
		subP2 = deleteUnoccupiedRows(subP2,numOfRowsP2);
		int subP1NumRows = subP1.length;
		int subP2NumRows = subP2.length;
		if(subP1NumRows > subP2NumRows) {
			int n = subP1NumRows - subP2NumRows;
			for(int i = 0; i < n; i++ ) {
				subP2 = addRowOnChannel(subP2,subP2NumRows);
				subP2NumRows++;
			}
		}else if(subP2NumRows > subP1NumRows) {
			int  n = subP2NumRows - subP1NumRows;
			for(int i = 0; i < n; i++ ) {
				subP1 = addRowOnChannel(subP1,subP1NumRows);
				subP1NumRows++;
			}
			
		}
		descendantRowsNum = subP1NumRows;
		descendant = new Integer[descendantRowsNum][numOfPins][layers];
		initializeDescendant(descendant,subP1,subP2,xc,descendantRowsNum);
		ArrayList<Pin> pinsConnectedP1 = new ArrayList<Pin>();
		ArrayList<Pin> pinsConnectedP2 = new ArrayList<Pin>();
		ArrayList<Pin> pinsNotConnectedP1 = new ArrayList<Pin>();
		ArrayList<Pin> pinsNotConnectedP2 = new ArrayList<Pin>();
		for (int col = 0; col <= xc; col++) {
			if (descendant[1][col][0] == -descendant[0][col][0] || descendant[1][col][1] == -descendant[0][col][1]) {
				int pinNum = -descendant[0][col][0];
				pinsConnectedP1.add(new Pin(pinNum, col, true));
			} else {
				int pinNum = -descendant[0][col][0];
				pinsNotConnectedP1.add(new Pin(pinNum, col, true));
			}
			if (descendant[descendantRowsNum - 2][col][0] == -descendant[descendantRowsNum - 1][col][0] ||
					     descendant[descendantRowsNum - 2][col][1] == -descendant[descendantRowsNum - 1][col][1]) {
				int pinNum = -descendant[descendantRowsNum - 1][col][0];
				pinsConnectedP1.add(new Pin(pinNum, col, false));
			} else {
				int pinNum = -descendant[descendantRowsNum - 1][col][0];
				pinsNotConnectedP1.add(new Pin(pinNum, col, false));
			}
		}
		for(int col = xc+1 ; col < numOfPins; col++ ) {
			if(descendant[1][col][0] == -descendant[0][col][0] || descendant[1][col][1] == -descendant[0][col][1]  ) {
				int pinNum = -descendant[0][col][0];
				pinsConnectedP2.add(new Pin(pinNum,col,true));
			}else {
				int pinNum = -descendant[0][col][0];
				pinsNotConnectedP2.add(new Pin(pinNum,col,true));
			}
			if (descendant[descendantRowsNum - 2][col][0] == -descendant[descendantRowsNum - 1][col][0] 
					|| descendant[descendantRowsNum - 2][col][1] == -descendant[descendantRowsNum - 1][col][1]) {
					int pinNum = -descendant[descendantRowsNum - 1][col][0];
					pinsConnectedP2.add(new Pin(pinNum, col, false));
			}else {
				int pinNum = -descendant[descendantRowsNum - 1][col][0];
				pinsNotConnectedP2.add(new Pin(pinNum, col, false));
			}
		}
		Genotype desc = new Genotype(descendant,descendantRowsNum,numOfPins);
		int res = desc.continueSolutionRandomly(pinsConnectedP1, pinsConnectedP2, pinsNotConnectedP1, pinsNotConnectedP2);
		if(res == 1 ) {
			return desc;
		}
		return null;
	}

	private void initializeDescendant(Integer[][][] descendant,Integer[][][] subP1,Integer[][][] subP2,int xc,int numOfRows) {
		for(int  z = 0 ; z < layers ;z++) {
			for(int row = 0 ; row < numOfRows; row++) {
				for(int col = 0; col <= xc ; col++) {
					descendant[row][col][z] = subP1[row][col][z];
				}
			}
		}
		for(int  z = 0 ; z < layers ;z++) {
			for(int row = 0 ; row < numOfRows; row++) {
				for(int col = xc+1; col < numOfPins ; col++) {
					descendant[row][col][z] = subP2[row][col][z];
				}
			}
		}
		
	}
	private Integer[][][] addRowOnChannel(Integer[][][] channel,int numOfRows) {
		int y = numOfRows-2;
		int yOfNewRow = randomNumInRange(1, y);
		//System.out.println("new row index = "+yOfNewRow);
		Integer[][][] newChannel = new Integer[numOfRows+1][this.numOfPins][layers];
		numOfRows++;
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
		return newChannel;
		
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
				transferRoutingWithinRange(parent1,subP1,1,i,1,i,parent1[1][i][0],0,xc,numOfRowsP1-1,0);
			}else {
				transferRoutingWithinRange(parent1,subP1,1,i,1,i,parent1[1][i][1],0,xc,numOfRowsP1-1,1);
			}
			
			if( parent1[numOfRowsP1-2][i][0] !=0 && parent1[numOfRowsP1-2][i][0] == -parent1[numOfRowsP1-1][i][0]) {
				transferRoutingWithinRange(parent1,subP1,numOfRowsP1-2,i,numOfRowsP1-2,i,parent1[numOfRowsP1-2][i][0],0,xc,numOfRowsP1-1,0);
			}else {
				transferRoutingWithinRange(parent1,subP1,numOfRowsP1-2,i,numOfRowsP1-2,i,parent1[numOfRowsP1-2][i][1],0,xc,numOfRowsP1-1,1);
			}
			
		}
	}
	private void updateSubP2(int xc,Integer[][][] subP2) {
		for(int i = xc+1; i < numOfPins; i++) {
			if( parent2[1][i][0] !=0 && parent2[1][i][0] == -parent2[0][i][0]) {
				transferRoutingWithinRange(parent2,subP2,1,i,1,i,parent2[1][i][0],xc+1,numOfPins-1,numOfRowsP2-1,0);
			}else {
				transferRoutingWithinRange(parent2,subP2,1,i,1,i,parent2[1][i][1],xc+1,numOfPins-1,numOfRowsP2-1,1);
			}
			
			if( parent2[numOfRowsP2-2][i][0] !=0 && parent2[numOfRowsP2-2][i][0] == -parent2[numOfRowsP2-1][i][0]) {
				transferRoutingWithinRange(parent2,subP2,numOfRowsP2-2,i,numOfRowsP2-2,i,parent2[numOfRowsP2-2][i][0],xc+1,numOfPins-1,numOfRowsP2-1,0);
			}else {
				transferRoutingWithinRange(parent2,subP2,numOfRowsP2-2,i,numOfRowsP2-2,i,parent2[numOfRowsP2-2][i][1],xc+1,numOfPins-1,numOfRowsP2-1,1);
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
		Genotype desc = cross.crossoverOp();
		if(desc != null) {
			desc.printBoard();
		}
	
	}

}
