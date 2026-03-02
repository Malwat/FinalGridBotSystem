package mapping;

import java.util.Objects;

public class Node {

    boolean isOpen;
    int target = 0;
    int timeStamp = -1;
    int rowNum;
    int colNum;
    int gCost = Integer.MAX_VALUE;
    int hCost = Integer.MAX_VALUE;
    int fCost = Integer.MAX_VALUE;
    int[] parent = null;
    boolean placeHolder = false;

    public Node(int row, int col){
        rowNum = row;
        colNum = col;
        isOpen = true;
    }

    public Node(Node node, int timeStamp){
        rowNum = node.rowNum;
        colNum = node.colNum;
        isOpen = node.isOpen;
        target = node.target;
        this.timeStamp = timeStamp;
        placeHolder = false;
    }

    public Node(int row, int col, boolean val){
        rowNum = row;
        colNum = col;
        isOpen = true;
        placeHolder = true;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTimeStamp(){
        return timeStamp;
    }

    public int getgCost() {
        return gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
    }

    public int getfCost() {
        return fCost;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public void setParent(int x,int y) {
        this.parent[0] = x;
        this.parent[1] = y;

    }

    public int[] getParent() {
        return parent;
    }

    public String toString(){
        String timeSpace = " ";
        int num = fCost;

        if(timeStamp < 10){
            timeSpace = "  ";
        }
        if(timeStamp > 100){
            timeSpace = "";
        }
        if(fCost > 1000){
            num = 0;
        }

        if(placeHolder) {
            return "null           ";
        }else if(!isOpen){
            return "BARRIER " + timeStamp + timeSpace + "    ";
        }else{
            String open = "O";
            return "{" + rowNum +"," + colNum + "}" + target + " T:" + timeStamp + timeSpace  + "   ";
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return isOpen == node.isOpen && target == node.target && rowNum == node.rowNum && colNum == node.colNum && placeHolder == node.placeHolder;
    }

}
