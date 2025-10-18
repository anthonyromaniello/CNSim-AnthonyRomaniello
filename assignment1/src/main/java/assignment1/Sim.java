package assignment1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Sim {
// "Global: quantities used throughout the simulation
    public static double simulationTime; // What time is it?
    public static double reportInterval; // How often should we report?
// quantities that determine how we model the real world
// In a more elaborate program, these might be input data.
// economics: profit per litre of gas, and cost to operate one pump for a day
    public static double profit = 0.025;
    public static double pumpCost = 20;
// demand; minimum and maximum amount of gas needed by a car
// See Car constructor.
    public static double litresNeededMin = 10;
    public static double litresNeededRange = 50;
// service times: constant base time+ time per litre+ random spread
// See Pump.serviceTime().
    public static double serviceTimeBase = 150;
    public static double serviceTimePerLitre = 0.5;
    public static double serviceTimeSpread = 30;
// customer behaviours probability of balkina depends on three
// ad-hoc constants. See Arrival.doesCarBalk().
    public static double balkA = 40;
    public static double balkB = 25; 
    public static double balkC = 3;
// customer arrival rate
// See Arrival.interarrivalTirne(). 


    public static double meanInterarrivalTime = 50; // seconds
// random-number streams used to model the world
    public static Random arrivalStream; // auto arrival times
    public static Random litreStream; // number of litres needed
    public static Random balkingStream; // balking probability
    public static Random serviceStream; // service times
// major data structures
    public static EventList eventList;
    public static CarQueue carQueue;
    public static PumpStand pumpStand;
    public static Statistics stats; 
/**
* main entrypoint - starts the application
* @param args java.lang.String[]
*/
public static void main(String[] args) throws IOException {
    BufferedReader in= new BufferedReader (new InputStreamReader(System.in));
    
    // Read data and print introduction.
    reportInterval = Double.parseDouble(in.readLine());
    double endingTime = Double.parseDouble(in.readLine());
    int numPumps = Integer.parseInt (in.readLine());
    System.out.print ("This simulation run uses " + numPumps + " pumps ");

    // Initialize the random-number streams.
    System.out.println ("and the following random number seeds:");
    int seed= Integer.parseInt (in.readLine());
    arrivalStream =new Random (seed);
    System.out.print("          " + seed);
    seed= Integer.parseInt (in.readLine());
    litreStream =new Random (seed);
    System.out.print("          " + seed);
    seed = Integer.parseInt (in.readLine());
    balkingStream =new Random (seed);
    System.out.print("          " + seed);
    seed= Integer.parseInt (in.readLine());
    serviceStream =new Random (seed);
    System.out.print("          " + seed);
    System.out.println ("");

// Create and initialize the event list, the car queue, the pump stand,
// and the statistics collector.
    eventList =new EventList ();
    carQueue =new CarQueue ();
    pumpStand =new PumpStand (numPumps);
    stats = new Statistics (); 


// Schedule the required events:
        // the end of the simulation;
        // the first progress report;
        // the arrival of the first car.
    EndOfSimulation lastEvent =new EndOfSimulation (endingTime);
    eventList.insert (lastEvent);
    if (reportInterval <= endingTime) {
        Report nextReport =new Report (reportInterval); 
        eventList.insert (nextReport);
    }
    eventList.insert (new Arrival (0));
// (Should the first car really arrive at time 0?}
// The "clock driver" loop
    while (true) {
        Event currentEvent = eventList.takeNextEvent();
        if (currentEvent == null) break;             // prevent a null event if list empty
        simulationTime = currentEvent.getTime();
        currentEvent.makeItHappen();
    if (currentEvent instanceof EndOfSimulation)
    break;

     }
    }
}

/** 
 * Statistics: the class for objects that collect statistics 
 * (there is only one such object in this program)
 */

class Statistics {
    //the eplicit initializations are not needed, but improve clarity 
    private int totalArrivals = 0;
    private int customersServed = 0;
    private int balkingCustomers = 0;
    private double totalLitresSold = 0.0;
    private double totalLitresMissed = 0.0;
    private double totalWaitingTime = 0.0;
    private double totalServiceTime = 0.0;

    /**
     * constructor 
     */
    public Statistics () {
        printHeaders();
    }

    /**
     * accumBalk: record and count a lost sale 
     * @param litres double
     */
    public void accumBalk (double litres) {
        balkingCustomers += 1;
        totalLitresMissed += litres;
    }

