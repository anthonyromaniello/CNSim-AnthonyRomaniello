package assignment1;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the CarQueue class
 */
public class CarQueueTest {
    private CarQueue queue;
    
    @BeforeEach
    void setUp() {
        queue = new CarQueue();
        // Initialize required static variables
        Sim.simulationTime = 0.0;
        Sim.litresNeededMin = 10.0;
        Sim.litresNeededRange = 50.0;
        Sim.litreStream = new Random(12345);
    }
    
    @Test
    void testEmptyQueueInitialization() {
        assertEquals(0, queue.getQueueSize(), "New queue should be empty");
        assertEquals(0.0, queue.getEmptyTime(), 0.001, "New queue should have zero empty time");
    }
    
    @Test
    void testInsertCar() {
        Car car = new Car();
        car.setArrivalTime(10.0);
        
        queue.insert(car);
        
        assertEquals(1, queue.getQueueSize(), "Queue should have 1 car after insert");
    }
    
    @Test
    void testInsertAndTakeFirstCar() {
        Car car1 = new Car();
        Car car2 = new Car();
        car1.setArrivalTime(10.0);
        car2.setArrivalTime(20.0);
        
        queue.insert(car1);
        queue.insert(car2);
        assertEquals(2, queue.getQueueSize(), "Queue should have 2 cars");
        
        Car retrievedCar = queue.takeFirstCar();
        assertSame(car1, retrievedCar, "Should retrieve the first car inserted");
        assertEquals(1, queue.getQueueSize(), "Queue should have 1 car remaining");
        
        Car secondCar = queue.takeFirstCar();
        assertSame(car2, secondCar, "Should retrieve the second car");
        assertEquals(0, queue.getQueueSize(), "Queue should be empty");
    }
    
    
    @Test
    void testTakeFromEmptyQueue() {
        Car result = queue.takeFirstCar();
        assertNull(result, "Taking from empty queue should return null");
    }
}