package mapping;

import java.util.*;

public class Traverser {

    public enum States {
        EXPLORING,
        APPROACHING,
        WAITING,
        GOINGHOME,
        MERGING,
        APPROACHINGTARGET,
        FINISHED
    }

    public int ID;
    public int target;
    public int[] position;
    public int[] start;
    public Grid miniMap;
    States currentState;
    int pingCounter = 0;
    int pingInterval = 13;
    int pingIndex = -1;
    int previousPingIndex;
    List<Node> targetNodes = new ArrayList<>();

    TravManager manager;

    public boolean completeTask = false;
    public List<List<Integer>> visitedNodes = new ArrayList<>();
    public Stack<int[]> adjacentNodes = new Stack<>();
    public Stack<Node> pathToTarget = new Stack<>();
    public Stack<Node> pathToHome = new Stack<>();

    public int[] temporaryNodePlace = new int[2];

    public boolean backtracking = false;

    public int[] targetSpotApproaching = new int[2];
    Node targetNodeFromShare = null;

    private static final int[][] DIRECTIONS = {
            {-1, 0}, {0, 1}, {1, 0}, {0, -1}
    };

    public Traverser(int id, int target, int[] start, int size){
        this.ID = id;
        this.start = start;
        this.position = start;
        this.target = target;
        miniMap = new Grid(size, true);
        visitedNodes.add(Arrays.asList(position[0],position[1]));
        adjacentNodes.push(position);
        currentState = States.EXPLORING;

    }

    public Grid getMiniMap() {
        return miniMap;
    }

