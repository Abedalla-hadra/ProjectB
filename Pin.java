package ProjectB;

/*
 * Pin class describing the pin
 */
public class Pin {
	int pin_num; 
	int index;  //index of the pin
	boolean on_upper; //is the pin located in the upper boundary
	public Pin(int pin_num,int index,boolean on_upper) {
		this.pin_num = pin_num;
		this.index = index;
		this.on_upper = on_upper;
	}
	public int getPinNum() {
		return pin_num;
	}
	public int getIndex() {
		return index;
	}
	public boolean isOnUpper() {
		return on_upper;
	}
}
