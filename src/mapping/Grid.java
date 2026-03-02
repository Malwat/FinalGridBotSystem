package mapping;

public class Grid {

    Node[][] grid;
    int size;

    public Grid(int size){
        this.size = size;
        grid = new Node[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid == null) {
                    throw new AssertionError();
                }
                grid[i][j] = new Node(i,j);
            }
        }
    }

    public Grid(int size, boolean val){
        this.size = size;
        grid = new Node[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid == null) {
                    throw new AssertionError();
                }
                grid[i][j] = new Node(i, j, false);
            }
        }
    }

    public Node getSpot(int row, int col){
        return grid[row][col];
    }

    public void setSpot(int rowNum, int colNum, Node node){
        grid[rowNum][colNum] = node;
    }

    public String toString(){
        String result = "";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result += grid[i][j].toString();
            }
            result += "\n";
        }
        return result;
    }

}