    public void travel(Grid grid, int timeStep){
        if(pingCounter >= pingInterval && currentState != States.APPROACHING && currentState != States.WAITING){
            if(manager.ping(this) == previousPingIndex){
                List<Integer> nearbyRobotIndexesInList = manager.allPings(this);
                for(Integer i: nearbyRobotIndexesInList){
                    if(i != previousPingIndex){
                        pingIndex = i;
                    }
                }
            }

            if(pingIndex <= manager.robotList.size() && pingIndex >=0 ){
                States state = manager.robotList.get(pingIndex).currentState;
                if(state != States.FINISHED && state != States.WAITING && state != States.APPROACHING){
                    System.out.println("ID " + ID + " got response ping and is now waiting");
                    System.out.println(miniMap);
                    System.out.println("ID " + manager.robotList.get(pingIndex).ID + " is now approaching ");
                    System.out.println(manager.robotList.get(pingIndex).miniMap);
                    currentState = States.WAITING;
                    previousPingIndex = pingIndex;
                    manager.robotList.get(pingIndex).previousPingIndex = manager.findOwnIndexInList(this);
                    manager.robotList.get(pingIndex).currentState = States.APPROACHING;
                    manager.robotList.get(pingIndex).targetSpotApproaching = position;
                    manager.robotList.get(pingIndex).pingIndex = manager.findOwnIndexInList(this);

                }else{
                    pingIndex = -1;

                    System.out.println("OTher robot is finished");
                }
            }else{
                System.out.println("ID: " + ID + " ping index not in valid range");
                //This resets the constraints on who can recieve a ping so that the last reciever can recieve it again
                previousPingIndex = -1;
            }
            pingCounter = 0;
        }
        pingCounter++;

        //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOo
        if(currentState == States.EXPLORING) {
            if(targetNodeFromShare == null) {

                if (!adjacentNodes.isEmpty()) {
                    int[] tempPeekNode = adjacentNodes.peek();
                    //If this is not a neighboring node
                    if (!getNeighbors(miniMap.getSpot(tempPeekNode[0], tempPeekNode[1])).contains(miniMap.getSpot(position[0], position[1])) && position != start) {
                        temporaryNodePlace = adjacentNodes.pop();
                        Node goalNode = getClosestNeighborToNode(miniMap.getSpot(position[0], position[1]), miniMap.getSpot(temporaryNodePlace[0], temporaryNodePlace[1]));
                        pathToTarget = calculatePathTarget(new int[]{goalNode.rowNum, goalNode.colNum}, true);
                        backtracking = true;
                    }
                }

                if (!backtracking && !adjacentNodes.isEmpty()) {
                    int[] spot = adjacentNodes.pop();
                    Node node = grid.getSpot(spot[0], spot[1]);
                    visitedNodes.add(Arrays.asList(spot[0], spot[1]));
                    if (node.isOpen) {
                        //Updating MiniMap
                        position = new int[]{node.rowNum, node.colNum};
                        Node timeNode = new Node(grid.getSpot(position[0], position[1]), timeStep);
                        miniMap.setSpot(position[0], position[1], timeNode);
                        //If it's their target
                        if (target == node.target) {
                            currentState = States.GOINGHOME;
                            completeTask = true;
                            System.out.println("ID " + ID + " FOUND TARGET here is current graph");
                            System.out.println(miniMap);
                            return;
                        }
                        if (node.target > 0) {
                            targetNodes.add(node);
                        }
                        for (int[] dir : DIRECTIONS) {

                            //System.out.println("checking adjacent squares for visited");
                            int[] adjDirection = {position[0] + dir[0], position[1] + dir[1]};
                            if (adjDirection[0] < 0 || adjDirection[0] >= (grid.size) || adjDirection[1] < 0 || adjDirection[1] >= (grid.size)) {
                                continue;
                            }

                            if (!visitedNodes.contains(Arrays.asList(adjDirection[0], adjDirection[1]))) {
                                //System.out.println("not visited so added " + adjDirection[0] + " " + adjDirection[1]);
                                adjacentNodes.push(adjDirection);
                            }
                        }
                    } else {
                        //System.out.println("node closed");
                        Node timeNode = new Node(grid.getSpot(spot[0], spot[1]), timeStep);
                        miniMap.setSpot(spot[0], spot[1], timeNode);
                        travel(grid, timeStep);
                    }
                } else if (backtracking) {
                    if (!pathToTarget.isEmpty()) {
                        Node tempNode = pathToTarget.pop();

                        position = new int[]{tempNode.rowNum, tempNode.colNum};
                        Node timeNode = new Node(grid.getSpot(position[0], position[1]), timeStep);
                        miniMap.setSpot(position[0], position[1], timeNode);

                        for (int[] dir : DIRECTIONS) {
                            //System.out.println("checking adjacent squares for visited");
                            int[] adjDirection = {position[0] + dir[0], position[1] + dir[1]};
                            if (adjDirection[0] < 0 || adjDirection[0] >= (grid.size) || adjDirection[1] < 0 || adjDirection[1] >= (grid.size)) {
                                continue;
                            }
                            //Node nodeCheck = miniMap.getSpot(adjDirection[0], adjDirection[1]);
                            if (!visitedNodes.contains(Arrays.asList(adjDirection[0], adjDirection[1]))) {
                                //System.out.println("not visited so added " + adjDirection[0] + " " + adjDirection[1]);
                                if (!adjacentNodes.contains(adjDirection)) {
                                    adjacentNodes.push(adjDirection);

                                }
                            }
                        }
                        if (pathToTarget.isEmpty()) {
                            backtracking = false;
                        }

                    } else {
                        backtracking = false;
                    }

                } else {
                    System.out.println("What happened");
                }
            }else{
                System.out.println("ID: " + ID + " Now knows it's target location through sharing and will approach it");
                System.out.println(miniMap);
                System.out.println("Here is the Node " +targetNodeFromShare);
                approachingPingTargetLogic(grid, timeStep);
            }

        //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
        }else if(currentState == States.GOINGHOME){
            pathToHome = calculatePathTarget(start, true);
            if(!pathToHome.isEmpty()) {
                Node tempNode = pathToHome.pop();
                if (tempNode.isOpen) {
                    position[0] = tempNode.rowNum;
                    position[1] = tempNode.colNum;
                    Node timeNode = new Node(grid.getSpot(position[0], position[1]), timeStep);
                    miniMap.setSpot(position[0], position[1], timeNode);
                }
            }else{
                System.out.println("ID: " + ID + " Finished Task and is home");
                System.out.println(miniMap);
                currentState = States.FINISHED;
            }
        //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
        } else if (currentState == States.WAITING) {
            System.out.println("I'm waiting and my id is " + ID);
            pingCounter = 0;
            //currentState = States.FINISHED;
        //ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
        } else if (currentState == States.APPROACHING){
            pingCounter = 0;
            System.out.println("I'm approaching and my id is " + ID);
            pathToTarget = calculatePathTarget(targetSpotApproaching, false);
            if(pathToTarget.size() > 1){
                Node nextNode = pathToTarget.pop();
                visitedNodes.add(Arrays.asList(nextNode.rowNum, nextNode.colNum));
                if(grid.getSpot(nextNode.rowNum, nextNode.colNum).isOpen){
                    position[0] = nextNode.rowNum;
                    position[1] = nextNode.colNum;
                    Node tempNode = grid.getSpot(position[0],position[1]);
                    if (tempNode.target > 0) {
                        if(tempNode.target == target){
                            targetNodeFromShare = tempNode;
                        }else{
                            targetNodes.add(tempNode);
                        }

                    }
                    Node timeNode = new Node(grid.getSpot(nextNode.rowNum, nextNode.colNum), timeStep);
                    miniMap.setSpot(nextNode.rowNum, nextNode.colNum, timeNode);
                    System.out.println(position[0] + " " + position[1]);

                }else{
                    Node timeNode = new Node(grid.getSpot(nextNode.rowNum, nextNode.colNum), timeStep);
                    miniMap.setSpot(nextNode.rowNum, nextNode.colNum, timeNode);
                    pathToTarget = calculatePathTarget(targetSpotApproaching, false);
                }

            }else{
                System.out.println("MADE IT TO OTHER BOT");
                System.out.println("ID: " +ID +" found other bot");
                System.out.println(miniMap);
                System.out.println("ID :" + manager.robotList.get(pingIndex).ID);
                System.out.println(manager.robotList.get(pingIndex).miniMap);

                currentState = States.MERGING;
                manager.robotList.get(pingIndex).currentState = States.MERGING;

                //MERGE MAPPSSSS
                manager.mergeMaps(manager.findOwnIndexInList(this), pingIndex);
            }

        //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
        }else if(currentState == States.MERGING){
            System.out.println("MERGED AND SHARING TARGET NODES");
            for(Node i: targetNodes){
                if(!manager.robotList.get(pingIndex).targetNodes.contains(i)) {
                    manager.robotList.get(pingIndex).targetNodes.add(i);
                    if(i.target == manager.robotList.get(pingIndex).target){
                        manager.robotList.get(pingIndex).targetNodeFromShare = i;
                    }
                }
            }
            for(List<Integer> i: visitedNodes){
                if(!manager.robotList.get(pingIndex).visitedNodes.contains(i)){
                    manager.robotList.get(pingIndex).visitedNodes.add(i);
                }
            }

            Stack<int[]> copyStack = adjacentNodes;
            manager.robotList.get(pingIndex).adjacentNodes.addAll(copyStack);

            System.out.println("ID: " + ID + "New Merged Map");
            System.out.println(miniMap);

            if(completeTask){
                currentState = States.GOINGHOME;
            }else{
                currentState = States.EXPLORING;
            }


            //currentState = States.FINISHED;
        } else if(currentState == States.APPROACHINGTARGET){

            approachingPingTargetLogic(grid, timeStep);


        }


    }

