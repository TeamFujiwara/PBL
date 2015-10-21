package lifegame;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class ArrayDequeWithListener<E> extends ArrayDeque<E> {
	ArrayList<StackListener> stackListener = new ArrayList<>();

	public void fireUpdate(){
		for(StackListener listener : this.stackListener){
			listener.stackUpdated();
		}
	}

	public void addListener(StackListener listener){
		stackListener.add(listener);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 2705292963342325275L;

}
