package sudokusolver;

import java.util.*;
import java.util.stream.Collectors;

public class SudokuSolver {

    @SuppressWarnings("WeakerAccess")
    public static final class Sudoku implements Cloneable {

        private final int[] grid;

        public Sudoku(final int... grid) {
            assert grid.length == 81;
            this.grid = grid;
        }

        private static int index(final int x, final int y) {
            return y * 9 + x;
        }

        public int get(final int x, final int y) {
            assert x >= 0 && x < 9 && y >= 0 && y < 9;
            return grid[index(x, y)];
        }

        private void set(final int x, final int y, final int digit) {
            assert x >= 0 && x < 9 && y >= 0 && y < 9 && digit >= 0 && digit <= 9;
            grid[index(x, y)] = digit;
        }

        public int[] column(final int x) {
            assert x >= 0 && x < 9;
            final int[] column = new int[9];
            for (int y = 0; y < 9; y += 1) {
                column[y] = get(x, y);
            }
            return column;
        }

        public int[] row(final int y) {
            assert y >= 0 && y < 9;
            final int[] row = new int[9];
            for (int x = 0; x < 9; x += 1) {
                row[x] = get(x, y);
            }
            return row;
        }

        public int[] sector(final int x, final int y) {
            assert x >= 0 && x < 9 && y >= 0 && y < 9;
            final int sx = x / 3 * 3;
            final int sy = y / 3 * 3;
            final int[] sector = new int[9];
            for (int ox = 0; ox < 3; ox += 1) {
                for (int oy = 0; oy < 3; oy += 1) {
                    final int d = get(sx + ox, sy + oy);
                    sector[oy * 3 + ox] = d;
                }
            }
            return sector;
        }

        public Set<Integer> candidates(final int x, final int y) {
            assert x >= 0 && x < 9 && y >= 0 && y < 9;
            final int d = get(x, y);
            if (d != 0) {
                return new TreeSet<>(Collections.singleton(d));
            }
            final Set<Integer> candidates = new TreeSet<>();
            for (int i = 1; i <= 9; i += 1) {
                candidates.add(i);
            }
            for (int i = 0; i < 9; i += 1) {
                candidates.remove(get(i, y));
            }
            for (int i = 0; i < 9; i += 1) {
                candidates.remove(get(x, i));
            }
            final int sx = x / 3 * 3;
            final int sy = y / 3 * 3;
            for (int ox = 0; ox < 3; ox += 1) {
                for (int oy = 0; oy < 3; oy += 1) {
                    candidates.remove(get(sx + ox, sy + oy));
                }
            }
            return candidates;
        }

        private static boolean hasDuplicates(final int[] arr) {
            final long c1 = Arrays.stream(arr).filter(a -> a > 0).count();
            final long c2 = Arrays.stream(arr).filter(a -> a > 0).distinct().count();
            return c1 != c2;
        }

