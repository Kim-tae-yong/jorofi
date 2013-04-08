package ch.shimbawa.jorofi;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import ch.shimbawa.jorofi.swing.JNumericField;

public class AppGui {

	private static final String PREF_NB_LIMITS = "nbLimits";
	private static final String PREF_METERS_MAX = "metersMax";
	private static final String PREF_METERS_MIN = "metersMin";
	private static final String PREF_OUTPUT_FILENAME = "outputFilename";
	private static final String PREF_INPUT_FILENAME = "inputFilename";
	final JTextField txtInputFilename;
	final JTextField txtOutputFilename;
	final JNumericField txtMetersMin;
	final JNumericField txtMetersMax;
	final JNumericField txtNbLimits;
	final JCheckBox chkVerbose;
	final JTextArea txtLog;
	FutureTask<Integer> futureComputing;

	public AppGui() {
		Preferences prefs = Preferences.userNodeForPackage(AppGui.class);

		// Fields
		txtInputFilename = new JTextField(prefs.get(PREF_INPUT_FILENAME, "(no filename)"), 30);
		txtOutputFilename = new JTextField(prefs.get(PREF_OUTPUT_FILENAME, "(no filename)"), 30);
		txtMetersMin = new JNumericField(prefs.getInt(PREF_METERS_MIN, 5000), 15);
		txtMetersMax = new JNumericField(prefs.getInt(PREF_METERS_MAX, 6000), 15);
		txtNbLimits = new JNumericField(prefs.getInt(PREF_NB_LIMITS, 5), 15);
		chkVerbose = new JCheckBox();

		// Log
		txtLog = new JTextArea("(Please start compute to see results here.)", 10, 50);
		new JScrollPane(txtLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		txtLog.setEditable(false);

		// Main frame
		final JFrame frame = new JFrame("JoRoFi, Jogging Route Finder");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.getContentPane().add(main);

		// Content
		main.add(buildOptions(frame), BorderLayout.NORTH);
		main.add(buildComputeButton(frame), BorderLayout.CENTER);
		main.add(buildLogPanel(), BorderLayout.SOUTH);

		frame.pack(); // minimal size
		frame.setLocationRelativeTo(null); // center
		frame.setVisible(true);
	}

	private JPanel buildLogPanel() {
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new BorderLayout());
		logPanel.setBorder(BorderFactory.createTitledBorder("Log"));
		logPanel.add(txtLog);
		txtLog.setBorder(BorderFactory.createLoweredBevelBorder());
		return logPanel;
	}

	private JButton buildComputeButton(final JFrame frame) {
		final JButton btnRun = new JButton("Compute !");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (futureComputing != null) {
					futureComputing.cancel(true);
				}

				txtLog.setText("");
				final ClientRequest clientRequest = buildClientRequest(frame);
				Preferences prefs = Preferences.userNodeForPackage(AppGui.class);
				prefs.put(PREF_INPUT_FILENAME, clientRequest.getInputFilename());
				prefs.put(PREF_OUTPUT_FILENAME, clientRequest.getOutputFilename());
				prefs.putInt(PREF_METERS_MIN, clientRequest.getMetersMin());
				prefs.putInt(PREF_METERS_MAX, clientRequest.getMetersMax());
				prefs.putInt(PREF_NB_LIMITS, clientRequest.getNbLimits());

				ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();
				futureComputing = new FutureTask<Integer>(new Callable<Integer>() {
					public Integer call() {
						try {
							App.execute(clientRequest);
						} catch (FileNotFoundException e1) {
							throw new RuntimeException(e1);
						}
						return 0;
					}
				});
				executor.execute(futureComputing);
				try {
					futureComputing.get();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame.getParent(), e1.getMessage(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
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
		c.anchor = GridBagConstraints.WEST;

		// --------------------------------------------------------------------------

		// Filename in label
		c.gridx = 0;
		c.gridy = 0;
		optionsPanel.add(new JLabel("Filename in: ", JLabel.RIGHT), c);

		// Filename in value
		c.gridx++;
		c.weightx = 3;
		optionsPanel.add(txtInputFilename, c);

		// File in chooser
		final JButton btnSelectFile = new JButton("Choose file...");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showDialog(btnSelectFile, "Attach");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String filenameIn = file.getAbsolutePath();
					String filenameOut = filenameIn.substring(0, filenameIn.length() - 4) + "-out.gpx";
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

		// --------------------------------------------------------------------------

		// Filename out label
		c.gridx = 0;
		c.gridy++;
		optionsPanel.add(new JLabel("Filename out: ", JLabel.RIGHT), c);

		// Filename out value
		c.gridx++;
		optionsPanel.add(txtOutputFilename, c);

		// --------------------------------------------------------------------------

		// Meters Min

		c.gridx = 0;
		c.gridy++;
		optionsPanel.add(new JLabel("Meters min: ", JLabel.RIGHT), c);

		// Filename out value
		c.gridx++;
		optionsPanel.add(txtMetersMin, c);

		// --------------------------------------------------------------------------

		// Meters Max

		c.gridx = 0;
		c.gridy++;
		optionsPanel.add(new JLabel("Meters max: ", JLabel.RIGHT), c);

		// Filename out value
		c.gridx++;
		optionsPanel.add(txtMetersMax, c);

		// --------------------------------------------------------------------------

		// Nb Limits

		c.gridx = 0;
		c.gridy++;
		optionsPanel.add(new JLabel("Nb results: ", JLabel.RIGHT), c);

		// Filename out value
		c.gridx++;
		optionsPanel.add(txtNbLimits, c);

		// --------------------------------------------------------------------------

		// Verbose

		c.gridx = 0;
		c.gridy++;
		optionsPanel.add(new JLabel("Verbose: ", JLabel.RIGHT), c);

		// Filename out value
		c.gridx++;
		optionsPanel.add(chkVerbose, c);

		return optionsPanel;
	}

	private ClientRequest buildClientRequest(final JFrame frame) {
		ClientRequest clientRequest = new ClientRequest();
		clientRequest.setInputFilename(txtInputFilename.getText());
		clientRequest.setOutputFilename(txtOutputFilename.getText());
		clientRequest.setMetersMin(Integer.parseInt(txtMetersMin.getText()));
		clientRequest.setMetersMax(Integer.parseInt(txtMetersMax.getText()));
		clientRequest.setVerbose(chkVerbose.isSelected());
		clientRequest.setNbLimits(Integer.parseInt(txtNbLimits.getText()));
		clientRequest.setLogListener(new LogListener() {
			public void message(String message) {
				txtLog.setText(txtLog.getText() + message + "\n");
				frame.repaint();
			}
		});
		return clientRequest;
	}

	public static void main(String[] args) {
		new AppGui();
	}

	class ThreadPerTaskExecutor implements Executor {
		public void execute(Runnable r) {
			new Thread(r).start();
		}
	}
}
