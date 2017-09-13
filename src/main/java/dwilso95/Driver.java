package dwilso95;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

public class Driver {

	@Parameters(commandNames = "encrypt", commandDescription = "Run encryption algorithm")
	public static class EncryptCommand {
		@ParametersDelegate
		private CipherSettings cipherSettings = new CipherSettings();
	}

	@Parameters(commandNames = "decrypt", commandDescription = "Run decryption algorithm")
	public static class DecryptCommand {
		@ParametersDelegate
		private CipherSettings cipherSettings = new CipherSettings();
	}

	public static class CipherSettings {
		@Parameter(names = {
				"-cipher" }, description = "Cipher to use. Valid values are 'mono', 'vernam', and 'other'", required = false)
		private String cipher;

		@Parameter(names = { "-keyFile", "-k" }, description = "Key file location", required = false)
		private String keyFile;

		@Parameter(names = { "-inputFile", "-i" }, description = "Input file location", required = false)
		private String inputFile;

		@Parameter(names = { "-outputFile", "-o" }, description = "Input file location", required = false)
		private String outputFile;

		@Parameter(names = "-help", description = "Shows (this) usage page")
		private boolean help = false;
	}

	public static void main(String[] args) throws URISyntaxException {
		final EncryptCommand encryptCommand = new EncryptCommand();
		final DecryptCommand decryptCommand = new DecryptCommand();
		final JCommander j = JCommander.newBuilder().addCommand(encryptCommand).addCommand(decryptCommand).build();
		j.parse(args);

		if (decryptCommand.cipherSettings.help) {
			j.usage("decrypt");
			System.exit(0);
		} else if (encryptCommand.cipherSettings.help) {
			j.usage("encrypt");
			System.exit(0);
		}

		switch ("") {
		case "mono":
			System.out.println("Executing Part 1:");

			final URI monoKeyFile = Driver.class.getClassLoader().getResource("mono_key").toURI();
			final URI monoPlainTextFile = Driver.class.getClassLoader().getResource("mono_plaintext").toURI();
			final URI monoCipherTextFile = Driver.class.getClassLoader().getResource("mono_ciphertext").toURI();

			final Cipher monoEncrypter = new MonoAlphabeticEncrypterDecrypter(monoKeyFile);
			System.out.println("<--- Key --->\n" + monoEncrypter.getKey());
			System.out.println("<--- Plain Text --->\n" + monoEncrypter.readFile(monoPlainTextFile));
			System.out.println("<--- Cipher Text --->\n" + monoEncrypter.encrypt(monoPlainTextFile));
			System.out.println("<--- Decrypted Cipher Text --->\n" + monoEncrypter.decrypt(monoCipherTextFile));

			break;
		case "vernam":
			System.out.println("Executing Part 2:");

			final URI vernamPlainTextFile = Driver.class.getClassLoader().getResource("vernam_plaintext").toURI();
			final URI vernamCipherTextFile = Driver.class.getClassLoader().getResource("vernam_ciphertext").toURI();
			final File vernamKeyFile = new File("vernam_keyfile");
			vernamKeyFile.deleteOnExit();

			VernamEncrypterDecrypter.generateKeyFile(1000, vernamKeyFile.toURI());
			final Cipher vernamEncrypter = new VernamEncrypterDecrypter(vernamKeyFile.toURI());
			System.out.println("<--- Plain Text --->\n" + vernamEncrypter.readFile(vernamPlainTextFile));
			System.out.println("<--- Cipher Text --->\n" + vernamEncrypter.encrypt(vernamPlainTextFile));
			System.out.println("<--- Decrypted Cipher Text --->\n" + vernamEncrypter.decrypt(vernamCipherTextFile));

			break;
		case "other":
			break;
		default:
			break;
		}

	}

}
