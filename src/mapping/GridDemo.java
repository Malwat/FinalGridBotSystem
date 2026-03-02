package mapping;

import java.util.List;
import java.util.Stack;

public class GridDemo {

    public static void main(String[] args) {

        Grid grid = new Grid(7);
        Traverser robot1 = new Traverser(1,1, new int[]{0, 0}, grid.size);
        Traverser robot2 = new Traverser(2,2, new int[]{0, 5}, grid.size);
        Traverser robot3 = new Traverser(3,3, new int[]{6, 6}, grid.size);


//        grid.getSpot(3,1).setOpen(false);
//        grid.getSpot(2,1).setOpen(false);
//        grid.getSpot(1,1).setOpen(false);
//
//        grid.getSpot(3,4).setOpen(false);
//        grid.getSpot(3,3).setOpen(false);
//
//        grid.getSpot(1,2).setOpen(false);
//        grid.getSpot(1,3).setOpen(false);
//        grid.getSpot(1,4).setOpen(false);
//
//        TravManager travManager = new TravManager(grid);
//        travManager.addTraverser(robot1);
//        robot1.setManager(travManager);
//
//        grid.getSpot(0,2).setTarget(1);
//        System.out.println(grid);
//        System.out.println(robot1.miniMap);
//        travManager.search();
//        System.out.println(robot1.miniMap);



//        grid.getSpot(4,3).setOpen(false);
//        grid.getSpot(3,3).setOpen(false);
//        grid.getSpot(2,3).setOpen(false);
//
//        grid.getSpot(2,2).setOpen(false);
//        grid.getSpot(2,1).setOpen(false);
//        grid.getSpot(4,4).setOpen(false);
//
//        grid.getSpot(9,8).setOpen(false);
//        grid.getSpot(8,8).setOpen(false);
//        grid.getSpot(7,8).setOpen(false);
//
//        grid.getSpot(0,6).setOpen(false);
//        grid.getSpot(1,6).setOpen(false);
//        grid.getSpot(2,6).setOpen(false);





        System.out.println(grid);
        System.out.println(robot1.getMiniMap().toString());
        System.out.println(robot2.getMiniMap().toString());


        grid.getSpot(6,3).setTarget(2);
        grid.getSpot(0,5).setTarget(3);
        grid.getSpot(3,3).setTarget(1);
//        grid.getSpot(7,9).setTarget(2);
//        grid.getSpot(1,7).setTarget(3);



        TravManager travManager = new TravManager(grid);
        travManager.addTraverser(robot1);
        travManager.addTraverser(robot2);
        travManager.addTraverser(robot3);



        robot1.setManager(travManager);
        robot2.setManager(travManager);
        robot3.setManager(travManager);


        travManager.search();

        System.out.println("Robot1");
        System.out.println(robot1.getMiniMap().toString());

        System.out.println("Robot2");
        System.out.println(robot2.getMiniMap().toString());

        System.out.println("Robot3");
        System.out.println(robot3.getMiniMap().toString());

        for(Node i: robot1.targetNodes ) {
            System.out.println(i);
        }
        for(Node i: robot2.targetNodes ) {
            System.out.println(i);
        }
        for(Node i: robot3.targetNodes ) {
            System.out.println(i);
        }

    }

    //cause of ping when they find something
    //robot1 will broadcast a ping in the set vicinity
    //robots in that vicinity will ping back with their own id and their position
    //robot1 will then ping back with the id of the closest one
    //robot1 and chosen robot (robot2) will have shared positions and id with their pings
    //they then will travel to the other person's position
}
