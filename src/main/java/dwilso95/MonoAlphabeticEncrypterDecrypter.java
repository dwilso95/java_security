package dwilso95;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Simple class for performing monoalphabetic key encryption
 * 
 * Uses a key file where each line in file contains the source character,
 * followed by a space, and last the destination character
 * 
 */
public class MonoAlphabeticEncrypterDecrypter {

	/**
	 * Bidirectional map used to store the key. Can be used as is for encryption and
	 * then inverted for decryption.
	 */
	private final BiMap<Character, Character> key = HashBiMap.create();

	/**
	 * @param keyFile
	 *            - URI to file containing key
	 *            {@link MonoAlphabeticEncrypterDecrypter} javadoc for required
	 *            format
	 */
	public MonoAlphabeticEncrypterDecrypter(URI keyFile) {
		readKeyFile(keyFile);
	}

	private void readKeyFile(final URI keyFile) {
		
		try (final Stream<String> stream = Files.lines(Paths.get(keyFile))) {
			stream.forEach(line -> key.putIfAbsent(line.charAt(0), line.charAt(2)));
		} catch (IOException e) {
			throw new RuntimeException("IOException when processing key file.", e);
		}
	}

	/**
	 * Read the contents of a file into a String
	 * 
	 * @param file
	 *            - URI of file to read
	 * @return the contents of the file as a String
	 */
	public String readFile(final URI file) {
		try {
			return new String(Files.readAllBytes(Paths.get(file)));
		} catch (IOException e) {
			throw new RuntimeException("IOException when processing text file.", e);
		}
	}

	/**
	 * @return the key in use by this instance
	 */
	public String getKey() {
		final StringBuffer sb = new StringBuffer();
		final Iterator<Entry<Character, Character>> iter = key.entrySet().iterator();

		while (iter.hasNext()) {
			final Entry<Character, Character> entry = iter.next();
			sb.append(entry.getKey());
			sb.append("->");
			sb.append(entry.getValue());
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	/**
	 * Encrypts the contents of the file at the given URI
	 * 
	 * @param file
	 *            URI of the file to read and encrypt
	 * @return the contents of the file, encrypted
	 */
	public String encrypt(final URI file) {
		return crypt(file, Function.ENCRYPT);
	}

	/**
	 * Decrypts the contents of the file at the given URI
	 * 
	 * @param file
	 *            URI of the file to read and decrypt
	 * @return the contents of the file, decrypted
	 */
	public String decrypt(final URI file) {
		return crypt(file, Function.DECRYPT);
	}

	/**
	 * Internal enum used as argument for specifying encrypt or decrypt
	 */
	private enum Function {
		ENCRYPT, DECRYPT
	};

	/**
	 * Responsible for encryption and decryption. Passes through all characters
	 * outside of the English alphabet.
	 * 
	 * @param file
	 *            - File for which to read and encrypt/decrypt the contents
	 * @param cryptFunction
	 *            - function to apply, encrypt or decrypt
	 * @return - the contents of the file, either encrypted or decrypted
	 */
	private String crypt(final URI file, final Function cryptFunction) {
		final StringBuffer result = new StringBuffer();
		final char[] chars = readFile(file).toCharArray();

		for (int i = 0, n = chars.length; i < n; i++) {
			final char c = chars[i];
			if ((c > 64 && c < 91) || (c > 97 && c < 123)) {
				switch (cryptFunction) {
				case DECRYPT:
					result.append(key.inverse().get(c).charValue());
					break;
				case ENCRYPT:
					result.append(key.get(c).charValue());
					break;
				default:
					throw new RuntimeException("Unknown CryptType, " + cryptFunction);
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

}
