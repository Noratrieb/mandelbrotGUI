package mandelbrotCalculator;

public class CNumber {

	// values
	private double real;
	private double imag;

	// constructor
	public CNumber(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}

	public CNumber() {
		this.real = 0;
		this.imag = 0;
	}

	// multiply 2 complex numbers
	public static CNumber multiply(CNumber a, CNumber b) {

		CNumber result = new CNumber();

		// DO NOT TOUCH, COPIED IT this is some weird math stuff
		result.real = a.real * a.real - b.imag * b.imag;
		result.imag = a.real * b.imag + b.real * a.imag;

		return result;
	}

	public static CNumber add(CNumber a, CNumber b) {
		CNumber result = new CNumber();

		result.real = a.real + b.real;
		result.imag = a.imag + b.imag;

		return result;
	}

	public double getReal() {
		return real;
	}

	public void setReal(double real) {
		this.real = real;
	}

	public double getImag() {
		return imag;
	}

	public void setImag(double imag) {
		this.imag = imag;
	}
}