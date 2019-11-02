package ProjectB;

public class Point {
	int y;
	int x;
	int z;
	boolean discovered;
	Point parent;
	public Point(int _y, int _x, int _z) {
		this.y = _y;
		this.x = _x;
		this.z = _z;
		this.discovered = false;
		this.parent = null;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	public boolean isDiscovered() {
		return discovered;
	}
	public Point getParent() {
		return parent;
	}
	public void setDiscovered(boolean val) {
		discovered = val;
	}
	public void setParetn(Point p) {
		parent = p;
	}
}