    /**
     * accumSale: record and count a sale
     * @param litres double 
     */
    public void accumSale (double litres) {
        customersServed += 1;
        totalLitresSold += litres;
    }

    /**
     * accumServiceTime: record a customer's service time 
     * @param interval double 
     */
    public void accumServiceTime (double interval) {
        totalServiceTime += interval; 
    }

    /**
     * accumWaitingTime: record a customer's waiting time 
     * @param interval double 
     */
    public void accumWaitingTime (double interval) {
        totalWaitingTime += interval;
    }

    /**
     * countArrival: record an Arrival
     */
    public void countArrival() {
        totalArrivals += 1;
    }
    /**
        * fmtDbl: convert a double to a string of a specified width representing
        * the number rounded to the specified nwnber of digits. The string
        *  returned is padded by blanks on the left if necessary. If it is too long,
        * it is not changed. If it is out of range for the "int" type, strange
        * results will be returned.
        * @return java.lang.Strina
        * @param number double
        • @param width int
        * @param precision int
    */
    private static String fmtDbl (double number, int width, int precision) {
        // round and convert to string without decimal point 
        // Use Locale.US to ensure '.' as decimal separator regardless of system locale
        try {
            String format = "%" + width + "." + precision + "f";
            return String.format(java.util.Locale.US, format, number);
        } catch (Exception e) {
            // Fallback: simple formatted number without fixed width
            return String.format(java.util.Locale.US, "%." + precision + "f", number);
        }

    }


    /**
        * fmtInt: convert an int to a string of a specified width.
        * The string returned is padded by blanks on the left if necessary.
        * If it is too long, it is not changed.
        * @return java.lang.string
        * @param number int
        * @param width int
     */

    private static String fmtInt (int number, int width) {
         try {
            String format = "%" + width + "d";
            return String.format(format, number);
        } catch (Exception e) {
            String s = Integer.toString(number);
            if (s.length() >= width) return s;
            return " ".repeat(Math.max(0, width - s.length())) + s;
        } 
    }

    /**
     * printHeaders: print column titles for the statistics summaries 
     * Current - Current simulation time
     * Total - Total number of cars that have arrived
     * NoQueue - Fraction of time the queue was empty
     * Car->Car - Average time between car arrivals
     * Average - Average litres needed per car
     * Number - Number of cars that balked (left without service)
     * Average - Average waiting time for served customers
     * Pump - Pump utilization rate
     * Total - Total profit/loss from operations
     * Lost - Profit lost from customers who balked
     */
    private static void printHeaders () {
        System.out.println("  Current Total  NoQueue  Car->Car Average   Number  Average  Pump   Total   Lost ");
        System.out.println("    Time   Cars  Fraction  Time    Litres    Balked   Wait   Usage  Profit  Profit");
        for (int i = 0; i < 79; i++){
            System.out.print("-");
        }
            System.out.println(""); 
    }


    /**
     * snapshot: print a summary of the statistics so far
     */
    public void snapshot() {
        System.out.print(fmtDbl (Sim.simulationTime, 8, 0));
        System.out.print(fmtInt (totalArrivals, 7));
        System.out.print(fmtDbl (Sim.carQueue.getEmptyTime()/Sim.simulationTime, 8, 3));

        if (totalArrivals > 0) {
            System.out.print(fmtDbl (Sim.simulationTime/totalArrivals, 9, 3));
            System.out.print(fmtDbl ((totalLitresSold + totalLitresMissed) / totalArrivals, 8,3));
        }
        else 
            System.out.print ("  Unknown Unknown");

        System.out.print(fmtInt(balkingCustomers, 8));
        if (customersServed > 0)
            System.out.print (fmtDbl (totalWaitingTime/customersServed, 9, 3));
        else    
            System.out.print ("  Unknown");

        System.out.print (fmtDbl (totalServiceTime / (Sim.pumpStand.getNumberOfPumps() * Sim.simulationTime), 7, 3));
        System.out.print (fmtDbl (totalLitresSold * Sim.profit - Sim.pumpCost * Sim.pumpStand.getNumberOfPumps(), 9, 2));
        System.out.print (fmtDbl (totalLitresMissed * Sim.profit, 7, 2));

        System.out.println("");
        }
    }

/**
 * Car: the class representing cars 
 */
class Car {
    double arrivalTime;
    double litresNeeded;

