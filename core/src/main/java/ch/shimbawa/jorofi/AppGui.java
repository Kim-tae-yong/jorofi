package ch.shimbawa.jorofi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ch.shimbawa.jorofi.data.ClientRequest;

public class AppGui {

	final JTextField txtInputFilename;
	final JTextField txtOutputFilename;
	final JTextArea txtLog;

	public AppGui() {
		txtInputFilename = new JTextField("(no filename)", 30);
		txtOutputFilename = new JTextField("(no filename)", 30);
		txtLog = new JTextArea("(Please start compute to see results here.)",10, 50);
		new JScrollPane(txtLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		final JFrame frame = new JFrame("JoRoFi, Jogging Route Finder");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.getContentPane().add(main);

		
		main.add(buildOptions(frame), BorderLayout.NORTH);
		main.add(buildComputeButton(frame), BorderLayout.CENTER);

		// results
		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new BorderLayout());
		resultsPanel.setBorder(BorderFactory.createTitledBorder("Log"));
		resultsPanel.add(txtLog);
		txtLog.setBorder(BorderFactory.createLoweredBevelBorder());
		main.add(resultsPanel, BorderLayout.SOUTH);

		frame.pack(); // minimal size
		frame.setLocationRelativeTo(null); // center
		frame.setVisible(true);
	}

	private JButton buildComputeButton(final JFrame frame) {
		final JButton btnRun = new JButton("Compute !");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtLog.setText("");
				ClientRequest clientRequest = new ClientRequest();
				clientRequest.setInputFilename(txtInputFilename.getText());
				clientRequest.setOutputFilename(txtOutputFilename.getText());
				clientRequest.setLogListener(new LogListener() {
					public void message(String message) {
						txtLog.setText(txtLog.getText() + message + "\n");
						frame.repaint();
					}
				});
				try {
					App.execute(clientRequest);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(frame.getParent(), e1.getMessage(),
							"Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		return btnRun;
	}

	private JPanel buildOptions(JFrame frame) {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
		GridBagConstraints c = new GridBagConstraints();

		// Filename in label
		c.gridx = 0;
		c.gridy = 0;
		optionsPanel.add(new JLabel("Filename in: ", JLabel.RIGHT), c);

		// Filename in value
		c.gridx++;
		c.weightx = 3;
		optionsPanel.add(txtInputFilename);

		// File in chooser
		final JButton btnSelectFile = new JButton("Choose file...");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showDialog(btnSelectFile, "Attach");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String filenameIn = file.getAbsolutePath();
					String filenameOut = filenameIn.substring(0,
							filenameIn.length() - 4)
							+ "-out.gpx";
					txtInputFilename.setText(filenameIn);
					txtOutputFilename.setText(filenameOut);
					// This is where a real application would open the file.
					// log.append("Opening: " + file.getName() + "." + newline);
				} else {
					// log.append("Open command cancelled by user." + newline);
				}
			}
		});
		c.gridx++;
		c.weightx = 1;
		optionsPanel.add(btnSelectFile, c);

		// Filename out label
		c.gridx = 0;
		c.gridy++;
		optionsPanel.add(new JLabel("Filename out: ", JLabel.RIGHT), c);

		// Filename out value
		c.gridx++;
		optionsPanel.add(txtOutputFilename, c);

		return optionsPanel;
	}

	public static void main(String[] args) {
		new AppGui();
	}
}
