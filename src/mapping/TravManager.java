package mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class TravManager {

    Grid grid;
    int count = 0;
    List<Traverser> robotList = new ArrayList<>();
    int pingRadius = 7;


    public void addTraverser(Traverser rob){
        robotList.add(rob);
    }

    public TravManager(Grid grid){
        this.grid = grid;

    }

    public void search(){
        boolean allFinished = false;

        while(!allFinished){
            allFinished = true;
            for(Traverser i: robotList){
                if(i.currentState != Traverser.States.FINISHED){
                    allFinished = false;
                    i.travel(grid, count);
                    count++;

                }
            }
        }
    }

    public void mergeMaps(int val1, int val2){
        List<List<Integer>> rob1VisitedNodes = robotList.get(val1).getVisitedNodes();
        List<List<Integer>> rob2VisitedNodes = robotList.get(val2).getVisitedNodes();

        Grid rob1Minimap = robotList.get(val1).getMiniMap();
        Grid rob2Minimap = robotList.get(val2).getMiniMap();

        for(List<Integer> i: rob1VisitedNodes){
            int rob2Timestamp = rob2Minimap.getSpot(i.get(0), i.get(1)).getTimeStamp();
            int rob1Timestamp = rob1Minimap.getSpot(i.get(0), i.get(1)).getTimeStamp();


            if(rob1Timestamp > rob2Timestamp){
                robotList.get(val2).miniMap.setSpot(i.get(0), i.get(1), robotList.get(val1).miniMap.getSpot(i.get(0), i.get(1)));
            }
        }

        for(List<Integer> i: rob2VisitedNodes){
            int rob2Timestamp = rob2Minimap.getSpot(i.get(0), i.get(1)).getTimeStamp();
            int rob1Timestamp = rob1Minimap.getSpot(i.get(0), i.get(1)).getTimeStamp();


            if(rob2Timestamp > rob1Timestamp){
                robotList.get(val1).miniMap.setSpot(i.get(0), i.get(1), robotList.get(val2).miniMap.getSpot(i.get(0), i.get(1)));
            }
        }
    }

    public int ping(Traverser robot1){
        int indexOfClosestRobot = -Integer.MAX_VALUE;
        int closestRobotDistance = Integer.MAX_VALUE;
        for(int i = 0; i < robotList.size(); i++){
            if(inRadius(robot1, robotList.get(i))){
                int distance = distance(robot1, robotList.get(i));
                if(distance < closestRobotDistance && distance != 0){
                    indexOfClosestRobot = i;
                    closestRobotDistance = distance;
                }
            }
        }
        return indexOfClosestRobot;
    }

    public List<Integer> allPings(Traverser robot1){
        ArrayList<Integer> allNearbyRobots = new ArrayList<>();
        for(int i = 0; i < robotList.size(); i++){
            if(inRadius(robot1, robotList.get(i)) && distance(robot1, robotList.get(i) ) > 0){
                allNearbyRobots.add(i);
            }
        }
        return allNearbyRobots;
    }

    public int distance(Traverser robot1, Traverser robot2){
        int diffX = Math.abs(robot1.position[0] - robot2.position[0]);
        int diffY = Math.abs(robot1.position[1] - robot2.position[1]);

        return diffX + diffY;
    }

    public boolean inRadius(Traverser robot1, Traverser robot2) {
        int diffX = Math.abs(robot1.position[0] - robot2.position[0]);
        int diffY = Math.abs(robot1.position[1] - robot2.position[1]);

        if (diffY < pingRadius && diffX < pingRadius) {
            return true;
        }
        return false;
    }

    public int findOwnIndexInList(Traverser robot){
        int size = robotList.size();
        for (int i = 0; i < size; i++) {
            if(robot.ID == robotList.get(i).ID){
                return i;
            }

        }
        return -1;
    }


    //make is so that the fore loop in the ping method goes back to for Traerser i and use the IDs for the returning int
    //just a bit cleaner

}
