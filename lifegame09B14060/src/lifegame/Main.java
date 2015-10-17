/**
 * メイン処理を担当するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

public class Main {

	public static void main(String[] args) {
		BoardModel tested = new BoardModel(10, 10);

		tested.changeCellsState(2, 2);
		tested.changeCellsState(3, 3);
		tested.changeCellsState(4, 1);
		tested.changeCellsState(4, 2);
		tested.changeCellsState(4, 3);

		tested.next();
		tested.undo();
		tested.printForDebug();
	}

}
