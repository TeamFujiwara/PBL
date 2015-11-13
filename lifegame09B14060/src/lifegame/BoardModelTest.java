package lifegame;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class BoardModelTest {

	@Test
	public void nextTest1(){
		BoardModel b = new BoardModel(5, 5);
		BoardModel b1 = new BoardModel(5, 5);
		b.changeCellsState(3, 2);
		b.changeCellsState(3, 3);
		b.changeCellsState(3, 4);

		b1.changeCellsState(2, 3);
		b1.changeCellsState(3, 3);
		b1.changeCellsState(4, 3);

		b.next();
		assertThat((boolean[][])b.getCells(), is(b1.getCells()));
	}

	@Test
	public void nextTest2(){
		BoardModel tested = new BoardModel(10, 10);
		BoardModel shouldBe = new BoardModel(10, 10);

		tested.changeCellsState(2, 2);
		tested.changeCellsState(3, 3);
		tested.changeCellsState(4, 1);
		tested.changeCellsState(4, 2);
		tested.changeCellsState(4, 3);

		shouldBe.changeCellsState(3, 1);
		shouldBe.changeCellsState(3, 3);
		shouldBe.changeCellsState(4, 2);
		shouldBe.changeCellsState(4, 3);
		shouldBe.changeCellsState(5, 2);

		tested.next();

		assertThat(tested.getCells(), is(shouldBe.getCells()));
	}

	@Test
	public void undoTest1(){
		BoardModel b = new BoardModel(5, 5);
		BoardModel b1 = new BoardModel(5, 5);

		b.changeCellsState(3, 2);
		b.changeCellsState(3, 3);
		b.changeCellsState(3, 4);

		b1.changeCellsState(3, 2);
		b1.changeCellsState(3, 3);
		b1.changeCellsState(3, 4);

		b.next();
		b.undo();

		assertThat(b.getCells(), is(b1.getCells()));


	}

	@Test
	public void undoTest2(){
		BoardModel b = new BoardModel(5, 5);
		b.changeCellsState(3, 2);
		b.changeCellsState(3, 3);
		b.changeCellsState(3, 4);

		BoardModel b1 = new BoardModel(5, 5);
		b1.changeCellsState(3, 2);
		b1.changeCellsState(3, 3);
		b1.changeCellsState(3, 4);

		for (int i = 0; i < 10; i++) {
			b.next();
		}

		BoardModel b2 = new BoardModel(5, 5);
		b2.changeToNewBoard(b.duplicateBoard());

		for (int i = 0; i < 10; i++) {
			b.next();
		}


		for(int i = 0; i< 10; i++){
			b.undo();
		}

		assertThat(b.getCells(),is(b2.getCells()));
	}

}
