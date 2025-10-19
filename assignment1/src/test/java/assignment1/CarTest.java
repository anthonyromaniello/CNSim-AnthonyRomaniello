package assignment1;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Car class
 */
public class CarTest {
    
    @BeforeEach
    void setUp() {
        // Initialize the required static variables for Car to work
        Sim.litresNeededMin = 10.0;
        Sim.litresNeededRange = 50.0;
        Sim.litreStream = new Random(12345); // Fixed seed for predictable tests
    }
    
    @Test
    void testCarConstructor() {
        Car car = new Car();
        
        // Test that litres needed is within expected range
        double litres = car.getLitresNeeded();
        assertTrue(litres >= Sim.litresNeededMin, 
                   "Litres needed should be at least " + Sim.litresNeededMin);
        assertTrue(litres <= Sim.litresNeededMin + Sim.litresNeededRange, 
                   "Litres needed should be at most " + (Sim.litresNeededMin + Sim.litresNeededRange));
    }
    
    @Test
    void testArrivalTimeSetterAndGetter() {
        Car car = new Car();
        double testTime = 123.45;
        
        car.setArrivalTime(testTime);
        assertEquals(testTime, car.getArrivalTime(), 0.001, 
                     "Arrival time should match what was set");
    }
    
    @Test
    void testMultipleCarsHaveDifferentLitres() {
        Car car1 = new Car();
        Car car2 = new Car();
        
        // With random generation, cars should likely have different fuel needs
        // (This test might occasionally fail due to randomness, but very unlikely)
        assertNotEquals(car1.getLitresNeeded(), car2.getLitresNeeded(), 
                        "Different cars should likely need different amounts of fuel");
    }
}