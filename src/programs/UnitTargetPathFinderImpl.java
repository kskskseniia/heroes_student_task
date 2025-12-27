package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    // 8 направлений
    private static final int[][] DIRS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Базовые проверки
        if (attackUnit == null || targetUnit == null) {
            return Collections.emptyList();
        }

        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int goalX = targetUnit.getxCoordinate();
        int goalY = targetUnit.getyCoordinate();

        if (!inBounds(startX, startY) || !inBounds(goalX, goalY)) {
            return Collections.emptyList();
        }

        // Если атакующий уже стоит в клетке цели
        if (startX == goalX && startY == goalY) {
            List<Edge> path = new ArrayList<>();
            path.add(new Edge(startX, startY));
            return path;
        }

        // blocked[x][y] = true, если клетка занята живым юнитом
        boolean[][] blocked = new boolean[WIDTH][HEIGHT];

        if (existingUnitList != null) {
            for (Unit u : existingUnitList) {
                if (u == null || !u.isAlive()) continue;

                int x = u.getxCoordinate();
                int y = u.getyCoordinate();
                if (!inBounds(x, y)) continue;

                blocked[x][y] = true;
            }
        }

        // Старт и цель должны быть проходимыми
        blocked[startX][startY] = false;
        blocked[goalX][goalY] = false;

        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Edge[][] prev = new Edge[WIDTH][HEIGHT];

        ArrayDeque<Edge> queue = new ArrayDeque<>();
        queue.add(new Edge(startX, startY));
        visited[startX][startY] = true;

        // BFS
        while (!queue.isEmpty()) {
            Edge cur = queue.poll();
            int cx = cur.getX();
            int cy = cur.getY();

            if (cx == goalX && cy == goalY) {
                return buildPath(prev, startX, startY, goalX, goalY);
            }

            for (int[] d : DIRS) {
                int nx = cx + d[0];
                int ny = cy + d[1];

                if (!inBounds(nx, ny)) continue;
                if (visited[nx][ny]) continue;
                if (blocked[nx][ny]) continue;

                visited[nx][ny] = true;
                prev[nx][ny] = cur;
                queue.add(new Edge(nx, ny));
            }
        }

        // Пути нет
        return Collections.emptyList();
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    // Восстановление пути от цели к старту через prev[][]
    private List<Edge> buildPath(Edge[][] prev, int sx, int sy, int gx, int gy) {
        List<Edge> path = new ArrayList<>();
        Edge step = new Edge(gx, gy);

        while (step != null) {
            path.add(step);
            if (step.getX() == sx && step.getY() == sy) break;
            step = prev[step.getX()][step.getY()];
        }

        // Если вдруг не дошли до старта (на всякий случай)
        Edge last = path.get(path.size() - 1);
        if (!(last.getX() == sx && last.getY() == sy)) {
            return Collections.emptyList();
        }

        Collections.reverse(path);
        return path;
    }
}
