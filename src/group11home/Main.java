﻿
package group11home;
import java.awt.Color;
import java.awt.geom.*;
import robocode.*;
import java.util.*;

/*
 * ソースコードについて
 * 文字コード... UTF-8
 * 変数を新しく作るときは
 * int direction;	//敵の向き
 * みたいにコメントを付けておいてください
 */

/**
 * ロボット本体のソースコード
 * レーダーからわかる敵の情報はhttp://www.solar-system.tuis.ac.jp/Java/robocode_api/を参照
 * 	→ScannedRobotEventクラスに保存されるa
 */
public class Main extends TeamRobot
{
	public final String RobotName = "TeamFujiwara";

	// 敵ロボットの名前とか
	public static final String Enemy1Name = "Leader";
	public static final String Enemy2Name = "Sub1";
	public static final String Enemy3Name = "Sub2";

	//自分自身の座標
	public double x;
	public double y;

	// それぞれ的とWallsの数
	private int NumOfEnemiesAlive = 3;
	private int NumOfWallsAlive = 3;

	final double PI = Math.PI;	//円周率

	Hashtable<String, Enemy> targets;	//Enemyのハッシュテーブル
	Enemy target;	//ターゲットにする的
	int direction = 1;

	double midpointstrength = 0;
	int midpointcount = 0;
	int EnemyCounter = 3;	//敵の生きている数
	int WallsCounter = 3;	//Wallsの生きている数
	/**
	 *  run: ロボットの全体動作をここに記入(担当: 広田)
	 */
	public void run() {

		// まずロボットの初期化
		initializeRobot();

		//色を設定
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		targets = new Hashtable();
		target = new Enemy();
		target.distance = 100000;

		//レーダーや砲台を機体と独立させる
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		turnRadarRightRadians(2*PI);


		// ロボットのメインループ
		while(true) {

			antiGravMove();
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
		//NumOfEnemiesAlive = countNumbOfEnemiesAilve();
		//NumOfWallsAlive = countNumOfWallsAlive();
		x = getX();
		y = getY();//ロボットの初期位置を取得

	}

	/**
	 * スキャンした敵が味方か相手かWallsかを判別する(担当 ,山下)
	 * @return 1 味方, 2 相手, 3 Walls
	 */
	private int identifyEnemy(ScannedRobotEvent e){
		if(e.getName() == "Walls (1)" || e.getName() == "Walls (2)" || e.getName() == "Walls (3)"){
			return 3;
		}
		else if(e.getName() == "" || e.getName() == "" || e.getName() == ""){	//味方の名前を入れる
			return 1;
		}
		else return 2;
	}

	/**
	 * 敵の動きが直線運動か円運動か停止しているかを判別する(担当 藤原)
	 * とりあえず放置
	 * @return 1:直線運動, 2: 円運動, 3: 停止
	 */
	public static int analyzeMoveType(ScannedRobotEvent e){



		return 0;
	}

	/**
	 * 生きている敵の数をカウントする(担当: 上田、山下)
	 * @return 生きている敵の数
	 */
	public int countNumbOfEnemiesAlive(RobotDeathEvent e) {
		if(e.getName() == "" || e.getName() == "" || e.getName() == ""){	//敵の名前を入れる
			EnemyCounter--;
		}
		return EnemyCounter;
	}

	/**
	 * 生きているWallsの数をカウントする(担当: 上田、山下)
	 * @return 生きているWallsの数
	 */
	public int countNumOfWallsAlive(RobotDeathEvent e) {
		if(e.getName() == "Walls (1)" || e.getName() == "Walls (2)" || e.getName() == "Walls (3)"){
			WallsCounter--;
		}
		return WallsCounter;
	}

	/**
	 * ロボットと自分との距離を測る(担当: 上田、山下)
	 * @return 距離
	 */
	private int measureDistanceOfEnemy(ScannedRobotEvent e){
		return (int)e.getDistance();
	}

	/**
	 * onHitByBullet: 弾が自分にあたったときの動作を書く
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		back(10);
	}

	/**
	 * onHitWall: 壁にぶつかったときの動作
	 */
	public void onHitWall(HitWallEvent e) {
		back(20);
	}

	/*反重力移動(要拡張) 参考:https://www.ibm.com/developerworks/jp/java/library/j-antigrav/*/
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
	/**
	 * onScannedRobot: 敵を察知したときの動作
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
		//敵ロボットが居る角度の計算
		double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*PI);
		//スキャンした敵ロボットの情報を保存
		en.name = e.getName();
		double h = normaliseBearing(e.getHeadingRadians() - en.heading);
		h = h/(getTime() - en.ctime);
		en.changehead = h;
		en.x = getX()+Math.sin(absbearing_rad)*e.getDistance(); //敵ロボットのx座標
		en.y = getY()+Math.cos(absbearing_rad)*e.getDistance(); //y座標
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

/**
 * 敵に関する情報をここに入れる
 *
 */
class Enemy {
	/*
	 * ok, we should really be using accessors and mutators here,
	 * (i.e getName() and setName()) but life's too short.
	 * ↑日本語訳(広田)
	 * getNameとかsetNameみたいなメソッドを作ったほうがいいんかもしれんけど、時間もったいないしやってない
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

/**
 * 重力点(この点に引きこまれて行ったり離れていったりという動作に用いる)
 *	(座標はint型で表すからxとかyもdoubleじゃなくてintのほうがよくない？) 広田
 *      (API見たけど座標や距離は全てdouble型で返ってくるので）藤原
 */
class GravPoint {
	public double x,y,power;
	public GravPoint(double pX,double pY,double pPower) {
		x = pX;
		y = pY;
		power = pPower;
	}
}
