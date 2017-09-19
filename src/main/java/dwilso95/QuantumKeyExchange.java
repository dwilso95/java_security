package dwilso95;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Class that runs a basic quantum key exchange simulation with random data.
 * Allows for running with and without a listener.
 * 
 */
public class QuantumKeyExchange {

	private final SecureRandom random;

	public QuantumKeyExchange() {
		try {
			this.random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Runs simulation and writes results to std out
	 * 
	 * @param eveIsListening
	 *            - whether or not to use an eavesdropper, eve
	 */
	public void run(final boolean eveIsListening) {

		final StringBuilder aliceBits = new StringBuilder();
		final StringBuilder aliceFilterScheme = new StringBuilder();
		final StringBuilder bobDetectionScheme = new StringBuilder();
		final StringBuilder bobBitMeasurements = new StringBuilder();
		final StringBuilder eveDetectionScheme = new StringBuilder();
		final StringBuilder eveBitMeasurements = new StringBuilder();
		final StringBuilder eveFilterScheme = new StringBuilder();

		for (int i = 0; i < 20; i++) {
			// Alice random selects polarization and sends bit
			Polarization sendingPolarizationValue = getRandomPolarization();
			aliceBits.append(sendingPolarizationValue.bitValue().toString());
			aliceBits.append(" ");
			aliceFilterScheme.append(sendingPolarizationValue.toString());
			aliceFilterScheme.append(" ");

			// if Eve is listening...
			if (eveIsListening) {
				final DetectionFilter eveDetectionFilter = getRandomDetectionFilter();
				eveDetectionScheme.append(eveDetectionFilter.toString());
				eveDetectionScheme.append(" ");

				eveBitMeasurements.append(eveDetectionFilter.filter(sendingPolarizationValue));
				eveBitMeasurements.append(" ");

				// update polarization value sent with new random polarization
				sendingPolarizationValue = getRandomPolarization();

				eveFilterScheme.append(sendingPolarizationValue.toString());
				eveFilterScheme.append(" ");
			}

			// Bob measures bit with his randomly selected detection filter and records bit
			// measurements
			final DetectionFilter bobDetectionFilter = getRandomDetectionFilter();
			bobDetectionScheme.append(bobDetectionFilter.toString());
			bobDetectionScheme.append(" ");
			bobBitMeasurements.append(bobDetectionFilter.filter(sendingPolarizationValue));
			bobBitMeasurements.append(" ");
		}

		System.out.println("Alice's bit sequence:   " + aliceBits);
		System.out.println("Alice's filter scheme:  " + aliceFilterScheme);

		if (eveIsListening) {
			System.out.println("\nEve's detection scheme: " + eveDetectionScheme);
			System.out.println("Eve's bit measurements: " + eveBitMeasurements);
			System.out.println("Eve's filter scheme:    " + eveFilterScheme);
		}

		System.out.println("\nBob's detection scheme: " + bobDetectionScheme);
		System.out.println("Bob's bit measurements: " + bobBitMeasurements);

	}

	private DetectionFilter getRandomDetectionFilter() {
		if (random.nextBoolean()) {
			return DetectionFilter.DIAGONAL;
		} else {
			return DetectionFilter.RECTILINEAER;
		}
	}

	private Polarization getRandomPolarization() {
		return Polarization.valueOf(random.nextInt(4));
	}

}
