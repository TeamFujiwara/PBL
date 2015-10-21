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
		Main NewGame = new Main();
		NewGame.main(null);
	}

}
