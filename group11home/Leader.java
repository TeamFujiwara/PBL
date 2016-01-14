package group11home;
import java.awt.Color;
import java.awt.geom.*;
import robocode.*;
import java.util.*;

/*
 * ソースコードについて
 * 文字コード... UTF-8
 * 動作自体はGroup11Robot.javaに記載してください
 * LeaderとSub機で動作が違う箇所は、whoAmI(Leaderなら1,sub1は2,sub2は3)でif文使ってください
 */

public class Leader extends Group11Robot{

	
	public Leader() {
		super.whoAmI = 1;
		super.robotColor = Color.red;
		super.gunColor = Color.green;
		super.radarColor = Color.orange;
	}
	
}
