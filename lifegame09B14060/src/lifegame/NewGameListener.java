package lifegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameListener implements ActionListener {
	BoardModel m;


	public NewGameListener(BoardModel m) {
		super();
		this.m = m;
	}


	/**
	 * 新しいゲームを開始(同一プロセスで実行)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Main.main(null);
	}

}
