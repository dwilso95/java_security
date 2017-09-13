package dwilso95;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Simple class for performing Vernam encryption
 * 
 */
public class VernamEncrypterDecrypter extends Cipher {

	private static final String DEFAULT_ALGORITHM = "SHA1PRNG";

	private List<Bit> key;

	/**
	 * 
	 * @param keyFile
	 */
	public VernamEncrypterDecrypter(final URI keyFile) {
		initializeKeyFromFile(keyFile);
	}

	private void initializeKeyFromFile(final URI keyFile) {
		final String fileContents = readFile(keyFile);
		this.key = new ArrayList<>(fileContents.length());
		for (final char c : fileContents.toCharArray()) {
			switch (c) {
			case '0':
				key.add(Bit.ZERO);
				break;
			case '1':
				key.add(Bit.ONE);
				break;
			default:
				throw new RuntimeException("Unable to parse " + c + " as Bit. Must be '0' or '1'.");
			}
		}
	}

	/**
	 * @return the key in use by this instance
	 */
	public String getKey() {
		final StringBuffer sb = new StringBuffer();
		for (Bit b : key) {
			switch (b) {
			case ONE:
				sb.append(1);
				break;
			case ZERO:
				sb.append(0);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * Generates key for later usage with class. Uses {@value #DEFAULT_ALGORITHM} by
	 * default.
	 * 
	 * @param keyLength
	 *            - how many bits long the key needs to be
	 * @param keyLocation
	 *            - location to write the generated keyfile
	 */
	public static void generateKeyFile(final int keyLength, final URI keyLocation) {
		VernamEncrypterDecrypter.generateKeyFile(keyLength, keyLocation, DEFAULT_ALGORITHM);
	}

	/**
	 * 
	 * @param keyLength
	 *            - how many bits long the key needs to be
	 * @param keyLocation
	 *            - location to write the generated keyfile
	 * @param algorithm
	 *            - Algorithm to use within {@link SecureRandom}
	 */
	public static void generateKeyFile(final int keyLength, final URI keyLocation, final String algorithm) {
		try {
			final StringBuffer sb = new StringBuffer();
			final SecureRandom random = SecureRandom.getInstance(algorithm);
			IntStream.rangeClosed(1, keyLength).forEach($ -> {
				if (random.nextBoolean()) {
					sb.append("0");
				} else {
					sb.append("1");
				}
			});

			Files.write(Paths.get(keyLocation), sb.toString().getBytes());
		} catch (NoSuchAlgorithmException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Responsible for encryption and decryption.
	 * 
	 * @param file
	 *            - File for which to read and encrypt/decrypt the contents
	 * @param cryptFunction
	 *            - function to apply, encrypt or decrypt
	 * @return - the contents of the file, either encrypted or decrypted
	 */
	protected String crypt(final URI file, final Function cryptFunction) {
		final char[] chars = readFile(file).toCharArray();

		if (key.size() < chars.length) {
			throw new IllegalArgumentException(
					"Cannot " + cryptFunction.toString() + " file because it is smaller than the current key.");
		}

		final StringBuffer sb = new StringBuffer(chars.length);

		for (int i = 0; i < chars.length; i++) {
			final Bit input = chars[i] == 0 ? Bit.ZERO : Bit.ONE;
			final Bit pad = this.key.get(i);
			sb.append(input.xor(pad).toString());
		}

		return sb.toString();
	}

}
