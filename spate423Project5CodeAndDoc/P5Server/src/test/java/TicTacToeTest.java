import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

class TicTacToeTest {

	// Test cases for the MinMax algorithm and Node class

	String[] init_board;
	ArrayList<Node> movesList;
	String[] s;
	MinMax m;
	Node n;

	// Before each test it sets up the board and makes instance of the class MinMax
	@BeforeEach
	void init(){
		s = new String[] {"b","b","b","b","b","b","b","b", "b"};
		init_board = s;
		m = new MinMax(init_board);
		movesList = m.findMoves();
	}

	// Testing to see if the length of the String array is 9
	@Test
	void testBoardLen(){
		assertEquals(9, init_board.length, "Board size is not 9");
	}

	// Testing to see if the user has 6 input char then it wouldnt accept it since it needs 9
	@Test
	void testInvalidBoard(){
		s = new String[] {"b","b","b","b","b","b"};
		init_board = s;
		assertNotEquals(init_board.length, 9, "Invalid amount of strings on board");
	}

	// Testing to see if the board is init with empty blocks
	@Test
	void testBoardString(){
		assertEquals(s, init_board, "Board not initilized to b b b b b b b b b");
	}

	// Testing to see if the node constructor has -1 for minmaxValue
	@Test
	void testinitNodeClass(){
		n = new Node(init_board, 5);
		assertEquals(-1, n.minMaxValue, "Node class didnt init minmax val to -1");
	}

	// Testing to see if the game is won by X
	@Test
	void testWinforX(){
		n = new Node(init_board, 5);
		n.minMaxValue = 10;
		assertEquals(10, n.minMaxValue, "MinmaxValue doesnt equal 10 which checks for win for X");
	}

	// Testing to see if the game is won by O
	@Test
	void testWinforO(){
		n = new Node(init_board, 5);
		n.minMaxValue = -10;
		assertEquals(-10, n.minMaxValue, "MinmaxValue doesnt equal -10 which checks for win for O");
	}

	// Testing to see if the game is draw and there is no blank space left
	@Test
	void testCheckForDraw(){
		s = new String[] {"X","X","O","O","O","X","X","O","X"};
		init_board = s;
		m = new MinMax(init_board);
		n = new Node(init_board, 5);
		assertTrue(n.checkForDraw(), "There is a blank space on the board");
	}

	// Testing to see if the game is not draw yet and there is a blank space left
	@Test
	void testCheckForNotDraw(){
		s = new String[] {"X","X","O","O","O","X","X","O","b"};
		init_board = s;
		m = new MinMax(init_board);
		n = new Node(init_board, 5);
		assertFalse(n.checkForDraw(), "There is no blank space on the board");
	}

}