    /**
     * Constructor:
     * the number of litres requried is a property of a car, so it belongs in this class. 
     * it is also something the car "knows" when it arrives, so it 
     * should be calculated in the constructor 
     * 
     * the distribution of litres requried is uniform between 10 and 60 
     */

    public Car () {
        litresNeeded = Sim.litresNeededMin + Sim.litreStream.nextDouble() * Sim.litresNeededRange;
    }

    /**
     * getArrivalTime: return the car's arrival time
     * @return double 
     */
    public double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * getLitresNeeded: return the number of litres of fuel needed by the car 
     * @return double 
    */
    public double getLitresNeeded() {
        return litresNeeded;
    }

    /**
     * setArrivalTime: set the car's arrival time 
     * @param time double 
     */
    public void setArrivalTime (double time) {
        arrivalTime = time; 
    }
}

/**
 * CarQueue: the class representing the lineup of cars at the gas station
 */
class CarQueue {

    //QueueItem: the class for objects stored in the car queue
    private class QueueItem {
        /**
         * the car queue is a linked list, so each item contains a data field
         * and a "next item" field. this is just a simple record structure, so 
         * we'll allow outsiders to access the fields directly instead of using 
         * get and set methods 
        */
        public Car data;
        public QueueItem next; 
    }
    private QueueItem firstWaitingCar;
    private QueueItem lastWaitingCar; 
    private int queueSize;
    private double totalEmptyQueueTime; 

    /**
     * constructor.
     */
    public CarQueue () {
        firstWaitingCar = null;
        lastWaitingCar = null;
        queueSize = 0;
        totalEmptyQueueTime = 0;
    }

    /**
     * getEmptyTime: return the total time the car queue has been empty 
     * @return double 
     */
    public double getEmptyTime() {
        if (queueSize > 0)
            return totalEmptyQueueTime;
        else 
            return totalEmptyQueueTime + Sim.simulationTime;
        }

    /** 
     * getQueueSize: return the number of cars in the car queue 
     * @return int
     */
    public int getQueueSize () {
        return queueSize;
    }

    /**
     * insert: put a newly-arrived car into the car queue 
     * @param newestCar sim.car 
     */
    public void insert (Car newestCar) {
        QueueItem item = new QueueItem();
        item.data = newestCar;
        item.next = null; 

        if (lastWaitingCar == null) {
            // the queue is empty 
            firstWaitingCar = item; 
            totalEmptyQueueTime += Sim.simulationTime;
        }
        else {
            // the queue already had at least one cat in it 
            lastWaitingCar.next = item; 
        }

        lastWaitingCar = item; 
        queueSize += 1; 
    }

    /** 
     * takeFirstCar: remove first car from car queue and return it 
     * @return sim.Car
     */
    public Car takeFirstCar () {
        //precondition: queueSize > 0 && firstWaitingCar != null 
        if (queueSize <= 0 || firstWaitingCar == null) {
            System.out.println ("Error! car queue unexpectedly empty");
            return null; 
        }

        Car carToReturn = firstWaitingCar.data; 
        queueSize--;
        firstWaitingCar = firstWaitingCar.next;

        if (firstWaitingCar == null) {
            /**
             * empty queue: update the end of the queue, and start 
             * counting empty queue time 
            */
           lastWaitingCar = null; 
           totalEmptyQueueTime -= Sim.simulationTime;
        }

        return carToReturn;
    }
}

/** 
 * Pump: the class representing single pumps at the gas station
 */
class Pump {
    private Car carInService;

    /** 
     * getCarInService: return the car currently being served by the pump.
     * @return sim.car
     */
    public Car getCarInService() {
        return carInService;
    }

    /**
     * serviceTime: determine how long the service will tak e
     * this is a property of the pump-car combination, so the method could have 
     * been in the Car class if the appropriate information were available there 
     * 
     * Service times have a normal distribution with a mean given by a constant base plus 
     * an amount of time per litre, and with a fixed standard deviation 
     * @return double
     */
    private double serviceTime () {
        if (carInService == null) {
            System.out.println ("Error! no car in service when expected");
            return -1.0;
        }

        return Sim.serviceTimeBase + Sim.serviceTimePerLitre * carInService.getLitresNeeded() + Sim.serviceTimeSpread * Sim.serviceStream.nextGaussian();
    }

