/**
 * 
 */
package jphs.sailboatsimulation;

/**
 * La classe <code>Sailboat</code> decrit le comportement d'un voilier.
 * @version 1.0
 * @version 2.0 Portage du code C++/QT en Java avec la librairie JOGL
 * @since 28/07/2014
 * @version 3.0 reprise des equations des forces et des moments
 * @since 02/04/2016
 * @author Fabrice Le Bars
 * @author Luc Jaulin
 * @author Club Robotique de l'ENSTA Bretagne
 * @author Jean-Philippe Schneider
 * @author Bruno Aizier
 */
public class Sailboat {

    /**
     * Position du voilier selon Ox0
     */
    private double x;
    /**
     * Position du voilier selon Oy0
     */
	private double y;
    /**
     * Angle de la voile
     */
	private double deltavmax;
    /**
     * Angle de rotation du voilier autour de l'axe Gx1
     */
	private double theta;
    /**
     * Angle de rotation du voilier autour de l'axe Gz0
     */
	private double phi;
    /**
     * Vitesse du voilier selon Ox0
     */
	private double xPoint;
    /**
     * Vitesse du voilier selon Oy0
     */
	private double yPoint;
    /**
     * Vitesse de rotation du voilier autour de l'axe Gx1
     */
	private double thetaPoint;
    /**
     * Vitesse de rotation du voilier autour de l'axe Gz0
     */
	private double phiPoint;
    /**
     * Distance maximum entre le centre de carene et le centre de gravite selon Gy1
     */
    private double dcgCcMax;
    /**
     * Distance maximum entre le centre de carene et le centre de gravite selon Gz1
     */
    private double lgCcz1;
    /**
     * Coque
     */
	private Hull theHull;
    /**
     * Voile
     */
	private Sail theSail;
    /**
     * Safran
     */
	private Rudder theRudder;

    /**
     * Constructeur
     */
	public Sailboat() {
		this.deltavmax = 0;
		this.x = 0;
		this.y = 0;
		this.phi = 0.2;
		this.theta = -0.25;
		this.xPoint = 0;
		this.yPoint = 0;
		this.thetaPoint = 0;
		this.phiPoint = 0;
        this.dcgCcMax = 0.8;
        this.lgCcz1 = 0.;

		/*
		 * Coque : masse = 500
		 * Coque : Jx = 10000
		 * Coque : Jz = 10000
		 * Coque : alphaf = 100
		 * Coque : alphatheta = 6000
		 * Coque : rG = 2.0
		 * Coque : rV = 1.0
		 */
		this.theHull = new Hull(500, 10000.0, 10000.0, 100.0, 6000.0, 2.0, 1.0);
		/*
		 * Voile : deltaV = 45
		 * Voile : surfaceVoile = 8
		 * Voile : hV = 6
		 * Voile : l = 1
		 */
		this.theSail = new Sail(45, 8, 6.0, 1.0);
		/*
		 * Gouvernail : rudderAng = 0
		 * Gouvernail : surfaceSafran = 0.2
		 */
		this.theRudder = new Rudder(0, 0.2);
	}

    /**
     * Getter de l'attribut deltavmax
     * @return valeur de l'angle de la voile
     */
	public double getDeltavmax() {
		return deltavmax;
	}

    /**
     * Recuperation de l'angle du safran
     * @return valeur de l'angle du safran en radian
     */
	public double getDeltag() {
		return this.theRudder.getRudderAng();
	}

    /**
     * Getter de l'attribut xPoint
     * @return valeur de la vitesse du voilier selon l'axe Ox0
     */
    public double getxPoint() {
        return xPoint;
    }

    /**
     * Getter de l'attribut  yPoint
     * @return valeur de la vitesse du voilier selon l'axe Oy0
     */
    public double getyPoint() {
        return yPoint;
    }

    /**
     * Getter de l'attribut x
     * @return position du voilier selon l'axe Ox0
     */
    public double getX() {
		return x;
	}

    /**
     * Setter de l'attribut x
     * @param x nouvelle valeur de position du voilier selon l'axe Ox0
     */
	public void setX(double x) {
		this.x = x;
	}

    /**
     * Getter de l'attribut y
     * @return position du voilier selon l'axe Oy0
     */
	public double getY() {
		return y;
	}

    /**
     * Setter de l'attribut y
     * @param y nouvelle position du voilier selon l'axe Oy0
     */
	public void setY(double y) {
		this.y = y;
	}

    /**
     * Getter de l'attribut theta
     * @return valeur de l'angle du voilier autour de l'axe Gx1
     */
	public double getTheta() {
		return theta;
	}

    /**
     * Getter de l'attribut phi
     * @return valeur de l'angle du voilier autour de l'axe Gz0
     */
	public double getPhi() {
		return phi;
	}

