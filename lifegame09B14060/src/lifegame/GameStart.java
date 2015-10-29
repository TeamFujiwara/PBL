package lifegame;

import javax.swing.SwingUtilities;

public class GameStart extends Thread {
	BoardModel m;

	public GameStart(BoardModel m) {
		super();
		this.m = m;
	}

	public GameStart() {
		super();
	}



	@Override
	public void run() {
		SwingUtilities.invokeLater(new Main(m));
	}
}
