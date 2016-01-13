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
public class Sub2 extends Group11Robot{

	/**
	 *  run: ロボットの全体動作をここに記入
	 */
	public void run() {

		//色を設定
		//homeのメイン
		//setColors(Colored,Color.pink,Color.orange); // body,gun,radar
		//homeのサブ
		setColors(Color.blue,Color.green,Color.magenta);
		//awayのリーダー・サブ
		//setColors(Color.white,Color.white,Color.white);

		targets = new Hashtable();	//敵一覧
		target = new Enemy();	//ターゲットにする敵
		target.name = "null";
		target.distance = 100000;	//ターゲットとの距離をとりあえず初期化

		double firePower = 0.1; 

		//レーダーや砲台を機体と独立させる
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		//まずは全索敵
		turnRadarRightRadians(2*PI);

		// ロボットのメインループ
		while(true) {

			System.out.println("presentMode:" + presentMode);
			System.out.println("Team:" + TeamCounter);
			System.out.println("Walls:" + WallsCounter);
			System.out.println("Enemy:" + EnemyCounter);

			if(presentMode != 3){
				//targetに近づきながら射撃
				firePower=3;
				setTurnRadarLeft(360);
				antiGravMove(10000);
				doGunCircle(firePower);
				execute();
				if(hitPossibility)fire(firePower);
			}else{
				//レーダー回転の予約
				setTurnRadarLeftRadians(2*PI);
				// 反重力運動
				antiGravMove(-1000);
				firePower = doFirePower();
				doGunCircle(firePower);
				execute();
				if(hitPossibility)fire(firePower);
			}
		System.out.println("Target:" + target.name);//デバッグ用．ターゲットを出力する
		}
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
			targets.put(e.getName(),en);
		}

		//標的を決定．
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
}
