package lifegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NextListener implements ActionListener {

	BoardModel m;



	public NextListener(BoardModel m) {
		super();
		this.m = m;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		m.next();
	}

}
