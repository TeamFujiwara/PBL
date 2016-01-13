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
 *
 * 11/27追記
 * 途中になってる部分は //TODO コメント
 * っていうコメントをつけていおいてください
 * 例 ↓
 */

//TODO: 別々の人が作ってる関数内で共有できる変数がいくつかあるので統合する
//TODO: 標的の共有をする
//TODO	射撃を実装

/**
 * ロボット本体のソースコード
 * レーダーからわかる敵の情報はhttp://www.solar-system.tuis.ac.jp/Java/robocode_api/を参照
 * 	→ScannedRobotEventクラスに保存される
 */
public class Group11Robot extends TeamRobot{

	public final double PI = Math.PI;	//円周率

	public	Hashtable<String, Enemy> targets;	//Enemyのハッシュテーブル
	public	Enemy target;	//ターゲットにする敵
	public	boolean hitPossibility; //targetの推定移動位置がフィールドの中かどうかを示す変数

	public	double midpointstrength = 0;
	public	int midpointcount = 0;
	public	int TeamCounter = 3;	//味方の生きている数
	public	int EnemyCounter = 3;	//敵の生きている数
	public	int WallsCounter = 3;	//Wallsの生きている数
	public	boolean leaderAlive = true; //リーダーが生きているかどうか

	/* 現在のモード
	 1...近くの敵を狙ってとにかく撃つ(敵が死んでない時)(最初はこれ)
	 2.. 敵が1~4体まで死んでる時
	 3.. それ以上敵が死んでいる時，味方が残り1機になった時，リーダーが死んだとき
	 */
	public int presentMode = 1;

	int whoAmI;	//Leaderなら1,sub1なら2,sub2なら3
        
	/**
	 *  run: 色がロボットごとに異なるので実装
	 */
	public void run() {
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event){
		int enemyID = identifyEnemy(event.getName());
		if (enemyID == 1)
			--TeamCounter;		
		else if(enemyID == 2)
			--EnemyCounter;
		else if(enemyID == 3)
			--WallsCounter;
		if(event.getName() == target.name){
			target.name = "null";
			targets.get(event.getName()).live = false;
		}
		modeChange();
		System.out.println("enemyID:" + enemyID);

	}
			
	
	/**
	 * スキャンした敵が味方か相手かWallsかを判別する
	 * @return 1 味方, 2 相手, 3 Walls
	 */
	public int identifyEnemy(String name){

		if(name.matches("group11.*")) return 1;
		
		else if(name.matches(".*Walls.*")==true) return 3;

		else return 2;

	}

	public double headingToBearing(double heading){
		if(heading <= 180)
			return heading;
		else
			return heading - 360;
	}

	public double bearingToHeading(double bearing){
		if(bearing >= 0)
			return bearing;
		else
			return bearing + 360;
	}

	/**
	 * onHitByBullet: 弾が自分にあたったときの動作を書く
	 */
	public void onHitByBullet(HitByBulletEvent e) {
	}

	/**
	 * onHitWall: 壁にぶつかったときの動作
	 */
	public void onHitWall(HitWallEvent e) {
		goTo(getBattleFieldWidth()/2, getBattleFieldHeight()/2);
	}

	/*反重力移動(要拡張)
	 * 参考:https://www.ibm.com/developerworks/jp/java/library/j-antigrav/*/
	void antiGravMove(double gravToTarget) {
		// xforceとyforceはそれぞれ敵の位置から計算して導出。forceが大きいほどその方向に押される
		double xforce = 0;
		double yforce = 0;
		double force;
		double ang;
		GravPoint p;
		Enemy en;
		Enumeration e = targets.elements();	//敵リスト
		//cycle through all the enemies.  If they are alive, they are repulsive.  Calculate the force on us
		//↑敵の位置を重力点にする(そこから避けるようにする)
		while (e.hasMoreElements()) {
			en = (Enemy)e.nextElement();
			if (en.live) {
				/*targetで示してある敵に対しては引数で指定した力場を発生させる*/
				if(en.name == target.name) p = new GravPoint(en.x,en.y, gravToTarget);
				else p = new GravPoint(en.x,en.y, -1000);

				force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
				//Find the bearing from the point to us
				//自分から見た敵の相対角度を計算
				ang = normaliseBearing(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x));
				//Add the components of this force to the total force in their respective directions
				xforce += Math.sin(ang) * force;
				yforce += Math.cos(ang) * force;
			}
		}

