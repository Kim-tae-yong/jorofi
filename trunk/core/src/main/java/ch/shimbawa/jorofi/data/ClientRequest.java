package ch.shimbawa.jorofi.data;

import ch.shimbawa.jorofi.LogListener;
import ch.shimbawa.jorofi.graph.Chemin;

/**
 * Request asked by the client for the search of chemins and the use of the
 * application.
 */
public class ClientRequest {

	private String inputFilename;
	private String outputFilename;
	private boolean verbose;
	private int nbLimits = 3;
	private int metersMin = 4600;
	private int metersMax = 4700;
	private LogListener logListener;

	public String getInputFilename() {
		return inputFilename;
	}

	public void setInputFilename(String inputFilename) {
		this.inputFilename = inputFilename;
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	public int getNbLimits() {
		return nbLimits;
	}

	public ClientRequest setNbLimits(int nbLimits) {
		this.nbLimits = nbLimits;
		return this;
	}

	public int getMetersMin() {
		return metersMin;
	}

	public ClientRequest setMetersMin(int metersMin) {
		this.metersMin = metersMin;
		return this;
	}

	public int getMetersMax() {
		return metersMax;
	}

	public ClientRequest setMetersMax(int metersMax) {
		this.metersMax = metersMax;
		return this;
	}

	public boolean shouldKeepChemin(Chemin chemin) {
		// System.out.println("Should keep ? " +
		// chemin.getTotalDistance().intValue());
		return chemin.getTotalDistance().intValue() >= metersMin
				&& chemin.getTotalDistance().intValue() < metersMax;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setLogListener(LogListener logListener) {
		this.logListener = logListener;
	}

	public LogListener getLogListener() {
		return logListener;
	}

}
