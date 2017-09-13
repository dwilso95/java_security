package dwilso95;

import java.net.URI;
import java.net.URISyntaxException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Driver {

	public static class Settings {
		@Parameter(names = {
				"-part" }, description = "Run part of the assignment. Valid values are '1', '2', and '3'", required = true)
		private int part;

		@Parameter(names = "-help", description = "Shows usage page")
		private boolean help = false;
	}

	public static void main(String[] args) throws URISyntaxException {
		final Settings settings = new Settings();
		final JCommander j = JCommander.newBuilder().addObject(settings).build();
		j.parse(args);

		if (settings.help) {
			j.usage();
			System.exit(0);
		}

		switch (settings.part) {
		case 1:
			System.out.println("Executing Part 1:");

			final URI keyFile = Driver.class.getClassLoader().getResource("mono_key").toURI();
			final URI plainTextFile = Driver.class.getClassLoader().getResource("mono_plaintext")
					.toURI();
			final URI cipherTextFile = Driver.class.getClassLoader().getResource("mono_ciphertext")
					.toURI();

			final MonoAlphabeticEncrypterDecrypter encrypter = new MonoAlphabeticEncrypterDecrypter(keyFile);
			System.out.println("<--- Key --->\n" + encrypter.getKey());
			System.out.println("<--- Plain Text --->\n" + encrypter.readFile(plainTextFile));
			System.out.println("<--- Cipher Text --->\n" + encrypter.encrypt(plainTextFile));
			System.out.println("<--- Decrypted Cipher Text --->\n" + encrypter.decrypt(cipherTextFile));

			break;
		case 2:
			break;
		case 3:
			break;
		default:
			break;
		}

	}

}
