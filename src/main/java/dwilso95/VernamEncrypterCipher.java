package dwilso95;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Simple class for performing Vernam encryption
 * 
 */
public class VernamEncrypterCipher extends Cipher {

	private static final String DEFAULT_ALGORITHM = "SHA1PRNG";

	/**
	 * 
	 * @param keyFile
	 */
	public VernamEncrypterCipher() {
	}

	private List<Bit> initializeKeyFromFile(final File keyFile) {
		final String fileContents = Cipher.readFile(keyFile);
		final List<Bit> key = new ArrayList<>(fileContents.length());
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
		return key;
	}

	/**
	 * @return the key in use by this instance
	 */
	public String printKey(final File keyFile) {
		final StringBuffer sb = new StringBuffer();
		final List<Bit> key = initializeKeyFromFile(keyFile);
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
	 * Responsible for encryption and decryption.
	 * 
	 * @param file
	 *            - File for which to read and encrypt/decrypt the contents
	 * @param cryptFunction
	 *            - function to apply, encrypt or decrypt
	 * @return - the contents of the file, either encrypted or decrypted
	 */
	protected String crypt(final File keyFile, final File file, final Function cryptFunction) {
		final char[] chars = Cipher.readFile(file).toCharArray();
		final List<Bit> key = initializeKeyFromFile(keyFile);
		if (key.size() < chars.length) {
			throw new IllegalArgumentException(
					"Cannot " + cryptFunction.toString() + " file because it is smaller than the current key.");
		}

		final StringBuffer sb = new StringBuffer(chars.length);

		for (int i = 0; i < chars.length; i++) {
			final Bit input = chars[i] == '0' ? Bit.ZERO : Bit.ONE;
			final Bit pad = key.get(i);

			sb.append(input.xor(pad).toString());
		}

		return sb.toString();
	}

	@Override
	public void generateKeyFile(File keyFile, File file) {
		final int keyLength = Cipher.readFile(file).length();
		try {
			final StringBuffer sb = new StringBuffer();
			final SecureRandom random = SecureRandom.getInstance(DEFAULT_ALGORITHM);
			IntStream.rangeClosed(1, keyLength).forEach($ -> {
				if (random.nextBoolean()) {
					sb.append("0");
				} else {
					sb.append("1");
				}
			});

			Cipher.writeFile(keyFile, sb.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
