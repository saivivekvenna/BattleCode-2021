package blueWater;
import battlecode.common.*;

import java.awt.*;
import java.util.*;
import scala.Int;

public strictfp class RobotPlayer {
    static RobotController rc;

    static final RobotType[] spawnableRobot = {
            RobotType.POLITICIAN,
            RobotType.SLANDERER,
            RobotType.MUCKRAKER,
    };

    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    static int turnCount;


    ///////////VARIABLES//////////////
    static MapLocation slandererPos;
    static MapLocation muckrakerPos;
    static MapLocation politicianPos;
    static MapLocation enlightenmentCenterPos;
    static MapLocation enemyEnlightenmentCenterPos;
    static MapLocation neturalEnlightenmentCenterPos;
    static int muckrakerCount = 0;
    static int influence = 0;
    static int pastVote = 0;   //by Paulina
    static int myBid = 1;      //by Paulina
    static int politicianCount = 0;
    static int slandererCount = 0;
    static boolean voted = false;
    static int enemyEnlightenmentCenterId = -1;
    static MapLocation muckrakerTarget = null;
    static Set<Integer> IDs
            = new LinkedHashSet<Integer>(200);
    static int ECID;
    static int EECID;
    static int NECID;
    static boolean TEST = false;
    //static MapLocation enemylightenmentCenterPos;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;

            for (RobotInfo robot : rc.senseNearbyRobots()) {
                if (enlightenmentCenterPos == null) {
                    if (robot.type.equals(RobotType.ENLIGHTENMENT_CENTER)) {
                        enlightenmentCenterPos = robot.location;
                        ECID = robot.ID;
                        break;
                    }
                }
                for (RobotInfo netural : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.NEUTRAL)) {
                    if (neturalEnlightenmentCenterPos == null) {
                        if (netural.getType().equals(RobotType.ENLIGHTENMENT_CENTER)) {
                            neturalEnlightenmentCenterPos = netural.location;
                            NECID = netural.ID;
                            break;
                        }
                    }
                }
            }

            // Try/catch blocks stop unhandled exceptions, which cause your robot to freeze
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You may rewrite this into your own control structure if you wish.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case ENLIGHTENMENT_CENTER:
                        runEnlightenmentCenter();
                        break;
                    case POLITICIAN:
                        runPolitician();
                        break;
                    case SLANDERER:
                        runSlanderer();
                        break;
                    case MUCKRAKER:
                        runMuckraker();
                        break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runEnlightenmentCenter() throws GameActionException {
        //flag stuff
        RobotInfo[] robotList = rc.senseNearbyRobots();
        for (RobotInfo r : robotList) {
            int rID = r.getID();
            IDs.add(rID);
        }

        int counter = 0;

        for (int id : IDs) {
//            if (counter == 100){
//                break;
//            }
            int flag = rc.getFlag(id);
            try {
                //int flag = rc.getFlag(id);
                if (flag != 0) {
                    MapLocation locationOfEnemyEC = getLocationFromFlag(flag);
                    sendLocation(locationOfEnemyEC);
                }
            } catch (Exception e) {
            }
            counter++;
        }
//            try {
//                int flagofnetural = rc.getFlag(id);
//                if (flagofnetural != 0 && flagofnetural != flag) {
//                    System.out.println("FLAG SENT. FLAG:" + flagofnetural);
//                    MapLocation locationOfNeturalEC = getLocationFromFlag(flagofnetural);
//                    System.out.println("THIS IS IDWITHFLAG: " + id + "LOCATION OF netural  EC: " + locationOfNeturalEC);
//                    sendLocation(locationOfNeturalEC);
//                }
//            } catch (Exception e) {
//            }
//            counter++;
//        }


        if (rc.getTeamVotes() == pastVote && voted) {
            myBid += 1;
        }
        if (rc.canBid(myBid)) {
            rc.bid(myBid);
            pastVote = rc.getTeamVotes();
            voted = true;
        } else {
            voted = false;
        }


//        boolean haveBid = false;
//        int bidinf = Math.round(rc.getInfluence() / 25);
//        if (rc.getRoundNum() >= 0 && rc.canBid(bidinf) && rc.getTeamVotes() <= 750) {
//            haveBid = true;
//            rc.bid(bidinf);
//        }
//
//        //SLANDERER SPAWNING
//        try {
//            if (rc.getRoundNum() <= 5 && rc.canBuildRobot(RobotType.SLANDERER, Direction.NORTH, 10)) {
//                rc.buildRobot(RobotType.SLANDERER, Direction.NORTHWEST,10);
//            }
//        } catch (Exception e) {
//            System.out.println();
//        }
//
//        //DIRECTIONS LIST AND MUCKRAKER SPAWNING
        Direction[] directionsList = new Direction[]{Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
//        if (rc.getRoundNum() <= 200) {
//            if (rc.canBuildRobot(RobotType.MUCKRAKER, directionsList[1], 1)) {
//                rc.buildRobot(RobotType.MUCKRAKER, directionsList[1], 1);
//                muckrakerCount++;
//            }
//        }

        //POLITICIAN SPAWNING
        if (rc.getRoundNum() > 20) {
            if (rc.canBuildRobot(RobotType.POLITICIAN, directionsList[1], 50)) {
                rc.buildRobot(RobotType.POLITICIAN, directionsList[1], 50);
                politicianCount += 1;
            }
        }
//
//        //RANDOM ROBOT AFTER ROUND 200
//        if (rc.getRoundNum() > 200 && rc.canBuildRobot(RobotType.MUCKRAKER, Direction.EAST, 1)) {
//            rc.buildRobot(RobotType.MUCKRAKER, Direction.EAST, 1);
//        }
   }


    static void runPolitician() throws GameActionException {
        int actionRadius = rc.getType().actionRadiusSquared;
        if (rc.getFlag(ECID) == 0) {
            smartMove(rc.getLocation().directionTo(enlightenmentCenterPos).opposite());
        }

        for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent())) {
            if (enemy.getType().equals(RobotType.ENLIGHTENMENT_CENTER)) {
                enemyEnlightenmentCenterPos = enemy.location;
                EECID = enemy.ID;
                sendLocation(enemyEnlightenmentCenterPos);
            }
            if (enemy.getType().equals(RobotType.ENLIGHTENMENT_CENTER)) {
                rc.empower(actionRadius);
            }
        }

        Team neutralEC = Team.NEUTRAL;
        RobotInfo[] attackablenEC = rc.senseNearbyRobots(actionRadius, neutralEC);

        for (RobotInfo neutral : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, neutralEC)) {
            rc.empower(actionRadius);
        }
    }

    static void runSlanderer() throws GameActionException {

        tryMove(randomDirection());
        //i have know idea what i am doing
    }


    static void runMuckraker() throws GameActionException {
//        Team enemy = rc.getTeam().opponent();
//        int actionRadius = rc.getType().actionRadiusSquared;
//        for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
//            if (robot.type.canBeExposed()) {
//                if (rc.canExpose(robot.location)) {
//                    rc.expose(robot.location);
//                    return;
//                }
//            }
//        }
//
//        for (RobotInfo enemyec : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent())) {
//            if (enemyec.getType().equals(RobotType.ENLIGHTENMENT_CENTER)) {
//                System.out.println("FOUND ENEMY!!! THIS IS ENEMY: " + enemy);
//                enemyEnlightenmentCenterPos = enemyec.location;
//                EECID = enemyec.ID;
//                sendLocation(enemyEnlightenmentCenterPos);
//            }
//        }
//
//        for (Direction dir : directions) {
//            if (rc.getLocation().add(dir).distanceSquaredTo(enlightenmentCenterPos) > 2 && rc.getLocation().add(dir).distanceSquaredTo(enlightenmentCenterPos) <= 8) {
//
//                }
//            }
//
////        if (rc.getRoundNum() >= 80 && rc.getFlag(ECID) == 0 && rc.getInfluence() == 1) {
////            tryMove(randomDirection());
////        }
//
//        if (rc.getFlag(ECID) != 0 && rc.getRoundNum() >= 110) {
//            goToLocation(getLocationFromFlag(rc.getFlag(ECID)));
//        }else {
//            tryMove(randomDirection());
//        }
    }


