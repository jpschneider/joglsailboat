/**
 * 
 */
package jphs.sailboatsimulation;

/**
 * La classe <code>Rudder</code> decrit le comportement du gouvernail
 * d'un voilier
 * @author Bruno Aizier
 * @author Jean-Philippe Schneider
 * @version 1.0
 * @version 2.0 Portage du code C++ en Java.
 * @version 3.0 reprise des equations des forces et des moments
 *
 */
public class Rudder {
	/**
	 * Angle de barre
	 */
	private double rudderAng;
	/**
	 * Surface du safran
	 */
	private double surfaceSafran;
    /**
     * longueur du safran
     */
    private double longueurSafran;
    /**
     * Hauteur du safran
     */
    private double hauteurSafran;

    /**
     * Constructeur
     * @param rudderAng angle initial du safran en radians
     * @param surfaceSafran surface du safran
     */
	public Rudder(double rudderAng, double surfaceSafran) {
		this.rudderAng = rudderAng;
		this.surfaceSafran = surfaceSafran;
        this.longueurSafran = 0.4;
        this.hauteurSafran = 1.0;
	}

    /**
     * Getter de l'attribut longueurSafran
     * @return la longueur du safran
     */
    public double getLongueurSafran() {
        return longueurSafran;
    }

    /**
     * Getter de l'attribut hauteurSafran
     * @return la hauteur du safran
     */
    public double getHauteurSafran() {
        return hauteurSafran;
    }

    /**
     * Getter de l'attribut rudderAng
     * @return l'angle du safran en radians
     */
	public double getRudderAng() {
		return rudderAng;
	}

    /**
     * Setter de l'attribut rudderAng
     * @param rudderAng nouvelle valeur de l'angle du safran
     */
	public void setRudderAng(double rudderAng) {
		this.rudderAng = rudderAng;
	}

    /**
     * Calcul de la force de deviation sur le safran selon l'axe O3z3
     * @param vx1 vitesse du voilier selon Gx1
     * @param vy1 vitesse du voilier selon Gy1
     * @param deltag angle du safran
     * @return valeur de la force de deviation suivant O3z3
     */
	public double fDeviation(double vx1, double vy1, double deltag) {
        this.rudderAng = deltag;
		double vx3 = vx1 * Math.cos(this.rudderAng) + vy1 * Math.sin(this.rudderAng);
        double vy3 = vy1 * Math.cos(this.rudderAng) - vx1 * Math.sin(this.rudderAng);
        double v2 = Math.pow(vx1, 2.) + Math.pow(vy1, 2.);
        double ang = Math.atan2(vy3, vx3);
        double fDev = this.surfaceSafran * v2 * Math.sin(ang) * 0.9;
        fDev /= (0.2 + 0.3 * Math.sin(ang));
        return fDev;
    }
}
