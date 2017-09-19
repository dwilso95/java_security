package dwilso95;

public enum Polarization {

	HORIZONTAL, VERTICAL, BACK_SLASH, FORWARD_SLASH;

	public static Polarization valueOf(final int polarization) {
		switch (polarization) {
		case 0:
			return HORIZONTAL;
		case 1:
			return VERTICAL;
		case 2:
			return BACK_SLASH;
		case 3:
			return FORWARD_SLASH;
		default:
			throw new IllegalArgumentException("Only values supported are 0, 1, 2, 3.");
		}
	}

	public Bit bitValue() {
		switch (this) {
		case HORIZONTAL:
		case BACK_SLASH:
			return Bit.ZERO;
		case VERTICAL:
		case FORWARD_SLASH:
			return Bit.ONE;
		default:
			throw new RuntimeException("Unsupported enum value: " + this.name());
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case BACK_SLASH:
			return "\\";
		case FORWARD_SLASH:
			return "/";
		case HORIZONTAL:
			return "-";
		case VERTICAL:
			return "|";
		}
		throw new RuntimeException("Unsupported enum value: " + this.name());
	}

}