    /**
     * startService: the start-of-service event routine.
     * connects the car to this pump, and determines when the service will stop 
     * @param car sim.Car
     */
    public void startService (Car car) {
        //precondition: Sim.pumpStand.aPumpIsAvailable().

        //Match the auto to an available pump 
        carInService = car; 
        final double pumpTime = serviceTime();

        //collect statistics 
        Sim.stats.accumWaitingTime (Sim.simulationTime - carInService.getArrivalTime());
        Sim.stats.accumServiceTime(pumpTime);

        //Schedule departure of a car from this pump 
        Departure dep = new Departure (Sim.simulationTime + pumpTime);
        dep.setPump(this);
        Sim.eventList.insert(dep);
    }
}

/**
 * PumpStand: the class for the complete collection of pumps at the gas station 
 */
class PumpStand {
    private Pump[] pumps; //array of pumps 
    private int numPumps;
    private int topPump; 

    /**
     * constructor: build a PumpStand of numPumps pumps, and make all of them available 
     * @param numPumps int 
     */
    public PumpStand (int numPumps) {
        if (numPumps < 1) {
            System.out.println("Error! pump stand needs more than 0 pumps");
            return;
        }

        pumps = new Pump[numPumps];
        this.numPumps = numPumps; 
        topPump = numPumps -1;

        for (int p = 0; p < numPumps; p++)
            pumps[p] = new Pump();
    }

    /**
     * aPumpIsAvailable: return true/false according to whether at least one 
     * pump is free for use 
     * @return boolean 
     */
    public boolean aPumpIsAvailable() {
        return topPump >= 0;
    }

    /**
     * getNumberofPumps: return th enumber of pumps in the pump stand 
     * (this method is needed when satistics are gathered)
     * @return int
     */
    public int getNumberOfPumps() {
        return numPumps;
    }


    /**
     * releasePump: put pump p back in the stock of available pumps.
     * @param p sim.pump
     */
    public void releasePump (Pump p) {
        if (topPump >= numPumps) {
            System.out.println ("Error! attempt to release a free pump?");
            return; 
        }
        pumps[++topPump] = p;
    }

    /**
     * takeAvailablePump: take a pump from the set of free pumps, and return pump. 
     * @return sim.Pump 
     */
    public Pump takeAvailablePump () {
        if (topPump < 0) {
            System.out.println("Error! no pump available when needed");
            return null; 
        }
        return pumps[topPump--];
    }
}

/**
 * Event: the class representing events within the simulation model 
 * 
 * Remember that events are not entities in the same sense as cars and pumps are, 
 * and the event queue does not have the same reality as the car queue.
 * the event queue is a data structure 
 */
abstract class Event {
    private double time; //the time when the event happens

    /**
     * constructor 
     * @param time double
     */
    public Event (double time) {
        this.time = time;
    }

    /**
     * getTime: return the time of the vent 
     * @return double 
     */
    public double getTime () {
        return time;
    }

    /**
     * setTime: set the time of the event 
     * @param time double 
     */
    public void setTime (double time) {
        this.time = time; 
    }

    // required so Event references can invoke the event routine
    public abstract void makeItHappen();
}

/**
 * EventList: the class for the event list 
 * (there is only one object of the class in the program )
 */
class EventList {

    //ListItem: the class for objects stored in the event list 
    private class ListItem {
        /**
         * the event list is a linked list, so each item contains a data field 
         * and a "next item" field. this is just a simple record structure, so 
         * we'll allow outsiders to access the fields directly instead of using 
         * get and set methods 
         */
        public Event data;
        public ListItem next;
    }
    ListItem firstEvent;

    /** 
     * constructor 
     */
    public EventList() {
        firstEvent = null; //happens automatically, but done explicitly here to clarify the "empty list" state 
    }

    /**
     * insert: add an event e to the event list in the appropriate place, prioritized by name 
     * @param e sim.Event
     */
    public void insert (Event e) {
        //Create the item to go on the vent list 
        ListItem item = new ListItem();
        item.data = e;

        //find the appropriate place for the item in the event list and put it there
        final double time = e.getTime();
        if (firstEvent == null || time < firstEvent.data.getTime()) {
            item.next = firstEvent;
            firstEvent = item;
        }
        else {
            ListItem behind = firstEvent;
            ListItem ahead = firstEvent.next;
            while (ahead != null && ahead.data.getTime() <= time) {
                behind = ahead;
                ahead = ahead.next;
            }
            behind.next = item; 
            item.next = ahead;
        }
    }

