package dwilso95;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
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
public class MonoAlphabeticCipher extends Cipher {

	private static final String DEFAULT_ALGORITHM = "SHA1PRNG";

	/**
	 * @param keyFile
	 *            - URI to file containing key {@link MonoAlphabeticCipher} javadoc
	 *            for required format
	 */
	public MonoAlphabeticCipher() {
	}

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
	protected String crypt(final File keyFile, final File file, final Function cryptFunction) {
		final StringBuffer result = new StringBuffer();
		final char[] chars = Cipher.readFile(file).toCharArray();

		final BiMap<Character, Character> key = initializeKeyFromFile(keyFile);

		for (int i = 0, n = chars.length; i < n; i++) {
			final char c = chars[i];
			if (inCharacterBounds(c)) {
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

	/**
	 * @return the key in use by this instance
	 */
	public String printKey(final File keyFile) {
		final StringBuffer sb = new StringBuffer();

		final Iterator<Entry<Character, Character>> iter = initializeKeyFromFile(keyFile).entrySet().iterator();

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

	@Override
	public void generateKeyFile(File keyFile, File file) {
		// read file
		final String fileContents = Cipher.readFile(file);

		// create set of unique characters
		final Set<Character> uniqueCharacters = new HashSet<>();
		for (Character c : fileContents.toCharArray()) {
			// only accept characters in [a-zA-Z]
			if (inCharacterBounds(c)) {
				uniqueCharacters.add(c);
			}
		}

		final List<Character> list = new ArrayList<>(uniqueCharacters);
		int randomOffset;

		try {
			final SecureRandom random = SecureRandom.getInstance(DEFAULT_ALGORITHM);
			do {
				randomOffset = random.nextInt(list.size());
			} while (randomOffset == 0);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Unsupported alogrithm [" + DEFAULT_ALGORITHM + "]", e);
		}

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			int mappedValueIndex = i + randomOffset;
			if (mappedValueIndex >= list.size()) {
				mappedValueIndex = mappedValueIndex - list.size();
			}

			sb.append(list.get(i));
			sb.append(" ");
			sb.append(list.get(mappedValueIndex));

			if (i + 1 < list.size()) {
				sb.append("\n");
			}
		}

		Cipher.writeFile(keyFile, sb.toString());
	}

	/**
	 * Checks give supplied character is within the bounds of [a-zA-Z]
	 * 
	 * @param c
	 *            Character to check
	 * @return boolean
	 */
	private boolean inCharacterBounds(final Character c) {
		return (c > 64 && c < 91) || (c > 97 && c < 123);
	}

	private BiMap<Character, Character> initializeKeyFromFile(final File keyFile) {
		// Bidirectional map used to store the key. Can be used as is for encryption and
		// then inverted for decryption.
		final BiMap<Character, Character> key = HashBiMap.create();

		try (final Stream<String> stream = Files.lines(keyFile.toPath())) {
			stream.forEach(line -> key.putIfAbsent(line.charAt(0), line.charAt(2)));
		} catch (IOException e) {
			throw new RuntimeException("IOException when processing key file.", e);
		}

		return key;
	}
}
