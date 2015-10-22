package lifegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameListener implements ActionListener {
	BoardModel m;


	public NewGameListener(BoardModel m) {
		super();
		this.m = m;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// なぜかNew gameが2つ生成されてしまう
		Main NewGame = new Main();
		NewGame.main(null);
	}

}
