import com.ibm.jc.JavaChallenge;
import com.ibm.rally.Car;
import com.ibm.rally.ICar;
import com.ibm.rally.IObject;
import com.ibm.rally.World;

@JavaChallenge(name = "Andrew", organization = "Andrew749 productions")
public class RallyCar extends Car {
	/**
	 * This car's is prioritized to first ensure that it has enough gas to run
	 * and then will fulfill checkpoints to ensure that it can run for the
	 * entirety of the match
	 **/
	// state 1 means that the car hits another car
	int wait = 0;
	int gasAmount;
	int heading;
	int headingtogas;
	int gasstate;
	int gasx, gasy;
	int gotodepot;
	int headingtocheckpoints;
	int checkpointnumber = 0;
	int flagx, flagy;
	int sparetirenum;
	static int checkpointlength;
	int headingsparetiredepot;
	static IObject[] Opponents;

	public byte getColor() {
		return CAR_YELLOW;
	}

	public void initialize() {

	}

	public void move(int lastMoveTime, boolean hitWall, ICar collidedWithCar,
			ICar hitBySpareTire) {
		Opponents = getOpponents();
		gasAmount = getFuel();
		IObject nearest = getNearestOpponent();
		IObject closestgas = getNearestGas();
		IObject checkpoints = World.getCheckpoints()[checkpointnumber];
		checkpointlength = World.getCheckpoints().length - 1;
		headingtocheckpoints = getHeadingTo(checkpoints);
		IObject sparetire = World.getSpareTireDepots()[0];
		headingsparetiredepot = getHeadingTo(sparetire);
		flagx = (int) checkpoints.getX();
		flagy = (int) checkpoints.getY();
		gotodepot = getHeadingTo(closestgas);
		gasx = (int) closestgas.getX();
		gasy = (int) closestgas.getY();
		heading = getHeading();
		headingtogas = (gotodepot);
		sparetirenum = getNumberOfSpareTires();
		if (collidedWithCar != null) {
			wait = 10;
		}
		// moves to gas depot when equal to or under 50 gas units
		if (gasAmount <= 50) {
			gasstate = 1;
			setSteeringToLocation(headingtogas);
			if (gasstate == 1) {
				if ((getX() >= gasx - 20) && (getX() <= gasx + 20)) {
					if ((getY() >= gasy - 20) && (getY() <= gasy + 20)) {
						setThrottle(0);
						enterProtectMode();
					}
				}
			}

		}
		
		// The gasstate when the car has a full tank to 50 fuel units
		if (gasstate == 0) {
			if (wait == 0) {
				if (getNumberOfSpareTires() >= 3) {
					setThrottle(80);
					setSteeringToLocation(headingtocheckpoints);
					gooverflag(flagy, flagx);
				} else if (getNumberOfSpareTires() < 3) {
					setSteeringToLocation(headingsparetiredepot);
					if ((getX() >= World.getSpareTireDepots()[0].getX() - 20)
							&& (getX() <= World.getSpareTireDepots()[0].getX() + 20)) {
						if ((getY() >= World.getSpareTireDepots()[0].getY() - 20)
								&& (getY() <= World.getSpareTireDepots()[0]
										.getY() + 20)) {
							setThrottle(0);
						}
					}
				}
			} else if (wait > 0) {
				setThrottle(MIN_THROTTLE);
				wait--;
			}
		}
		// sets the gas state to 0 once done refueling to tell the car to resume
		// racing
		if (gasAmount == 100) {
			gasstate = 0;
		}
		// throws a spare tire at the nearest opponent
		//doestn work
		/** 
		if (getDistanceTo(getNearestOpponent()) <= 100) {
			enterProtectMode();
			setSteeringToLocation(getHeadingTo(nearest.getX(),nearest.getY()));
			if (getHeading() >= getHeadingTo(nearest) - 30
					|| getHeading() <= getHeadingTo(nearest) + 30){
				throwSpareTire();
			}
		}
		**/
	}

	// gets the closest car
	public IObject getNearestOpponent() {
		double minDistance = 1000000, distance;
		IObject theNearestOneO = null;
		for (int f = 0; f < Opponents.length; f++) {
			distance = getDistanceTo(Opponents[f]);
			if (distance < minDistance) {
				minDistance = distance;
				theNearestOneO = Opponents[f];
			}
		}
		return theNearestOneO;
	}

	// finds the nearest gas station
	public IObject getNearestGas() {
		double distance, mindistance = 10000000;
		IObject closestgas = null;
		for (int i = 0; i < World.getFuelDepots().length; ++i) {
			distance = getDistanceTo(World.getFuelDepots()[i]);
			if (distance < mindistance) {
				mindistance = distance;
				closestgas = World.getFuelDepots()[i];
			}
		}
		return closestgas;
	}

	public void setSteeringToLocation(int headingtolocation) {
		if (heading < headingtolocation) {
			setSteeringSetting(MAX_STEER_RIGHT);

		} else if (heading > headingtolocation) {
			setSteeringSetting(MAX_STEER_LEFT);
		}
	}

	// finds the closest checkpoint
	public IObject closestcheckpoint() {
		double distance, mindistance = 1000000;
		IObject checkpoint = null;
		for (int i = 0; i < World.getCheckpoints().length; ++i) {
			distance = getDistanceTo(World.getCheckpoints()[i]);
			if (distance < mindistance) {
				checkpoint = World.getCheckpoints()[i];
			}
		}
		return checkpoint;
	}

	// determines if the car goes over the checkpoint in order to tell it to go
	// to the next checkpoint
	public void gooverflag(int locationy, int locationx) {
		if ((getX() >= locationx - 30) && (getX() <= locationx + 30)) {
			if ((getY() >= locationy - 30) && (getY() <= locationy + 30)) {
				if (checkpointnumber == checkpointlength) {
					++checkpointnumber;
					setThrottle(20);
				} else {
					checkpointnumber = 0;

				}

			}
		}
	}
}