/**
 * 
 */
package jphs.sailboatsimulation;

/**
 * La classe <code>Sail</code> decrit le comportement des voiles d'un voilier
 * @author Bruno Aizier
 * @author Jean-Philippe Schneider
 * @version 1.0
 * @version 2.0 Portage du code C++ en Java
 *
 */
public class Sail {
	/**
	 * Masse volumique de l'air
	 */
	public static double RHO_AIR = 1.293;
	/**
	 * Angle de la voile
	 */
	private double deltaV;
	/**
	 * Surface de la voile
	 */
	private double surfaceVoile;
	/**
	 * Hauteur du centre de poussee de la voile
	 */
	private double hV;
	/**
	 * Distance centre de poussee de la voile - mat
	 */
	private double l;
    /**
     * Force velique
     */
    private double fV;

    /**
     * Constructeur
     * @param deltaV angle de la voile en degres
     * @param surfaceVoile surface de la voile
     * @param hV hauteur du centre de poussee de la voile
     * @param l distance entre le centre de poussee de la voile et le mat
     */
	public Sail(double deltaV, double surfaceVoile,
			double hV, double l) {
		this.deltaV = Math.toRadians(deltaV);
		this.surfaceVoile = surfaceVoile;
		this.hV = hV;
		this.l = l;
        this.fV = 0;
	}

    /**
     * Getter de l'attribut deltaV
     * @return la valeur de l'angle de la voile en radians
     */
	public double getDeltaV() {
		return deltaV;
	}

    /**
     * Setter de l'attribut deltaV
     * @param deltaV nouvelle valeur de l'angle de la voile en radians
     */
	public void setDeltaV(double deltaV) {
		this.deltaV = deltaV;
	}

    /**
     * Getter de l'attribut hV
     * @return la valeur de la hauteur du centre de poussee de la voile
     */
	public double gethV() {
		return hV;
	}

    /**
     * Getter de l'attribut l
     * @return la valeur de la distance entre le centre de poussee de la voile et le mat
     */
	public double getL() {
		return l;
	}

    /**
     * Setter de l'attribut l
     * @param l nouvelle valeur de la distance entre le centre de poussee de la voile et le mat
     */
	public void setL(double l) {
		this.l = l;
	}

    /**
     * Setter de l'attribut fV
     * @return force velique suivant l'axe O2x2
     */
    public double getfV() {
        return this.fV;
    }

    /**
     * Calcul du coefficient de portance
     * @param psiv angle du vent par rapport à la voile
     * @return coefficient de portance
     */
	public double computeCx(double psiv) {
        double a = 0;
        double b = 0;
        double ang = Math.toDegrees(Math.abs(psiv));
        if (ang <= 15.) {
            a = 0.9 / 15.;
        } else if (ang <= 90.) {
            a = -0.9 / 75.;
            b = 0.9;
            ang -= 15.;
        } else if (ang <= 165.) {
            a = 0.9 / 75.;
            ang -= 90.;
        } else if (ang <= 180.) {
            a = -0.9 / 75.;
            b = 0.9;
            ang -= 165.;
        }
        return a * ang + b;
    }

    /**
     * Calcul de la force velique sur la voile selon O2z2
     * @param deltavmax angle de la voile
     * @param a intensite du vent
     * @param psiv angle du vent  dans R0
     * @param sailboat voilier sur lequel les forces s'exercent
     * @return la valeur de la force velique sur la voile selon 02z2
     */
	public double update(double deltavmax, double a, double psiv, Sailboat sailboat) {
		this.deltaV = deltavmax;
		double wappx2 = (a * Math.cos(psiv) - sailboat.getxPoint()) * (Math.cos(sailboat.getPhi()) *
                Math.cos(this.deltaV) - Math.sin(sailboat.getPhi()) * Math.cos(sailboat.getTheta()) *
                Math.sin(this.deltaV));
        wappx2 += (a * Math.sin(psiv) - sailboat.getyPoint()) * (Math.sin(sailboat.getPhi()) *
                Math.cos(this.deltaV) + Math.cos(sailboat.getPhi()) * Math.cos(sailboat.getTheta()) *
                Math.sin(this.deltaV));
        double wappy2 = -(a * Math.cos(psiv) - sailboat.getxPoint()) * (Math.cos(sailboat.getPhi()) *
                Math.sin(deltaV) + Math.sin(sailboat.getPhi()) * Math.cos(sailboat.getTheta()) *
                Math.cos(this.deltaV));
        wappy2 += (a * Math.sin(psiv) - sailboat.getyPoint()) * (Math.cos(sailboat.getPhi()) *
                Math.cos(sailboat.getTheta()) * Math.cos(this.deltaV) - Math.sin(sailboat.getPhi() *
                Math.sin(this.deltaV)));
        double wappz2 = (a * Math.cos(psiv) - sailboat.getxPoint()) * Math.sin(sailboat.getPhi()) *
                Math.sin(sailboat.getTheta());
        wappz2 -= (a * Math.sin(psiv) - sailboat.getyPoint()) * Math.cos(sailboat.getPhi()) *
                Math.cos(sailboat.getTheta());
        double a_ap2 = Math.pow(wappx2, 2.) + Math.pow(wappy2, 2.) + Math.pow(wappz2, 2.);
        double psiv2 = Math.atan2(wappy2, wappx2);
		this.fV =  0.5 * Sail.RHO_AIR * this.surfaceVoile * a_ap2 * this.computeCx(psiv2)*Math.signum(wappy2);
        return this.fV;
	}
}
