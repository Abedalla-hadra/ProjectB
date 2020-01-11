package ProjectB;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
public class GUI{
	ArrayList<Integer> inputs;
	ArrayList<Integer> outputs;
	Genotype channel;
	JFrame f;
	JButton b;
	JLabel label_1;
	JLabel label_2;
	JTextField textfield_1;
	JTextField textfield_2;
	public GUI() {
		inputs = new ArrayList<Integer>();
		outputs = new ArrayList<Integer>();
		f = new JFrame("Input Window");
		// submit button
		b = new JButton("Start");
		b.setBounds(130, 260, 140, 40);
		
		// enter name label
		label_1 = new JLabel();
		label_1.setText("Please enter the input pins in order:");
		label_1.setFont(new Font("TimesRoman",Font.ROMAN_BASELINE,24));
		label_1.setBounds(10, 10, 500, 100);
		// empty label which will show event after button clicked
		// textfield to enter name
		label_2 = new JLabel();
		label_2.setText("Please enter the output pins in order:");
		label_2.setFont(new Font("TimesRoman",Font.ROMAN_BASELINE,24));
		label_2.setBounds(10, 90, 500, 100);
		textfield_1 = new JTextField();
		textfield_1.setBounds(10, 80, 400, 30);
		textfield_2 = new JTextField();
		textfield_2.setBounds(10, 160, 400, 30);
		// add to frame
		f.add(textfield_2);
		f.add(label_2);
		f.add(textfield_1);
		f.add(label_1);
		f.add(b);
		f.setSize(450, 350);
		f.setLayout(null);
		f.setVisible(true);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] inPins = textfield_1.getText().split(" ");
				String[] outPins = textfield_2.getText().split(" ");
				for(String a : inPins) {
					inputs.add(Integer.valueOf(a));
				}
				for(String a : outPins) {
					outputs.add(Integer.valueOf(a));
				}
				startSolution();
			}
			});
		
	}
	class DrawPanel extends JPanel{
		private void doDrawing(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Stroke stroke1 = new BasicStroke(2f);
			g2.setStroke(new BasicStroke(2f));
			g2.setColor(Color.black);
			float[] dashingPattern1 = { 4f, 1.5f };
			Stroke stroke2 = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern1,
					2.0f);
			
			Integer[][][] board = channel.getChannel();
			int numOfRows = channel.getNumOfRows() + 2;
			int lastValue = 0;
			for (int z = 0; z < 2; z++) {
				int x = 49;
				int y = 30;
				if(z == 1) {
					x = 52;
					y+=3;
				}
				
				for (int row = 0; row < numOfRows; row++) {
					lastValue = 0;
					for (int col = 0; col < channel.getNumOfPins(); col++) {
						if (z == 0) {
							g2.setStroke(stroke1);
						} else {
							g2.setStroke(stroke2);
						}
						if(row == 0 && z == 1) {
							continue;
						}
						if (row == 0 && z == 0 ) {
							g2.drawString(String.valueOf(-1 * board[row][col][z]), x + col * 80-2, y-8);
							g2.setStroke(new BasicStroke(1.5f));
							g2.setColor(Color.gray);
							if(col != 0) {
								g2.drawLine(x+(col-1)*80, y, x+col*80, y);
							}
							g.drawRect(x+col*80-4, y-4, 8, 8);
							g.fillRect(x+col*80-4, y-4, 8, 8);
							g2.setColor(Color.black);
							g2.setStroke(stroke1);

						}else if(row == numOfRows - 1 && z == 0 ) {
							g2.drawString(String.valueOf(-1 * board[row][col][z]), x + col * 80-2, y+7);
							g2.setStroke(new BasicStroke(1.5f));
							g2.setColor(Color.gray);
							if(col != 0) {
								g2.drawLine(x+(col-1)*80, y-10, x+col*80, y-10);
							}
							g.drawRect(x+col*80-4, y-14, 8, 8);
							g.fillRect(x+col*80-4, y-14, 8, 8);
							g2.setColor(Color.black);
							g2.setStroke(stroke1);
							if(board[row-1][col][z]*-1 == board[row][col][z] && board[row][col][z] != 0) {
								g2.drawLine(x+col*80, y-50, x+col*80, y-10);
							}
						}else {
							if(board[row-1][col][z]*-1 == board[row][col][z] && board[row][col][z] != 0) {
								if(row == numOfRows-1) {
									g2.drawLine(x+col*80, y-50, x+col*80, y-10);
								}else {
									g2.drawLine(x+col*80, y-50, x+col*80, y);
								}
							}
							if(board[row-1][col][z] == board[row][col][z] && board[row][col][z] != 0) {
								g2.drawLine(x+col*80, y-50, x+col*80, y);
							}
							if(board[row][col][z] == lastValue && board[row][col][z] != 0) {
								g2.drawLine(x+(col-1)*80, y, x+col*80, y);
							}
							if(z == 1 && board[row][col][z] == board[row][col][0] && board[row][col][z] > 0) {
								g2.setStroke(stroke1);
								g.drawRect(x+col*80-6, y-4, 8, 8);
								g.fillRect(x+col*80-6, y-4, 8, 8);
								g2.setStroke(stroke2);
							}

						}
						lastValue = board[row][col][z];
					}
					y += 50;
				}
				
			}
			
		}
		public void paint(Graphics g) {
			super.paint(g);
			doDrawing(g);
		}
	}
	public void startSolution() {
		f.dispose();
		Solution sol = new Solution(inputs,outputs);
		channel = sol.getSolution();
		channel.printBoard();
		
		JFrame f2 = new JFrame("Solution");
		f2.getContentPane().add(new DrawPanel());
		int width = inputs.size()*80+100;
		int height = (channel.getNumOfRows()+2)*50+50;
		f2.setSize(width, height);
		f2.setVisible(true);
		f2.setResizable(false);
	
	}

	public static void main(String[] args) {
		GUI window = new GUI();
	}
}