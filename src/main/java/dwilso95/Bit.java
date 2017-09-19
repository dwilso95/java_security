package dwilso95;

public enum Bit {
	ZERO, ONE;

	public final Bit xor(final Bit bit) {
		if (this == bit) {
			return Bit.ZERO;
		} else {
			return Bit.ONE;
		}
	}

	public final String toString() {
		switch (this) {
		case ZERO:
			return "0";
		case ONE:
			return "1";
		default:
			return "";
		}

	}
}