    /**
     * Recuperation de l'angle de la voile
     * @return valeur de l'angle de la voile en radian
     */
	public double getDeltav() {
		return this.theSail.getDeltaV();
	}

    /**
     * Getter de l'attribut thetaPoint
     * @return valeur de la vitesse de rotation du voilier autour de l'axe Gx1
     */
    public double getThetaPoint() {
        return thetaPoint;
    }

    /**
     * Getter de l'attribut phiPoint
     * @return valeur de la vitesse de rotation du voilier autour de l'axe Gz0
     */
    public double getPhiPoint() {
        return phiPoint;
    }

    /**
     * Getter de l'attribut theSail
     * @return l'objet decrivant la voile du voilier
     */
    public Sail getTheSail() {
        return this.theSail;
    }

    /**
     * Calcul de la force velique
     * @param theWind vent auquel le voilier est soumis
     * @return valeur de la force velique selon O2x2
     */
    private double fVelique(Wind theWind) {
        //Force du vent dans 0
        double a = theWind.getWindForce();
        //Angle du vent dans 0
        double psi0 = theWind.getWindDir();
        //Composante de la force velique selon y2
        return this.theSail.update(deltavmax, a, psi0, this);
    }

    /**
     * Projection de la vitesse du voilier suivant l'axe Gx1
     * @return composante selon Gx1 de la vitesse du voilier
     */
    private double getVx1() {
        return this.xPoint * Math.cos(this.phi) + this.yPoint * Math.sin(this.phi);
    }

    /**
     * Projection de la vitesse du voilier suivant l'axe Gy1
     * @return composante selon Gy1 de la vitesse du voilier
     */
    private double getVy1() {
        double vy1 = this.yPoint * Math.cos(this.phi) * Math.cos(this.theta);
        vy1 -= this.xPoint * Math.sin(this.phi) * Math.cos(this.theta);
        return vy1;
    }

    /**
     * Calcul de la force anti-derive
     * @return force anti-derive selon l'axe Gy1
     */
    private double fAntiDerive() {
        return  this.theHull.fAntiDerive(-this.getVx1(), -this.getVy1());
    }

    /**
     * Calcul de la force de deviation sur le safran
     * @param deltag angle du safran
     * @return la force de de deviation sur le safran selon O3y3
     */
    private double fDeviation(double deltag) {
        return this.theRudder.fDeviation(-this.getVx1(), -this.getVy1(), deltag);
    }

    /**
     * Calcul de la project de la force de resistance a l'avancement sur l'axe Gx1
     * @return la valeur de la force de resistance a l'avancement selon l'axe Gx1
     */
    private double fResistanceX1() {
        return this.theHull.fResistance(this.getVx1());
    }

    /**
     * Calcul de la project de la force de resistance a l'avancement sur l'axe Gy1
     * @return la valeur de la force de resistance a l'avancement selon l'axe Gy1
     */
    private double fResistanceY1() {
        return this.theHull.fResistance(this.getVy1());
    }