    public void travelThroughUnknown(){

    }


    public int heuristicCalculator(int startx, int starty, int endx, int endy){
        int totalx = Math.abs(endx - startx);
        int totaly = Math.abs(endy - starty);
        return totalx + totaly;
    }

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();


        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] d : directions) {
            int nx = node.rowNum + d[0];
            int ny = node.colNum + d[1];

            if (nx >= 0 && ny >= 0 &&
                    nx < miniMap.size && ny < miniMap.size &&
                    !miniMap.getSpot(nx,ny).placeHolder && miniMap.getSpot(nx,ny).isOpen) {

                neighbors.add(miniMap.getSpot(nx, ny));
            }
        }
        return neighbors;
    }

    public List<Node> getAllNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();


        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] d : directions) {
            int nx = node.rowNum + d[0];
            int ny = node.colNum + d[1];

            if (nx >= 0 && ny >= 0 &&
                    nx < miniMap.size && ny < miniMap.size && miniMap.getSpot(nx,ny).isOpen) {

                neighbors.add(miniMap.getSpot(nx, ny));
            }
        }
        return neighbors;
    }



    public Stack<Node> calculatePathTarget(int[] goal, boolean onlyThroughKnownNodes){
        PriorityQueue<Node> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(a -> a.fCost)
        );

        boolean[][] closedSet = new boolean[miniMap.size][miniMap.size];
        Node start = miniMap.getSpot(position[0], position[1]);
        start.setgCost(0);
        start.sethCost(heuristicCalculator(start.rowNum, start.colNum, goal[0], goal[1]));
        start.setfCost(start.gCost + start.hCost);

        openSet.add(start);

        while(!openSet.isEmpty()){
            Node current = openSet.poll();

            //goal check
            if(current.rowNum == goal[0] && current.colNum == goal[1]){
                resetNodeCosts();
                return reconstructPath(current);
            }

            closedSet[current.rowNum][current.colNum] = true;
            List<Node> neighbors;
            if(onlyThroughKnownNodes){
                neighbors = getNeighbors(current);
            } else{
                neighbors = getAllNeighbors(current);
            }


            for(Node neighbor : neighbors){
                if(closedSet[neighbor.rowNum][neighbor.colNum]){
                    continue;
                }
                int tentativeG = current.gCost + 1; // cost of moving to neighbor

                boolean inOpen = openSet.contains(neighbor);



                if (!inOpen || tentativeG < neighbor.gCost) {
                    neighbor.setgCost(tentativeG);
                    neighbor.sethCost(heuristicCalculator(neighbor.rowNum, neighbor.colNum, goal[0], goal[1]));
                    neighbor.setfCost(neighbor.gCost + neighbor.hCost);
                    neighbor.parent = new int[]{current.rowNum,current.colNum};

                    if (!inOpen) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null;

    }

    public Stack<Node> reconstructPath(Node goal){
        Stack<Node> path = new Stack<>();
        Node current = goal;

        while(current.parent != null){
            path.push(current);
            Node temp = current;
            current = miniMap.getSpot(current.parent[0], current.parent[1]);
            temp.parent = null;
        }

        return path;
    }

    public Node getClosestNeighborToNode(Node currentNode, Node goalNode){
        List<Node> neighbors = getNeighbors(goalNode);
        Node closestNode = currentNode;
        int shortestDistance = Integer.MAX_VALUE;
        for(Node i: neighbors){
            int[] position = {i.rowNum,i.colNum};
            int distance = calculatePathTarget(position,true).size();
            if(distance < shortestDistance){
                closestNode = i;
            }
        }
        return closestNode;
    }

    public void approachingPingTargetLogic(Grid grid, int timeStep){
        currentState = States.APPROACHINGTARGET;
        int[] targetSpot = {targetNodeFromShare.rowNum, targetNodeFromShare.colNum};
        pathToTarget = calculatePathTarget(targetSpot, false);

        if(!pathToTarget.isEmpty()) {
            Node tempNode = pathToTarget.pop();
            if (tempNode.isOpen) {
                position[0] = tempNode.rowNum;
                position[1] = tempNode.colNum;

                Node timeNode = new Node(grid.getSpot(position[0], position[1]), timeStep);
                miniMap.setSpot(position[0], position[1], timeNode);
            }
        }else{
            currentState = States.GOINGHOME;
            System.out.println("ID: " + ID + "Found target from share");
            System.out.println(miniMap);
        }
    }

    public void resetNodeCosts(){
        for (int i = 0; i < miniMap.size; i++) {
            for (int j = 0; j < miniMap.size; j++) {
                miniMap.getSpot(i,j).fCost = Integer.MAX_VALUE;
                miniMap.getSpot(i,j).gCost = Integer.MAX_VALUE;
                miniMap.getSpot(i,j).hCost = Integer.MAX_VALUE;


            }

        }
    }
    public List<List<Integer>> getVisitedNodes() {
        return visitedNodes;
    }

    public void setCompleteTask(boolean completeTask) {
        this.completeTask = completeTask;
    }

    public void setManager(TravManager manager) {
        this.manager = manager;
    }
}
