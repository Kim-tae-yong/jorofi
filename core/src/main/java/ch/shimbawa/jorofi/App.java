package ch.shimbawa.jorofi;

import java.io.FileNotFoundException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ch.shimbawa.jorofi.data.CheminsFinderResponse;
import ch.shimbawa.jorofi.data.ClientRequest;
import ch.shimbawa.jorofi.data.Dataset;
import ch.shimbawa.jorofi.graph.CheminsFinder;
import ch.shimbawa.jorofi.io.GPXReader;
import ch.shimbawa.jorofi.io.GPXWriter;

public class App {

	public static void main(String[] args) throws FileNotFoundException {
		// Create client request + check options
		ClientRequest clientRequest = buildRequest(args);
		if (clientRequest == null) {
			return;
		}

		execute(clientRequest);
	}

	static void execute(ClientRequest clientRequest)
			throws FileNotFoundException {
		if (clientRequest.getLogListener() == null) {
			clientRequest.setLogListener(new ConsoleLogListener());
		}

		// Read GPX
		Dataset data = new GPXReader().read(clientRequest);
		clientRequest.getLogListener().message("Imported data: " + data.getStats());

		// Search chemins
		CheminsFinder finder = new CheminsFinder();
		CheminsFinderResponse response = finder.find(data, clientRequest);

		// Write GPX
		GPXWriter writer = new GPXWriter();
		writer.write(response, clientRequest);
	}

	private static ClientRequest buildRequest(String[] args) {
		Options options = createOptions();

		CommandLineParser parser = new BasicParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help") || line.getArgList().size() != 1) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("App [filename]", options);
				return null;
			}

			ClientRequest request = new ClientRequest();
			String inputFilename = line.getArgs()[0];
			request.setInputFilename(inputFilename);
			if (line.hasOption("output")) {
				request.setOutputFilename(line.getOptionValue("output"));
			} else {
				request.setOutputFilename(inputFilename.substring(0,
						inputFilename.length() - 4)
						+ "-out."
						+ inputFilename.substring(inputFilename.length() - 3));
			}
			request.setMetersMin(Integer.parseInt(line.getOptionValue(
					"min_dist", "4000")));
			request.setMetersMax(Integer.parseInt(line.getOptionValue(
					"max_dist", "5000")));
			request.setNbLimits(Integer.parseInt(line.getOptionValue(
					"nb_routes", "3")));
			request.setVerbose("true".equals(line.getOptionValue("verbose",
					"false")));

			return request;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("static-access")
	private static Options createOptions() {
		Options options = new Options();
		Option help = new Option("h", "help", false, "print this help message");
		Option verbose = new Option("v", "verbose", false, "be extra verbose");
		Option output = OptionBuilder.withArgName("output").hasArg()
				.withDescription("output filename").create("output");
		Option minDist = OptionBuilder.withArgName("distance").hasArg()
				.withValueSeparator()
				.withDescription("minimum distance [meters]")
				.create("min_dist");
		Option maxDist = OptionBuilder.withArgName("distance").hasArg()
				.withValueSeparator()
				.withDescription("maximum distance [meters]")
				.create("max_dist");
		Option nbRoutes = OptionBuilder.withArgName("nb_routes")
				.hasOptionalArg().withValueSeparator()
				.withDescription("number of routes to be found")
				.create("nb_routes");
		options.addOption(minDist);
		options.addOption(maxDist);
		options.addOption(nbRoutes);
		options.addOption(output);
		options.addOption(help);
		options.addOption(verbose);
		return options;
	}

}
