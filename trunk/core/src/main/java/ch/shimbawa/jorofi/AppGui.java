package ch.shimbawa.jorofi;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AppGui {

	public AppGui() {
		JFrame frame = new JFrame("JoRoFi, Jogging Route Finder");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//		frame.getContentPane().add((new JLabel("Hello, World!")));
		
		

		JPanel panelOptions = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		panelOptions.setLayout(new GridBagLayout());
		
		c.gridx=0;
		c.gridy=0;
		panelOptions.add(new JLabel("Filename in"), c);
		final JTextField txtInputFilename = new JTextField("(no filename)",30);
		JPanel filePanel = new JPanel();
		GridBagConstraints c2 = new GridBagConstraints();
		filePanel.setLayout(new GridBagLayout());
		final JButton btnSelectFile = new JButton("Select input file");
		btnSelectFile.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();				
				int returnVal = fc.showDialog(btnSelectFile, "Attach");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                txtInputFilename.setText(file.getAbsolutePath());
	                //This is where a real application would open the file.
//	                log.append("Opening: " + file.getName() + "." + newline);
	            } else {
//	                log.append("Open command cancelled by user." + newline);
	            }
			}
		});
		c2.gridx=0;
		c2.gridy=0;
		c2.weightx=3;		
		filePanel.add(txtInputFilename, c2);
		c2.gridx++;
		c2.weightx=1;
		filePanel.add(btnSelectFile);
		c.gridx=1;
		panelOptions.add(filePanel,c);
		

		c.gridx=0;
		c.gridy++;
		panelOptions.add(new JLabel("Filename out"), c);
		c.gridx++;
		panelOptions.add(new JTextField());
		frame.getContentPane().add(panelOptions, BorderLayout.NORTH);
		
		JButton btnRun = new JButton("Compute !");
		frame.getContentPane().add(btnRun, BorderLayout.CENTER);
		
		JTextArea txtResults = new JTextArea("Please start compute to see results here.");
		frame.getContentPane().add(txtResults, BorderLayout.SOUTH);
		
		// file: http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
		

		frame.pack(); // minimal size
		frame.setLocationRelativeTo(null); // center
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new AppGui();
	}
}