    /**
     * takeNextEvent: remove the item at the head of the event list and return it
     * @return sim.Event
     */
    public Event takeNextEvent() {
        //precondition: firstEvent != null
        if (firstEvent == null) {
            System.out.println ("Error! ran out of events");
            return null;
        }

        Event eventToReturn = firstEvent.data;
        firstEvent = firstEvent.next;
        return eventToReturn; 
    }
}

/** 
 * Arrival: the class representing arrival events: 
 */
class Arrival extends Event {
    /**
     * constructor
     * @param time double 
     */
    public Arrival (double time) {
        super(time);
    }

    /**
     * doesCarBalk: decide whether a car should balk.
     * Deciding whether to balk is an activity that forms part of the arrival 
     * event, so this method belons among the event routines
     * 
     * the probability that a car leaves without buying gas (i.e., balks) grows 
     * larger as the queue length gets larger, and grows smaller when the car requires 
     * a greater number of litres of gas, so that: 
     * (1) there is no balking if the queue length is zero, and 
     * (2) otherwise, the probability of NOT balking is 
     *      (40 + litres)/(25 * (3 + queueLength))
     * @return boolean
     * @param litres double 
     * @param queueLength int
     */

    public boolean doesCarBalk(double litres, int queueLength) {
        if (queueLength <= 0)
            return false;
        return Sim.balkingStream.nextDouble() > (Sim.balkA + litres) / (Sim.balkB * (Sim.balkC + queueLength));
    }

    /** 
     * interarrivalTime: the time until the next arrival, from an exponential distribution 
     * @return double 
     */
    private double interarrivalTime() {
        double u = Sim.arrivalStream.nextDouble();
        if (u <= 0.0) u = 1e-12;
        return -Sim.meanInterarrivalTime * Math.log(u);
    }

    /**
     * makeItHappen: arrival event routine
     */
    public void makeItHappen() {
        //create and initialize a new auto record 
        Car arrivingCar = new Car();
        Sim.stats.countArrival();
        final double litres = arrivingCar.getLitresNeeded();
        if (doesCarBalk (litres, Sim.carQueue.getQueueSize()))
            Sim.stats.accumBalk(litres);
        else {
            arrivingCar.setArrivalTime(Sim.simulationTime);
            if (Sim.pumpStand.aPumpIsAvailable())
                Sim.pumpStand.takeAvailablePump().startService(arrivingCar);
            else 
                Sim.carQueue.insert(arrivingCar);
        }

        //schedule the next arrival, reusing the current event object.
        setTime (Sim.simulationTime + interarrivalTime());
        Sim.eventList.insert (this);
    }
}

/** 
 * Departure: the class representing departure events
 */
class Departure extends Event {
    private Pump pump; 

    /**
     * Constructor
     * @param time double 
     */
    public Departure(double time) {
        super(time);
    }

    /**
     * makeItHappen: departure event routine 
     */
    public void makeItHappen() {
        //precondition: pump != null && pump.getCarInService != null

        //Identify the departing car and collect statistics 
        Car departingCar = pump.getCarInService();
        Sim.stats.accumSale(departingCar.getLitresNeeded());

        //the car vanishes and the pump is free, can we serve another car?
        if (Sim.carQueue.getQueueSize() > 0)
            pump.startService(Sim.carQueue.takeFirstCar());
        else
            Sim.pumpStand.releasePump(pump);
    }

    /**
     * setPump: assign a pump to this arrival
     * @param pump sim.pump 
     */
    public void setPump (Pump pump) {
        this.pump = pump;
    }
}

/**
 * report: the class representing reporting events:
 */
class Report extends Event {

    /**
     * constructor 
     * @param time double 
     */
    public Report(double time) {
        super(time);
    }

    /**
     * makeItHappen: interim reporting event routine
     */
    public void makeItHappen() {
        Sim.stats.snapshot();

        //schedule the next interim report 
        setTime (Sim.simulationTime + Sim.reportInterval);
        Sim.eventList.insert(this);
    }
}

/**
 * EndOfSimulation: the class representing the final event that stops the simulation
 */
class EndOfSimulation extends Event {

    /**
     * constructor
     * @param time double
     */
    public EndOfSimulation(double time) {
        super(time);
    }

    /**
     * makeItHappen: end of simulation event routine 
     */
    public void makeItHappen() {
        Sim.stats.snapshot();
    }
}