run: src/sudokusolver/SudokuSolver.class
	java -cp src/ sudokusolver.SudokuSolver

src/sudokusolver/SudokuSolver.class: src/sudokusolver/SudokuSolver.java
	javac src/sudokusolver/SudokuSolver.java

clean:
	find src -name '*.class' -delete

.PHONY: run clean
