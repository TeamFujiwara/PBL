package group11home;
import robocode.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.*;


/**
 * ���{�b�g�{�̂̃\�[�X�R�[�h
 * ���[�_�[����킩��G�̏���http://www.solar-system.tuis.ac.jp/Java/robocode_api/���Q��
 * 	��ScannedRobotEvent�N���X�ɕۑ������
 */
public class Main extends TeamRobot
{
	public final String RobotName = "TeamFujiwara";

	// �G���{�b�g�̖��O
	public static final String Enemy1Name = "Leader";
	public static final String Enemy2Name = "Sub1";
	public static final String Enemy3Name = "Sub2";

	// ���ꂼ��I��Walls�̐�
	private int NumOfEnemiesAlive = 3;
	private int NumOfWallsAlive = 3;

	final double PI = Math.PI;
	Hashtable targets;
	Enemy target;
	int direction = 1;
	double midpointstrength = 0;
	int midpointcount = 0;
	/**
	 *  run: ���{�b�g�̑S�̓���������ɋL��(�S��: �L�c)
	 */
	public void run() {

		// �܂����{�b�g�̏�����
		initializeRobot();

		//�F��ݒ�
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		targets = new Hashtable();
		target = new Enemy();
		target.distance = 100000;

		//���[�_�[��C����@�̂ƓƗ�������
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		turnRadarRightRadians(2*PI);
			

		// ���{�b�g�̃��C�����[�v
		while(true) {
			
			antiGravMove();
			//���[�_�[��]�̗\��
			setTurnRadarLeftRadians(2*PI);			

			//�\�񂳂ꂽ�����̎��s
			execute();
			
		}
	}

	/**
	 * ���{�b�g�̏�������������(�S��:a ���c)
	 */
	private void initializeRobot() {
		// ��... �G�̐���Walls�̐������ꂼ��N���X�̕ϐ��ɓ����
		NumOfEnemiesAlive = countNumbOfEnemiesAilve();
		NumOfWallsAlive = countNumOfWallsAlive();

	}

	/**
	 * �X�L���������G�����������肩Walls���𔻕ʂ���(�S�� ��c,�R��)
	 * @return 1 ����, 2 ����, 3 Walls
	 */
	private int identifyEnemy(ScannedRobotEvent e){
		if(e.getName() == "Walls (1)" && e.getName() == "Walls (2)" && e.getName() == "Walls (3)"){
			return 3;
		}
		//else if(get)
		return 0;
	}

	/**
	 * �G�̓����������^�����~�^������~���Ă��邩�𔻕ʂ���(�S�� ����)
	 * @return 1:�����^��, 2: �~�^��, 3: ��~
	 */
	public static int analyzeMoveType(ScannedRobotEvent e){



		return 0;
	}

	/**
	 * �����Ă���G�̐����J�E���g����(�S��: ��c�A�R��)
	 * @return �����Ă���G�̐�
	 */
	public int countNumbOfEnemiesAilve() {
		return 0;
	}

	/**
	 * �����Ă���Walls�̐����J�E���g����(�S��: ��c�A�R��)
	 * @return �����Ă���Walls�̐�
	 */
	public int countNumOfWallsAlive() {
		return 0;
	}

	/**
	 * ���{�b�g�Ǝ����Ƃ̋����𑪂�(�S��: ��c�A�R��)
	 * @return ����
	 */
	private int measureDistanceOfEnemy(ScannedRobotEvent e){
		return 0;
	}

