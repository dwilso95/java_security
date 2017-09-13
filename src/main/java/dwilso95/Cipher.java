package dwilso95;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Cipher {

	/**
	 * Read the contents of a file into a String
	 * 
	 * @param file
	 *            - File of file to read
	 * @return the contents of the file as a String
	 */
	protected static String readFile(final File file) {
		try {
			return new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			throw new RuntimeException("IOException reading file. [" + file.toString() + "]", e);
		}
	}

	/**
	 * Write a string to a file
	 * 
	 * @param file
	 *            - File of the file in which to write
	 * @return the contents of the file as a String
	 */
	protected static void writeFile(final File file, final String string) {
		try {
			Files.write(file.toPath(), string.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("IOException writing file. [" + file.toString() + "]", e);
		}
	}

	/**
	 * Encrypts the contents of the file at the given File
	 * 
	 * @param keyFile
	 *            file containing key
	 * @param file
	 *            File of the file to read and encrypt
	 * @return the contents of the file, encrypted
	 */
	public String encrypt(final File keyFile, final File file) {
		return crypt(keyFile, file, Function.ENCRYPT);
	}

	/**
	 * Decrypts the contents of the file at the given File
	 * 
	 * @param keyFile
	 *            File containing key
	 * @param file
	 *            File of the file to read and decrypt
	 * @return the contents of the file, decrypted
	 */
	public String decrypt(final File keyFile, final File file) {
		return crypt(keyFile, file, Function.DECRYPT);
	}

	/**
	 * Internal enum used as argument for specifying encrypt or decrypt
	 */
	protected enum Function {
		ENCRYPT, DECRYPT
	};

	/**
	 * Generates a cipher specific key based on the input file
	 * 
	 * @param keyFile
	 *            - output file containing key
	 * @param file
	 *            - input file for basis of key file
	 */
	public abstract void generateKeyFile(File keyFile, File file);

	/**
	 * Print a cipher specific key in the given file
	 * 
	 * @param keyFile
	 *            - file containing key to print
	 */
	public abstract String printKey(File keyFile);

	/**
	 * Responsible for encryption and decryption.
	 * 
	 * @param file
	 *            - File for which to read and encrypt/decrypt the contents
	 * @param cryptFunction
	 *            - function to apply, encrypt or decrypt
	 * @return - the contents of the file, either encrypted or decrypted
	 */
	protected abstract String crypt(final File keyFile, final File file, final Function cryptFunction);

}
