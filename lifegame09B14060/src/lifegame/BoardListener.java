/**
 * 盤面が更新された時に通知を受け取るクラスに実装する
 * @author wataru hirota
 */
package lifegame;

public interface BoardListener {

	public void updated(BoardModel m);
}
