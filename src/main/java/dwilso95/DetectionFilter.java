package dwilso95;

public enum DetectionFilter {
	DIAGONAL, RECTILINEAER;

	/**
	 * Returns the result of passing the given orientation through the current
	 * filter.
	 * 
	 * @param polarization
	 *            - The polarization of the photon
	 * @return the measurement of the filter applied to the polarization
	 */
	public final Bit filter(final Polarization polarization) {

		switch (this) {
		case DIAGONAL:
			switch (polarization) {
			case BACK_SLASH:
			case FORWARD_SLASH:
				return Bit.ZERO;
			case HORIZONTAL:
			case VERTICAL:
				return Bit.ONE;
			}
		case RECTILINEAER:
			switch (polarization) {
			case BACK_SLASH:
			case FORWARD_SLASH:
				return Bit.ONE;
			case HORIZONTAL:
			case VERTICAL:
				return Bit.ZERO;
			}
		}

		throw new RuntimeException(
				"Unknown state in DetectionFilter#filter. Polarization: " + polarization + " DetectionFilter: " + this);
	}

	@Override
	public String toString() {
		switch (this) {
		case DIAGONAL:
			return "X";
		case RECTILINEAER:
			return "+";
		}
		throw new RuntimeException("Unsupported enum value: " + this.name());
	}
}