	/**
	 * onHitByBullet: �e�������ɂ��������Ƃ��̓��������
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}

	/**
	 * onHitWall: �ǂɂԂ������Ƃ��̓�����w��(���Ƃ�)
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}

	/*���d�͈ړ�(�v�g��) �Q�l:https://www.ibm.com/developerworks/jp/java/library/j-antigrav/*/
	void antiGravMove() {
   		double xforce = 0;
		double yforce = 0;
		double force;
		double ang;
		GravPoint p;
		Enemy en;
    		Enumeration e = targets.elements();
		//cycle through all the enemies.  If they are alive, they are repulsive.  Calculate the force on us
		while (e.hasMoreElements()) {
    			en = (Enemy)e.nextElement();
			if (en.live) {
				p = new GravPoint(en.x,en.y, -1000);
				force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
				//Find the bearing from the point to us
				ang = normaliseBearing(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
				//Add the components of this force to the total force in their respective directions
				xforce += Math.sin(ang) * force;
		        		yforce += Math.cos(ang) * force;
			}
	    }
	    
		/**The next section adds a middle point with a random (positive or negative) strength.
		The strength changes every 5 turns, and goes between -1000 and 1000.  This gives a better
		overall movement.**/
		midpointcount++;
		if (midpointcount > 5) {
			midpointcount = 0;
			midpointstrength = (Math.random() * 2000) - 1000;
		}
		p = new GravPoint(getBattleFieldWidth()/2, getBattleFieldHeight()/2, midpointstrength);
		force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),1.5);
	  	ang = normaliseBearing(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    	xforce += Math.sin(ang) * force;
	    	yforce += Math.cos(ang) * force;
	   
	    	/**The following four lines add wall avoidance.  They will only affect us if the bot is close 
	    to the walls due to the force from the walls decreasing at a power 3.**/
	    	xforce += 5000/Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 3);
	    	xforce -= 5000/Math.pow(getRange(getX(), getY(), 0, getY()), 3);
	    	yforce += 5000/Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 3);
	    	yforce -= 5000/Math.pow(getRange(getX(), getY(), getX(), 0), 3);
	    
	    	//Move in the direction of our resolved force.
	    	goTo(getX()-xforce,getY()-yforce);
	}


	/*���W(x,y)�Ɍ������悤�ɍs����\�񂷂�*/
	void goTo(double x, double y) {
		double dist = 20; 
		double angle = Math.toDegrees(absbearing(getX(),getY(),x,y));
		double r = turnTo(angle);
		setAhead(dist * r);
	}	
	
	int turnTo(double angle) {
		double ang;
		int dir;
		ang = normaliseBearing(getHeading() - angle);
		if (ang > 90) {
			ang -= 180;
			dir = -1;
		}
		else if (ang < -90) {
			ang += 180;
			dir = -1;
		}
		else {
			dir = 1;
		}
		setTurnLeft(ang);
		return dir;
	}
	public double absbearing( double x1,double y1, double x2,double y2 ){
		double xo = x2-x1;
		double yo = y2-y1;
		double h = getRange( x1,y1, x2,y2 );
		if( xo > 0 && yo > 0 )
		{
			return Math.asin( xo / h );
		}
		if( xo > 0 && yo < 0 )
		{
			return Math.PI - Math.asin( xo / h );
		}
		if( xo < 0 && yo < 0 )
		{
			return Math.PI + Math.asin( -xo / h );
		}
		if( xo < 0 && yo > 0 )
		{
			return 2.0*Math.PI - Math.asin( -xo / h );
		}
		return 0;
	}
	double normaliseBearing(double ang) {
		if (ang > PI)
			ang -= 2*PI;
		if (ang < -PI)
			ang += 2*PI;
		return ang;
	}
	
	//if a heading is not within the 0 to 2pi range, alters it to provide the shortest angle
	double normaliseHeading(double ang) {
		if (ang > 2*PI)
			ang -= 2*PI;
		if (ang < 0)
			ang += 2*PI;
		return ang;
	}	
	public double getRange( double x1,double y1, double x2,double y2 )
	{
		double xo = x2-x1;
		double yo = y2-y1;
		double h = Math.sqrt( xo*xo + yo*yo );
		return h;	
	}
	/**
	 * onScannedRobot: �G���@�m�����Ƃ��̓���
	 */
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		Enemy en;
		if (targets.containsKey(e.getName())) {
			en = (Enemy)targets.get(e.getName());
		} else {
			en = new Enemy();
			targets.put(e.getName(),en);
		}
		//�G���{�b�g������p�x�̌v�Z
		double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*PI);
		//�X�L���������G���{�b�g�̏���ۑ�
		en.name = e.getName();
		double h = normaliseBearing(e.getHeadingRadians() - en.heading);
		h = h/(getTime() - en.ctime);
		en.changehead = h;
		en.x = getX()+Math.sin(absbearing_rad)*e.getDistance(); //�G���{�b�g��x���W
		en.y = getY()+Math.cos(absbearing_rad)*e.getDistance(); //y���W
		en.bearing = e.getBearingRadians();
		en.heading = e.getHeadingRadians();
		en.ctime = getTime();				//game time at which this scan was produced
		en.speed = e.getVelocity();
		en.distance = e.getDistance();	
		en.live = true;
		if ((en.distance < target.distance)||(target.live == false)) {
			target = en;
		}
	}
}

class Enemy {
	/*
	 * ok, we should really be using accessors and mutators here,
	 * (i.e getName() and setName()) but life's too short.
	 */
	String name;
	public double bearing,heading,speed,x,y,distance,changehead;
	public long ctime; 		//game time that the scan was produced
	public boolean live; 	//is the enemy alive?
	public Point2D.Double guessPosition(long when) {
		double diff = when - ctime;
		double newY = y + Math.cos(heading) * speed * diff;
		double newX = x + Math.sin(heading) * speed * diff;
		
		return new Point2D.Double(newX, newY);
	}
}

/**Holds the x, y, and strength info of a gravity point**/
class GravPoint {
    public double x,y,power;
    public GravPoint(double pX,double pY,double pPower) {
        x = pX;
        y = pY;
        power = pPower;
    }
}