    /**
     * Calcul des forces et des moments pour mettre a jour la position, les angles et leurs derives
     * @param dt intervalle de temps pour l'integration
     * @param deltag angle du safran
     * @param deltavmax angle de la voile
     * @param theWind vent auquel le voilier est soumis
     */
	public void update(double dt, double deltag, double deltavmax, Wind theWind) {
        this.deltavmax = deltavmax;

        /*
            Force velique sur 2 dans R2.
         */
        double fvy2 = this.fVelique(theWind);
        /*
            Force anti-derive sur 1 dans R1.
         */
        double fady1 = this.fAntiDerive();
        /*
            Force de deviation du gouvernail sur 3 dans R3
         */
        double fdevy3 = this.fDeviation(deltag);
        /*
            Force de resistance a l'avancement sur 1 dans R1
         */
        double frx1 = this.fResistanceX1();
        double fry1 = this.fResistanceY1();
        /*
            Poids dans sur 1 dans R0
         */
        double pz0 = this.theHull.poids();
        /*
            Force velique dans sur 2 dans R0
         */
        double deltav = this.theSail.getDeltaV();
        double fvx0 = -(Math.sin(deltav) * Math.cos(this.phi) + Math.cos(deltav) * Math.sin(this.phi) *
                Math.sin(this.theta)) * fvy2;
        double fvy0 = (Math.cos(deltav) * Math.cos(this.theta) * Math.cos(this.phi) - Math.sin(deltav) *
                Math.sin(this.phi)) * fvy2;
        double fvz0 = Math.cos(deltav) * Math.sin(this.theta) *fvy2;
        /*
            Force anti-derive sur 1 dans R0
         */
        double fadx0 = -(Math.cos(this.theta) * Math.sin(this.phi) * fady1);
        double fady0 = Math.cos(this.theta) * Math.sin(this.phi) * fady1;
        double fadz0 = Math.sin(this.theta) * fady1;
        /*
            Force de deviation sur 3 dans R0
         */
        double rudderAng = this.theRudder.getRudderAng();
        double fdevx0 = -(Math.sin(rudderAng) * Math.cos(this.phi) + Math.cos(rudderAng) * Math.sin(this.phi) *
                Math.cos(this.theta)) * fdevy3;
        double fdevy0 = (Math.cos(rudderAng) * Math.cos(this.theta) * Math.cos(this.phi) - Math.sin(rudderAng) *
                Math.sin(this.phi) * fdevy3);
        double fdevz0 = Math.cos(rudderAng) * Math.sin(this.theta) * fdevy3;

        /*
            Force de resistance sur 1 dans R0
         */
        double frx0 = Math.cos(this.phi) * frx1 - Math.cos(this.theta) * Math.sin(this.phi) * fry1;
        double fry0 = Math.sin(this.phi) * frx1 + Math.cos(this.theta) * Math.cos(this.phi) * fry1;
        double frz0 = Math.sin(this.theta) * fry1;
        /*
            Poussee d'archimede sur 1 dans R0
         */
        double faz0 = -(fvz0 + fadz0 + fdevz0 +pz0 + frz0);
        /*
            Acceleration dans R0
         */
        double m1 = this.theHull.getM();
        double xPointPoint = (1. / m1) * (fvx0 + fadx0 + fdevx0 + frx0);
        double yPointPoint = (1. / m1) * (fvy0 + fady0 + fdevy0 + fry0);
        /*
            Moment de la force velique en G dans R2
         */
        double mfvx2 = -this.theSail.gethV() * fvy2;
        double mfvz2 = fvy2 * (this.theHull.getrV() * Math.cos(deltav) - this.theSail.getL());
        /*
            Moment de la force anti-derive en G dans 1
         */
        double mfadx1 = this.theHull.getLadz1() * fady1;
        /*
            Moment de la force de deviation en G dans 3
         */
        double mfdevx3 = 0.5 * this.theRudder.getHauteurSafran() * fdevy3;
        double mfdevz3 = -fdevy3 * (this.theHull.getrG() + 0.5 * this.theRudder.getLongueurSafran());
        /*
            Moment de la poussee d'Archimede en G dans 1
         */
        double mfax1 = -this.dcgCcMax * Math.sin(this.theta) * Math.cos(this.theta) * faz0;
        mfax1 -= this.lgCcz1 * faz0 * Math.sin(this.theta);
        /*
            Moment du couple de frottement dans 0
         */
        double mcfz0 = -this.theHull.getAlphaPhi() * this.phiPoint;
        double mcfx1 = -this.theHull.getAlphaPhi() * this.thetaPoint;
        /*
            Moment de la force velique en G dans 1
         */
        double mfvx1 = mfvx2 * Math.cos(deltav);
        double mfvy1 = mfvx2 * Math.sin(deltav);
        double mfvz1 = mfvz2;
        /*
            Moment de la force de deviation en G dans 1
         */
        double mfdevx1 = mfdevx3 * Math.cos(rudderAng);
        double mfdevy1 = mfdevx3 * Math.sin(rudderAng);
        double mfdevz1 = mfdevz3;
        /*
            Moment du couple de frottement en G dans 1
         */
        double mcfy1 = mcfz0 * Math.sin(this.theta);
        double mcfz1 = mcfz0 * Math.cos(this.theta);
        /*
         * Derivee du vecteur rotation dans 1
         */
        double jx1 = this.theHull.getJx();
        double jz1 = this.theHull.getJz();
        double jy1 = this.theHull.getJy(this, mfvy1, mfdevy1, mcfy1, mfvz1, mfdevz1, mcfz1);


        double thetaPointPoint = (1. / jx1) * (mfvx1 + mfadx1 + mfdevx1 + mfax1 + mcfx1 - Math.pow(this.phiPoint, 2.) *
                Math.cos(this.theta) * Math.sin(this.theta) * (jz1 - jy1));
        double phiPointPoint = (1. / (jz1 * Math.cos(this.theta))) *
                ( mfvz1  + mfdevz1 + mcfz1 - this.thetaPoint * this.phiPoint * Math.sin(this.theta) * (jy1 - jx1 - jz1));
        /*
            Integration selon schema d'Euler
         */
        this.xPoint = this.xPoint + xPointPoint * dt;
        this.yPoint = this.yPoint + yPointPoint * dt;
        this.x = this.x + this.xPoint * dt;
        this.y = this.y + this.yPoint * dt;
        this.thetaPoint = this.thetaPoint + thetaPointPoint * dt;
        this.phiPoint = this.phiPoint + phiPointPoint * dt;
        this.theta = this.theta + this.thetaPoint * dt;
        this.phi = this.phi + this.phiPoint * dt;
	}
}
 