package Ba_ba_booie;
import battlecode.common.*;

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
    static MapLocation enemyEnlightenmentCenter;
    static int muckrakerCount = 0;
    static int influence = 0;
    static int pastVote = 0;   //by Paulina
    static int myBid = 1;      //by Paulina
    static int politicianCount =0;
    static int slandererCount =0;
    static boolean voted = false;
    static int enemyEnlightenmentCenterId = -1;
    static MapLocation muckrakerTarget = null;

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
                if (slandererPos == null) {
                    if (robot.type == RobotType.SLANDERER) {
                        slandererPos = robot.location;
                        break;
                    }
                }

                if (muckrakerPos == null) {
                    if (robot.type == RobotType.MUCKRAKER) {
                        muckrakerPos = robot.location;
                        break;
                    }
                }

                if (politicianPos == null) {
                    if (robot.type == RobotType.POLITICIAN) {
                        politicianPos = robot.location;
                        break;
                    }
                }

                if (enlightenmentCenterPos == null) {
                    if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
                        enlightenmentCenterPos = robot.location;
                        break;
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
        if (rc.getTeamVotes() == pastVote && voted){
            myBid += 1;
        }

        if(rc.canBid(myBid)){
            rc.bid(myBid);
            pastVote =  rc.getTeamVotes();
            voted = true;
        }else{
            voted = false;
        }

        //if(rc.canBid(1)){
        //    rc.bid(1);
        //}

        if (slandererCount < 1) {
            tryBuild(RobotType.SLANDERER, Direction.NORTHEAST, 1);
            slandererCount++;
        }
        if (politicianCount < 20) {
            tryBuild(RobotType.POLITICIAN, randomDirection(),1);
            politicianCount++;
        }
        if (muckrakerCount < 12) {
            Direction dir = randomDirection();
            for(int i = 0; i < 7; i++) {
                if(rc.canBuildRobot(RobotType.MUCKRAKER, dir, 1)) {
                    rc.buildRobot(RobotType.MUCKRAKER, dir, 1);
                    muckrakerCount++; // ++ is synonymous with += 1
                    break;
                }
                dir = dir.rotateRight();
            }
        }
        sendLocation(rc.getLocation().translate(15, 7));
    }


    static void runPolitician() throws GameActionException {
        //Senses the bad guys enlightment center
        // not sure what happened here lol
        // tryMove(rc.adjacentLocation(Direction.valueOf(Integer.toString(location.x) + Integer.toString(location.y)).opposite()));
        // for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent())) {
        //if (enemy.type == RobotType.ENLIGHTENMENT_CENTER) {
        //tell them what to do once they find it
            Team enemy = rc.getTeam().opponent();
            int actionRadius = rc.getType().actionRadiusSquared;
            RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
            if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
                System.out.println("empowering...");
                rc.empower(actionRadius);
                System.out.println("empowered");
                return;
            }
            if (tryMove(randomDirection()))
                System.out.println("I moved!");
        }



        static void runSlanderer () throws GameActionException {
            for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent())) {
                if (enemy.type == RobotType.MUCKRAKER) {
                    tryMove(Direction.NORTH.opposite());

                }
            }
        }



    static void runMuckraker() throws GameActionException {
        //smartMove(rc.getLocation().directionTo(enlightenmentCenterPos).opposite());
        //if (enemyEnlightenmentCenterId == -1) {
            for (RobotInfo enemy : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent())) {
                if (enemy.getType().equals(RobotType.ENLIGHTENMENT_CENTER)) {
                    System.out.println("ok i am working");
                    enemyEnlightenmentCenterId = enemy.ID;
                    sendLocation(enemyEnlightenmentCenter);
                    if (rc.canGetFlag(enemyEnlightenmentCenterId)) {
                        System.out.println("joe mama");
                        goToLocation(getLocationFromFlag(rc.getFlag(enemyEnlightenmentCenterId)));
                    }
                }else {tryMove(randomDirection());
                    System.out.println("yes it is me moving");
                }
               // if (rc.canGetFlag(enemyEnlightenmentCenterId)) {
                //muckrakerTarget = getLocationFromFlag(rc.getFlag(enemyEnlightenmentCenterId));
                  //}
                }
            }
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
    //new build function by TIM
    static boolean tryBuild(RobotType typ, Direction dir, int inf) throws GameActionException {
        if (rc.canBuildRobot(typ, dir, inf)) {
            rc.buildRobot(typ,dir,inf);
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
        int encodedLocation = (extraInformation << (2*NBITS)) + ((x & BITMASK) << NBITS) + (y & BITMASK);
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
    static boolean goToLocation(MapLocation location) throws GameActionException{
        MapLocation myLocation = rc.getLocation();
        while (true){
            if (myLocation.x > location.x && myLocation.y > location.y){
                smartMove(Direction.SOUTHWEST);
            }else if (myLocation.x < location.x && myLocation.y < location.y){
                smartMove(Direction.NORTHEAST);
            }else if (myLocation.x > location.x && myLocation.y < location.y){
                smartMove(Direction.NORTHWEST);
            }else if (myLocation.x > location.x && myLocation.y > location.y){
                smartMove(Direction.SOUTHEAST);
            }
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







