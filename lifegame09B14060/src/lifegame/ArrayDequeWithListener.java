/**
 * 更新通知を送信する機能を付け加えたArrayDeque
 */
package lifegame;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class ArrayDequeWithListener<E> extends ArrayDeque<E> {
	ArrayList<StackListener> stackListener = new ArrayList<>();

	/**
	 * スタックの更新を通知する
	 */
	public void fireUpdate(){
		for(StackListener listener : this.stackListener){
			listener.stackUpdated();
		}
	}

	/**
	 * listenerを追加する
	 * @param listener 追加するlistener
	 */
	public void addListener(StackListener listener){
		stackListener.add(listener);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 2705292963342325275L;

}