//            Team enemy = rc.getTeam().opponent();
//            int actionRadius = rc.getType().actionRadiusSquared;
//            for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
//                if (robot.type.canBeExposed()) {
//                    // It's a slanderer... go get them!
//                    if (rc.canExpose(robot.location)) {
//                        System.out.println("e x p o s e d");
//                        rc.expose(robot.location);
//                        return;
//                    }
//                }
//            }
    // if (rc.getFlag(ECID)!= 0) {
    //muckrakerTarget = getLocationFromFlag(rc.getFlag(enemyEnlightenmentCenterId));
    //}

    //}


    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnableRobotType() {
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    //new build function by very cool and awsome TIM
    static boolean tryBuild(RobotType typ, Direction dir, int inf) throws GameActionException {
        if (rc.canBuildRobot(typ, dir, inf)) {
            rc.buildRobot(typ, dir, inf);
            return true;
        } else return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // COMMUNICATION

    static final int NBITS = 7;
    static final int BITMASK = (1 << NBITS) - 1;

    static void sendLocation(MapLocation location) throws GameActionException {
        int x = location.x, y = location.y;
        int encodedLocation = ((x & BITMASK) << NBITS) + (y & BITMASK);
        if (rc.canSetFlag(encodedLocation)) {
            rc.setFlag(encodedLocation);
        }
    }

    static void sendLocation(MapLocation location, int extraInformation) throws GameActionException {
        int x = location.x, y = location.y;
        int encodedLocation = (extraInformation << (2 * NBITS)) + ((x & BITMASK) << NBITS) + (y & BITMASK);
        if (rc.canSetFlag(encodedLocation)) {
            rc.setFlag(encodedLocation);
        }
    }

    static MapLocation getLocationFromFlag(int flag) {
        int y = flag & BITMASK;
        int x = (flag >> NBITS) & BITMASK;
        // int extraInformation = flag >> (2*NBITS);

        MapLocation currentLocation = rc.getLocation();
        int offsetX128 = currentLocation.x >> NBITS;
        int offsetY128 = currentLocation.y >> NBITS;
        MapLocation actualLocation = new MapLocation((offsetX128 << NBITS) + x, (offsetY128 << NBITS) + y);

        // You can probably code this in a neater way, but it works
        MapLocation alternative = actualLocation.translate(-(1 << NBITS), 0);
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(1 << NBITS, 0);
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, -(1 << NBITS));
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, 1 << NBITS);
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        return actualLocation;
    }

    static void smartMove(Direction dir) throws GameActionException {
        //FUZZY NAVIGATION

        if (tryMove(dir)) {
            return;
        } else if (tryMove(dir.rotateRight())) {
            return;
        } else if (tryMove(dir.rotateRight().rotateRight())) {
            return;
        } else if (tryMove(dir.rotateRight().rotateRight().rotateRight())) {
            return;
        } else if (tryMove(dir.rotateLeft())) {
            return;
        } else if (tryMove(dir.rotateLeft().rotateLeft())) {
            return;
        } else if (tryMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
            return;
        }
    }

    ///////////////////jacob made this function////////////////
    static void goToLocation(MapLocation location) throws GameActionException {
        MapLocation myLocation = rc.getLocation();
        if (myLocation.x > location.x && myLocation.y > location.y) {
            smartMove(Direction.SOUTHWEST);
        } else if (myLocation.x < location.x && myLocation.y < location.y) {
            smartMove(Direction.NORTHEAST);
        } else if (myLocation.x > location.x && myLocation.y < location.y) {
            smartMove(Direction.NORTHWEST);
        } else if (myLocation.x < location.x && myLocation.y > location.y) {
            smartMove(Direction.SOUTHEAST);
        } else if (myLocation.x == location.x && myLocation.y > location.y) {
            smartMove(Direction.SOUTH);
        } else if (myLocation.x > location.x && myLocation.y == location.y) {
            smartMove(Direction.WEST);
        } else if (myLocation.x < location.x && myLocation.y == location.y) {
            smartMove(Direction.EAST);
        } else if (myLocation.x == location.x && myLocation.y < location.y) {
            smartMove(Direction.NORTH);
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    // BASIC BUG - just follow the obstacle while it's in the way

    static final double passabilityThreshold = 0.7;
    static Direction bugDirection = null;

    static void basicBug(MapLocation target) throws GameActionException {
        Direction d = rc.getLocation().directionTo(target);
        if (rc.getLocation().equals(target)) {
            // do something else, now that you're there
            // here we'll just explode
            if (rc.canEmpower(1)) {
                rc.empower(1);
            }
        } else if (rc.isReady()) {
            if (rc.canMove(d) && rc.sensePassability(rc.getLocation().add(d)) >= passabilityThreshold) {
                rc.move(d);
                bugDirection = null;
            } else {
                if (bugDirection == null) {
                    bugDirection = d;
                }
                for (int i = 0; i < 8; ++i) {
                    if (rc.canMove(bugDirection) && rc.sensePassability(rc.getLocation().add(bugDirection)) >= passabilityThreshold) {
                        rc.setIndicatorDot(rc.getLocation().add(bugDirection), 0, 255, 255);
                        rc.move(bugDirection);
                        bugDirection = bugDirection.rotateLeft();
                        break;
                    }
                    rc.setIndicatorDot(rc.getLocation().add(bugDirection), 255, 0, 0);
                    bugDirection = bugDirection.rotateRight();
                }
            }
        }
    }
}