		/**The next section adds a middle point with a random (positive or negative) strength.
		The strength changes every 5 turns, and goes between -1000 and 1000.  This gives a better
		overall movement.**/

		//midpointって何?
		//壁を避けるために中央に引力を発生させてる．
		//midpointcountは，時間毎に引力の強さをちょっと変更するためのカウンタ．
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
	
		//あまり壁と平行に動かないように，力を加える	
		if(xforce > -50 && xforce < 50){
			if(xforce > 0) xforce += 50;
			else xforce -= 50;
		}
		if(yforce > -50 && yforce < 50){
			if(yforce > 0) yforce += 50;
			else yforce -= 50;
		}

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
		if( xo > 0 && yo > 0 )	return Math.asin( xo / h );
		if( xo > 0 && yo < 0 )	return Math.PI - Math.asin( xo / h );
		if( xo < 0 && yo < 0 )	return Math.PI + Math.asin( -xo / h );
		if( xo < 0 && yo > 0 )	return 2.0*Math.PI - Math.asin( -xo / h );
		return 0;
	}
	double normaliseBearing(double ang) {
		if (ang > PI)	ang -= 2*PI;
		if (ang < -PI)	ang += 2*PI;
		return ang;
	}

	//if a heading is not within the 0 to 2pi range, alters it to provide the shortest angle
	double normaliseHeading(double ang) {
		if (ang > 2*PI)	ang -= 2*PI;
		if (ang < 0)	ang += 2*PI;
		return ang;
	}

	public double getRange( double x1,double y1, double x2,double y2 ){
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
			// 敵かどうかチェック
			en.isEnemy = (identifyEnemy(e.getName()) == 2) ? true : false;
		} else {
			en = new Enemy();
			if(identifyEnemy(e.getName()) == 1) en.isTeamMate = true;
			targets.put(e.getName(),en);
		}

		if(whoAmI == 1){
		//標的を決定．
		if(identifyEnemy(e.getName()) !=1){
			if(en.isEnemy){
				if(target.name == "null"){
					target = en;
					try{
						broadcastMessage(target.name);
						System.out.println("targetchange:" + target.name);
					}catch (Exception error){
						System.out.println("メッセージ送信中にエラー");
					}
				}else if (targets.get(target.name).live == false){
					target = en;
					try{
						broadcastMessage(target.name);
					}catch (Exception error){
						System.out.println("メッセージ送信中にエラー");
					}
				}
			}else if(EnemyCounter <= 0){
				if(target.name == "null"){
					target = en;
				}else if (targets.get(target.name).live == false){
					target = en;
				}
			}
		}
		}else{
				if(identifyEnemy(e.getName()) !=1){
			if(en.isEnemy){
				if(target.name == "null"){
					target = en;
				}else if (targets.get(target.name).live == false){
					target = en;
				}
			}else if(EnemyCounter <= 0){
				if(target.name == "null"){
					target = en;
				}else if (targets.get(target.name).live == false){
					target = en;
				}
			}
		}
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
		if (presentMode!=2&&identifyEnemy(e.getName()) !=1&&(en.distance < target.distance)) {
			target = en;
		}
	}

	public void modeChange(){
		if(EnemyCounter + WallsCounter >= 6) presentMode = 1;
		else if(EnemyCounter + WallsCounter >= 3) presentMode = 2;
		else presentMode = 3;
		if(TeamCounter <=1) presentMode = 3;
	}
	
	//反復での時間計算によって弾丸到達時刻推定を改善する機能を搭載した射撃メソッド 参考:https://www.ibm.com/developerworks/jp/java/library/j-circular/
	void doGunLinear(double firePower) {
	    long time;
	    long nextTime;
	    Point2D.Double p;
	    p = new Point2D.Double(target.x, target.y);
	    for (int i = 0; i < 10; i++){
	        nextTime = (int)Math.round((getRange(getX(),getY(),p.x,p.y)/(20-(3*firePower))));
	        time = getTime() + nextTime;
	        p = target.guessPositionLinear(time);
	    }
	    /**Turn the gun to the correct angle**/
	    double gunOffset = getGunHeadingRadians() - 
	                  (Math.PI/2 - Math.atan2(p.y - getY(), p.x - getX()));
	    setTurnGunLeftRadians(normaliseBearing(gunOffset));
	}
	void doGunCircle(double firePower) {
	    long time;
	    long nextTime;
	    Point2D.Double p;
	    p = new Point2D.Double(target.x, target.y);
	    for (int i = 0; i < 10; i++){
	        nextTime = (int)Math.round((getRange(getX(),getY(),p.x,p.y)/(20-(3*firePower))));
	        time = getTime() + nextTime;
	        p = target.guessPositionCircle(time);
	    }
	    /**Turn the gun to the correct angle**/
	    double gunOffset = getGunHeadingRadians() - 
	                  (Math.PI/2 - Math.atan2(p.y - getY(), p.x - getX()));
	    setTurnGunLeftRadians(normaliseBearing(gunOffset));
		judgeGunFire(p);
	}

	// ここに味方を打ちそうなら打たないという条件も追加する
	void judgeGunFire(Point2D.Double p){

		Enumeration ens = targets.elements();
		while(ens.hasMoreElements()){
			Enemy en = (Enemy)ens.nextElement();
			if(!en.isTeamMate)
				break;
			else{
				
			}
		}
				

		
		if(p.x < 0 || p.y < 0 || p.x > getBattleFieldWidth() || p.y > getBattleFieldHeight()){
			hitPossibility = false;
			return;
		}

		hitPossibility = true;
		return;
	}
	double doFirePower(){
		double firePower;
		firePower = 400/target.distance;//selects a bullet power based on our distance away from the target
		if (firePower > 3) {
			return 3;
		}
		return firePower;
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
	 * ↑日本語訳()
	 * getNameとかsetNameみたいなメソッドを作ったほうがいいんかもしれんけど、時間もったいないしやってない
	 */
	String name;
	public double bearing,heading,speed,x,y,distance,changehead;
	public long ctime; 		//game time that the scan was produced
	public boolean live; 	//is the enemy alive?
	public boolean isEnemy = true;	//wallsならfalseにする
	public boolean isTeamMate = false; //チームメイトならtrueにする
	public Point2D.Double guessPositionLinear(long when) {
		double diff = when - ctime;
		double newY = y + Math.cos(heading) * speed * diff;
		double newX = x + Math.sin(heading) * speed * diff;

		return new Point2D.Double(newX, newY);
	}
	//円形予測 参考:https://www.ibm.com/developerworks/jp/java/library/j-circular/
	public Point2D.Double guessPositionCircle(long when) {
    	
	//time は相手をスキャンしたゲーム内時刻．whenはターゲットに弾が当たると予想される時刻．diffはその2つの間の時間．
    	
	double diff = when - ctime;
   		double newX, newY;
	//敵の車体の向きが変わっているようなら円形予測を使う

   		if (Math.abs(changehead) > 0.00001) {
   		    double radius = speed/changehead;
   		    double tothead = diff * changehead;
	        newY = y + (Math.sin(heading + tothead) * radius) - 
               		      (Math.sin(heading) * radius);
       		newX = x + (Math.cos(heading) * radius) - 
	                      (Math.cos(heading + tothead) * radius);
   		}
	//車体の向きがほぼ同じなら線形予測を使う

   		else {
       		newY = y + Math.cos(heading) * speed * diff;
       		newX = x + Math.sin(heading) * speed * diff;
   		}
   		return new Point2D.Double(newX, newY);
	}

	public double getBearing() {
		return this.bearing;
	}

}

/**
 * 重力点(この点に引きこまれて行ったり離れていったりという動作に用いる)
 *	(座標はint型で表すからxとかyもdoubleじゃなくてintのほうがよくない？) 
 *      (API見たけど座標や距離は全てdouble型で返ってくるので）
 */
class GravPoint {
	public double x,y,power;
	public GravPoint(double pX,double pY,double pPower) {
		x = pX;
		y = pY;
		power = pPower;
	}
}

