/**
 * 
 */
package jphs.sailboatsimulation;

/**
 * La classe <code>Hull</code> decrit le comportement de la coque d'un
 * voilier
 * @author Bruno Aizier
 * @author Jean-Philippe Schneider
 * @version 1.0
 * @version 2.0 portage du code C++ en Java
 * @version 3.0 reprise des equations des forces et des moments
 *
 */
public class Hull {
	public final static double RHO_EAU = 1.100;
    public final static double COEFF_GRAVITE = 9.81;
	/**
	 * Masse ou deplacement
	 */
	private double m;
	/**
	 * Moment d'inertie Jx
	 */
	private double Jx;
	/**
	 * Moment d'inertie Jz
	 */
	private double Jz;
	/**
	 * Coefficient de la force de frottement
	 */
	private double alphaF;
	/**
	 * Coefficient du couple de frottement
	 */
	private double alphaPhi;
	/**
	 * Distance centre de gravite - axe du gouvernail
	 */
	private double rG;
	/**
	 * Distance centre de gravite - mat
	 */
	private double rV;
	/**
	 * Surface du plan anti-derive
	 */
	private double sAntiderive;
    /**
     * Distance centre de gravite - point d'application de la force anti-derive selon z1
     */
    private double ladz1;

    /**
     * Constructeur
     * @param m masse de la coque
     * @param Jx coefficient d'inertie suivant l'axe Gx
     * @param Jz coefficient d'inertie suivant l'axe Gz
     * @param alphaF coefficient d'amortissement pour les mouvements longitudinaux
     * @param alphaPhi coefficient d'amortissement pour les rotations
     * @param rG distance entre le centre de gravite et le gouvernail
     * @param rV distance entre le centre de gravite et le mat
     */
	public Hull(double m, double Jx, double Jz, double alphaF, double alphaPhi,
			double rG, double rV) {
		this.m = m;
		this.Jx = Jx;
		this.Jz = Jz;
		this.alphaF = alphaF;
		this.alphaPhi = alphaPhi;
		this.rG = rG;
		this.rV = rV;
		this.sAntiderive = 0.4;
        this.ladz1 = 1.5;
	}

    /**
     * Getter de l'attribut ladz1
     * @return distance entre le centre de gravite et le point d'application de la force anti-derive selon z1
     */
    public double getLadz1() {
        return ladz1;
    }


    /**
     * Calcul de la force anti-derive selon y1
     * @param vx1 vitesse du voilier selon x1
     * @param vy1 vitesse du voilier selon y1
     * @return force anti-derive selon y1
     */
    public double fAntiDerive(double vx1, double vy1) {
        double v2 = Math.pow(vx1, 2.) + Math.pow(vy1, 2.);
		return 0.5 * Hull.RHO_EAU * this.sAntiderive * v2 * Math.signum(vy1);
	}

    /**
     * Calcul de la force de resistance a l'avancement
     * @param v vitesse du voilier
     * @return valeur de la force de resistance a l'avancement
     */
    public double fResistance(double v) {
        return -this.alphaF * v;
    }

    /**
     * Calcul du poids de la coque
     * @return valeur du poids de la coque
     */
    public double poids() {
        return -m * Hull.COEFF_GRAVITE;
    }

    /**
     * Getter pour l'attribut m
     * @return la masse de la coque
     */
	public double getM() {
		return m;
	}

    /**
     * Getter pour l'attribut Jx
     * @return le coefficient d'inertie de la coque selon l'axe Gx1
     */
	public double getJx() {
		return Jx;
	}

    /**
     * Getter pour l'attribut Jz
     * @return la valeur du coefficient d'inertie de la coque selon l'axe Gz1
     */
	public double getJz() {
		return Jz;
	}

    /**
     * Getter pour l'attribut alphaPhi
     * @return la valeur du coefficient d'amortissement des rotations
     */
	public double getAlphaPhi() {
		return alphaPhi;
	}


    /**
     * Getter pour l'attribut rG
     * @return valeur de la distance entre le centre de gravite et le gouvernail selon x1
     */
	public double getrG() {
		return rG;
	}


    /**
     * Getter pour l'attribut rV
     * @return valeur de la distance entre le centre de gravite et le mat selon x1
     */
	public double getrV() {
		return rV;
	}


    /**
     * Calcul du coefficient d'inertie selon l'axe Gy1
     * @param sailboat le voilier a considerer
     * @param mfvy1 le moment de la force velique suivant l'axe Gy1
     * @param mfdevy1 le moment de la force de deviation suivant l'axe Gy1
     * @param mfvz1 le moment de la force velique suivant l'axe Gz1
     * @param mfdevz1 le moment de la force de deviation suivant l'axe Gz1
     * @return la valeur du coefficient d'inertie selon l'axe Gy1, 0 si le calcul est impossible
     */
	public double getJy(Sailboat sailboat, double mfvy1, double mfdevy1,double mfcy1,  double mfvz1, double mfdevz1,
        double mfcz1) {
		double a = (sailboat.getPhiPoint() * sailboat.getThetaPoint() * Math.pow(Math.sin(sailboat.getTheta()), 2.))
                / (this.Jz * Math.cos(sailboat.getTheta()));
        if (a == 0) {
            return 0;
        }
        double b = -(sailboat.getPhiPoint() * sailboat.getThetaPoint() * Math.cos(sailboat.getTheta()));
        b -= (Math.sin(sailboat.getTheta()) / (this.Jz * Math.cos(sailboat.getTheta()))) *
                (mfvz1 + mfdevz1 + mfcz1 + sailboat.getPhiPoint() * sailboat.getThetaPoint() * Math.sin(sailboat.getTheta()) *
                        (this.Jx + this.Jz));
        double c = mfvy1 + mfdevy1 + mfcy1 - sailboat.getPhiPoint() * sailboat.getThetaPoint() *
                Math.cos(sailboat.getTheta()) * (this.Jx - this.Jz);
        double delta = Math.pow(b, 2.) - 4 * a * c;
        if (delta > 0) {
            double sol1 = (-b - Math.sqrt(delta)) / (2 * a);
            double sol2 = (-b + Math.sqrt(delta)) / (2 * a);
            return (sol1 > 0) ? sol1 : sol2;
        } else if (delta == 0){
            return (-b / (2 * a));
        } else {
            return 0;
        }
	}
}
