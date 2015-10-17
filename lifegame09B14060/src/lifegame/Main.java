/**
 * メイン処理を担当するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

public class Main {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		// 正常にコンストラクタ、changeCellsState、printForDebugができてることを確認
		BoardModel m1 = new BoardModel(4, 4);
		m1.changeCellsState(3, 2);
		m1.printForDebug();
	}

}
