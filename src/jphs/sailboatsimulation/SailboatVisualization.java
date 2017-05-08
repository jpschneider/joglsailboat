package jphs.sailboatsimulation;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.*;

/**
 * La classe <code>SailboatVisualization</code> permet de decrire l'affichage
 * des elements dans une fenetre AWT. Elle implemente egalement les methodes des
 * interfaces:
 * - GLEventListener : methodes d'interface avec OpenGL;
 * - MouseListener : methodes pour gerer la souris;
 * - MouseMotionListener : methodes pour gerer les mouvements de la souris;
 * - KeyListener : methodes pour gerer le clavier.
 * @author Fabrice Le Bars
 * @author Luc Jaulin
 * @author Club Robotique de l'ENSTA Bretagne
 * @author Jean-Philippe Schneider
 * @version 1.0
 * @version 2.0 Portage du code C++/QT en Java avec la librairie JOGL
 * @version 3.0 reprise des equations des forces et des moments
 *
 */
public class SailboatVisualization implements GLEventListener, MouseListener,
        MouseMotionListener, KeyListener {

    /**
     * valeur du zoom
     */
    private float zoom;
    /**
     * angle de la camera suivant l'axe Ox0
     */
    private int angleX;
    /**
     * position de la camera suivant l'axe Oy0
     */
    private int yCam;
    /**
     * angle de la camera suivant l'axe Oz0
     */
    private int angleZ;
    /**
     * angle de la voile
     */
    private double deltavmax;
    /**
     * angle du safran
     */
    private double deltag;
    /**
     * position de la lumiere
     */
    private float lightPos[] = {5.0f, 5.0f, 10.0f, 1.0f} ;
    /**
     * derniere position du curseur suivant l'axe Ox0
     */
    private int lastPosX;
    /**
     * derniere position du curseur suivant l'axe Oy0
     */
    private int lastPosY;
    /**
     * voilier a simuler
     */
    private Sailboat sailboat;
    /**
     * simulation en cours
     */
    private boolean isStarted;
    /**
     * animation
     */
    private FPSAnimator fpsAnimator;
    /**
     * vent pour la simulation
     */
    private Wind theWind;

    /**
     * Constructeur
     */
    public SailboatVisualization() {
        this.zoom = 0.5f;
        this.angleZ = 0;
        this.yCam = -10;
        this.angleX = 5;
        this.deltag = 0;
        this.deltavmax = 0.3;
        this.sailboat = new Sailboat();
        this.isStarted = false;
		/*
		 * Vent : a = 4
		 * Vent : psi = 0*PI
		 */
        this.theWind = new Wind(4, 0*Math.PI);
    }

    public void setFpsAnimator(FPSAnimator fpsAnimator) {
        this.fpsAnimator = fpsAnimator;
    }

    public static void main(String args[]) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("Sailboat Simulation");
        frame.setSize(300, 300);
        frame.add(canvas);
        frame.setVisible(true);
        SailboatVisualization visu = new SailboatVisualization();

        canvas.setFocusable(true);
        canvas.requestFocus();
        canvas.addGLEventListener(visu);
        canvas.addMouseListener(visu);
        canvas.addMouseMotionListener(visu);
        canvas.addKeyListener(visu);

        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        FPSAnimator animator = new FPSAnimator(canvas, 50, true);
        visu.setFpsAnimator(animator);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0.9f, 0.9f, 1f, 1f);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, this.lightPos, 0);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        double dt = this.isStarted?0.020:0.0;
        update(dt);
        render(glAutoDrawable.getGL().getGL2());
    }

    private void update(double dt) {
        this.sailboat.update(dt, this.deltag, this.deltavmax, this.theWind);
        System.out.println("xPoint " + this.sailboat.getxPoint());
    }

    private void render(GL2 gl) {
        GLU glu = GLU.createGLU(gl);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT|GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(-this.yCam, 0, 0, 0, 0, 0, 0, 0, 1);
        //enables to have x-axis pointing to the right, y-axis back and z-axis up
        gl.glRotatef((float)90., 0.f, 0.f, 1.f);
        gl.glRotatef((float)this.angleX, 1.f, 0.f, 0.f);
        gl.glRotatef((float)this.angleZ, 0.f, 0.f, 1.f);
        gl.glScalef(this.zoom, this.zoom, this.zoom);
        gl.glPushMatrix();
        drawSea(gl);
        drawBuoys(gl);
        gl.glPopMatrix();
        gl.glTranslated(this.sailboat.getX(), this.sailboat.getY(), 0.0);
        drawBoat(gl);
        drawWind(gl);
    }

    private void drawWind(GL2 gl) {
        double psi = this.theWind.getWindDir();
        gl.glPushMatrix();
        gl.glRotatef((float)((psi + 0.5*Math.PI) * 180.0 / Math.PI), 0.0f, 0.0f, 1.0f);
        gl.glBegin(GL2.GL_LINES);
        double a = 1;
        gl.glColor3f(0.7f, 0.1f, 0.0f);
        gl.glVertex3f(3, 14, 13);
        gl.glVertex3f(3,  (float)(14 - a), 13);
        gl.glVertex3f(3,(float)(14-a),13);
        gl.glVertex3f(3,(float)(14-a),14);
        gl.glVertex3f(3,(float)(14-a),14);
        gl.glVertex3f(3,(float)(12.5-a),12.5f);
        gl.glVertex3f(3,(float)(12.5-a),12.5f);
        gl.glVertex3f(3,(float)(14-a),11);
        gl.glVertex3f(3,(float)(14-a),11);
        gl.glVertex3f(3,(float)(14-a),12);
        gl.glVertex3f(3,(float)(14-a),12);
        gl.glVertex3f(3,14,12);
        gl.glVertex3f(3,14,12);
        gl.glVertex3f(3,14,13);
        gl.glEnd();
        gl.glPopMatrix();
    }


    private void drawAxis(GL2 gl, float echelle) {
        gl.glPushMatrix();
        gl.glScalef(echelle, echelle, echelle);
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3ub((byte)255, (byte)0, (byte)0);
        gl.glVertex3i(0, 0, 0);
        gl.glVertex3i(1, 0, 0);
        gl.glColor3ub((byte)0, (byte)255, (byte)0);
        gl.glVertex2i(0, 0);
        gl.glVertex2i(0, 1);
        gl.glColor3ub((byte)0, (byte)0, (byte)255);
        gl.glVertex2i(0, 0);
        gl.glVertex3i(0, 0, 1);
        gl.glEnd();
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(70, width / height, 1, 1000);
    }

    private void drawSea(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(0.5f, 0.5f, 1f);
        gl.glVertex3f(-100000, 100000, 0);
        gl.glVertex3f(-100000, -100000, 0);
        gl.glVertex3f(100000, -100000, 0);
        gl.glVertex3f(100000, 100000, 0);
        gl.glEnd();
    }

    private void drawBuoys(GL2 gl) {
        for (int i = 0; i < 3; i++) {

            gl.glTranslatef(10.0f, 0.0f, 0.0f);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            GLU glu = new GLU();
            GLUquadric q = glu.gluNewQuadric();
            glu.gluQuadricDrawStyle(q, GLU.GLU_FILL);
            glu.gluSphere(q, 1.0, 10, 10);
            glu.gluDeleteQuadric(q);
        }
    }

    private void drawBoat(GL2 gl) {
        gl.glRotatef((float)(this.sailboat.getPhi()*180.0/Math.PI), 0.0f, 0.0f,1.0f);
        gl.glRotatef((float)(this.sailboat.getTheta()*180.0/Math.PI), 1.0f, 0.f, 0.0f);
        gl.glPushMatrix();

        {
            gl.glBegin(GL2.GL_QUADS);   //plancher
            gl.glVertex3f(-1,-1.5f,0.1f);        gl.glVertex3f(3,-1.5f, 0.1f);
            gl.glVertex3f(3,1.5f,0.1f);          gl.glVertex3f(-1,1.5f,0.1f);
            gl.glVertex3f(4.5f,-0.75f, 0.1f);     gl.glVertex3f(5.06f,-0.375f, 0.1f);
            gl.glVertex3f(5.25f,0, 0.1f);       gl.glVertex3f(5.06f,0.375f, 0.1f);
            gl.glVertex3f(4.5f,0.75f, 0.1f);
            gl.glEnd();
            // ---------------------------- dessus avant ----------------------------------------------------------------------
            gl.glTranslatef(0.01f,0,0.01f);
            gl.glBegin(GL2.GL_POLYGON);  //dessus avant
            gl.glColor3f(0.8f,0.2f,0.2f);            gl.glVertex3f(3,-2,1.0f);
            gl.glVertex3f(3.1f,-1.9f,1.01f);         gl.glVertex3f(3.75f,-1.6f, 1.01f);
            gl.glVertex3f(5.0f,-1,1.01f);           gl.glVertex3f(5.75f,-0.5f,1.01f);
            gl.glVertex3f(6.0f, 0, 1.01f);          gl.glVertex3f(5.75f,0.5f,1.01f);
            gl.glVertex3f(5.0f,1,1.01f);            gl.glVertex3f(3.75f,1.6f,1.01f);
            gl.glVertex3f(3.1f,1.9f,1.01f);          gl.glVertex3f(3,2,1.01f);
            gl.glEnd();

            //--------------------------- faces lat�rales ---------------------------------------------------------------------
            gl.glTranslatef(-0.01f,0,0);
            gl.glBegin(GL2.GL_POLYGON); //faces lat�rales droite
            gl.glColor3f(0,1,0);
            gl.glVertex3f(-1,-1.5f,0);       gl.glVertex3f(-1, -1.9f, 0.5f);
            gl.glVertex3f(-1,-2,1.0f);       gl.glVertex3f(3,-2,1.0f);
            gl.glVertex3f(3, -1.9f, 0.5f);    gl.glVertex3f(3,-1.5f,0);
            gl.glVertex3f(-1,-1.5f,0);
            gl.glEnd();
            gl.glBegin(GL2.GL_POLYGON); //faces lat�rales gauche
            gl.glColor3f(0,0,1);
            gl.glVertex3f(-1,1.5f,0);        gl.glVertex3f(-1, 1.9f, 0.5f);
            gl.glVertex3f(-1,2,1.0f);        gl.glVertex3f(3,2,1.0f);
            gl.glVertex3f(3, 1.9f, 0.5f);     gl.glVertex3f(3,1.5f,0);
            gl.glVertex3f(-1,1.5f,0);
            gl.glEnd();
            //--------------------------- banc ----------------------------------------------------------------------------------
            gl.glTranslatef(0,-0.01f,0);
            gl.glBegin(GL2.GL_QUADS);              //banc
            gl.glColor3f(1,1,1);
            gl.glVertex3f(-1,2,1.0f);         gl.glVertex3f(-1, 1.3f, 1.0f);
            gl.glVertex3f(3, 1.3f, 1.0f);      gl.glVertex3f(3,2,1.0f);
            gl.glVertex3f(-1, 1.3f, 1.0f);     gl.glVertex3f(-1, 1.1f, (float)(4*1.0/5));
            gl.glVertex3f(3, 1.1f, (float)(4*1.0/5));  gl.glVertex3f(3, 1.3f, 1.0f);
            gl.glVertex3f(-1, 1.1f, (float)(4*1.0/5)); gl.glVertex3f(-1, 0.9f, (float)(3*1.0/5));
            gl.glVertex3f(3, 0.9f, (float)(3*1.0/5));  gl.glVertex3f(3, 1.1f, (float)(4*1.0/5));
            gl.glVertex3f(-1, 0.9f, (float)(3*1.0/5)); gl.glVertex3f(-1,0.8f,0);
            gl.glVertex3f(3,0.8f,0);          gl.glVertex3f(3, 0.9f, (float)(3*1.0/5));
            gl.glEnd();
            gl.glTranslatef(0,0.01f,0);

            // -------------------------------- autre banc ----------------------------------------------------------------
            gl.glBegin(GL2.GL_QUADS);              //banc
            gl.glColor3f(1,1,1);
            gl.glVertex3f(-1,-2,1.0f);        gl.glVertex3f(-1, -1.3f, 1.0f);
            gl.glVertex3f(3, -1.3f, 1.0f);     gl.glVertex3f(3,-2,1.0f);
            gl.glVertex3f(-1, -1.3f, 1.0f);    gl.glVertex3f(-1, -1.1f, (float)(4*1.0/5));
            gl.glVertex3f(3, -1.1f, (float)(4*1.0/5)); gl.glVertex3f(3, -1.3f, 1.0f);
            gl.glVertex3f(-1, -1.1f, (float)(4*1.0/5));gl.glVertex3f(-1, -0.9f, (float)(3*1.0/5));
            gl.glVertex3f(3, -0.9f, (float)(3*1.0/5)); gl.glVertex3f(3, -1.1f, (float)(4*1.0/5));
            gl.glVertex3f(-1, -0.9f, (float)(3*1.0/5));gl.glVertex3f(-1,-0.8f,0);
            gl.glVertex3f(3,-0.8f,0);         gl.glVertex3f(3, -0.9f, (float)(3*1.0/5));
            gl.glEnd();
            gl.glTranslatef(0,-0.01f,0);
            // ---------------------- face avant mat --------------------------------
            gl.glBegin(GL2.GL_POLYGON); //face "avant mat"
            gl.glColor3f(0,0,1);
            gl.glNormal3f(-1,0,0);
            gl.glVertex3f(3+0,-1.5f+0.4f,0);
            gl.glVertex3f(3+0, -1.9f+0.4f, 0.5f);    gl.glVertex3f(3+0,-2+0.4f,1.0f);
            gl.glVertex3f(3+0,2-0.4f,1.0f);         gl.glVertex3f(3+0, 1.9f-0.4f, 0.5f);
            gl.glVertex3f(3+0,1.5f-0.4f,0);         gl.glVertex3f(3+0,-1.5f+0.4f,0);
            gl.glEnd();
            // ------------------------ face arri�re -----------------------------------------------
            gl.glTranslatef(-0.01f,0,0);
            gl.glBegin(GL2.GL_POLYGON); //face arri�re
            gl.glColor3f(0.5f,1,0);
            gl.glNormal3f(-1,0,0);
            gl.glVertex3f(-1,-1.5f,0);           gl.glVertex3f(-1, -1.9f, 0.5f);
            gl.glVertex3f(-1,-2,1.0f);           gl.glVertex3f(-1,2,1.0f);
            gl.glVertex3f(-1, 1.9f, 0.5f);        gl.glVertex3f(-1,1.5f,0);
            gl.glVertex3f(-1,-1.5f,0);
            gl.glEnd();
            gl.glTranslatef(0.01f,-0.01f,0);
            // ----------------------------- face avant ------------------------------------------
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3f(3,-2,1.0f);           gl.glVertex3f(3.1f,-1.7f,1.0f);
            gl.glVertex3f(3.75f,-1.5f, 1.0f);     gl.glVertex3f(5.0f,-1,1.0f);
            gl.glVertex3f(5.75f,-0.5f,1.0f);      gl.glVertex3f(6.0f, 0, 1.0f);
            gl.glVertex3f(5.25f,0, 0);          gl.glVertex3f(5.06f,-0.375f, 0);
            gl.glVertex3f(4.5f,-0.75f, 0);       gl.glVertex3f(3,-1.5f,0);
            gl.glVertex3f(3, -1.9f, 0.5f);       gl.glVertex3f(3,-2,1.0f);
            gl.glVertex3f(3,2,1.0f);            gl.glVertex3f(3.1f,1.7f,1.0f);
            gl.glVertex3f(3.75f,1.5f, 1.0f);      gl.glVertex3f(5.0f,1,1.0f);
            gl.glVertex3f(5.75f,0.5f,1.0f);       gl.glVertex3f(6.0f, 0, 1.0f);
            gl.glVertex3f(5.25f,0, 0);          gl.glVertex3f(5.06f,0.375f, 0);
            gl.glVertex3f(4.5f,0.75f, 0);        gl.glVertex3f(3,1.5f,0);
            gl.glVertex3f(3, 1.9f, 0.5f);        gl.glVertex3f(3,2,1.0f);
            gl.glEnd();
        }
        gl.glPopMatrix();

        gl.glPushMatrix();

        gl.glTranslatef(-1,0,0);
        gl.glRotatef((float)(this.sailboat.getDeltag()*180.0/Math.PI),0,0,1);

        // ***************************************
        //       RUDDER
        // ***************************************
        {
            gl.glColor3f(0,0,1);
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3f(0,-0.05f,1);       gl.glVertex3f(0,-0.05f,0);      gl.glVertex3f(-0.6f,-0.05f,0);
            gl.glVertex3f(-0.3f,-0.05f,1);    gl.glVertex3f(0,-0.05f,1);      gl.glVertex3f(0,0.05f,1);
            gl.glVertex3f(0,0.05f,0);       gl.glVertex3f(-0.6f,0.05f,0);    gl.glVertex3f(-0.3f,0.05f,1);
            gl.glVertex3f(0,0.05f,1);
            gl.glVertex3f(0,-0.05f,1);       gl.glVertex3f(0,0.05f,1);       gl.glVertex3f(0,-0.05f,0);
            gl.glVertex3f(0,0.05f,0);        gl.glVertex3f(-0.6f,-0.05f,0);   gl.glVertex3f(-0.6f,0.05f,0);
            gl.glVertex3f(-0.3f,-0.05f,1);    gl.glVertex3f(-0.3f,0.05f,1);    gl.glVertex3f(0,-0.05f,1);
            gl.glVertex3f(0,0.05f,1);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glTranslatef(-0.6f,0,1);
            gl.glRotatef(90,0,1,0);

            GLU glu = new GLU();
            GLUquadric q1 = glu.gluNewQuadric();
            glu.gluQuadricDrawStyle(q1, GLU.GLU_FILL);
            glu.gluQuadricOrientation(q1, GLU.GLU_OUTSIDE);
            glu.gluCylinder(q1, 0.05, 0.05, 3, 10, 10);
            glu.gluDeleteQuadric(q1);

        }

        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(5,0,0);
        gl.glRotatef((float)(this.sailboat.getDeltav()*180.0/Math.PI),0,0,1);

        // ***************************************
        //       SAIL
        // ***************************************

        {
            gl.glColor3f(0.9f,0.9f,0.9f);
            GLU glu = new GLU();
            GLUquadric q1 = glu.gluNewQuadric();  //m�t
            glu.gluQuadricOrientation(q1, GLU.GLU_OUTSIDE);
            glu.gluQuadricTexture(q1, true);
            glu.gluCylinder(q1,0.08,0.08,14, 10,10);
            glu.gluDeleteQuadric(q1);

            gl.glDisable(GL.GL_TEXTURE_2D);

            gl.glColor3f(1,1,0);
            float b=(float)-Math.atan(this.sailboat.getTheSail().getfV() / 500.);  // courbure de la voile
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glBegin(GL.GL_TRIANGLE_FAN);
            gl.glVertex3f(0,0,2);     gl.glVertex3f(0,0,12);    gl.glVertex3f(-1,b*1.5f,10);
            gl.glVertex3f(-2,b*2,8);  gl.glVertex3f(-3,b*2,6);  gl.glVertex3f(-4,b*1.5f,4);
            gl.glVertex3f(-5,0,2);
            gl.glEnd();

            gl.glDisable(GL.GL_BLEND);
            gl.glDisable(GL.GL_TEXTURE_2D);
            gl.glTranslatef(0,0,2);
            gl.glRotatef(-90,0,1,0);
            gl.glEnable(GL.GL_TEXTURE_2D);         //bome
            gl.glBindTexture(GL.GL_TEXTURE_2D,5);

            q1=glu.gluNewQuadric();
            glu.gluQuadricOrientation(q1, GLU.GLU_OUTSIDE);
            glu.gluQuadricTexture(q1, true);
            glu.gluCylinder(q1,0.1,0.1,5.5, 10,10);
            glu.gluDeleteQuadric(q1);
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
        gl.glPopMatrix();
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_I:
                this.zoom *= 1.1;
                break;
            case KeyEvent.VK_O:
                this.zoom /= 1.1;
                break;
            case KeyEvent.VK_UP:
                this.deltavmax = Math.max(-1.57, this.sailboat.getDeltavmax()-0.05);
                break;
            case KeyEvent.VK_DOWN:
                this.deltavmax = Math.min(1.57, this.sailboat.getDeltavmax()+0.05);
                break;
            case KeyEvent.VK_RIGHT:
                this.deltag = Math.min(this.sailboat.getDeltag()+0.05, 0.5);
                break;
            case KeyEvent.VK_LEFT:
                this.deltag = Math.max(this.sailboat.getDeltag()-0.05, -0.5);
                break;
            case KeyEvent.VK_S:
                this.isStarted = !this.isStarted;
                break;
            case KeyEvent.VK_B:
                this.yCam--;
                break;
            case KeyEvent.VK_F:
                this.yCam++;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.lastPosX = e.getX();
        this.lastPosY = e.getY();
        this.sailboat.setX(0.0);
        this.sailboat.setY(0.0);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - this.lastPosX;
        int dy = e.getY() - this.lastPosY;
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.angleX += dy;
            if (this.angleX <= 0) {
                this.angleX = 0;
            } else if (this.angleX >= 90) {
                this.angleX = 90;
            }
            this.angleZ += dx;
        }
        this.lastPosX = e.getX();
        this.lastPosY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
