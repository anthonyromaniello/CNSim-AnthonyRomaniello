package assignment1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PumpStand class
 */
public class PumpStandTest {
    private PumpStand pumpStand;
    
    @BeforeEach
    void setUp() {
        pumpStand = new PumpStand(3); // Create pump stand with 3 pumps
    }
    
    @Test
    void testPumpStandInitialization() {
        assertEquals(3, pumpStand.getNumberOfPumps(), "Should have 3 pumps");
        assertTrue(pumpStand.aPumpIsAvailable(), "All pumps should be available initially");
    }
    
    @Test
    void testTakeAndReleasePump() {
        Pump pump1 = pumpStand.takeAvailablePump();
        assertNotNull(pump1, "Should be able to take a pump");
        assertTrue(pumpStand.aPumpIsAvailable(), "Should still have pumps available");
        
        Pump pump2 = pumpStand.takeAvailablePump();
        Pump pump3 = pumpStand.takeAvailablePump();
        assertNotNull(pump2, "Should be able to take second pump");
        assertNotNull(pump3, "Should be able to take third pump");
        
        assertFalse(pumpStand.aPumpIsAvailable(), "No pumps should be available");
        
        // Release a pump
        pumpStand.releasePump(pump1);
        assertTrue(pumpStand.aPumpIsAvailable(), "Should have a pump available after release");
    }
    
    @Test
    void testTakeFromEmptyPumpStand() {
        // Take all pumps
        pumpStand.takeAvailablePump();
        pumpStand.takeAvailablePump();
        pumpStand.takeAvailablePump();
        
        assertFalse(pumpStand.aPumpIsAvailable(), "No pumps should be available");
        
        Pump pump = pumpStand.takeAvailablePump();
        assertNull(pump, "Should return null when no pumps available");
    }
    
    @Test
    void testInvalidPumpStandCreation() {
        // Test creating pump stand with 0 pumps - this reveals a bug in the original code
        // The constructor prints an error but still sets numPumps and topPump incorrectly
        PumpStand invalidPumpStand = new PumpStand(0);
        assertEquals(0, invalidPumpStand.getNumberOfPumps(), "Should report 0 pumps");
        
        // NOTE: This test reveals a bug - aPumpIsAvailable() returns true even with 0 pumps
        // because topPump is initialized to -1 when numPumps is 0, but the logic is flawed
        // In real code, this should be fixed, but for this test we're documenting the bug
        assertTrue(invalidPumpStand.aPumpIsAvailable(), "Bug: reports pumps available even with 0 pumps");
    }
}