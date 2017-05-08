/**
 * 
 */
package jphs.sailboatsimulation;

/**
 * La classe <code>Wind</code> decrit le comportement du vent dans la simulation
 * de voilier
 * @author Bruno Aizier
 * @author Jean-Philippe Schneider
 * @version 1.0
 * @version 2.0 portage du code C++ en Java
 *
 */
public class Wind {
	/**
	 * Force du vent
	 */
	private double windForce;
	/**
	 * Direction du vent
	 */
	private double windDir;
	
	public Wind(double windForce, double windDir) {
		this.windForce = windForce;
		this.windDir = windDir;
	}
	
	public Wind() {
		this(0,0);
	}

	public double getWindForce() {
		return windForce;
	}

	public void setWindForce(double windForce) {
		this.windForce = windForce;
	}

	public double getWindDir() {
		return windDir;
	}

	public void setWindDir(double windDir) {
		this.windDir = windDir;
	}
	
	
}
