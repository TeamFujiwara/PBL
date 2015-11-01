
package lifegame;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * 更新通知を送信する機能を付け加えたArrayDeque
 */
public class ArrayDequeWithListener<E> extends ArrayDeque<E> {
	ArrayList<DequeListener> stackListener = new ArrayList<>();

	/**
	 * スタックの更新を通知する
	 */
	public void fireUpdate(){
		for(DequeListener listener : this.stackListener){
			listener.dequeUpdated();
		}
	}

	/**
	 * listenerを追加する
	 * @param listener 追加するlistener
	 */
	public void addListener(DequeListener listener){
		stackListener.add(listener);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 2705292963342325275L;

}
