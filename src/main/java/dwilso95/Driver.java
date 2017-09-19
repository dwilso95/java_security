package dwilso95;

import java.io.File;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.google.common.io.Files;

public class Driver {

	@Parameters(commandNames = "demo", commandDescription = "Run a demo of the encryptiong algorithms")
	public static class DemoCommand {
	}

	@Parameters(commandNames = "quantum", commandDescription = "Run a demo of the qunatum key simulator")
	public static class QunatumCommand {
	}

	@Parameters(commandNames = "encrypt", commandDescription = "Run encryption algorithm")
	public static class EncryptCommand {
		@ParametersDelegate
		private CipherSettings cipherSettings = new CipherSettings();

		@ParametersDelegate
		private KeyFileLocation keyFileLocation = new KeyFileLocation();
	}

	@Parameters(commandNames = "decrypt", commandDescription = "Run decryption algorithm")
	public static class DecryptCommand {

		@ParametersDelegate
		private CipherSettings cipherSettings = new CipherSettings();

		@ParametersDelegate
		private KeyFileLocation keyFileLocation = new KeyFileLocation();
	}

	@Parameters(commandNames = "generateKey", commandDescription = "Generate key files")
	public static class KeyCommand {
		@ParametersDelegate
		private CipherSettings cipherSettings = new CipherSettings();
	}

	public static class KeyFileLocation {
		@Parameter(names = { "-keyFile", "-k" }, description = "Key file location", required = false)
		private String keyFile;
	}

	public static class CipherSettings {
		@Parameter(names = {
				"-cipher" }, description = "Cipher to use. Valid values are 'mono', 'vernam', and ''", required = false)
		private String cipher;

		@Parameter(names = { "-inputFile", "-i" }, description = "Input file location", required = false)
		private String inputFile;

		@Parameter(names = { "-outputFile", "-o" }, description = "Input file location", required = false)
		private String outputFile;

		@Parameter(names = "-help", description = "Shows (this) usage page")
		private boolean help = false;
	}

	public static void main(String[] args) throws Exception {
		final EncryptCommand encryptCommand = new EncryptCommand();
		final DecryptCommand decryptCommand = new DecryptCommand();
		final KeyCommand keyCommand = new KeyCommand();
		final DemoCommand demoCommand = new DemoCommand();
		final QunatumCommand quantumCommand = new QunatumCommand();
		final JCommander j = JCommander.newBuilder().addCommand(quantumCommand).addCommand(demoCommand)
				.addCommand(decryptCommand).addCommand(keyCommand).addCommand(encryptCommand).build();
		j.parse(args);

		if (decryptCommand.cipherSettings.help) {
			System.out.println(1);
			j.usage("decrypt");
			System.exit(0);
		} else if (encryptCommand.cipherSettings.help) {
			j.usage("encrypt");
			System.exit(0);
		}

		final String commandChosen = j.getParsedCommand();

		if (commandChosen.toLowerCase().equals("demo")) {
			runDemo();
			System.exit(0);
		}

		if (commandChosen.toLowerCase().equals("quantum")) {
			runQuantumDemo();
			System.exit(0);
		}

		if (commandChosen.toLowerCase().equals("generatekey")) {
			generate(keyCommand);
			System.exit(0);
		}

		final String output;
		final Cipher cipher;
		switch (commandChosen) {
		case "encrypt":
			cipher = getCipher(encryptCommand.cipherSettings.cipher);
			output = cipher.encrypt(new File(encryptCommand.keyFileLocation.keyFile),
					new File(encryptCommand.cipherSettings.inputFile));
			break;
		case "decrypt":
			cipher = getCipher(decryptCommand.cipherSettings.cipher);
			output = cipher.decrypt(new File(encryptCommand.keyFileLocation.keyFile),
					new File(decryptCommand.cipherSettings.inputFile));
			break;
		default:
			throw new IllegalArgumentException("Provided command [" + commandChosen + "] is unknown.");
		}
		Files.write(output.getBytes(), new File(encryptCommand.cipherSettings.outputFile));
	}

	private static void generate(final KeyCommand keyCommand) {
		final String cipherType = keyCommand.cipherSettings.cipher;
		final File inputFile = new File(keyCommand.cipherSettings.inputFile);
		final File outputFile = new File(keyCommand.cipherSettings.outputFile);

		switch (cipherType) {
		case "mono":
			new MonoAlphabeticCipher().generateKeyFile(outputFile, inputFile);
			return;
		case "vernam":
			new VernamCipher().generateKeyFile(outputFile, inputFile);
			return;
		default:
			throw new IllegalArgumentException("Unsupported cipher type [" + cipherType + "]");
		}
	}

	private static Cipher getCipher(final String cipherType) {
		switch (cipherType) {
		case "mono":
			return new MonoAlphabeticCipher();
		case "vernam":
			return new VernamCipher();
		default:
			throw new IllegalArgumentException("Unsupported cipher type [" + cipherType + "]");
		}
	}

	private static void runQuantumDemo() throws Exception {
		System.out.println("Executing Qunatum Key Simulator:");

		System.out.println("\nRunning without Eve eavesdropping:");
		new QuantumKeyExchange().run(false);

		System.out.println("\nRunning with Eve eavesdropping:");
		new QuantumKeyExchange().run(true);
	}

	private static void runDemo() throws Exception {
		System.out.println("Executing Mono Alphabetic:");
		final File monoKey = new File(Driver.class.getClassLoader().getResource("mono_key").getFile());
		final File monoPlain = new File(Driver.class.getClassLoader().getResource("mono_plaintext").getFile());
		final File monoCipher = new File(Driver.class.getClassLoader().getResource("mono_ciphertext").getFile());
		runDemo(new MonoAlphabeticCipher(), monoPlain, monoKey, monoCipher);

		System.out.println("\n");

		System.out.println("Executing Vernam:");

		final File vernamKey = new File(Driver.class.getClassLoader().getResource("vernam_keyfile").getFile());
		final File vernamPlain = new File(Driver.class.getClassLoader().getResource("vernam_plaintext").getFile());
		final File vernamCipher = new File(Driver.class.getClassLoader().getResource("vernam_ciphertext").getFile());

		runDemo(new VernamCipher(), vernamPlain, vernamKey, vernamCipher);

	}

	private static void runDemo(final Cipher cipher, final File plainText, final File keyFile, final File cipherFile) {
		System.out.println("<--- Plain Text --->\n" + Cipher.readFile(plainText));
		System.out.println("<--- Key --->\n" + cipher.printKey(keyFile));
		System.out.println("<--- Cipher Text --->\n" + cipher.encrypt(keyFile, plainText));
		System.out.println("<--- Decrypted Cipher Text --->\n" + cipher.decrypt(keyFile, cipherFile));
	}

}
