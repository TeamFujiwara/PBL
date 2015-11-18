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

	//�W�I�̏��
	private String target;
	private int LiveTarget; //�^�[�Q�b�g���ݒ肳��Ă��邩�ǂ��� 0:NO 1:YES
	private double InfoOfTarget[];//[0]:�^�[�Q�b�g��x���W,[1]:�^�[�Q�b�g��y���W:[2]:�^�[�Q�b�g�̑��ΓI�Ȋp�x,[3]:�^�[�Q�b�g�̌����Ă������,[4]:�^�[�Q�b�g�̑��x
	private ArrayList<double[]>  InfoHistory;

	final double PI = Math.PI;
	/**
	 *  run: ���{�b�g�̑S�̓���������ɋL��(�S��: �L�c)
	 */
	public void run() {

		// �܂����{�b�g�̏�����
		initializeRobot();

		//�F��ݒ�
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		//���[�_�[��C����@�̂ƓƗ�������
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		turnRadarRightRadians(2*PI);	

		// ���{�b�g�̃��C�����[�v
		while(true) {


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

		//�W�I�̏������郊�X�g�쐬(�������ǉ�)
		InfoHistory = new ArrayList<double[]>();
		LiveTarget = 0;

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
	 * onScannedRobot: �G���@�m�����Ƃ��̓���
	 */
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		// �^�[�Q�b�g�̐ݒ�
		if(LiveTarget == 0) {
			target = e.getName();
			LiveTarget = 1;
		}

		//�^�[�Q�b�g�̏���ۑ�
		if (e.getName()==target){
			
			double bearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*PI);
			
			InfoOfTarget = new double[5];
			InfoOfTarget[0] = getX()+Math.sin(bearing_rad)*e.getDistance();
			InfoOfTarget[1] = getY()+Math.cos(bearing_rad)*e.getDistance();
			InfoOfTarget[2] = e.getBearing();
			InfoOfTarget[3] = e.getHeading();
			InfoOfTarget[4] = e.getVelocity();
			
			InfoHistory.add(InfoOfTarget);
		
			
		
		}

		
		
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
}