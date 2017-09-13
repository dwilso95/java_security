package dwilso95;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Cipher {

	/**
	 * Read the contents of a file into a String
	 * 
	 * @param file
	 *            - URI of file to read
	 * @return the contents of the file as a String
	 */
	protected String readFile(final URI file) {
		try {
			return new String(Files.readAllBytes(Paths.get(file)));
		} catch (IOException e) {
			throw new RuntimeException("IOException processing  file.", e);
		}
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
	protected enum Function {
		ENCRYPT, DECRYPT
	};

	/**
	 * @return the key in use by this instance
	 */
	public abstract String getKey();

	/**
	 * Responsible for encryption and decryption.
	 * 
	 * @param file
	 *            - File for which to read and encrypt/decrypt the contents
	 * @param cryptFunction
	 *            - function to apply, encrypt or decrypt
	 * @return - the contents of the file, either encrypted or decrypted
	 */
	protected abstract String crypt(final URI file, final Function cryptFunction);

}
