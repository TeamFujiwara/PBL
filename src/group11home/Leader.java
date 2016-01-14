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
public class Leader extends Group11Robot{
	/**
	 *  run: ロボットの全体動作をここに記入
	 */
	@Override
	public void run() {

		//色を設定
		//homeのメイン
		setColors(Color.red,Color.pink,Color.orange); // body,gun,radar
		//homeのサブ
		//setColors(Color.blue,Color.green,Color.magenta);
		//awayのリーダー・サブ
		//setColors(Color.white,Color.white,Color.white);

		whoAmI = 1;

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
				antiGravMove(1000);
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


}