        public boolean isValid() {
            for (int i = 0; i < 9; i += 1) {
                if (hasDuplicates(row(i))) {
                    return false;
                }
                if (hasDuplicates(column(i))) {
                    return false;
                }
            }
            for (int x = 0; x < 9; x += 3) {
                for (int y = 0; y < 9; y += 3) {
                    if (hasDuplicates(sector(x, y))) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean isSolved() {
            return Arrays.stream(grid).noneMatch(a -> a == 0) && isValid();
        }

        private int[][] order() {
            final int[][] order = new int[81][2];
            int i = 0;
            for (int x = 0; x < 9; x += 1) {
                for (int y = 0; y < 9; y += 1) {
                    order[i][0] = x;
                    order[i][1] = y;
                    i += 1;
                }
            }
            adjustOrder(order, 0);
            return order;
        }

        private int[][] adjustOrder(final int[][] order, final int start) {
            final int[] candidates = new int[81];
            for (int i = start; i < 81; i += 1) {
                candidates[index(order[i][0], order[i][1])] = candidates(order[i][0], order[i][1]).size();
            }
            Arrays.sort(order, start, 81, (ci, cj) -> {
                final int si = candidates[index(ci[0], ci[1])];
                final int sj = candidates[index(cj[0], cj[1])];
                return Integer.compare(si, sj);
            });
            return order;
        }

        public boolean solve() {
            if (!isValid()) {
                return false;
            }
            steps = 0;
            return solve(order(), 0);
        }

        private int steps = 0;

        public int steps() {
            return steps;
        }

        private boolean solve(final int[][] order, final int ix) {
            steps += 1;
            if (ix == 81) {
                return isSolved();
            }
            final int x = order[ix][0];
            final int y = order[ix][1];
            final int d = get(x, y);
            final Set<Integer> cs = candidates(x, y);
            if (cs.isEmpty()) {
                return false;
            }
            for (final int c : cs) {
                set(x, y, c);
                if (solve(adjustOrder(order.clone(), ix + 1), ix + 1)) {
                    return true;
                }
            }
            set(x, y, d);
            return false;
        }

        public String toString() {
            final StringBuilder b = new StringBuilder();
            for (int i = 0; i < 9; i += 1) {
                final String row = Arrays.stream(row(i)).mapToObj(Objects::toString).collect(Collectors.joining(" "));
                b.append(row);
                b.append('\n');
            }
            return b.toString();
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public Sudoku clone() {
            return new Sudoku(grid.clone());
        }
    }

    public static void main(final String... args) {
        final Sudoku s0 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0
        );
        final Sudoku s1 = new Sudoku(
                0, 9, 8, 0, 4, 0, 0, 0, 0,
                4, 2, 0, 0, 9, 0, 0, 8, 0,
                0, 0, 0, 3, 0, 1, 0, 0, 0,
                6, 3, 9, 0, 0, 8, 7, 0, 0,
                2, 0, 4, 9, 0, 7, 3, 0, 8,
                0, 0, 7, 5, 0, 0, 9, 2, 6,
                0, 0, 0, 4, 0, 3, 0, 0, 0,
                0, 6, 0, 0, 1, 0, 0, 4, 9,
                0, 0, 0, 0, 5, 0, 8, 3, 0
        );
        final Sudoku s2 = new Sudoku(
                1, 0, 0, 0, 6, 0, 0, 3, 0,
                0, 5, 4, 0, 0, 0, 0, 7, 8,
                2, 6, 0, 0, 0, 8, 0, 0, 0,
                0, 0, 0, 0, 2, 1, 0, 0, 0,
                0, 0, 6, 0, 0, 0, 4, 0, 0,
                0, 0, 0, 5, 8, 0, 0, 0, 0,
                0, 0, 0, 8, 0, 0, 0, 4, 2,
                8, 3, 0, 0, 0, 0, 7, 9, 0,
                0, 1, 0, 0, 9, 0, 0, 0, 5
        );
        final Sudoku s3 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 9, 8, 7, 0, 0, 0,
                1, 2, 3, 6, 5, 4, 8, 7, 9,
                0, 0, 0, 1, 2, 3, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 2, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 0,
                0, 0, 0, 0, 0, 0, 3, 0, 0
        );
        final Sudoku s4 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                9, 8, 7, 6, 5, 4, 3, 2, 1
        );
        final Sudoku s5 = new Sudoku(
                1, 0, 0, 0, 0, 0, 0, 0, 6,
                0, 2, 0, 0, 0, 0, 0, 5, 0,
                0, 0, 3, 0, 0, 0, 4, 0, 0,
                0, 0, 9, 4, 0, 0, 0, 0, 0,
                0, 8, 0, 0, 5, 0, 0, 0, 0,
                7, 0, 0, 0, 0, 6, 0, 0, 0,
                0, 0, 0, 0, 0, 3, 7, 0, 0,
                0, 0, 0, 0, 2, 0, 0, 8, 0,
                0, 0, 0, 1, 0, 0, 0, 0, 9
        );
        final Sudoku s6 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 1, 6, 8, 0, 0, 0,
                0, 0, 0, 2, 0, 5, 0, 0, 0,
                0, 0, 0, 9, 3, 4, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0
        );
        final Sudoku s7 = new Sudoku(
                1, 0, 0, 2, 0, 0, 3, 0, 0,
                2, 0, 0, 3, 0, 0, 4, 0, 0,
                3, 0, 0, 4, 0, 0, 5, 0, 0,
                4, 0, 0, 5, 0, 0, 6, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 3, 0, 0, 4, 0, 0, 5,
                0, 0, 4, 0, 0, 5, 0, 0, 6,
                0, 0, 5, 0, 0, 6, 0, 0, 7,
                0, 0, 6, 0, 0, 7, 0, 0, 8
        );
        final Sudoku s8 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 2, 0, 0, 3,
                0, 0, 0, 4, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 5, 0, 0,
                4, 0, 1, 6, 0, 0, 0, 0, 0,
                0, 0, 7, 1, 0, 0, 0, 0, 0,
                0, 5, 0, 0, 0, 0, 2, 0, 0,
                0, 0, 0, 0, 8, 0, 0, 4, 0,
                0, 3, 0, 9, 1, 0, 0, 0, 0
        );
        final Sudoku s9 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 9, 0, 0, 1, 0, 0, 3, 0,
                0, 0, 6, 0, 2, 0, 7, 0, 0,
                0, 0, 0, 3, 0, 4, 0, 0, 0,
                2, 1, 0, 0, 0, 0, 0, 9, 8,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 2, 5, 0, 6, 4, 0, 0,
                0, 8, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0
        );
        final Sudoku s10 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 1, 2, 0, 3, 4, 5, 6, 7,
                0, 3, 4, 5, 0, 6, 1, 8, 2,
                0, 0, 1, 0, 5, 8, 2, 0, 6,
                0, 0, 8, 6, 0, 0, 0, 0, 1,
                0, 2, 0, 0, 0, 7, 0, 5, 0,
                0, 0, 3, 7, 0, 5, 0, 2, 8,
                0, 8, 0, 0, 6, 0, 7, 0, 0,
                2, 0, 7, 0, 8, 3, 6, 1, 5
        );
        final Sudoku s11 = new Sudoku(
                0, 5, 0, 0, 0, 0, 2, 0, 0,
                0, 0, 0, 0, 8, 0, 0, 4, 0,
                0, 3, 0, 9, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 7, 2, 0, 0, 3,
                0, 0, 0, 4, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 5, 0, 0,
                4, 0, 1, 6, 0, 0, 0, 9, 0,
                0, 0, 7, 1, 0, 0, 6, 0, 0
        );
        final Sudoku s12 = new Sudoku(
                0, 5, 0, 0, 0, 0, 2, 0, 0,
                0, 0, 0, 0, 8, 0, 0, 4, 0,
                0, 3, 0, 9, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 7, 2, 0, 0, 3,
                0, 0, 0, 4, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 5, 0, 0,
                4, 0, 1, 6, 0, 0, 0, 0, 0,
                0, 0, 7, 1, 0, 0, 6, 0, 0
        );
        final Sudoku s13 = new Sudoku(
                0, 5, 0, 0, 0, 0, 2, 0, 0,
                0, 0, 0, 0, 8, 0, 0, 4, 0,
                0, 3, 0, 9, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 7, 2, 0, 0, 3,
                0, 0, 0, 4, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 5, 0, 0,
                4, 0, 1, 6, 0, 0, 0, 9, 0,
                0, 0, 7, 1, 0, 0, 0, 0, 0
        );
        final Sudoku s14 = new Sudoku(
                0, 5, 0, 0, 0, 0, 2, 0, 0,
                0, 0, 0, 0, 8, 0, 0, 4, 0,
                0, 3, 0, 9, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 7, 2, 0, 0, 3,
                0, 0, 0, 4, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 5, 0, 0,
                4, 0, 1, 6, 0, 0, 0, 0, 0,
                0, 0, 7, 1, 0, 0, 0, 0, 0
        );
        final Sudoku s15 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 0, 0, 7, 0, 5, 0, 0, 9,
                0, 0, 6, 0, 9, 0, 3, 0, 0,
                0, 8, 0, 9, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 8, 0, 7,
                0, 0, 4, 1, 0, 0, 0, 0, 2,
                3, 1, 0, 2, 0, 0, 0, 0, 0,
                7, 0, 0, 0, 0, 6, 0, 0, 5,
                6, 0, 0, 0, 0, 1, 0, 0, 0
        );
        final Sudoku s16 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 5, 0,
                0, 1, 0, 0, 0, 9, 0, 0, 0,
                0, 4, 0, 0, 2, 0, 0, 0, 0,
                0, 9, 2, 0, 0, 0, 0, 0, 0,
                0, 3, 0, 9, 1, 4, 6, 0, 0,
                0, 0, 4, 0, 6, 8, 7, 0, 0,
                0, 0, 0, 6, 7, 0, 4, 8, 0,
                0, 0, 0, 0, 0, 0, 0, 3, 0,
                8, 7, 0, 3, 0, 0, 9, 0, 0
        );
        final Sudoku s17 = new Sudoku(
                5, 3, 0, 0, 7, 0, 0, 0, 0,
                6, 0, 0, 1, 9, 5, 0, 0, 0,
                0, 9, 8, 0, 0, 0, 0, 6, 0,
                8, 0, 0, 0, 6, 0, 0, 0, 3,
                4, 0, 0, 8, 0, 3, 0, 0, 1,
                7, 0, 0, 0, 2, 0, 0, 0, 6,
                0, 6, 0, 0, 0, 0, 2, 8, 0,
                0, 0, 0, 4, 1, 9, 0, 0, 5,
                0, 0, 0, 0, 8, 0, 0, 7, 9
        );
        final Sudoku s18 = new Sudoku(
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 3, 0, 8, 5,
                0, 0, 1, 0, 2, 0, 0, 0, 0,
                0, 0, 0, 5, 0, 7, 0, 0, 0,
                0, 0, 4, 0, 0, 0, 1, 0, 0,
                0, 9, 0, 0, 0, 0, 0, 0, 0,
                5, 0, 0, 0, 0, 0, 0, 7, 3,
                0, 0, 2, 0, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 4, 0, 0, 0, 9
        );
        run(s0);
        run(s1);
        run(s2);
        run(s3);
        run(s4);
        run(s5);
        run(s6);
        run(s7);
        run(s8);
        run(s9);
        run(s10);
        run(s11);
        run(s12);
        run(s13);
        run(s14);
        run(s15);
        run(s16);
        run(s17);
        run(s18);
    }

    private static void run(final Sudoku s) {
        System.out.println(s.isValid());
        System.out.println(s.isSolved());
        final long started = System.nanoTime();
        System.out.println(s.solve());
        System.out.println(s.steps());
        System.out.printf("Took %dms\n", (System.nanoTime() - started) / 1000000);
        System.out.println(s.isValid());
        System.out.println(s.isSolved());
        System.out.println(s.toString());
    }

}
