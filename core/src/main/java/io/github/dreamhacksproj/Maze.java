package io.github.dreamhacksproj;
import java.util.*;

public class Maze {
    private Stack<Node> stack = new Stack<>();
    private Random rand = new Random();
    public int[][] maze;
    private int dimension;
    public int enemyId = 3;

    Maze(int dim) {
        maze = new int[dim][dim];
        dimension = dim;
    }

    public void generateMaze() {
        stack.push(new Node(0, 0));

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                maze[i][j] = 0; // All ground initially
            }
        }

        while (!stack.empty()) {
            Node next = stack.pop();
            if (validNextNode(next)) {
                maze[next.y][next.x] = 1;
                ArrayList<Node> neighbors = findNeighbors(next);
                randomlyAddNodesToStack(neighbors);
            }
        }
        ensureConnectivity();
    }

    public String getRawMaze() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : maze) {
            sb.append(Arrays.toString(row) + "\n");
        }
        return sb.toString();
    }

    public String getSymbolicMaze() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                sb.append(maze[i][j] == 1 ? "*" : " ");
                sb.append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private boolean validNextNode(Node node) {
        int numNeighboringOnes = 0;
        for (int y = node.y - 1; y < node.y + 2; y++) {
            for (int x = node.x - 1; x < node.x + 2; x++) {
                if (pointOnGrid(x, y) && pointNotNode(node, x, y) && maze[y][x] == 1) {
                    numNeighboringOnes++;
                }
            }
        }
        return (numNeighboringOnes < 3) && maze[node.y][node.x] != 1;
    }

    private void randomlyAddNodesToStack(ArrayList<Node> nodes) {
        int targetIndex;
        while (!nodes.isEmpty()) {
            targetIndex = rand.nextInt(nodes.size());
            stack.push(nodes.remove(targetIndex));
        }
    }

    private ArrayList<Node> findNeighbors(Node node) {
        ArrayList<Node> neighbors = new ArrayList<>();
        for (int y = node.y - 1; y < node.y + 2; y++) {
            for (int x = node.x - 1; x < node.x + 2; x++) {
                if (pointOnGrid(x, y) && pointNotCorner(node, x, y)
                    && pointNotNode(node, x, y)) {
                    neighbors.add(new Node(x, y));
                }
            }
        }
        return neighbors;
    }

    private Boolean pointOnGrid(int x, int y) {
        return x >= 0 && y >= 0 && x < dimension && y < dimension;
    }

    private Boolean pointNotCorner(Node node, int x, int y) {
        return (x == node.x || y == node.y);
    }

    private Boolean pointNotNode(Node node, int x, int y) {
        return !(x == node.x && y == node.y);
    }

    private void ensureConnectivity() {
        boolean[][] visited = new boolean[dimension][dimension];
        int wallCount = 0;

        outerLoop:
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (maze[i][j] == 1) {
                    dfs(i, j, visited);
                    wallCount++;
                    break outerLoop;
                }
            }
        }

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (maze[i][j] == 1 && !visited[i][j]) {
                    maze[i][j] = 0;
                }
            }
        }

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (maze[i][j] == 1 && !visited[i][j]) {
                    connectIsolatedWall(i, j);
                }
            }
        }
    }

    private void connectIsolatedWall(int y, int x) {
        // Check for nearby walls and connect the isolated pocket
        if (pointOnGrid(x, y)) {
            maze[y][x] = 1;
        }
    }


    private void dfs(int y, int x, boolean[][] visited) {
        if (y < 0 || x < 0 || y >= dimension || x >= dimension || visited[y][x] || maze[y][x] != 1) {
            return;
        }
        visited[y][x] = true;

        dfs(y + 1, x, visited);
        dfs(y - 1, x, visited);
        dfs(y, x + 1, visited);
        dfs(y, x - 1, visited);
    }

    public void setRandomToTwo() {
        Node randomFloor = getRandomLocation(0);
        if (randomFloor != null) {
            maze[randomFloor.y][randomFloor.x] = 2;
        }
    }

    public Node getRandomLocation(int val) {
        ArrayList<Node> wallLocations = new ArrayList<>();

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (maze[i][j] == val) {
                    wallLocations.add(new Node(j, i));
                }
            }
        }

        if (wallLocations.isEmpty()) {
            return null;
        }

        int randomIndex = rand.nextInt(wallLocations.size());
        return wallLocations.get(randomIndex);
    }
    public Node getPlayer()
    {
        return getRandomLocation(2);
    }

    public boolean goSouth() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.y > 0 && maze[randomTile.y - 1][randomTile.x] == 0) {
            maze[randomTile.y - 1][randomTile.x] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goEast() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.x < dimension - 1 && maze[randomTile.y][randomTile.x + 1] == 0) {
            maze[randomTile.y][randomTile.x + 1] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goNorth() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.y < dimension - 1 && maze[randomTile.y + 1][randomTile.x] == 0) {
            maze[randomTile.y + 1][randomTile.x] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goWest() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.x > 0 && maze[randomTile.y][randomTile.x - 1] == 0) {
            maze[randomTile.y][randomTile.x - 1] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goSouthTeleport() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.y > 1
            && maze[randomTile.y - 1][randomTile.x] != 0  // Adjacent tile must NOT be floor
            && maze[randomTile.y - 2][randomTile.x] == 0) { // 2nd tile must be floor
            maze[randomTile.y - 2][randomTile.x] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goEastTeleport() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.x < dimension - 2
            && maze[randomTile.y][randomTile.x + 1] != 0
            && maze[randomTile.y][randomTile.x + 2] == 0) {
            maze[randomTile.y][randomTile.x + 2] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goNorthTeleport() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.y < dimension - 2
            && maze[randomTile.y + 1][randomTile.x] != 0
            && maze[randomTile.y + 2][randomTile.x] == 0) {
            maze[randomTile.y + 2][randomTile.x] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goWestTeleport() {
        Node randomTile = getRandomLocation(2);
        if (randomTile != null && randomTile.x > 1
            && maze[randomTile.y][randomTile.x - 1] != 0
            && maze[randomTile.y][randomTile.x - 2] == 0) {
            maze[randomTile.y][randomTile.x - 2] = 2;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goSouthEnemy(int id) {
        Node randomTile = getRandomLocation(id);
        if (randomTile != null && randomTile.y > 0 && maze[randomTile.y - 1][randomTile.x] == 0) {
            maze[randomTile.y - 1][randomTile.x] = id;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goEastEnemy(int id) {
        Node randomTile = getRandomLocation(id);
        if (randomTile != null && randomTile.x < dimension - 1 && maze[randomTile.y][randomTile.x + 1] == 0) {
            maze[randomTile.y][randomTile.x + 1] = id;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goNorthEnemy(int id) {
        Node randomTile = getRandomLocation(id);
        if (randomTile != null && randomTile.y < dimension - 1 && maze[randomTile.y + 1][randomTile.x] == 0) {
            maze[randomTile.y + 1][randomTile.x] = id;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public boolean goWestEnemy(int id) {
        Node randomTile = getRandomLocation(id);
        if (randomTile != null && randomTile.x > 0 && maze[randomTile.y][randomTile.x - 1] == 0) {
            maze[randomTile.y][randomTile.x - 1] = id;
            maze[randomTile.y][randomTile.x] = 0;
            return true;
        }
        return false;
    }

    public void makeEnemiesMove() {
        int[] enemyIds = {3, 4, 5}; // Enemy IDs
        Random rand = new Random();

        for (int id : enemyIds) {
            int direction = rand.nextInt(4); // 0 = North, 1 = South, 2 = East, 3 = West

            switch (direction) {
                case 0:
                    goNorthEnemy(id);
                    break;
                case 1:
                    goSouthEnemy(id);
                    break;
                case 2:
                    goEastEnemy(id);
                    break;
                case 3:
                    goWestEnemy(id);
                    break;
            }
        }
    }

    public void makeEnemiesSmart() {
        int[] enemyIds = {3, 4, 5};
        Node player = getPlayer();

        for (int id : enemyIds) {
            Node enemy = getRandomLocation(id);
            if (enemy != null) {
                ArrayList<Node> potentialMoves = new ArrayList<>();
                int bestDistance = Integer.MAX_VALUE;
                Node bestMove = null;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (Math.abs(dx) != Math.abs(dy)) {
                            int newY = enemy.y + dy;
                            int newX = enemy.x + dx;

                            if (pointOnGrid(newX, newY) && maze[newY][newX] != 1) {
                                potentialMoves.add(new Node(newX, newY));
                                int distance = Math.abs(newX - player.x) + Math.abs(newY - player.y);

                                if (distance < bestDistance) {
                                    bestDistance = distance;
                                    bestMove = new Node(newX, newY);
                                }
                            }
                        }
                    }
                }

                if (bestMove != null) {
                    maze[enemy.y][enemy.x] = 0;
                    maze[bestMove.y][bestMove.x] = id;
                }
            }
        }
    }


    public boolean checkIfAdjacent(Node n1, Node n2)
    {
        if (n1.x == n2.x && (n1.y == n2.y + 1 || n1.y == n2.y - 1))
        {
            return true;
        }
        if (n1.y == n2.y && (n1.x == n2.x + 1 || n1.x == n2.x - 1))
        {
            return true;
        }
        return false;
    }

    public boolean isPlayerAdjacentToEnemy()
    {
        Node player = getRandomLocation(2);
        int[] enemyIds = {3, 4, 5};
        for (int id : enemyIds) {
            Node enemy = getRandomLocation(id);
            if (checkIfAdjacent(player, enemy))
            {
                return true;
            }
        }
        return false;
    }
    public void killPlayer()
    {
        Node player = getRandomLocation(2);
        maze[player.y][player.x] = 0;
    }
    public void setRandomEnemy(int radius) {
        Node randomFloor = getRandomLocation(0);
        boolean success = false;
        while (!success) {
            Node two = getRandomLocation(2);
            if (two != null) {
                if (isWithinRadius(randomFloor.x, randomFloor.y, two.x, two.y, radius))
                {
                    randomFloor = getRandomLocation(0);
                } else {
                    success = true;
                }
            }
        }
        maze[randomFloor.y][randomFloor.x] = enemyId;
        enemyId++;
    }

    public boolean isWithinRadius(int x1, int y1, int x2, int y2, int radius) {
        int manhattanDistance = Math.abs(x1 - x2) + Math.abs(y1 - y2);
        return manhattanDistance <= radius;
    }
}

