package group11home;
import robocode.*;
import java.awt.Color;


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



	/**
	 * run: ロボットの全体動作をここに記入(担当: 広田)
	 */
	public void run() {

		// まずロボットの初期化
		initializeRobot();

		//色を設定
		setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// ロボットのメインループ
		while(true) {
			// 下4行はサンプル
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);
		}
	}

	/**
	 * ロボットの情報を初期化する(担当: 松田)
	 */
	private void initializeRobot() {
		// 例... 敵の数とWallsの数をそれぞれクラスの変数に入れる
		NumOfEnemiesAlive = countNumbOfEnemiesAilve();
		NumOfWallsAlive = countNumOfWallsAlive();

	}

	/**
	 * スキャンした敵が味方か相手かWallsかを判別する(担当 上田,山下)
	 * @return 1 味方, 2 相手, 3 Walls
	 */
	private int identifyEnemy(ScannedRobotEvent e){
		if(getName(e) == Walls (1) && getName(e) == Walls (2) && getName(e) == Walls (3)){
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
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		analyzeMoveType(e);
		fire(1);
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
}
