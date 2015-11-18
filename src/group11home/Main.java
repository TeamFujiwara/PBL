package group11home;
import robocode.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.*;


/**
 * ロボット本体のソースコード
 * レーダーからわかる敵の情報はhttp://www.solar-system.tuis.ac.jp/Java/robocode_api/を参照
 * 	→ScannedRobotEventクラスに保存される
 */
public class Main extends TeamRobot
{
	public final String RobotName = "TeamFujiwara";

	// 敵ロボットの名前
	public static final String Enemy1Name = "Leader";
	public static final String Enemy2Name = "Sub1";
	public static final String Enemy3Name = "Sub2";

	// それぞれ的とWallsの数
	private int NumOfEnemiesAlive = 3;
	private int NumOfWallsAlive = 3;

	//標的の情報
	private String target;
	private int LiveTarget; //ターゲットが設定されているかどうか 0:NO 1:YES
	private double InfoOfTarget[];//[0]:ターゲットのx座標,[1]:ターゲットのy座標:[2]:ターゲットの相対的な角度,[3]:ターゲットの向いている向き,[4]:ターゲットの速度
	private ArrayList<double[]>  InfoHistory;

	final double PI = Math.PI;
	/**
	 *  run: ロボットの全体動作をここに記入(担当: 広田)
	 */
	public void run() {

		// まずロボットの初期化
		initializeRobot();

		//色を設定
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		//レーダーや砲台を機体と独立させる
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		turnRadarRightRadians(2*PI);	

		// ロボットのメインループ
		while(true) {


			//レーダー回転の予約
			setTurnRadarLeftRadians(2*PI);
			
			//予約された動きの実行
			execute();
			
		}
	}

	/**
	 * ロボットの情報を初期化する(担当:a 松田)
	 */
	private void initializeRobot() {
		// 例... 敵の数とWallsの数をそれぞれクラスの変数に入れる
		NumOfEnemiesAlive = countNumbOfEnemiesAilve();
		NumOfWallsAlive = countNumOfWallsAlive();

		//標的の情報を入れるリスト作成(藤原が追加)
		InfoHistory = new ArrayList<double[]>();
		LiveTarget = 0;

	}

	/**
	 * スキャンした敵が味方か相手かWallsかを判別する(担当 上田,山下)
	 * @return 1 味方, 2 相手, 3 Walls
	 */
	private int identifyEnemy(ScannedRobotEvent e){
		if(e.getName() == "Walls (1)" && e.getName() == "Walls (2)" && e.getName() == "Walls (3)"){
			return 3;
		}
		//else if(get)
		return 0;
	}

	/**
	 * 敵の動きが直線運動か円運動か停止しているかを判別する(担当 藤原)
	 * @return 1:直線運動, 2: 円運動, 3: 停止
	 */
	public static int analyzeMoveType(ScannedRobotEvent e){



		return 0;
	}

	/**
	 * 生きている敵の数をカウントする(担当: 上田、山下)
	 * @return 生きている敵の数
	 */
	public int countNumbOfEnemiesAilve() {
		return 0;
	}

	/**
	 * 生きているWallsの数をカウントする(担当: 上田、山下)
	 * @return 生きているWallsの数
	 */
	public int countNumOfWallsAlive() {
		return 0;
	}

	/**
	 * ロボットと自分との距離を測る(担当: 上田、山下)
	 * @return 距離
	 */
	private int measureDistanceOfEnemy(ScannedRobotEvent e){
		return 0;
	}




	/**
	 * onScannedRobot: 敵を察知したときの動作
	 */
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		// ターゲットの設定
		if(LiveTarget == 0) {
			target = e.getName();
			LiveTarget = 1;
		}

		//ターゲットの情報を保存
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
	 * onHitByBullet: 弾が自分にあたったときの動作を書く
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}

	/**
	 * onHitWall: 壁にぶつかったときの動作を指定(あとで)
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}


	/*座標(x,y)に向かうように行動を予約する*/
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