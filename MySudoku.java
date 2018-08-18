import z.sudoku.Sudoku;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class MySudoku extends JFrame{
	private Sudoku m_sdk;

	//private JTextField m_data[][];
	private JPanel m_input;
	private Vector<JTextField> m_dataVector;

	private JMenuBar m_menubar;
	private JMenu m_menu_file;
	private JMenuItem m_item_savetofile;
	private JMenuItem m_item_readfromfile;

	private JToolBar m_buttons;
	private JButton m_solve;
	private JButton m_next;
	private JButton m_pre;
	private JButton m_clear;

	private JLabel m_status;

	private int m_anssum;
	private Font m_textfont;

	private void clearData(){
		for (int i = 0; i<81; i++){
			m_dataVector.get(i).setText("");
			m_dataVector.get(i).setForeground(Color.BLUE);
		}
		m_anssum = 0;
		m_sdk.Clear();
		m_status.setText("Ready");
		m_next.setEnabled(false);
		m_pre.setEnabled(false);
	}

	public MySudoku(){
		super("MySudoku");
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		m_sdk = new Sudoku();

		m_input = new JPanel();
		m_input.setLayout(new GridLayout(9,9));


		m_textfont = new Font("Arial", Font.BOLD, 30);
		m_dataVector = new Vector<JTextField>(81);
		//m_data = new JTextField[9][9];
		JTextField m_data;
		for (int i = 0; i < 81; i++){
			m_data = new JTextField("",1);
			m_data.setHorizontalAlignment(JTextField.CENTER);
			m_data.setFont(m_textfont);

			//m_data.setSize(100,100);
			//m_data.setPreferredSize(new Dimension(30,30));

			m_data.setBackground(Color.WHITE);
			m_data.setForeground(Color.BLUE);

			m_data.addKeyListener(new TextChange());
			m_dataVector.addElement(m_data);
			m_input.add(m_data);
		}

//the buttons 
		m_buttons = new JToolBar();
		m_buttons.setFloatable(false);

		m_solve = new JButton("Solve");
		m_solve.addActionListener(new ButtonClick());
		m_buttons.add(m_solve);

		m_clear = new JButton("Clear");
		m_clear.addActionListener(new ButtonClick());
		m_buttons.add(m_clear);

		m_buttons.addSeparator();

		m_next = new JButton("Next Ans");
		m_next.addActionListener(new ButtonClick());
		m_next.setEnabled(false);
		m_buttons.add(m_next);

		m_pre = new JButton("Pre Ans");
		m_pre.addActionListener(new ButtonClick());
		m_pre.setEnabled(false);
		m_buttons.add(m_pre);

//the status label
		m_status = new JLabel("Ready");	

//the menu bar
		m_menubar = new JMenuBar();
		m_menu_file = new JMenu("File");

		m_item_savetofile = new JMenuItem("Save the result");
		m_item_savetofile.addActionListener(new MenuClick());
		m_item_readfromfile = new JMenuItem("Import files");
		m_item_readfromfile.addActionListener(new MenuClick());

		m_menu_file.add(m_item_savetofile);
		m_menu_file.add(m_item_readfromfile);
		m_menubar.add(m_menu_file);

		add(m_input,BorderLayout.CENTER);
		add(m_status,BorderLayout.SOUTH);
		add(m_buttons,BorderLayout.NORTH);
		setJMenuBar(m_menubar);

		setSize(300,400);
		setResizable(false);
		//pack();
		setVisible(true);
	};

//the listener
//the menu listener
	private class MenuClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
			if (cmd.equals("Save the result")){
		//save the answers
				String filename = new String("Ans_Sudoku_BY_ZeRo.txt");
				try{
					BufferedWriter fout = new BufferedWriter(new FileWriter(filename));
					for (int k = 0; k< m_sdk.GetLen(); k++){
						fout.write("Ans No." + Integer.toString(k+1) + ":");
						fout.newLine();
						for (int i=0; i<9; i++){
							for (int j=0; j<9; j++)
								fout.write(Integer.toString(m_sdk.GetElm(k,i,j))+' ');
							fout.newLine();
						}
						fout.newLine();							
					}
					fout.close();
				}
				catch(IOException iox){
					System.out.println("Something goes wrong when writing " + filename);
				}
			}else if (cmd.equals("Import files")){
		//open the file
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(MySudoku.this) == JFileChooser.APPROVE_OPTION ){

					String filename = fc.getSelectedFile().getName();
					String line;
					int num = 0;
					char value;

					try{
						clearData();
						BufferedReader fin = new BufferedReader(new FileReader(filename));
						line = fin.readLine();
						while (line != null && num < 81){
							for (int i = 0; i<line.length(); i++){
								value = line.charAt(i);
								if (Character.isDigit(value)){
									if (Character.compare(value,'0') != 0){
										m_dataVector.get(num).setText(Character.toString(value));
										m_dataVector.get(num).setForeground(Color.BLACK);
									}
									num++;
								}
							}
							line = fin.readLine();
						}
						fin.close();
					}
					catch(IOException iox){
						System.out.println("Can't Read from" + filename);
					}
				}
			};
		}
	}
	private class ButtonClick implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
			if (cmd.equals("Solve")){

				m_anssum = 0;
				m_sdk.Clear();
				m_status.setText("Start Searching...");

				for (int i = 0; i<9; i++)
					for (int j = 0; j<9; j++)
						if (m_dataVector.get(i*9+j).getText().equals(""))
							m_sdk.AddPoint(i,j,0);
						else
							m_sdk.AddPoint(i,j,Integer.decode(m_dataVector.get(i*9+j).getText()));

				m_sdk.Solve(10);

				if (m_sdk.GetLen() != 0){
					for (int i = 0; i<9; i++)
						for (int j = 0; j<9; j++)
							if (m_dataVector.get(i*9+j).getText().equals(""))
								m_dataVector.get(i*9+j).setText(Integer.toString(m_sdk.GetElm(m_anssum,i,j)));
	
					m_status.setText(Integer.toString(m_sdk.GetLen()) + " Answers have been found!");
				}else m_status.setText("No Answers have been found!");

				if (m_anssum == m_sdk.GetLen() - 1)
					m_next.setEnabled(false);
				else m_next.setEnabled(true);
				if (m_anssum == 0)
					m_pre.setEnabled(false);
				else m_pre.setEnabled(true);

			}else if (cmd.equals("Next Ans")){
				m_pre.setEnabled(true);
				if (m_anssum < m_sdk.GetLen()-1){
					m_anssum++;
					m_status.setText("Answer No." + Integer.toString(m_anssum+1) + " | " + Integer.toString(m_sdk.GetLen()) + " Answers in all");
					for (int i = 0; i<9; i++)
						for (int j = 0; j<9; j++)
							m_dataVector.get(i*9+j).setText(Integer.toString(m_sdk.GetElm(m_anssum,i,j)));
					if (m_anssum == m_sdk.GetLen() - 1) m_next.setEnabled(false);
				}

			}else if (cmd.equals("Pre Ans")){
				m_next.setEnabled(true);
				if (m_anssum > 0){
					m_anssum--;
					m_status.setText("Answer No." + Integer.toString(m_anssum+1) + " | " + Integer.toString(m_sdk.GetLen()) + " Answers in all");
					for (int i = 0; i<9; i++)
						for (int j = 0; j<9; j++)
							m_dataVector.get(i*9+j).setText(Integer.toString(m_sdk.GetElm(m_anssum,i,j)));
					if (m_anssum == 0) m_pre.setEnabled(false);
				}
			}else if (cmd.equals("Clear")){
				clearData();
			};
		};
	};

	private class TextChange extends KeyAdapter{
		public void keyTyped(KeyEvent e){
			JTextField text = (JTextField)e.getSource();
			int index = m_dataVector.indexOf(text);
			char keychar = e.getKeyChar();
			boolean next = false;

			//System.out.println(e.getExtendedKeyCode());

			if (Character.isDigit(keychar) && Character.getNumericValue(keychar) > 0){
				text.setText("");
				text.setForeground(Color.BLACK);
				next = true;
			}else{
				if (Character.compare(keychar,'0') == 0)
					next = true;
				e.setKeyChar('\0');
				text.setText("");
				text.setForeground(Color.BLUE);
			};

			if (next && index < 80)
				m_dataVector.get(index+1).grabFocus();
		};

		public void keyPressed(KeyEvent e){
			int index = m_dataVector.indexOf((JTextField)e.getSource());

			//System.out.println(e.getExtendedKeyCode());

			switch(e.getExtendedKeyCode()){
				case KeyEvent.VK_UP:
					if (index > 8 ) m_dataVector.get(index-9).grabFocus(); 
					break;
				case KeyEvent.VK_DOWN:
					if (index < 72) m_dataVector.get(index+9).grabFocus(); 
					break;
				case KeyEvent.VK_LEFT:
					if (index > 0 ) m_dataVector.get(index-1).grabFocus(); 
					break;
				case KeyEvent.VK_RIGHT:
					if (index < 80) m_dataVector.get(index+1).grabFocus(); 
					break;
				//default: System.out.println("nothing"); break;
			}
		};
	};

//the main
	public static void main(String args[]){
		MySudoku m_sdk = new MySudoku();
	};
};